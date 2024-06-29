package com.blog_application.service.impl;

import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.reaction.PostReactionRequestDto;
import com.blog_application.service.PostReactionService;
import org.springframework.stereotype.Service;

@Service
public class PostReactionServiceImpl implements PostReactionService {

    @Override
    public PostGetDto likePost(PostReactionRequestDto postReactionRequestDto) {
        return null;
    }
    
}
