package com.blog_application.service;

import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.reaction.PostReactionRequestDto;

public interface PostReactionService {

    PostGetDto likePost(PostReactionRequestDto postReactionRequestDto);

}
