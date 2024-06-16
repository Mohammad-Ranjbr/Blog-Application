package com.blog_application.service.impl;

import com.blog_application.config.mapper.CommentMapper;
import com.blog_application.config.mapper.PostMapper;
import com.blog_application.config.mapper.UserMapper;
import com.blog_application.dto.CommentDto;
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
    public CommentDto createComment(CommentDto commentDto, Long post_id,Long user_id) {
        logger.info("Creating comment with content : {}",commentDto.getContent());
        Post post = postMapper.toEntity(postService.getPostById(post_id));
        User user = userMapper.toEntity(userService.getUserById(user_id));
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);
        comment.setUser(user);
        Comment savedComment = commentRepository.save(comment);
        logger.info("Comment created successfully with content : {}",commentDto.getContent());
        return commentMapper.toDto(savedComment);
    }

    @Override
    public void deleteComment(Long comment_id) {
        logger.info("Deleting comment with ID : {}",comment_id);
        Comment comment = commentRepository.findById(comment_id).orElseThrow(() -> {
            logger.warn("Comment with ID {} not found, Delete comment operation not performed",comment_id);
            return new ResourceNotFoundException("Comment","ID",String.valueOf(comment_id),"Delete Comment operation not performed");
        });
        commentRepository.delete(comment);
        logger.info("Comment with ID : {} deleted successfully",comment_id);
    }

}
