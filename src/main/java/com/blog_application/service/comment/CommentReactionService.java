package com.blog_application.service.comment;

import com.blog_application.dto.comment.CommentGetDto;
import com.blog_application.dto.comment.reaction.CommentReactionRequestDto;
import com.blog_application.dto.comment.reaction.ReactionCountsDto;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface CommentReactionService {

    boolean checkIfLikedByCurrentUser(Long commentId, String userEmail);
    boolean checkIfDislikedByCurrentUser(Long commentId, String userEmail);
    void updateReactionStatusForComments(List<CommentGetDto> commentGetDtos, String userEmail);
    ReactionCountsDto likeDislikeComment(CommentReactionRequestDto commentReactionRequestDTO) throws AccessDeniedException;

}
