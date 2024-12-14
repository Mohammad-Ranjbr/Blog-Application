package com.blog_application.service.post;

import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.reaction.PostReactionRequestDto;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface PostReactionService {

    boolean checkIfLikedByCurrentUser(Long postId, String userEmail);
    void updateLikedStatusForPosts(List<PostGetDto> postGetDtos, String userEmail);
    PostGetDto likePost(PostReactionRequestDto postReactionRequestDto) throws AccessDeniedException;

}
