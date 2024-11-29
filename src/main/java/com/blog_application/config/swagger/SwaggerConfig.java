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
                description = "This API allows users to manage blogs, comments, and categories. It also supports authentication and authorization using JWT.",
                version = "v1.0",
                contact = @Contact(name = "Mohammad Ranjbar", email = "mohammadranjbar.mmr81@gmail.com"),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        externalDocs = @ExternalDocumentation(description = "For more details, visit the GitHub repository",
                url = "https://github.com/Mohammad-Ranjbr/Blog-Application")
)
public class SwaggerConfig {
}
