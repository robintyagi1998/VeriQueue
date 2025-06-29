package com.robin.veriqueue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.robin.veriqueue.service.OTPService;

@RestController
@RequestMapping("/api/otp")
public class OTPController {

	@Autowired
	private OTPService otpService;
	
	@PostMapping("/generate")
	public String generateOtp(@RequestParam String email) {
		otpService.generateOtp(email);
		return "OTP sent successfully to : " + email;
	}
	
	@PostMapping("/verify")
	public String verifyOtp(@RequestParam String email,@RequestParam String otp ) {
		boolean isValid = otpService.verifyOtp(email,otp);
		return isValid?"verified successfully":"Incorrect OTP !";
	}
	
	
}
