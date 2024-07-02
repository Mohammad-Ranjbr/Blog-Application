package com.blog_application.service.impl;

import com.blog_application.config.mapper.CommentMapper;
import com.blog_application.config.mapper.PostMapper;
import com.blog_application.config.mapper.UserMapper;
import com.blog_application.dto.comment.CommentCreateDto;
import com.blog_application.dto.comment.CommentGetDto;
import com.blog_application.dto.comment.CommentUpdateDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.Comment;
import com.blog_application.model.Post;
import com.blog_application.model.User;
import com.blog_application.repository.CommentRepository;
import com.blog_application.service.CommentService;
import com.blog_application.service.PostService;
import com.blog_application.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class CommentServiceImpl implements CommentService {

    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final UserService userService;
    private final PostService postService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final static Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    public CommentServiceImpl(CommentMapper commentMapper,PostService postService,UserMapper userMapper,
                              CommentRepository commentRepository,PostMapper postMapper,UserService userService){
        this.userMapper = userMapper;
        this.postMapper = postMapper;
        this.userService = userService;
        this.postService = postService;
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
    }

    @Override
    public CommentGetDto createComment(CommentCreateDto commentCreateDto, Long postId, UUID userId) {
        logger.info("Creating comment with content : {}",commentCreateDto.getContent());
        Post post = postMapper.toEntity(postService.getPostById(postId));
        User user = userMapper.toEntity(userService.getUserById(userId));
        Comment comment = commentMapper.toEntity(commentCreateDto);
        comment.setPost(post);
        comment.setUser(user);
        if(commentCreateDto.getParent() != null){
            Comment parentComment = commentRepository.findById(commentCreateDto.getParent()).orElseThrow(() ->{
                logger.warn("Parent comment with ID {} not found",commentCreateDto.getParent());
                return new ResourceNotFoundException("Comment","ID",String.valueOf(commentCreateDto.getParent()),"Parent comment not found");
            });
            comment.setParent(parentComment);
        }
        Comment savedComment = commentRepository.save(comment);
        logger.info("Comment created successfully with content : {}",commentCreateDto.getContent());
        return commentMapper.toCommentGetDto(savedComment);
    }

    @Override
    public void deleteComment(Long commentId) {
        logger.info("Deleting comment with ID : {}",commentId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            logger.warn("Comment with ID {} not found, Delete comment operation not performed",commentId);
            return new ResourceNotFoundException("Comment","ID",String.valueOf(commentId),"Delete Comment operation not performed");
        });
        commentRepository.delete(comment);
        logger.info("Comment with ID : {} deleted successfully",commentId);
    }

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
    public CommentGetDto updateComment(CommentUpdateDto commentUpdateDto, Long commentId) {
        logger.info("Updating comment with ID : {}",commentId);
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
    }

}
