package com.blog_application.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class BlogSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests.
                requestMatchers(HttpMethod.GET, "api/v1/categories/**").permitAll()
                .requestMatchers(HttpMethod.POST, "api/v1/categories/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/v1/categories/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/v1/categories/**").authenticated()
                .requestMatchers(HttpMethod.OPTIONS, "api/v1/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/comments/**").permitAll()
                .requestMatchers(HttpMethod.POST,"api/v1/comments/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/v1/comments/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/v1/comments/**").authenticated()
                .requestMatchers(HttpMethod.OPTIONS, "api/v1/comments/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/posts/**").permitAll()
                .requestMatchers(HttpMethod.POST,"api/v1/posts/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/v1/posts/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/v1/posts/**").authenticated()
                .requestMatchers(HttpMethod.OPTIONS, "api/v1/posts/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/tags/**").permitAll()
                .requestMatchers(HttpMethod.POST,"api/v1/tags/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/v1/tags/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/v1/tags/**").authenticated()
                .requestMatchers(HttpMethod.OPTIONS, "api/v1/tags/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/users/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/users/{user_id}/save_post/{post_id}").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/users/{user_id}/follow/{follow_user_id}").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/v1/users/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/v1/users/**").authenticated()
                .requestMatchers(HttpMethod.OPTIONS, "api/v1/users/**").permitAll()
                .requestMatchers("api/v1/notices/","api/v1/contacts/","/error", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated());
        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
