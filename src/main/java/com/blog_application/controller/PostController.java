package com.blog_application.controller;

import com.blog_application.dto.post.PostDto;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.service.PostService;
import com.blog_application.util.ApiResponse;
import com.blog_application.util.PostResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final static Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    public PostController(PostService postService){
        this.postService = postService;
    }

    //POST Mapping-Create Post
    @PostMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, @PathVariable("userId") Long userId, @PathVariable("categoryId") Long categoryId){
        logger.info("Received request to create post for user with ID : {} and category with ID : {}", userId, categoryId);
        PostDto createdPost = postService.createPost(postDto,userId,categoryId);
        logger.info("Returning response for post creation with title: {}", createdPost.getTitle());
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    //GET Mapping-Get All Posts By User
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable("userId") Long userId){
        logger.info("Received request to get posts for user with ID : {}", userId);
        List<PostDto> posts = postService.getPostsByUser(userId);
        logger.info("Returning response for get posts for user with ID : {}", userId);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    //GET Mapping-Get All Posts By Category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable("categoryId") Long categoryId){
        logger.info("Received request to get posts for category with ID: {}", categoryId);
        List<PostDto> posts = postService.getPostsByCategory(categoryId);
        logger.info("Returning response for posts for category with ID: {}", categoryId);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    //GET Mapping-Get All Posts
    @GetMapping("/")
    public ResponseEntity<PostResponse> getAllPosts(@RequestParam(value = "pageNumber",defaultValue = "0",required = false) int pageNumber,
                                                    @RequestParam(value = "pageSize",defaultValue = "10",required = false) int pageSize,
                                                    @RequestParam(value = "sortBy",defaultValue = "title",required = false) String sortBy,
                                                    @RequestParam(value = "sortDir",defaultValue = "asc",required = false) String sortDir){
        logger.info("Received request to fetch all posts");
        PostResponse postResponse = postService.getAllPosts(pageNumber,pageSize,sortBy,sortDir);
        logger.info("Returning response for all posts");
        return new ResponseEntity<>(postResponse,HttpStatus.OK);
    }

    //GET Mapping-Get Post By ID
    @GetMapping("/{id}")
    public ResponseEntity<PostGetDto> git (@PathVariable("id") Long postId){
        logger.info("Received request to fetch post with ID: {}", postId);
        PostGetDto postDto = postService.getPostById(postId);
        logger.info("Returning response for post with ID: {}", postId);
        return new ResponseEntity<>(postDto,HttpStatus.OK);
    }

    //DELETE Mapping-Delete Post
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable("id") Long postId){
        logger.info("Received request to delete post with ID: {}", postId);
        postService.deletePost(postId);
        logger.info("Returning response for delete post with ID: {}", postId);
        return new ResponseEntity<>(new ApiResponse("Post Deleted Successfully",true),HttpStatus.OK);
    }

    //PUT Mapping-Update Post
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@RequestBody PostDto postDto,@PathVariable("id") Long postId){
        logger.info("Received request to update post with ID: {}", postId);
        PostDto updatedPost = postService.updatePost(postDto,postId);
        logger.info("Returning response for updated post with ID: {}", postId);
        return new ResponseEntity<>(updatedPost,HttpStatus.OK);
    }

    //GET Mapping-Search Post
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<PostDto>> searchPostByTitle(@PathVariable("keyword") String title){
        logger.info("Received request to search posts by title containing: {}", title);
        List<PostDto> posts = postService.searchPosts(title);
        logger.info("Returning response for search posts by title containing: {}", title);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    //GET Mapping-Search Post
    @GetMapping("/search2/{keyword}")
    public ResponseEntity<List<PostDto>> searchPostByTitleWithQueryMethod(@PathVariable("keyword") String title){
        logger.info("Received request to search posts by title using query method with keyword: {}", title);
        List<PostDto> posts = postService.searchPostsWithQueryMethod(title);
        logger.info("Returning response for search posts by title using query method with keyword: {}", title);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

}
