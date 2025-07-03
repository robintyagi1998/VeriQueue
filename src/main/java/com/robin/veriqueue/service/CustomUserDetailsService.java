package com.robin.veriqueue.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.robin.veriqueue.model.Admin;
import com.robin.veriqueue.repository.AdminRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private AdminRepository adminRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		System.out.println("Authenticationg ....."+username);
		
		Admin admin=adminRepository.findByUsername(username);
		if(admin==null)
			throw new UsernameNotFoundException("User not found with email: " + username);
		else
		return new org.springframework.security.core.userdetails.User(admin.getUsername(),admin.getPassword(),
				Collections.singleton(() -> "ROLE_ADMIN"));
	}

}
