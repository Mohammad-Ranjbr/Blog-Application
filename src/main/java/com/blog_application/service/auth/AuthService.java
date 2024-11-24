package com.blog_application.service.auth;

import com.blog_application.dto.login.LoginDto;

public interface AuthService {

    String login(LoginDto loginDto);

}
