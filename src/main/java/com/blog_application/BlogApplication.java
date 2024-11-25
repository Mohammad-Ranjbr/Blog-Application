package com.blog_application;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Spring Boot Blog Application REST APIs",
				description = "Spring Boot Blog Application REST APIs Documentation",
				version = "v1.0",
				contact = @Contact(name = "Mohammad Ranjbar", email = "mohammadranjbar.mmr81@gmail.com"),
				license = @License(name = "Apache 2.0")
		),
		externalDocs = @ExternalDocumentation(description = "Spring Boot Blog Application Documentation",
		url = "https://github.com/Mohammad-Ranjbr/Blog-Application")
)
public class BlogApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	@Override
	public void run(String... args) {
		LocalDateTime now = LocalDateTime.now();
		System.out.println("Current DateTIme : " + now);
		System.out.println("Password : " + new BCryptPasswordEncoder().encode("123456Qq!"));
	}
}
