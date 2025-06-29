package com.robin.veriqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VeriqueueApplication {

	public static void main(String[] args) {
		SpringApplication.run(VeriqueueApplication.class, args);
	}

}
