package com.blog_application.service;

import com.blog_application.dto.comment.CommentGetDto;
import com.blog_application.dto.comment.reaction.CommentReactionRequestDTO;

public interface CommentReactionService {

    CommentGetDto likeDislikeComment(CommentReactionRequestDTO commentReactionRequestDTO);

}
