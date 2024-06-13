package com.blog_application.service.impl;

import com.blog_application.config.CommentMapper;
import com.blog_application.dto.CommentDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.Comment;
import com.blog_application.model.Post;
import com.blog_application.repository.CommentRepository;
import com.blog_application.repository.PostRepository;
import com.blog_application.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentMapper commentMapper,PostRepository postRepository,CommentRepository commentRepository){
        this.commentMapper = commentMapper;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long post_id) {
        Post post = postRepository.findById(post_id).orElseThrow(() -> new ResourceNotFoundException("Post","ID",String.valueOf(post_id),"Get Post not performed"));
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    public void deleteComment(Long comment_id) {
        Comment comment = commentRepository.findById(comment_id).orElseThrow(() -> new ResourceNotFoundException("Comment","ID",String.valueOf(comment_id),"Get Comment not performed"));
        commentRepository.delete(comment);
    }

}
