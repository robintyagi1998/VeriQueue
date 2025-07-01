package com.robin.veriqueue.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.robin.veriqueue.model.Admin;

public interface AdminRepository extends JpaRepository<Admin,Long>{

	Admin findByUsernameAndPassword(String username, String password);

}
