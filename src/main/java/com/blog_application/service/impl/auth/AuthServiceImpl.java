package com.blog_application.service.impl.auth;

import com.blog_application.dto.auth.LoginRequestDTO;
import com.blog_application.model.user.User;
import com.blog_application.service.auth.AuthService;
import com.blog_application.service.user.UserService;
import com.blog_application.util.constants.ApplicationConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final Environment env;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthServiceImpl(Environment env, UserService userService, AuthenticationManager authenticationManager){
        this.env = env;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }
    @Override
    public String generateToken(LoginRequestDTO loginRequestDTO) {
        String jwt;
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(loginRequestDTO.username(),
                loginRequestDTO.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);
        if(authenticationResponse != null && authenticationResponse.getAuthorities() != null && authenticationResponse.isAuthenticated()) {
            if (null != env) {

                User user = userService.fetchUserByEmail(authenticationResponse.getName());

                String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
                        ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                jwt = Jwts.builder().issuer("Blog").subject("JWT Token")
                        .claim("id", user.getId())
                        .claim("name", user.getName())
                        .claim("about", user.getAbout())
                        .claim("gender", user.getGender())
                        .claim("phoneNumber", user.getPhoneNumber())
                        .claim("createdAt", user.getCreatedAt().toString())
                        .claim("updatedAt", user.getUpdatedAt().toString())
                        .claim("username", authenticationResponse.getName())
                        .claim("authorities", authenticationResponse.getAuthorities().stream().map(
                                GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                        .issuedAt(new java.util.Date())
                        .expiration(new java.util.Date((new java.util.Date()).getTime() + 86400000))
                        .signWith(secretKey).compact();
                        return jwt;
            }
        }
        return null;
    }

}
