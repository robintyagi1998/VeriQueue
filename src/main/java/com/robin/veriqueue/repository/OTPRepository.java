package com.robin.veriqueue.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.robin.veriqueue.model.OTPVerification;

public interface OTPRepository extends JpaRepository<OTPVerification,Long>{

	void deleteByExpiresAtBefore(LocalDateTime now);

	Optional<OTPVerification> findByEmailAndOtp(String email, String otp);

}
