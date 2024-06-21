package com.blog_application.util;

import org.springframework.stereotype.Component;

@Component
public class UriResourceExtractor {

    public String extractResourceFromUri(String uri) {
        String[] segments = uri.split("/");
        if (segments.length > 1) {
            return switch (segments[3]) {
                case "categories" -> "Category";
                case "users" -> "User";
                case "comments" -> "Comment";
                case "posts" -> "Post";
                default -> segments[3];
            };
        }
        return "Unknown";
    }

}
