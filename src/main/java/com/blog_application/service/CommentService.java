package com.blog_application.service;

import com.blog_application.dto.comment.CommentCreateDto;
import com.blog_application.dto.comment.CommentDto;
import com.blog_application.dto.comment.CommentGetDto;

public interface CommentService {

    void deleteComment(Long commentId);
    CommentDto getCommentById(Long commentId);
    CommentDto updateComment(CommentDto commentDto,Long commentId);
    CommentGetDto createComment(CommentCreateDto commentCreateDto, Long postId, Long userId);

}
