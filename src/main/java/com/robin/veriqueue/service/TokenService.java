package com.robin.veriqueue.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.robin.veriqueue.model.Token;
import com.robin.veriqueue.model.TokenStatus;
import com.robin.veriqueue.model.User;
import com.robin.veriqueue.repository.TokenRepository;
import com.robin.veriqueue.repository.UserRepository;

@Service
public class TokenService {

	@Autowired
	private TokenRepository tokenRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JavaMailSender mailSender;
	
	public Token generateToken(String email) {
		// TODO Auto-generated method stub
		Optional<User> optionalUser=userRepository.findByEmail(email);
		
		if(optionalUser.isEmpty())
			throw new RuntimeException("User not found, please check email !!");
				
		int maxToken = 100;
		
		List<Token> tokens = tokenRepository.findAll();
		for(Token t:tokens)
		{
			if(t.getTokenNumber()>maxToken)
				maxToken=t.getTokenNumber();
		}
		int tokenNumber=maxToken+1;
		
		Token token=new Token();
				token.setUser(optionalUser.get());
				token.setCreatedAt(LocalDateTime.now());
				token.setStatus(TokenStatus.ACTIVE);
				token.setTokenNumber(tokenNumber);
			return tokenRepository.save(token);
	}


	public List<Token> getActiveTokens() {
		// TODO Auto-generated method stub
		return tokenRepository.findByStatusOrderByCreatedAtAsc(TokenStatus.ACTIVE);
	}


	public Token callNextToken() {
		// TODO Auto-generated method stub
		List<Token> activeTokens=tokenRepository.findByStatusOrderByCreatedAtAsc(TokenStatus.ACTIVE);
		if(activeTokens.isEmpty())
			throw new RuntimeException("No token found");
		Token nextToken=activeTokens.get(0);
		nextToken.setStatus(TokenStatus.CALLED);
		nextToken.setCalledAt(LocalDateTime.now());
		return tokenRepository.save(nextToken);
	}
	
	public Optional<Token> getCurrentToken() {
		return tokenRepository.findFirstByStatusOrderByCreatedAtAsc(TokenStatus.ACTIVE);
	}
	
	public int getUserPosition(Token userToken) {
		return tokenRepository.countByCreatedAtBeforeAndStatus(userToken.getCreatedAt(),TokenStatus.ACTIVE);
	}
	
	public void sendTokenDetails(User user, Token token, Token currentToken, int position, int i) {
		// TODO Auto-generated method stub
		SimpleMailMessage mail= new SimpleMailMessage();
		String to=user.getEmail();
		String subject="VeriQueue token Details";
		String body="Dear User,\n\n"
				+ "Your token has been generated successfully !!\n"
				+"Token Number : " + token.getTokenNumber()+" \n"
				+"Queue Position : "+ position +" \n"
				+"Current Token Being Served : "+ currentToken.getTokenNumber() + " \n"
				+"Estimated Waiting Time : "+ i +" minutes." +" \n\n\n"
				+"Thank you for using VeriQueue"+"\n"
				;
		mail.setTo(to);
		mail.setSubject(subject);
		mail.setText(body);
		mailSender.send(mail);
	}
	
	
}
