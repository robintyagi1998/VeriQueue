package com.robin.veriqueue.controller;

import java.util.Arrays;
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
	public String handleLogin(@RequestParam String email,@RequestParam String contact,@RequestParam String name,Model model) {
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
		}
		catch(Exception e) {
			 model.addAttribute("error", "Failed to send OTP. Try again.");
			 return "user-login";
		}
		
		System.out.println("OTP Sent successfully to email");
		
		 return "redirect:/user/verify-otp?email=" + email;
	}
	
	@GetMapping("/verify-otp")
	public String otpPage(@RequestParam String email,Model model) {
		model.addAttribute("email",email);
		return "verify-otp";
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam String email,@RequestParam String otp, Model model) {
		
		if(!otpService.verifyOtp(email, otp))
		{
			model.addAttribute("email",email);
			model.addAttribute("error", "Invalid or expired OTP. Try again.");
	        return "verify-otp";
		}
		
		return "redirect:/user?email=" + email;
	}
	
	@GetMapping
	public String userDashboard(@RequestParam String email, Model model) {
		
		Optional<User> existingUser=userRepository.findByEmail(email);
		
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
	public String generateToken(@RequestParam String email, Model model) throws Exception{
		Optional<User> existingUser=userRepository.findByEmail(email); 
		User user=existingUser.get();
		
		Token existingToken= tokenRepository.findByUserAndStatusIn(user,Arrays.asList(TokenStatus.ACTIVE,TokenStatus.CALLED));
		if(existingToken != null) {
			model.addAttribute("email",user.getEmail());
			model.addAttribute("message","Hey "+user.getName()+", You already have a token : "+existingToken.getTokenNumber());
			return "token-exists";
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
