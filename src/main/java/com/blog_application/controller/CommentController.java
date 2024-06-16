package com.blog_application.controller;

import com.blog_application.dto.CommentDto;
import com.blog_application.service.CommentService;
import com.blog_application.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;
    private final static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    //POST Mapping-Create Comment
    @PostMapping("/post/{post_id}/user/{user_id}")
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto, @PathVariable("post_id") Long postId,
                                                    @PathVariable("user_id") Long userId){
        logger.info("Received request to create comment with content: {}",commentDto.getContent());
        CommentDto createdComment = commentService.createComment(commentDto,postId,userId);
        logger.info("Returning response for comment creation with content : {}",commentDto.getContent());
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
    public ResponseEntity<CommentDto> getCommentById(@PathVariable("id") Long commentId){
        logger.info("Received request to get comment with ID : {}",commentId);
        CommentDto commentDto = commentService.getCommentById(commentId);
        logger.info("Returning response for get comment with ID : {}",commentId);
        return new ResponseEntity<>(commentDto,HttpStatus.OK);
    }

    //PUT Mapping-Update comment
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@RequestBody CommentDto commentDto,@PathVariable("id") Long commentId){
        logger.info("Received request to update comment with ID : {}",commentId);
        CommentDto updatedComment = commentService.updateComment(commentDto,commentId);
        logger.info("Returning response for update comment with ID : {}",commentId);
        return new ResponseEntity<>(updatedComment,HttpStatus.OK);
    }

}
