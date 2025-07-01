package com.robin.veriqueue.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@PostMapping("/login")
	public String processLogin(@RequestParam String username,@RequestParam String password, HttpSession session) {
		Admin admin= adminRepository.findByUsernameAndPassword(username,password);
		if(admin != null) {
			session.setAttribute("admin", admin);
			return "redirect:/admin/dashboard";
		}
		else
		return "redirect:/admin/login?error=true";
	}
	
	@GetMapping("/dashboard")
	public String adminDashboard(HttpSession session,Model model) {
		Admin admin=(Admin) session.getAttribute("admin");
		if(admin==null)
			return "redirect:/admin/dashboard";
		
		long totalUsers=userRepository.count();
		long totalTokens=tokenRepository.count();
		long activeTokens=tokenRepository.countByStatus(TokenStatus.ACTIVE);
		long expiredTokens=tokenRepository.countByStatus(TokenStatus.EXPIRED);
		
		model.addAttribute("adminUsername", admin.getUsername());
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("totalTokens", totalTokens);
		model.addAttribute("activeTokens", activeTokens);
		model.addAttribute("expiredTokens", expiredTokens);
		
		return "admin-dashboard";
	}
	
	@GetMapping("/logout")
	public String adminLogout(HttpSession session) {
		session.invalidate();
		return "redirect:/admin/login";
	}
	
	@GetMapping("/users")
	public String viewAllUsers(HttpSession session, Model model) {
		Admin admin=(Admin) session.getAttribute("admin");
		if(admin==null) {
			session.invalidate();
			return "redirect:/admin/login";
		}
		
		List<User> users=userRepository.findAll();
		Map<Long,Token> usersActiveTokens=new HashMap<>();
		
		for(User user: users) {
			Token token=tokenRepository.findByUserAndStatus(user,TokenStatus.ACTIVE);
			if(token !=null) {
				usersActiveTokens.put(user.getId(), token);
			}
		}
		model.addAttribute("users",users);
		model.addAttribute("activeTokens",usersActiveTokens);
		
		return "view-users";
		
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
	    // For now, just a placeholder — you can log it or flag it
	    Token token = tokenRepository.findById(tokenId).orElse(null);
	    if (token != null && token.getStatus()==TokenStatus.ACTIVE) {
	    	token.setCalledAt(LocalDateTime.now());
	    	token.setStatus(TokenStatus.CALLED);
	    	tokenRepository.save(token);
	    }
	    return "redirect:/admin/users";
	}
	
}
