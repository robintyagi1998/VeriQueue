package com.robin.veriqueue.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.robin.veriqueue.model.Token;
import com.robin.veriqueue.model.TokenStatus;

public interface TokenRepository extends JpaRepository<Token,Long>{
	List<Token> findByStatusOrderByCreatedAtAsc(TokenStatus active);

	Optional<Token> findFirstByStatusOrderByCreatedAtAsc(TokenStatus active);

	int countByCreatedAtBeforeAndStatus(LocalDateTime createdAt, TokenStatus active);
}
