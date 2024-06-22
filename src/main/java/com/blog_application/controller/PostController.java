package com.blog_application.controller;

import com.blog_application.dto.post.PostCreateDto;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.PostUpdateDto;
import com.blog_application.service.PostService;
import com.blog_application.util.responses.ApiResponse;
import com.blog_application.util.constants.ApplicationConstants;
import com.blog_application.util.responses.PaginatedResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<PostGetDto> createPost(@Valid @RequestBody PostCreateDto postCreateDto, @PathVariable("userId") UUID userId, @PathVariable("categoryId") Long categoryId){
        logger.info("Received request to create post for user with ID : {} and category with ID : {}", userId, categoryId);
        PostGetDto createdPost = postService.createPost(postCreateDto,userId,categoryId);
        logger.info("Returning response for post creation with title: {}", createdPost.getTitle());
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    //GET Mapping-Get All Posts By User
    @GetMapping("/user/{userId}")
    public ResponseEntity<PaginatedResponse<PostGetDto>> getPostsByUser(@PathVariable("userId") UUID userId,
            @RequestParam(value = ApplicationConstants.PAGE_NUMBER,defaultValue = ApplicationConstants.DEFAULT_PAGE_NUMBER,required = false) int pageNumber,
            @RequestParam(value = ApplicationConstants.PAGE_SIZE,defaultValue = ApplicationConstants.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = ApplicationConstants.SORT_BY,defaultValue = ApplicationConstants.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(value = ApplicationConstants.SORT_DIR,defaultValue = ApplicationConstants.DEFAULT_SORT_DIR,required = false) String sortDir){
        logger.info("Received request to get posts for user with ID : {}", userId);
        PaginatedResponse<PostGetDto> posts = postService.getPostsByUser(userId,pageNumber,pageSize,sortBy,sortDir);
        logger.info("Returning response for get posts for user with ID : {}", userId);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    //GET Mapping-Get All Posts By Category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PostGetDto>> getPostsByCategory(@PathVariable("categoryId") Long categoryId){
        logger.info("Received request to get posts for category with ID : {}", categoryId);
        List<PostGetDto> posts = postService.getPostsByCategory(categoryId);
        logger.info("Returning response for posts for category with ID : {}", categoryId);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    //GET Mapping-Get All Posts
    @GetMapping("/")
    public ResponseEntity<PaginatedResponse<PostGetDto>> getAllPosts(
            @RequestParam(value = ApplicationConstants.PAGE_NUMBER,defaultValue = ApplicationConstants.DEFAULT_PAGE_NUMBER,required = false) int pageNumber,
            @RequestParam(value = ApplicationConstants.PAGE_SIZE,defaultValue = ApplicationConstants.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = ApplicationConstants.SORT_BY,defaultValue = ApplicationConstants.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(value = ApplicationConstants.SORT_DIR,defaultValue = ApplicationConstants.DEFAULT_SORT_DIR,required = false) String sortDir){
        logger.info("Received request to fetch all posts");
        PaginatedResponse<PostGetDto> postResponse = postService.getAllPosts(pageNumber,pageSize,sortBy,sortDir);
        logger.info("Returning response for all posts");
        return new ResponseEntity<>(postResponse,HttpStatus.OK);
    }

    //GET Mapping-Get Post By ID
    @GetMapping("/{id}")
    public ResponseEntity<PostGetDto> git (@PathVariable("id") Long postId){
        logger.info("Received request to fetch post with ID : {}", postId);
        PostGetDto postDto = postService.getPostById(postId);
        logger.info("Returning response for post with ID : {}", postId);
        return new ResponseEntity<>(postDto,HttpStatus.OK);
    }

    //DELETE Mapping-Delete Post
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable("id") Long postId){
        logger.info("Received request to delete post with ID : {}", postId);
        postService.deletePost(postId);
        logger.info("Returning response for delete post with ID : {}", postId);
        return new ResponseEntity<>(new ApiResponse("Post Deleted Successfully",true),HttpStatus.OK);
    }

    //PUT Mapping-Update Post
    @PutMapping("/{id}")
    public ResponseEntity<PostGetDto> updatePost(@Valid @RequestBody PostUpdateDto postUpdateDto, @PathVariable("id") Long postId){
        logger.info("Received request to update post with ID : {}", postId);
        PostGetDto updatedPost = postService.updatePost(postUpdateDto,postId);
        logger.info("Returning response for updated post with ID : {}", postId);
        return new ResponseEntity<>(updatedPost,HttpStatus.OK);
    }

    //GET Mapping-Search Post
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<PostGetDto>> searchPostByTitle(@PathVariable("keyword") String title){
        logger.info("Received request to search posts by title containing : {}", title);
        List<PostGetDto> posts = postService.searchPosts(title);
        logger.info("Returning response for search posts by title containing : {}", title);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    //GET Mapping-Search Post
    @GetMapping("/search2/{keyword}")
    public ResponseEntity<List<PostGetDto>> searchPostByTitleWithQueryMethod(@PathVariable("keyword") String title){
        logger.info("Received request to search posts by title using query method with keyword : {}", title);
        List<PostGetDto> posts = postService.searchPostsWithQueryMethod(title);
        logger.info("Returning response for search posts by title using query method with keyword : {}", title);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    //OPTIONS Mapping for all posts
    @RequestMapping(value = "/",method = RequestMethod.OPTIONS)
    public ResponseEntity<?> optionsForAllPosts(){
        logger.info("Received OPTIONS request for all posts");
        HttpHeaders headers = new HttpHeaders();
        headers.add(ApplicationConstants.HEADER_ALLOW,"GET,POST,OPTIONS");
        logger.info("Returning response with allowed methods for all posts");
        return new ResponseEntity<>(headers,HttpStatus.OK);
    }

    //OPTIONS Mapping fo single post
    @RequestMapping(value = "/{id}",method = RequestMethod.OPTIONS)
    public ResponseEntity<?> optionsForSinglePost(@PathVariable("id") Long postId){
        logger.info("Received OPTIONS request for post with ID : {}", postId);
        ResponseEntity<?> response = ResponseEntity.ok()
                .allow(HttpMethod.GET,HttpMethod.PUT,HttpMethod.DELETE,HttpMethod.OPTIONS)
                .build();
        logger.info("Returning response with allowed methods for post with ID : {}", postId);
        return response;
    }

}
