package com.blog_application.service.post;

import com.blog_application.dto.post.PostCreateDto;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.PostUpdateDto;
import com.blog_application.dto.tag.TagCreateDto;
import com.blog_application.util.responses.PaginatedResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface PostService {

    void deletePost(Long post_id) throws AccessDeniedException;
    PostGetDto getPostById(Long postId);
    PostGetDto updatePost(PostUpdateDto postDto, Long postId) throws AccessDeniedException;
    void removeTagsFromPost(Long postId,List<Long> tagIdsToRemove ) throws AccessDeniedException;
    PostGetDto addTagToPost(Long postId, List<TagCreateDto> tagNames) throws AccessDeniedException;
    PostGetDto createPost(PostCreateDto postCreateDto, UUID userId, Long categoryId) throws IOException;
    PaginatedResponse<PostGetDto> getAllPosts(int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginatedResponse<PostGetDto> searchPosts(String keyword, int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginatedResponse<PostGetDto> getPostsByUser(UUID userId, int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginatedResponse<PostGetDto> getPostsByCategory(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginatedResponse<PostGetDto> searchPostsWithQueryMethod(String keyword, int pageNumber, int pageSize, String sortBy, String sortDir);

}
