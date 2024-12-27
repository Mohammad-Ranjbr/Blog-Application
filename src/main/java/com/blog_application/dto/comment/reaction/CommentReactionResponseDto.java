package com.blog_application.dto.comment.reaction;

public record CommentReactionResponseDto(boolean success, int likes, int dislikes, String message) {
}
