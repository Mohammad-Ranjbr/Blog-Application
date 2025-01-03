package com.blog_application.controller;

import com.blog_application.dto.comment.CommentCreateDto;
import com.blog_application.dto.comment.CommentGetDto;
import com.blog_application.dto.comment.CommentUpdateDto;
import com.blog_application.dto.comment.reaction.CommentReactionRequestDto;
import com.blog_application.dto.comment.reaction.CommentReactionResponseDto;
import com.blog_application.dto.comment.reaction.ReactionCountsDto;
import com.blog_application.service.comment.CommentReactionService;
import com.blog_application.service.comment.CommentService;
import com.blog_application.util.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentReactionService commentReactionService;
    private final static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    public CommentController(CommentService commentService,CommentReactionService commentReactionService){
        this.commentService = commentService;
        this.commentReactionService = commentReactionService;
    }

    //POST Mapping-Create Comment
    @PostMapping("/post/{post_id}/user/{user_id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<CommentGetDto> createComment(@Valid @RequestBody CommentCreateDto commentCreateDto, @PathVariable("post_id") Long postId,@PathVariable("user_id") UUID userId) throws AccessDeniedException {
        logger.info("Received request to create comment with content: {}",commentCreateDto.getContent());
        CommentGetDto createdComment = commentService.createComment(commentCreateDto,postId,userId);
        logger.info("Returning response for comment creation with content : {}",commentCreateDto.getContent());
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    //DELETE Mapping-Delete Comment
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable("id") Long commentId) throws AccessDeniedException {
        logger.info("Received request to delete comment with ID : {}",commentId);
        commentService.deleteComment(commentId);
        logger.info("Returning response for delete comment with ID : {}",commentId);
        return new ResponseEntity<>(new ApiResponse("Comment Deleted Successfully",true),HttpStatus.OK);
    }

    //GET Mapping-Get comment by ID
    @GetMapping("/{id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<CommentGetDto> getCommentById(@PathVariable("id") Long commentId){
        logger.info("Received request to get comment with ID : {}",commentId);
        CommentGetDto commentGetDto = commentService.getCommentById(commentId);
        logger.info("Returning response for get comment with ID : {}",commentId);
        return new ResponseEntity<>(commentGetDto,HttpStatus.OK);
    }

    //GET Mapping-Get comment by Post ID
    @GetMapping("/post/{post_id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<List<CommentGetDto>> getCommentsByPostId(@PathVariable("post_id") Long postId){
        logger.info("Received request to fetch comments for post with ID : {}", postId);
        List<CommentGetDto> commentGetDtos = commentService.getCommentsByPostId(postId);
        logger.info("Returning response for get comments with post ID : {}", postId);
        return new ResponseEntity<>(commentGetDtos,HttpStatus.OK);
    }

    //GET Mapping-Get comment by Parent ID
    @GetMapping("/parent/{parent_id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<List<CommentGetDto>> getCommentsByParentId(@PathVariable("parent_id") Long parentId){
        logger.info("Received request to fetch comments for parent comment with ID: {}", parentId);
        List<CommentGetDto> commentGetDtos = commentService.getCommentsByParentId(parentId);
        logger.info("Returning response for parent comment with ID : {}", parentId);
        return new ResponseEntity<>(commentGetDtos,HttpStatus.OK);
    }

    //PUT Mapping-Update comment
    @PutMapping("/{id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<ApiResponse> updateComment(@Valid @RequestBody CommentUpdateDto commentUpdateDto, @PathVariable("id") Long commentId) throws AccessDeniedException {
        logger.info("Received request to update comment with ID : {}",commentId);
        commentService.updateComment(commentUpdateDto,commentId);
        logger.info("Returning response for update comment with ID : {}",commentId);
        return new ResponseEntity<>(new ApiResponse("Comment updated successfully.", true),HttpStatus.OK);
    }

    @PostMapping("/like-dislike")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<CommentReactionResponseDto> likeDislikeComment(@RequestBody CommentReactionRequestDto requestDto) throws AccessDeniedException {
        logger.info("Received like/dislike request for User {} on Comment {}", requestDto.getUserId(), requestDto.getCommentId());
        ReactionCountsDto reactionCountsDto = commentReactionService.likeDislikeComment(requestDto);
        logger.info("Returning response for like/dislike comment with User {} on Comment {}", requestDto.getUserId(), requestDto.getCommentId());
        return new ResponseEntity<>(new CommentReactionResponseDto(true, reactionCountsDto.likes(), reactionCountsDto.dislikes(),
                "The comment reaction was successful."),HttpStatus.OK);
    }


}
