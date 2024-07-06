package com.blog_application.service.impl.comment;

import com.blog_application.config.mapper.comment.CommentMapper;
import com.blog_application.config.mapper.user.UserMapper;
import com.blog_application.dto.comment.CommentGetDto;
import com.blog_application.dto.comment.reaction.CommentReactionRequestDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.comment.Comment;
import com.blog_application.model.comment.CommentReaction;
import com.blog_application.model.user.User;
import com.blog_application.repository.comment.CommentReactionRepository;
import com.blog_application.repository.comment.CommentRepository;
import com.blog_application.service.comment.CommentReactionService;
import com.blog_application.service.user.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentReactionServiceImpl implements CommentReactionService {

    private final UserMapper userMapper;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final CommentReactionRepository commentReactionRepository;

    private static final Logger logger = LoggerFactory.getLogger(CommentReactionServiceImpl.class);

    @Autowired
    public CommentReactionServiceImpl(UserService userService, CommentReactionRepository commentReactionRepository,
                                      UserMapper userMapper, CommentRepository commentRepository, CommentMapper commentMapper){
        this.userMapper = userMapper;
        this.userService  =userService;
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
        this.commentReactionRepository = commentReactionRepository;
    }
    @Override
    @Transactional
    public CommentGetDto likeDislikeComment(CommentReactionRequestDto requestDTO) {
        logger.info("Starting like/Dislike Comment...");
        User user = userMapper.toEntity(userService.getUserById(requestDTO.getUserId()));
        Comment comment = commentRepository.findById(requestDTO.getCommentId()).orElseThrow(() -> {
            logger.warn("Comment with ID {} not found, Get comment operation not performed",requestDTO.getCommentId());
            return new ResourceNotFoundException("Comment","ID",String.valueOf(requestDTO.getCommentId()),"Get Comment operation not performed");
        });
        Optional<CommentReaction> existing  = commentReactionRepository.findByUserAndComment(user,comment);

        CommentReaction commentReaction;
        if(existing .isPresent()){
            commentReaction = existing.get();
            if(commentReaction.isLike() == requestDTO.isLike()){
                // If the action is the same, remove the like/dislike
                commentReactionRepository.delete(commentReaction);
                logger.info("Like/Dislike removed for user {} on comment {}", user.getId(), comment.getId());
            } else {
                // If the action is different, update the like/dislike
                commentReaction.setLike(requestDTO.isLike());
                commentReactionRepository.save(commentReaction);
                logger.info("Like/Dislike updated for user {} on comment {}", user.getId(), comment.getId());
            }
        } else {
            // If not liked or disliked, add a new like/dislike
            commentReaction = new CommentReaction();
            commentReaction.setUser(user);
            commentReaction.setComment(comment);
            commentReaction.setLike(requestDTO.isLike());
            commentReactionRepository.save(commentReaction);
            logger.info("New Like/Dislike added for user {} on comment {}", user.getId(), comment.getId());
        }

        int likeCount = commentReactionRepository.countLikesByComment(comment);
        int dislikeCount = commentReactionRepository.countDislikesByComment(comment);
        comment.setLikes(likeCount);
        comment.setDislikes(dislikeCount);
        commentRepository.save(comment);
        
        logger.info("likeDislikeComment finished.");
        return commentMapper.toCommentGetDto(comment);
    }

}
