package com.blog_application.service.impl;

import com.blog_application.config.mapper.PostMapper;
import com.blog_application.config.mapper.UserMapper;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.reaction.PostReactionRequestDto;
import com.blog_application.model.Post;
import com.blog_application.model.PostReaction;
import com.blog_application.model.User;
import com.blog_application.repository.PostReactionRepository;
import com.blog_application.repository.PostRepository;
import com.blog_application.service.PostReactionService;
import com.blog_application.service.PostService;
import com.blog_application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostReactionServiceImpl implements PostReactionService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final PostService postService;
    private final PostRepository postRepository;
    private final PostReactionRepository postReactionRepository;

    @Autowired
    public PostReactionServiceImpl(PostMapper postMapper, UserMapper userMapper, UserService userService, PostService postService,
                                   PostRepository postRepository , PostReactionRepository postReactionRepository){
        this.postMapper = postMapper;
        this.userMapper = userMapper;
        this.userService = userService;
        this.postService = postService;
        this.postRepository = postRepository;
        this.postReactionRepository = postReactionRepository;
    }
    @Override
    public PostGetDto likePost(PostReactionRequestDto postReactionRequestDto) {
        User user = userMapper.toEntity(userService.getUserById(postReactionRequestDto.getUserId()));
        Post post = postMapper.toEntity(postService.getPostById(postReactionRequestDto.getPostId()));
        Optional<PostReaction> existing = postReactionRepository.findByUserAndPost(user,post);

        PostReaction postReaction;
        if(existing.isPresent()){
            postReaction = existing.get();
            if(postReaction.isLike() == postReactionRequestDto.isLike()){
                postReactionRepository.delete(postReaction);
            }
        } else {
            if (postReactionRequestDto.isLike()){
                postReaction = new PostReaction();
                postReaction.setUser(user);
                postReaction.setPost(post);
                postReaction.setLike(postReaction.isLike());
                postReactionRepository.save(postReaction);
            }
        }

        int likeCount = postReactionRepository.countLikByPost(post);
        post.setLikes(likeCount);
        postRepository.save(post);

        return postMapper.toPostGetDto(post);
    }
    
}
