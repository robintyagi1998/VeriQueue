package com.robin.veriqueue.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="tokens")
public class Token {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long Id;
	
	private Integer tokenNumber;
	
	@Enumerated(EnumType.STRING)
	private TokenStatus status;
	
	private boolean notified=false;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime calledAt;
	
	@ManyToOne
	@JoinColumn(name="user_id",nullable=false)
	private User user;

	public Token() {}
	
	public Token(Long id, Integer tokenNumber, TokenStatus status, boolean notified, LocalDateTime createdAt,
			LocalDateTime calledAt, User user) {
		super();
		Id = id;
		this.tokenNumber = tokenNumber;
		this.status = status;
		this.notified = notified;
		this.createdAt = createdAt;
		this.calledAt = calledAt;
		this.user = user;
	}



	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Integer getTokenNumber() {
		return tokenNumber;
	}

	public void setTokenNumber(Integer tokenNumber) {
		this.tokenNumber = tokenNumber;
	}

	public TokenStatus getStatus() {
		return status;
	}

	public void setStatus(TokenStatus status) {
		this.status = status;
	}

	public boolean isNotified() {
		return notified;
	}

	public void setNotified(boolean notified) {
		this.notified = notified;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getCalledAt() {
		return calledAt;
	}

	public void setCalledAt(LocalDateTime calledAt) {
		this.calledAt = calledAt;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
}
