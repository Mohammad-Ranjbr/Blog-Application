package com.blog_application.service.comment;

import com.blog_application.dto.comment.CommentCreateDto;
import com.blog_application.dto.comment.CommentGetDto;
import com.blog_application.dto.comment.CommentUpdateDto;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    void deleteComment(Long commentId);
    CommentGetDto getCommentById(Long commentId);
    List<CommentGetDto> getCommentsByPostId(Long postId);
    List<CommentGetDto> getCommentsByParentId(Long parentId);
    CommentGetDto updateComment(CommentUpdateDto commentUpdateDto, Long commentId);
    CommentGetDto createComment(CommentCreateDto commentCreateDto, Long postId, UUID userId);

}
