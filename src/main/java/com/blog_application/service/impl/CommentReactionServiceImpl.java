package com.blog_application.service.impl;

import com.blog_application.config.mapper.CommentMapper;
import com.blog_application.config.mapper.UserMapper;
import com.blog_application.model.Comment;
import com.blog_application.model.CommentReaction;
import com.blog_application.model.User;
import com.blog_application.repository.CommentReactionRepository;
import com.blog_application.service.CommentService;
import com.blog_application.service.CommentReactionService;
import com.blog_application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CommentReactionServiceImpl implements CommentReactionService {

    private final UserMapper userMapper;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final CommentService commentService;
    private final CommentReactionRepository commentReactionRepository;

    @Autowired
    public CommentReactionServiceImpl(UserService userService, CommentService commentService, CommentReactionRepository commentReactionRepository,
                                      UserMapper userMapper, CommentMapper commentMapper){
        this.userMapper = userMapper;
        this.userService  =userService;
        this.commentMapper = commentMapper;
        this.commentService = commentService;
        this.commentReactionRepository = commentReactionRepository;
    }
    @Override
    public void likeComment(UUID userId, Long commentId) {
        User user = userMapper.toEntity(userService.getUserById(userId));
        Comment comment = commentMapper.toEntity(commentService.getCommentById(commentId));
        Optional<CommentReaction> existing  = commentReactionRepository.findByUserAndComment(user,comment);

        if(existing .isPresent()){
            CommentReaction commentReaction = existing.get();
            if(commentReaction.isLike()){
                // If already liked, remove the like
                commentReactionRepository.delete(commentReaction);
            } else {
                // If currently disliked, change to like
                commentReaction.setLike(true);
                commentReactionRepository.save(commentReaction);
            }
        } else {
            // If not liked or disliked, add a like
            CommentReaction commentReaction = new CommentReaction();
            commentReaction.setUser(user);
            commentReaction.setComment(comment);
            commentReaction.setLike(true);
            commentReactionRepository.save(commentReaction);
        }
    }

    @Override
    public void dislikeComment(UUID userId, Long commentId) {
        User user = userMapper.toEntity(userService.getUserById(userId));
        Comment comment = commentMapper.toEntity(commentService.getCommentById(commentId));
        Optional<CommentReaction> existing  = commentReactionRepository.findByUserAndComment(user,comment);

        if (existing.isPresent()){
            CommentReaction commentReaction = existing.get();
            if (!commentReaction.isLike()){
                // If already disliked, remove the dislike
                commentReactionRepository.delete(commentReaction);
            } else {
                // If currently liked, change to dislike
                commentReaction.setLike(false);
                commentReactionRepository.save(commentReaction);
            }
        } else {
            // If not liked or disliked, add a dislike
            CommentReaction commentReaction = new CommentReaction();
            commentReaction.setUser(user);
            commentReaction.setComment(comment);
            commentReaction.setLike(false);
            commentReactionRepository.save(commentReaction);
        }
    }

}
