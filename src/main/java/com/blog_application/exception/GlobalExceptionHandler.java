package com.blog_application.exception;

import com.blog_application.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resourceNotfoundExceptionHandler(ResourceNotFoundException resourceNotFoundException){
        return new ResponseEntity<>(new ApiResponse(resourceNotFoundException.getMessage(),false), HttpStatus.NOT_FOUND);
    }

}
