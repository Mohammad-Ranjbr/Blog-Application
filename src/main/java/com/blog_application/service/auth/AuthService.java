package com.blog_application.service.auth;

import com.blog_application.dto.auth.LoginRequestDTO;

public interface AuthService {

    String generateToken(LoginRequestDTO loginRequestDTO);

}
