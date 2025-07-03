package com.robin.veriqueue.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.robin.veriqueue.model.Admin;
import com.robin.veriqueue.model.Token;
import com.robin.veriqueue.model.TokenStatus;
import com.robin.veriqueue.model.User;
import com.robin.veriqueue.repository.AdminRepository;
import com.robin.veriqueue.repository.TokenRepository;
import com.robin.veriqueue.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminRepository adminRepository;
	
	@Autowired
	private TokenRepository tokenRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/login")
	public String loginPage() {
		return "admin-login";
	}
	
	@GetMapping("/dashboard")
	public String adminDashboard(HttpSession session,Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String username = auth.getName(); 
	    Admin admin = adminRepository.findByUsername(username);
		
		if(admin==null)
			return "redirect:/admin/dashboard";
		
		long totalUsers=userRepository.count();
		long totalTokens=tokenRepository.count();
		long activeTokens=tokenRepository.countByStatus(TokenStatus.ACTIVE);
		long expiredTokens=tokenRepository.countByStatus(TokenStatus.EXPIRED);
		long calledTokens = tokenRepository.countByStatus(TokenStatus.CALLED);
		
		model.addAttribute("adminUsername", admin.getUsername());
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("totalTokens", totalTokens);
		model.addAttribute("activeTokens", activeTokens);
		model.addAttribute("expiredTokens", expiredTokens);
		model.addAttribute("calledTokens", calledTokens);
		
		return "admin-dashboard";
	}
	
	@GetMapping("/users")
	public String viewAllUsers(HttpSession session, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String username = auth.getName(); 
	    Admin admin = adminRepository.findByUsername(username);
		
		if(admin==null)
			return "redirect:/admin/dashboard";
		
		List<User> users=userRepository.findAll();
		Map<Long,Token> usersActiveTokens=new HashMap<>();
		
		for(User user: users) {
			//Token token=tokenRepository.findByUserAndStatus(user,TokenStatus.ACTIVE);
			Token token = tokenRepository.findTopByUserOrderByCreatedAtDesc(user);
			if(token !=null) {
				usersActiveTokens.put(user.getId(), token);
			}
		}
		model.addAttribute("users",users);
		model.addAttribute("activeTokens",usersActiveTokens);
		
		return "view-users";
		
	}
	
	@GetMapping("/tokens")
	public String viewTokens(HttpSession session,Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String username = auth.getName(); 
	    Admin admin = adminRepository.findByUsername(username);
		
		if(admin==null)
			return "redirect:/admin/dashboard";
		List<Token> tokens=tokenRepository.findAllByOrderByCreatedAtDesc();
		model.addAttribute("tokens",tokens);
		return "view-tokens";
	}
	
	
	@PostMapping("/token/expire")
	public String expireToken(@RequestParam Long tokenId) {
	    Token token = tokenRepository.findById(tokenId).orElse(null);
	    if (token != null) {
	        token.setStatus(TokenStatus.EXPIRED);
	        tokenRepository.save(token);
	    }
	    return "redirect:/admin/users";
	}

	@PostMapping("/token/call")
	public String callToken(@RequestParam Long tokenId) {
	    
	    Token token = tokenRepository.findById(tokenId).orElse(null);
	    if (token != null && token.getStatus()==TokenStatus.ACTIVE) {
	    	token.setCalledAt(LocalDateTime.now());
	    	token.setStatus(TokenStatus.CALLED);
	    	tokenRepository.save(token);
	    }
	    return "redirect:/admin/users";
	}
	
	@GetMapping("/tokens/filter")
	public String viewFilteredTokens(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,Model model) {
		
		LocalDateTime startOfDay=date.atStartOfDay();
		LocalDateTime endOfDay= date.atTime(LocalTime.MAX);
		
		List<Token> tokens = tokenRepository.findByCalledAtBetween(startOfDay, endOfDay);
		
		List<String> userEmails=new ArrayList<>();
		
		for(Token token: tokens) {
			if(!userEmails.contains(token.getUser().getEmail()))
				userEmails.add(token.getUser().getEmail());
		}
		   int distinctUserCount = userEmails.size();

		    model.addAttribute("tokens", tokens);
		    model.addAttribute("filterDate", date);
		    model.addAttribute("filteredUserCount", distinctUserCount);
		
		return "view-tokens";
	}
	
	
}
