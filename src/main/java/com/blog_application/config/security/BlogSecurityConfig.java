package com.blog_application.config.security;

import com.blog_application.exception.BlogAccessDeniedHandler;
import com.blog_application.exception.BlogAuthenticationEntryPoint;
import com.blog_application.filter.JwtTokenGeneratorFilter;
import com.blog_application.filter.JwtTokenValidatorFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@SecurityScheme(name = "Jwt Token Authentication", type = SecuritySchemeType.APIKEY, paramName = "Authorization", in = SecuritySchemeIn.HEADER)
public class BlogSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
        http
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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
                .requiresChannel(crm -> crm.anyRequest().requiresInsecure()) // Http Only
                .csrf(AbstractHttpConfigurer::disable)
//                .csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
//                .ignoringRequestMatchers("api/v1/contacts/", "/api/v1/users/register")
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
//                .addFilterAfter(new CsrfTokenFilter(), BasicAuthenticationFilter.class)
               .addFilterAfter(new JwtTokenGeneratorFilter(), BasicAuthenticationFilter.class)
               .addFilterBefore(new JwtTokenValidatorFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests((requests) -> requests.
                requestMatchers(HttpMethod.GET, "api/v1/categories/**").permitAll()
                .requestMatchers(HttpMethod.POST, "api/v1/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "api/v1/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "api/v1/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "api/v1/comments/**").permitAll()
                .requestMatchers(HttpMethod.POST,"api/v1/comments/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/v1/comments/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/v1/comments/**").authenticated()
                .requestMatchers(HttpMethod.GET, "api/v1/posts/user/{userId}").authenticated()
                .requestMatchers(HttpMethod.POST,"api/v1/posts/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/v1/posts/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/v1/posts/**").authenticated()
                .requestMatchers(HttpMethod.GET, "api/v1/tags/**").permitAll()
                .requestMatchers(HttpMethod.POST,"api/v1/tags/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/v1/tags/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/v1/tags/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/users/{user_id}/save_post/{post_id}").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/users/{user_id}/follow/{follow_user_id}").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/v1/users/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/v1/users/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "api/v1/users/{id}/status").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "api/v1/users/{user_id}/followers", "api/v1/users/{user_id}/following", "api/v1/users/saved/{user_id}").authenticated()
                .requestMatchers(HttpMethod.GET, "api/v1/users/", "api/v1/users/basic-info/").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "api/v1/users/basic-info/{id}").permitAll()
                .requestMatchers("/api/v1/auth/basic-authentication").authenticated()
                .requestMatchers("api/v1/notices/","api/v1/contacts/","/error", "/swagger-ui/**", "/v3/api-docs/**","/api/v1/auth/login"
                        , "/api/v1/users/register", "api/v1/users/{id}").permitAll()
                .anyRequest().authenticated());
        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        http.exceptionHandling(ehc -> {
            ehc.accessDeniedHandler(new BlogAccessDeniedHandler());
            ehc.authenticationEntryPoint(new BlogAuthenticationEntryPoint());
        });
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        BlogUsernamePasswordAuthenticationProvider authenticationProvider = new BlogUsernamePasswordAuthenticationProvider(passwordEncoder, userDetailsService);
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

}
