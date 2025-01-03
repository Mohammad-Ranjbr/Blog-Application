package com.blog_application.service.post;

import com.blog_application.dto.post.PostCreateDto;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.PostUpdateDto;
import com.blog_application.dto.tag.TagCreateDto;
import com.blog_application.model.post.Post;
import com.blog_application.util.responses.PaginatedResponse;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface PostService {

    List<PostGetDto> getHomePosts();
    List<PostGetDto> getExplorePosts();
    Post getPostById(Long postId);
    PostGetDto getPostByIdWithImage(Long postId);
    List<PostGetDto> convertPostsToPostDtos(List<Post> posts);
    void deletePost(Long post_id) throws AccessDeniedException;
    boolean checkIfSavedByCurrentUser(Long postId, String userEmail);
    void updateSavedStatusForPosts(List<PostGetDto> postGetDtos, String userEmail);
    void updatePostInteractionStatus(PostGetDto postGetDto, String userEmail);
    void updatePostsInteractionStatus(List<PostGetDto> postGetDtoList, String userEmail);
    void updatePost(PostUpdateDto postDto, Long postId) throws AccessDeniedException;
    void removeTagsFromPost(Long postId,List<Long> tagIdsToRemove ) throws AccessDeniedException;
    PostGetDto addTagToPost(Long postId, List<TagCreateDto> tagNames) throws AccessDeniedException;
    void createPost(PostCreateDto postCreateDto, UUID userId, Long categoryId) throws IOException;
    PaginatedResponse<PostGetDto> getAllPosts(int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginatedResponse<PostGetDto> searchPosts(String keyword, int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginatedResponse<PostGetDto> getPostsByUser(UUID userId, int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginatedResponse<PostGetDto> getPostsByCategory(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginatedResponse<PostGetDto> searchPostsWithQueryMethod(String keyword, int pageNumber, int pageSize, String sortBy, String sortDir);

}
