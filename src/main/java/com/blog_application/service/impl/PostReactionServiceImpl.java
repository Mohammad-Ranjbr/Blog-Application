package com.blog_application.service.impl;

import com.blog_application.config.mapper.post.PostMapper;
import com.blog_application.config.mapper.user.UserMapper;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.reaction.PostReactionRequestDto;
import com.blog_application.model.post.Post;
import com.blog_application.model.post.PostReaction;
import com.blog_application.model.user.User;
import com.blog_application.repository.PostReactionRepository;
import com.blog_application.repository.PostRepository;
import com.blog_application.service.PostReactionService;
import com.blog_application.service.PostService;
import com.blog_application.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final static Logger logger = LoggerFactory.getLogger(PostReactionServiceImpl.class);

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
    @Transactional
    public PostGetDto likePost(PostReactionRequestDto postReactionRequestDto) {
        logger.info("Starting like post...");
        User user = userMapper.toEntity(userService.getUserById(postReactionRequestDto.getUserId()));
        Post post = postMapper.toEntity(postService.getPostById(postReactionRequestDto.getPostId()));
        Optional<PostReaction> existing = postReactionRepository.findByUserAndPost(user,post);

        PostReaction postReaction;
        if(existing.isPresent()){
            postReaction = existing.get();
            if(postReaction.isLike() == postReactionRequestDto.isLike()){
                // If the action is the same, remove the like
                postReactionRepository.delete(postReaction);
                logger.info("Like removed for user {} on post {}", user.getId(), post.getId());
            }
        } else {
            if (postReactionRequestDto.isLike()){
                postReaction = new PostReaction();
                postReaction.setUser(user);
                postReaction.setPost(post);
                postReaction.setLike(true);
                postReactionRepository.save(postReaction);
                logger.info("New Like added for user {} on post {}", user.getId(), post.getId());
            }
        }

        int likeCount = postReactionRepository.countLikByPost(post);
        post.setLikes(likeCount);
        postRepository.save(post);

        logger.info("like Post finished.");
        return postMapper.toPostGetDto(post);
    }
    
}
