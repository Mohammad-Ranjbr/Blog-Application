package com.blog_application.config.swagger;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Blog Application REST APIs",
                description = "Spring Boot Blog Application REST APIs Documentation",
                version = "v1.0",
                contact = @Contact(name = "Mohammad Ranjbar", email = "mohammadranjbar.mmr81@gmail.com")
        ),
        externalDocs = @ExternalDocumentation(description = "Blog Application Github",
                url = "https://github.com/Mohammad-Ranjbr/Blog-Application")
)
public class SwaggerConfig {
}
