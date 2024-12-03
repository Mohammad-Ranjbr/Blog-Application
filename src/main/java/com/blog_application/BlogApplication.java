package com.blog_application;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableWebSecurity(debug = true)
public class BlogApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	@Override
	public void run(String... args) {
		LocalDateTime now = LocalDateTime.now();
		System.out.println("Current DateTIme : " + now);
		System.out.println("Password : " + new BCryptPasswordEncoder().encode("654321"));
	}
}
