package com.blog_application.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class TimeUtils {

    public String getCurrentTimeAsString(String format){
        //Calculates the current system time in milliseconds since January 1, 1970
        long currentTimeMillis = System.currentTimeMillis();

        // Convert to LocalDateTime
        //LocalDateTime not save information about zone
        LocalDateTime localDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(currentTimeMillis),
                ZoneId.systemDefault()
        );

        // Format LocalDateTime as String
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }

}
