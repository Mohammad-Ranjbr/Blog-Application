package com.blog_application.exception;

import com.blog_application.util.responses.ApiResponse;
import com.blog_application.util.constants.ApplicationConstants;
import com.blog_application.util.utils.TimeUtils;
import com.blog_application.util.extractors.UriResourceExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final TimeUtils timeUtils;
    private final UriResourceExtractor uriResourceExtractor;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotfoundExceptionHandler(ResourceNotFoundException resourceNotFoundException){
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                resourceNotFoundException.getMessage(),
                resourceNotFoundException.getAction(),
                false,
                timeUtils.getCurrentTimeAsString(ApplicationConstants.DATE_TIME_FORMAT)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> resourceNotfoundExceptionHandler(ResourceAlreadyExistsException resourceAlreadyExistsException){
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                resourceAlreadyExistsException.getMessage(),
                resourceAlreadyExistsException.getAction(),
                false,
                timeUtils.getCurrentTimeAsString(ApplicationConstants.DATE_TIME_FORMAT)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException methodArgumentNotValidException){
        Map<String,String> errors = new HashMap<>();
        methodArgumentNotValidException.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName,message);
        });
        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }

    //This error is due to the fact that the value you put in the fields exceeds the length specified in the database.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> dataIntegrityViolationExceptionHandler(DataIntegrityViolationException dataIntegrityViolationException){
        System.out.println("DataIntegrityViolationException message : " + dataIntegrityViolationException.getMessage());
        return new ResponseEntity<>(new ApiResponse(dataIntegrityViolationException.getMessage(),false),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException methodArgumentTypeMismatchException,
                                                                                    HttpServletRequest request) {
        String httpMethod = request.getMethod();
        String requestUri = request.getRequestURI(); //Example : /api/v1/categories/string

        // Extracting resource from URI
        String resource = uriResourceExtractor.extractResourceFromUri(requestUri);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "For argument : " + methodArgumentTypeMismatchException.getName() + " - " +
                        methodArgumentTypeMismatchException.getMessage(),
                httpMethod + " , " + resource + " operation not performed",
                false,
                timeUtils.getCurrentTimeAsString(ApplicationConstants.DATE_TIME_FORMAT)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentialsExceptionHandler(BadCredentialsException badCredentialsException, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                badCredentialsException.getMessage(),
                request.getRequestURI(),
                false,
                timeUtils.getCurrentTimeAsString(ApplicationConstants.DATE_TIME_FORMAT)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDeniedExceptionHandler(AccessDeniedException accessDeniedException, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                accessDeniedException.getMessage(),
                request.getRequestURI(),
                false,
                timeUtils.getCurrentTimeAsString(ApplicationConstants.DATE_TIME_FORMAT)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

}
