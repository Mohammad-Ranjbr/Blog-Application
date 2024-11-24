package com.blog_application.security;

import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.user.User;
import com.blog_application.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUserNameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(() -> {
            logger.warn("User with Username Or Email {} not found, Get user operation not performed", usernameOrEmail);
            return new ResourceNotFoundException("User","Username Or Email",String.valueOf(usernameOrEmail),"Get User operation not performed");
        });

        Set<GrantedAuthority> authorities = user.getRoles().stream().map((authority) -> new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
    }

}
