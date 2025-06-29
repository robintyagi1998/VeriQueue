package com.robin.veriqueue.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.robin.veriqueue.repository.OTPRepository;

import jakarta.transaction.Transactional;

@Service
public class OPTCleanupService {

	@Autowired
	private OTPRepository otpRepository;
	
	@Transactional
	@Scheduled(fixedRate=600000)
	public void cleanExpiredOtps(){
		otpRepository.deleteByExpiresAtBefore(LocalDateTime.now());
		System.out.println("Expired OTPs cleaned at : "+ LocalDateTime.now());
	}
	
}
