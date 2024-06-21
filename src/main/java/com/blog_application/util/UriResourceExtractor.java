package com.blog_application.util;

import org.springframework.stereotype.Component;

@Component
public class UriResourceExtractor {

    public String extractResourceFromUri(String uri) {
        // Example logic to extract resource name from URI
        // Assuming the resource is the first path segment after the base path
        String[] segments = uri.split("/");
        if (segments.length > 3) {
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
