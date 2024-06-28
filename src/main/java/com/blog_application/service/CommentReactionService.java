package com.blog_application.service;

import com.blog_application.dto.comment.reaction.LikeDislikeRequestDTO;
import com.blog_application.dto.comment.reaction.LikeDislikeResponseDTO;

public interface CommentReactionService {

    LikeDislikeResponseDTO likeDislikeComment(LikeDislikeRequestDTO likeDislikeRequestDTO);

}
