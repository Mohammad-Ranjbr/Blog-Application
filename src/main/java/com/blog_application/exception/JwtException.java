package com.blog_application.exception;

import com.blog_application.util.constants.ApplicationConstants;
import com.blog_application.util.utils.TimeUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class JwtException {

    public static void handle(Exception exception, HttpServletRequest request, HttpServletResponse response) throws IOException {
        TimeUtils timeUtils = new TimeUtils();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String jsonResponse = String.format(
                "{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"path\": \"%s\"}",
                timeUtils.getCurrentTimeAsString(ApplicationConstants.DATE_TIME_FORMAT),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );
        response.getWriter().write(jsonResponse);
    }

}
