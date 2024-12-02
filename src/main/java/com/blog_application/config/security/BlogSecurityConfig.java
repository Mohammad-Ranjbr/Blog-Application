package com.blog_application.config.security;

import com.blog_application.exception.BlogAccessDeniedHandler;
import com.blog_application.exception.BlogBasicAuthenticationEntryPoint;
import com.blog_application.filter.CsrfTokenFilter;
import com.blog_application.filter.JwtTokenGeneratorFilter;
import com.blog_application.filter.JwtTokenValidatorFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class BlogSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
        http.securityContext(contextConfig -> contextConfig.requireExplicitSave(false))
                .cors(scc -> scc.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration corsConfiguration = new CorsConfiguration();
                        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
                        corsConfiguration.setAllowCredentials(true);
                        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
                        corsConfiguration.setExposedHeaders(List.of("Authorization"));
                        corsConfiguration.setMaxAge(3600L);
                        return corsConfiguration;
                    }
                }))
               //.addFilterAfter(new JwtTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                //.addFilterBefore(new JwtTokenValidatorFilter(), BasicAuthenticationFilter.class)
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requiresChannel(crm -> crm.anyRequest().requiresInsecure()) // Http Only
                .csrf(AbstractHttpConfigurer::disable)
//                .csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
//                        .ignoringRequestMatchers("api/v1/contacts/", "/api/v1/users/register")
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
//                .addFilterAfter(new CsrfTokenFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests((requests) -> requests.
                requestMatchers(HttpMethod.GET, "api/v1/categories/**").permitAll()
                .requestMatchers(HttpMethod.POST, "api/v1/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "api/v1/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "api/v1/categories/**").hasRole("ADMIN")
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
        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new BlogBasicAuthenticationEntryPoint()));
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new BlogAccessDeniedHandler()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
