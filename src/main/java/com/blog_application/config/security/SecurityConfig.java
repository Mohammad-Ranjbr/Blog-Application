package com.blog_application.config.security;

import com.blog_application.security.JwtAuthenticationEntryPoint;
import com.blog_application.security.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@SecurityScheme(name = "Bear Authentication", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter){
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers(HttpMethod.GET,"/api/**").permitAll()
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/api/v1/users/register").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(ehc -> ehc.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // By default, use PasswordEncoder and CustomUserDetailsService bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
