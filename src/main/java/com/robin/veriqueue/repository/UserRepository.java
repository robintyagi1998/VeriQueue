package com.robin.veriqueue.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.robin.veriqueue.model.User;

public interface UserRepository extends JpaRepository<User,Long>{

	Optional<User> findByEmail(String email);

}
