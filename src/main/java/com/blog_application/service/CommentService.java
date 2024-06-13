package com.blog_application.service;

import com.blog_application.dto.CommentDto;

public interface CommentService {

    CommentDto createComment(CommentDto commentDto,Long post_id);
    void deletePost(Long comment_id);

}
