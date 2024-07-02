package com.blog_application.controller;

import com.blog_application.dto.comment.CommentCreateDto;
import com.blog_application.dto.comment.CommentGetDto;
import com.blog_application.dto.comment.CommentUpdateDto;
import com.blog_application.dto.comment.reaction.CommentReactionRequestDto;
import com.blog_application.service.CommentReactionService;
import com.blog_application.service.CommentService;
import com.blog_application.util.responses.ApiResponse;
import com.blog_application.util.constants.ApplicationConstants;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CommentGetDto> createComment(@Valid @RequestBody CommentCreateDto commentCreateDto, @PathVariable("post_id") Long postId,@PathVariable("user_id") UUID userId){
        logger.info("Received request to create comment with content: {}",commentCreateDto.getContent());
        CommentGetDto createdComment = commentService.createComment(commentCreateDto,postId,userId);
        logger.info("Returning response for comment creation with content : {}",commentCreateDto.getContent());
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    //DELETE Mapping-Delete Comment
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable("id") Long commentId){
        logger.info("Received request to delete comment with ID : {}",commentId);
        commentService.deleteComment(commentId);
        logger.info("Returning response for delete comment with ID : {}",commentId);
        return new ResponseEntity<>(new ApiResponse("Comment Deleted Successfully",true),HttpStatus.OK);
    }

    //GET Mapping-Get comment by ID
    @GetMapping("/{id}")
    public ResponseEntity<CommentGetDto> getCommentById(@PathVariable("id") Long commentId){
        logger.info("Received request to get comment with ID : {}",commentId);
        CommentGetDto commentGetDto = commentService.getCommentById(commentId);
        logger.info("Returning response for get comment with ID : {}",commentId);
        return new ResponseEntity<>(commentGetDto,HttpStatus.OK);
    }

    //PUT Mapping-Update comment
    @PutMapping("/{id}")
    public ResponseEntity<CommentGetDto> updateComment(@Valid @RequestBody CommentUpdateDto commentUpdateDto, @PathVariable("id") Long commentId){
        logger.info("Received request to update comment with ID : {}",commentId);
        CommentGetDto updatedComment = commentService.updateComment(commentUpdateDto,commentId);
        logger.info("Returning response for update comment with ID : {}",commentId);
        return new ResponseEntity<>(updatedComment,HttpStatus.OK);
    }

    //OPTIONS Mapping for all comments
    @RequestMapping(value = "/",method = RequestMethod.OPTIONS)
    public ResponseEntity<?> optionsForAllComments(){
        logger.info("Received OPTIONS request for all comments");
        HttpHeaders headers = new HttpHeaders();
        headers.add(ApplicationConstants.HEADER_ALLOW,"POST,OPTIONS");
        logger.info("Returning response with allowed methods for all comments");
        return new ResponseEntity<>(headers,HttpStatus.OK);
    }

    //OPTIONS Mapping fo single comment
    @RequestMapping(value = "/{id}",method = RequestMethod.OPTIONS)
    public ResponseEntity<?> optionsForSingleComment(@PathVariable("id") Long commentId){
        logger.info("Received OPTIONS request for comment with ID : {}", commentId);
        ResponseEntity<?> response = ResponseEntity.ok()
                .allow(HttpMethod.GET,HttpMethod.PUT,HttpMethod.DELETE,HttpMethod.OPTIONS)
                .build();
        logger.info("Returning response with allowed methods for comment with ID : {}", commentId);
        return response;
    }

    @PostMapping("/like-dislike")
    public ResponseEntity<CommentGetDto> likeDislikeComment(@RequestBody CommentReactionRequestDto requestDto){
        logger.info("Received like/dislike request for User {} on Comment {}", requestDto.getUserId(), requestDto.getCommentId());
        CommentGetDto commentGetDto = commentReactionService.likeDislikeComment(requestDto);
        if (commentGetDto == null) {
            logger.warn("No response DTO generated for like/dislike comment request");
            return ResponseEntity.noContent().build();
        }
        logger.info("Returning response for like/dislike comment with User {} on Comment {}", requestDto.getUserId(), requestDto.getCommentId());
        return new ResponseEntity<>(commentGetDto,HttpStatus.OK);
    }


}
