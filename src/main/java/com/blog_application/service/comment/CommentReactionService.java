package com.blog_application.service.comment;

import com.blog_application.dto.comment.CommentGetDto;
import com.blog_application.dto.comment.reaction.CommentReactionRequestDto;

public interface CommentReactionService {

    CommentGetDto likeDislikeComment(CommentReactionRequestDto commentReactionRequestDTO);

}
