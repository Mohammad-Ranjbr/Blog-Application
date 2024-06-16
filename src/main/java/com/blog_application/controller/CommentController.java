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
    @PostMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto, @PathVariable("postId") Long post_id,
                                                    @PathVariable("userId") Long user_id){
        logger.info("Received request to create comment with content: {}",commentDto.getContent());
        CommentDto createdComment = commentService.createComment(commentDto,post_id,user_id);
        logger.info("Returning response for comment creation with content : {}",commentDto.getContent());
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    //DELETE Mapping-Delete Comment
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable("id") Long comment_id){
        logger.info("Received request to delete comment with ID : {}",comment_id);
        commentService.deleteComment(comment_id);
        logger.info("Returning response for delete comment with ID : {}",comment_id);
        return new ResponseEntity<>(new ApiResponse("Comment Deleted Successfully",true),HttpStatus.OK);
    }

}
