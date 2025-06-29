package com.robin.veriqueue.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.robin.veriqueue.model.OTPVerification;
//import com.robin.veriqueue.model.User;
import com.robin.veriqueue.repository.OTPRepository;
//import com.robin.veriqueue.repository.UserRepository;

@Service
public class OTPService {

	@Autowired
	private OTPRepository otpRepository;
	
	@Autowired
	private JavaMailSender mailSender;
	
	/*
	@Autowired
	private UserRepository userRepository; */
	
	private static final int EXPIRY_MINUTES = 5;
	
	public void generateOtp(String email) {
		// TODO Auto-generated method stub
		String otp=String.format("%06d", new Random().nextInt(999999));
		OTPVerification otpVerification = new OTPVerification(email,otp,LocalDateTime.now().plusMinutes(EXPIRY_MINUTES));
		otpRepository.save(otpVerification);
		sendMail(email,otp);
	}
	
	private void sendMail(String toEmail, String otp) {
		SimpleMailMessage mail=new SimpleMailMessage();
		mail.setTo(toEmail);
		mail.setSubject("OTP For veriQueue token");
		mail.setText("Use OTP : " + otp + "."+" It will expire in 5 minutes.");
		mailSender.send(mail);
	}

	public boolean verifyOtp(String email, String otp) {
		// TODO Auto-generated method stub
		Optional<OTPVerification> optionalOtp=otpRepository.findByEmailAndOtp(email, otp);
		
		if(optionalOtp.isPresent()) {
		OTPVerification entry=optionalOtp.get();
		if(entry.getExpiresAt().isAfter(LocalDateTime.now())) {
		entry.setVerified(true);
		
		otpRepository.save(entry);
		
	/*	//user addition in DB
		
		Optional<User> optionalUser=userRepository.findByEmail(email);
		if(optionalUser.isEmpty()) {
			User user =new User();
			user.setEmail(email);
			userRepository.save(user);
		}*/
		return true;
		}
		}
		return false;
		
	}

}
