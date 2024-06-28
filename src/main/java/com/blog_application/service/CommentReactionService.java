package com.blog_application.service;

import java.util.UUID;

public interface CommentReactionService {

    void likeComment(UUID userId, Long commentId);
    void dislikeComment(UUID userId, Long commentId);

}
