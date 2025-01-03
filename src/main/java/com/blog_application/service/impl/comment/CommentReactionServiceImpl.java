package com.blog_application.service.impl.comment;

import com.blog_application.dto.comment.CommentGetDto;
import com.blog_application.dto.comment.reaction.CommentReactionRequestDto;
import com.blog_application.dto.comment.reaction.ReactionCountsDto;
import com.blog_application.model.comment.Comment;
import com.blog_application.model.comment.CommentReaction;
import com.blog_application.model.user.User;
import com.blog_application.repository.comment.CommentReactionRepository;
import com.blog_application.repository.comment.CommentRepository;
import com.blog_application.service.comment.CommentReactionService;
import com.blog_application.service.comment.CommentService;
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
public class CommentReactionServiceImpl implements CommentReactionService {

    private final UserService userService;
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final CommentReactionRepository commentReactionRepository;

    private static final Logger logger = LoggerFactory.getLogger(CommentReactionServiceImpl.class);

    @Autowired
    public CommentReactionServiceImpl(UserService userService, CommentReactionRepository commentReactionRepository
                                     , CommentRepository commentRepository, CommentService commentService){
        this.userService  =userService;
        this.commentService = commentService;
        this.commentRepository = commentRepository;
        this.commentReactionRepository = commentReactionRepository;
    }

    @Override
    @Transactional
    public ReactionCountsDto likeDislikeComment(CommentReactionRequestDto requestDTO) throws AccessDeniedException {
        logger.info("Starting like/Dislike Comment...");
        User user = userService.fetchUserById(requestDTO.getUserId());
        Comment comment = commentService.fetchCommentById(requestDTO.getCommentId());
        Optional<CommentReaction> existing  = commentReactionRepository.findByUserAndComment(user,comment);

        if(userService.isLoggedInUserMatching(requestDTO.getUserId())){
            logger.warn("Unauthorized attempt to react to a comment using another user's account.");
            throw new AccessDeniedException("You can only react to comments using your own account.");
        }

        try {
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
            return new ReactionCountsDto(comment.getLikes(), comment.getDislikes());
        } catch (Exception exception){
            logger.error("Error while adding a reaction to the comment. Error: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public boolean checkIfLikedByCurrentUser(Long commentId, String userEmail) {
        return commentReactionRepository.existsLikeByCommentIdAndUserEmail(commentId, userEmail);
    }

    @Override
    public boolean checkIfDislikedByCurrentUser(Long commentId, String userEmail) {
        return commentReactionRepository.existsDislikeByCommentIdAndUserEmail(commentId, userEmail);
    }

    @Override
    public void updateReactionStatusForComments(List<CommentGetDto> commentGetDtos, String userEmail) {
        logger.info("Updating reaction status for comments for user {}", userEmail);
        List<Long> likedCommentIds = commentReactionRepository.findLikedCommentIdsByUserEmail(userEmail);
        List<Long> dislikedCommentIds = commentReactionRepository.findDislikedCommentIdsByUserEmail(userEmail);
        commentGetDtos.forEach(comment -> {
            comment.setLikedByCurrentUser(likedCommentIds.contains(comment.getId()));
            comment.setDislikeByCurrentUser(dislikedCommentIds.contains(comment.getId()));
        });
    }

}
