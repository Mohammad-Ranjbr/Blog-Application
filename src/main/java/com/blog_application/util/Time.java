package com.blog_application.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class Time {

    public String getCurrentTimeAsString(String format){
        long currentTimeMillis = System.currentTimeMillis();

        // Convert to LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(currentTimeMillis),
                ZoneId.systemDefault()
        );

        // Format LocalDateTime as String
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }

}
