package com.robin.veriqueue.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.robin.veriqueue.model.Token;
import com.robin.veriqueue.service.TokenService;

@RestController
@RequestMapping("/api/token")
public class TokenController {

	@Autowired
	private TokenService tokenService;
	
	@PostMapping("/generate")
	public Token generateToken(@RequestParam String email) {
		return tokenService.generateToken(email); 
	}
	
	@GetMapping("/active")
	public List<Token> getActiveTokens(){
		return tokenService.getActiveTokens();
	}
	
	 @PostMapping("/call-next")
	    public Token callNextToken() {
	        return tokenService.callNextToken();
	    }
	
}
