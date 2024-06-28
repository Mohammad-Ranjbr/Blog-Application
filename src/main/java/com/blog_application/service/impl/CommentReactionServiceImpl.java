package com.blog_application.service.impl;

import com.blog_application.config.mapper.CommentMapper;
import com.blog_application.config.mapper.UserMapper;
import com.blog_application.dto.comment.reaction.LikeDislikeRequestDTO;
import com.blog_application.dto.comment.reaction.LikeDislikeResponseDTO;
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
    public LikeDislikeResponseDTO likeDislikeComment(LikeDislikeRequestDTO requestDTO) {
        User user = userMapper.toEntity(userService.getUserById(requestDTO.getUserId()));
        Comment comment = commentMapper.toEntity(commentService.getCommentById(requestDTO.getCommentId()));
        Optional<CommentReaction> existing  = commentReactionRepository.findByUserAndComment(user,comment);

        CommentReaction commentReaction;
        if(existing .isPresent()){
            commentReaction = existing.get();
            if(commentReaction.isLike() == requestDTO.isLike()){
                // If the action is the same, remove the like/dislike
                commentReactionRepository.delete(commentReaction);
            } else {
                // If the action is different, update the like/dislike
                commentReaction.setLike(requestDTO.isLike());
                commentReactionRepository.save(commentReaction);
            }
        } else {
            // If not liked or disliked, add a new like/dislike
            commentReaction = new CommentReaction();
            commentReaction.setUser(user);
            commentReaction.setComment(comment);
            commentReaction.setLike(requestDTO.isLike());
            commentReactionRepository.save(commentReaction);
        }
        return new LikeDislikeResponseDTO(commentReaction.getId(),commentReaction.getUser().getId(),commentReaction.getComment().getId(), commentReaction.isLike());
    }

}
