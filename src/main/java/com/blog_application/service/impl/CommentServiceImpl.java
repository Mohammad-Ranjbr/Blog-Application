package com.blog_application.service.impl;

import com.blog_application.config.CommentMapper;
import com.blog_application.config.PostMapper;
import com.blog_application.dto.CommentDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.Comment;
import com.blog_application.model.Post;
import com.blog_application.model.User;
import com.blog_application.repository.CommentRepository;
import com.blog_application.service.CommentService;
import com.blog_application.service.PostService;
import com.blog_application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final UserService userService;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final PostMapper postMapper;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentMapper commentMapper,PostService postService,
                              CommentRepository commentRepository,PostMapper postMapper,UserService userService){
        this.userService = userService;
        this.commentMapper = commentMapper;
        this.postMapper = postMapper;
        this.postService = postService;
        this.commentRepository = commentRepository;
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long post_id,Long user_id) {
        Post post = postMapper.toEntity(postService.getPostById(post_id));
        User user = userService.userDtoToUser(userService.getUserById(user_id));
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);
        comment.setUser(user);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    public void deleteComment(Long comment_id) {
        Comment comment = commentRepository.findById(comment_id).orElseThrow(() -> new ResourceNotFoundException("Comment","ID",String.valueOf(comment_id),"Get Comment not performed"));
        commentRepository.delete(comment);
    }

}
