package com.blog_application.controller;

import com.blog_application.dto.CommentDto;
import com.blog_application.service.CommentService;
import com.blog_application.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    //POST Mapping-Create Comment
    @PostMapping("/post/{id}")
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto, @PathVariable("id") Long post_id){
        CommentDto createdComment = commentService.createComment(commentDto,post_id);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    //DELETE Mapping-Delete Comment
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable("id") Long comment_id){
        commentService.deleteComment(comment_id);
        return new ResponseEntity<>(new ApiResponse("Comment Deleted Successfully",true),HttpStatus.OK);
    }

}
