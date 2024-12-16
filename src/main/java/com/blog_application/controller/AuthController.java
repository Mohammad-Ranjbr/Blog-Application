package com.blog_application.controller;

import com.blog_application.dto.auth.LoginRequestDTO;
import com.blog_application.dto.auth.LoginResponseDTO;
import com.blog_application.service.auth.AuthService;
import com.blog_application.util.constants.ApplicationConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> apiLogin (@RequestBody LoginRequestDTO loginRequest) {
        String jwt = authService.generateToken(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).header(ApplicationConstants.JWT_HEADER,jwt)
                .body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt));
    }

    @GetMapping("/basic-authentication")
    public String vid(){
        return "Token Successfully Generated (See Response Headers)";
    }

}
