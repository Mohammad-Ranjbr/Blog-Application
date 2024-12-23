package com.blog_application.service.impl.comment;

import com.blog_application.config.mapper.comment.CommentMapper;
import com.blog_application.config.mapper.post.PostMapper;
import com.blog_application.dto.comment.CommentCreateDto;
import com.blog_application.dto.comment.CommentGetDto;
import com.blog_application.dto.comment.CommentUpdateDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.comment.Comment;
import com.blog_application.model.post.Post;
import com.blog_application.model.user.User;
import com.blog_application.repository.comment.CommentRepository;
import com.blog_application.service.comment.CommentService;
import com.blog_application.service.post.PostService;
import com.blog_application.service.user.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class CommentServiceImpl implements CommentService {

    private final PostMapper postMapper;
    private final UserService userService;
    private final PostService postService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final static Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    public CommentServiceImpl(CommentMapper commentMapper,PostService postService,
                              CommentRepository commentRepository,PostMapper postMapper,UserService userService){
        this.postMapper = postMapper;
        this.userService = userService;
        this.postService = postService;
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public CommentGetDto createComment(CommentCreateDto commentCreateDto, Long postId, UUID userId) throws AccessDeniedException {
        logger.info("Creating comment with content : {}",commentCreateDto.getContent());
        Post post = postMapper.toEntity(postService.getPostById(postId));
        User user = userService.fetchUserById(userId);

        if(userService.isLoggedInUserMatching(user.getId())){
            logger.warn("Unauthorized attempt to add a comment to another user's post.");
            throw new AccessDeniedException("You can only add comments to posts using your own account.");
        }

        try {
            Comment comment = commentMapper.toEntity(commentCreateDto);
            comment.setPost(post);
            comment.setUser(user);
            if(commentCreateDto.getParent() != null){
                Comment parentComment = commentRepository.findById(commentCreateDto.getParent()).orElseThrow(() ->{
                    logger.warn("Parent comment with ID {} not found",commentCreateDto.getParent());
                    return new ResourceNotFoundException("Comment","ID",String.valueOf(commentCreateDto.getParent()),"Parent comment not found");
                });
                parentComment.addReply(comment);
            }
            Comment savedComment = commentRepository.save(comment);
            logger.info("Comment created successfully with content : {}",commentCreateDto.getContent());
            return commentMapper.toCommentGetDto(savedComment);
        } catch (Exception exception){
            logger.error("Error occurred while creating comment, Error: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) throws AccessDeniedException {
        logger.info("Deleting comment with ID : {}",commentId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            logger.warn("Comment with ID {} not found, Delete comment operation not performed",commentId);
            return new ResourceNotFoundException("Comment","ID",String.valueOf(commentId),"Delete Comment operation not performed");
        });

        try{
            if(!userService.isLoggedInUserMatching(comment.getUser().getId()) || userService.isAdmin()) {
                commentRepository.delete(comment);
                logger.info("Comment with ID : {} deleted successfully", commentId);
            } else {
                logger.warn("Unauthorized attempt to delete a comment.");
                throw new AccessDeniedException("Only the comment owner or an admin can delete this comment.");
            }
        } catch (Exception exception){
            logger.error("Error occurred while removing comment, Error: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    // getCommentById don't need @Transactional because they don't make any changes to the database and just retrieve the data.
    @Override
    public CommentGetDto getCommentById(Long commentId) {
        logger.info("Fetching comment with ID : {}",commentId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            logger.warn("Comment with ID {} not found, Get comment operation not performed",commentId);
            return new ResourceNotFoundException("Comment","ID",String.valueOf(commentId),"Get Comment operation not performed");
        });
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        Consumer<Comment> printCommentDetails = foundComment -> System.out.println("Comment Found : "+comment);
        optionalComment.ifPresent(printCommentDetails);
        logger.info("Comment found with ID : {}",commentId);
        return commentMapper.toCommentGetDto(comment);
    }

    @Override
    public List<CommentGetDto> getCommentsByPostId(Long postId) {
        logger.info("Fetching comments for post with ID : {}", postId);
        Post post = postMapper.toEntity(postService.getPostById(postId));
        List<Comment> comments = commentRepository.findByPost(post);
        if(comments.isEmpty()){
            logger.info("No comments found for post with ID : {}", postId);
            return Collections.emptyList();
        } else {
            List<CommentGetDto> commentGetDtos = comments.stream().map(commentMapper::toCommentGetDto).toList();
            logger.info("{} comments found for post with ID : {}", comments.size(), postId);
            return commentGetDtos;
        }
    }

    @Override
    public List<CommentGetDto> getCommentsByParentId(Long parentId) {
        logger.info("Fetching comments for parent comment with ID: {}", parentId);
        Comment comment = commentRepository.findById(parentId).orElseThrow(() -> {
            logger.warn("Comment with ID {} not found, Get Parent Comment operation not performed",parentId);
            return new ResourceNotFoundException("Comment","ID",String.valueOf(parentId),"Get Parent Comment operation not performed");
        });
        List<Comment> comments = commentRepository.findByParentId(comment.getId());
        if(comments.isEmpty()){
            logger.info("No child comments found for parent comment with ID: {}", parentId);
            return Collections.emptyList();
        } else {
            List<CommentGetDto> commentGetDtos = comments.stream().map(commentMapper::toCommentGetDto).toList();
            logger.info("{} child comments found for parent comment with ID: {}", comments.size(), parentId);
            return commentGetDtos;
        }
    }

    @Override
    @Transactional
    public CommentGetDto updateComment(CommentUpdateDto commentUpdateDto, Long commentId) throws AccessDeniedException {
        logger.info("Updating comment with ID : {}",commentId);
        Comment commentInDb = commentMapper.toEntity(this.getCommentById(commentId));

        if(userService.isLoggedInUserMatching(commentInDb.getUser().getId())){
            logger.warn("Unauthorized attempt to edit another user's comment.");
            throw new AccessDeniedException("You can only edit comments that you have created.");
        }

        try {
            Comment updatedComment = commentRepository.findById(commentId).map(comment -> {
                comment.setContent(commentUpdateDto.getContent());
                Comment savedComment = commentRepository.save(comment);
                logger.info("Comment with ID {} updated successfully",commentId);
                return savedComment;
            }).orElseThrow(() -> {
                logger.warn("Comment with ID {} not found, Delete comment operation not performed",commentId);
                return new ResourceNotFoundException("Comment","ID",String.valueOf(commentId),"Delete Comment operation not performed");
            });
            return commentMapper.toCommentGetDto(updatedComment);
        } catch (Exception exception){
            logger.error("Error occurred while updating comment, Error: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    // @Transactional Annotation in Java programming language is mainly used to manage transactions at the method level.
    // When you annotate a method with this annotation, Spring automatically creates a transaction for that method,
    // and Spring handles transactions such as starting, committing, or canceling the transaction.
    // A transaction in programming and database language means a unit of work that includes one or more database operations that are executed atomically,
    // meaning that if any of these operations fail, all their changes are rolled back. (Rollback), and if all are executed successfully, the changes become final and permanent (Commit).
    // Transaction features:
    // Atomicity: All operations in a transaction are executed atomically, meaning all or none of them are executed.
    // Durability: After the successful execution of the transaction and applying the changes, the changes must be permanently recorded in the database and not be lost.
    // Isolation: The operation of a transaction should be independent , from other transactions and not affect them.
    // Commit: If all operations are successful, the transaction must be committed and the changes will be final and permanent.
    // Rollback: If a problem occurs, the transaction must be canceled and the changes returned to the previous state.
    // In the service layer (Service Layer): This annotation is mostly used in services that are related to the business logic of the program.

}
