package com.blog_application.service.impl.post;

import com.blog_application.config.mapper.post.PostMapper;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.reaction.PostReactionRequestDto;
import com.blog_application.model.post.Post;
import com.blog_application.model.post.PostReaction;
import com.blog_application.model.user.User;
import com.blog_application.repository.post.PostReactionRepository;
import com.blog_application.repository.post.PostRepository;
import com.blog_application.service.post.PostReactionService;
import com.blog_application.service.post.PostService;
import com.blog_application.service.user.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@Service
public class PostReactionServiceImpl implements PostReactionService {

    private final PostMapper postMapper;
    private final UserService userService;
    private final PostService postService;
    private final PostRepository postRepository;
    private final PostReactionRepository postReactionRepository;
    private final static Logger logger = LoggerFactory.getLogger(PostReactionServiceImpl.class);

    @Autowired
    public PostReactionServiceImpl(PostMapper postMapper, UserService userService, PostService postService,
                                   PostRepository postRepository, PostReactionRepository postReactionRepository){
        this.postMapper = postMapper;
        this.userService = userService;
        this.postService = postService;
        this.postRepository = postRepository;
        this.postReactionRepository = postReactionRepository;
    }

    @Override
    @Transactional
    public PostGetDto likePost(PostReactionRequestDto postReactionRequestDto) throws AccessDeniedException {
        logger.info("Starting like post...");
        User user = userService.fetchUserById(postReactionRequestDto.getUserId());
        Post post = postMapper.toEntity(postService.getPostById(postReactionRequestDto.getPostId()));
        Optional<PostReaction> existing = postReactionRepository.findByUserAndPost(user,post);

        if(userService.isLoggedInUserMatching(user.getId())){
            logger.warn("Unauthorized attempt to add a reaction to a post on behalf of another user. ");
            throw new AccessDeniedException("You can only add reactions to posts on behalf of your own account.");
        }

        try{
            boolean isLiked = false;
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
                    isLiked = true;
                    logger.info("New Like added for user {} on post {}", user.getId(), post.getId());
                }
            }

            int likeCount = postReactionRepository.countLikByPost(post);
            post.setLikes(likeCount);
            postRepository.save(post);

            logger.info("like Post finished.");
            PostGetDto postGetDto = postMapper.toPostGetDto(post);
            postGetDto.setLikedByCurrentUser(isLiked);
            return postGetDto;
        } catch (Exception exception){
            logger.error("Error occurred while like post {} by user  {}, Error: {}", postReactionRequestDto.getPostId(), postReactionRequestDto.getUserId(), exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public void updateLikedStatusForPosts(List<PostGetDto> postGetDtos, String userEmail) {
        logger.info("Updating liked status for posts for user {}", userEmail);
        List<Long> likedPostIds = postReactionRepository.findLikedPostIdsByUserEmail(userEmail);
        postGetDtos.forEach(post -> post.setLikedByCurrentUser(likedPostIds.contains(post.getId())));
    }

    @Override
    public boolean checkIfLikedByCurrentUser(Long postId, String userEmail) {
        return postReactionRepository.existsLikeByPostIdAndUserEmail(postId, userEmail);
    }
    
}
