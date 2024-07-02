package com.blog_application.config.mapper;

import com.blog_application.dto.comment.CommentCreateDto;
import com.blog_application.dto.comment.CommentGetDto;
import com.blog_application.model.Comment;
import com.blog_application.model.Post;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public CommentMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public Comment toEntity(CommentCreateDto commentCreateDto){
        return modelMapper.map(commentCreateDto,Comment.class);
    }

    public Comment toEntity(CommentGetDto commentGetDto){
        return modelMapper.map(commentGetDto,Comment.class);
    }

    public CommentGetDto toCommentGetDto(Comment comment){
        return modelMapper.map(comment,CommentGetDto.class);
    }

}
