package com.blog_application.service;

import com.blog_application.dto.comment.CommentDto;

public interface CommentService {

    void deleteComment(Long commentId);
    CommentDto getCommentById(Long commentId);
    CommentDto updateComment(CommentDto commentDto,Long commentId);
    CommentDto createComment(CommentDto commentDto,Long postId,Long userId);

}
