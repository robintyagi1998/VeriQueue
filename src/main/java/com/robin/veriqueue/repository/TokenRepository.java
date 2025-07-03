package com.robin.veriqueue.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.robin.veriqueue.model.Token;
import com.robin.veriqueue.model.TokenStatus;
import com.robin.veriqueue.model.User;

public interface TokenRepository extends JpaRepository<Token,Long>{
	List<Token> findByStatusOrderByCreatedAtAsc(TokenStatus active);

	Optional<Token> findFirstByStatusOrderByCreatedAtAsc(TokenStatus active);

	int countByCreatedAtBeforeAndStatus(LocalDateTime createdAt, TokenStatus active);

	long countByStatus(TokenStatus status);

	Token findByUserAndStatus(User user, TokenStatus active);

	User findByUser(Optional<User> optionalUser);

	Token findByUserAndStatusIn(User user, List<TokenStatus> asList);

	List<Token> findAllByOrderByCreatedAtDesc();

	List<Token> findByCalledAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

	Token findTopByUserOrderByCreatedAtDesc(User user);
}
