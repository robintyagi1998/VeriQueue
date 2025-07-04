package com.robin.veriqueue.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.robin.veriqueue.model.Token;
import com.robin.veriqueue.model.TokenStatus;
import com.robin.veriqueue.model.User;
import com.robin.veriqueue.repository.TokenRepository;
import com.robin.veriqueue.repository.UserRepository;
import com.robin.veriqueue.service.OTPService;
import com.robin.veriqueue.service.TokenService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserWebController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TokenRepository tokenRepository;
	
	@Autowired
	private OTPService otpService;
	
	@Autowired
	private TokenService tokenService;
	
	@GetMapping("/login")
	public String home() {
		return "user-login"; 
	}
	
	@PostMapping("/send-otp")
	public String handleLogin(@RequestParam String email,@RequestParam String contact,@RequestParam String name,Model model,HttpSession session) {
		Optional<User> existingUser= userRepository.findByEmail(email);
		
		if(existingUser.isEmpty()) {
			User user=new User();
			user.setEmail(email);
			user.setContact(contact);
			user.setName(name);
			userRepository.save(user);
		}
		try {
		otpService.generateOtp(email);
		session.setAttribute("otp_requested", email);
		}
		catch(Exception e) {
			 model.addAttribute("error", "Failed to send OTP. Try again.");
			 return "user-login";
		}
		
		System.out.println("OTP Sent successfully to email");
		
		 return "redirect:/user/verify-otp?email=" + email;
	}
	
	@GetMapping("/verify-otp")
	public String otpPage(@RequestParam String email,Model model,HttpSession session) {
		model.addAttribute("email",email);
		 String otpRequestedEmail = (String) session.getAttribute("otp_requested");
		    if (otpRequestedEmail == null || !otpRequestedEmail.equals(email)) {
		        return "redirect:/user/login";
		    }
		return "verify-otp";
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam String email,@RequestParam String otp, Model model,HttpSession session) {
		
		if(!otpService.verifyOtp(email, otp))
		{
			model.addAttribute("email",email);
			model.addAttribute("error", "Invalid or expired OTP. Try again.");
	        return "verify-otp";
		}
		session.setAttribute("verifiedEmail", email);
		
		return "redirect:/user?email=" + email;
	}
	
	@GetMapping
	public String userDashboard(Model model,HttpSession session) {
		
		String verifiedemail = (String) session.getAttribute("verifiedEmail");

	    if (verifiedemail == null) {
	        return "redirect:/user/login";
	    }
	    
		Optional<User> existingUser=userRepository.findByEmail(verifiedemail);
		
		if(existingUser.isPresent()) {
			User user=existingUser.get();
			model.addAttribute("user",user);
			return "user-dashboard";
		}else {
	        model.addAttribute("error", "User not found.");
	        return "user-login";
		}
	}
	
	@PostMapping("/generate-token")
	public String generateToken(HttpSession session, Model model) throws Exception{
		String email = (String) session.getAttribute("verifiedEmail");

	    if (email == null) {
	        return "redirect:/user/login";
	    }
		Optional<User> existingUser=userRepository.findByEmail(email); 
		User user=existingUser.get();
		
		 List<Token> todaysTokens = tokenRepository.findAllByUserAndCreatedAtBetween(
		        user,
		        LocalDate.now().atStartOfDay(),
		        LocalDate.now().atTime(LocalTime.MAX)
		    );

		    for (Token t : todaysTokens) {
		        if (t.getStatus() == TokenStatus.ACTIVE || t.getStatus() == TokenStatus.CALLED) {
		            model.addAttribute("email", user.getEmail());
		            model.addAttribute("message", "Hey " + user.getName() + ", You already have a token: " + t.getTokenNumber());
		            return "token-exists";
		        }
		    }
		Token token=tokenService.generateToken(email);
		model.addAttribute("user",user);
		model.addAttribute("tokenNumber",token.getTokenNumber());
		Optional<Token> currentToken=tokenService.getCurrentToken();
		int position=tokenService.getUserPosition(token);
		if(currentToken.isPresent()) {
			model.addAttribute("currentServing",currentToken.get().getTokenNumber());
		}
		model.addAttribute("queuePosition",position);
		model.addAttribute("estimatedWait",position*3);
		
		//send details over mail
		tokenService.sendTokenDetails(user,token,currentToken.get(),position,position*3);
		return "user-dashboard";
	}
	
}
