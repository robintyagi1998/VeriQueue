package com.robin.veriqueue.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.robin.veriqueue.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,DaoAuthenticationProvider authProvider) throws Exception{
		httpSecurity.authenticationProvider(authProvider).csrf(csrf -> csrf.disable())
					.authorizeHttpRequests(request->request.requestMatchers("/admin/**").hasRole("ADMIN")
					.anyRequest().permitAll())
					.formLogin(login -> login.loginPage("/admin/login").loginProcessingUrl("/admin/login")
								.failureUrl("/admin/login?error=true").defaultSuccessUrl("/admin/dashboard",true).permitAll())
					.logout(logout -> logout.logoutUrl("/admin/logout").permitAll());
		
		return httpSecurity.build();
	}
	
	   @SuppressWarnings("deprecation")
	   @Bean
	    public DaoAuthenticationProvider authenticationProvider() {
	        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	        authProvider.setPasswordEncoder(passwordEncoder());       // ✅ IMPORTANT
	        authProvider.setUserDetailsService(customUserDetailsService); // ✅ IMPORTANT
	        return authProvider;
	    }
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
