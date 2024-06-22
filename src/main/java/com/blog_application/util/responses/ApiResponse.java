package com.blog_application.util.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private String message;
    private boolean success;

}
