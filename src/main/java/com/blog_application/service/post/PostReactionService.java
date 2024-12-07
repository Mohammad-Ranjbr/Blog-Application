package com.blog_application.service.post;

import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.reaction.PostReactionRequestDto;

import java.nio.file.AccessDeniedException;

public interface PostReactionService {

    PostGetDto likePost(PostReactionRequestDto postReactionRequestDto) throws AccessDeniedException;

}
