package com.blog_application.config;

import com.blog_application.controller.CommentController;
import com.blog_application.dto.CommentDto;
import com.blog_application.model.Comment;
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

    public Comment toEntity(CommentDto commentDto){
        return modelMapper.map(commentDto,Comment.class);
    }

    public CommentDto toDto(Comment comment){
        return  modelMapper.map(comment,CommentDto.class);
    }

}
