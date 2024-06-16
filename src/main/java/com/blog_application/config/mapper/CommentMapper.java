package com.blog_application.config.mapper;

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
        modelMapper.createTypeMap(Comment.class,CommentDto.class)
                .addMapping(Comment::getUser,CommentDto::setUserDto);
    }

    public Comment toEntity(CommentDto commentDto){
        return modelMapper.map(commentDto,Comment.class);
    }

    public CommentDto toDto(Comment comment){
        return  modelMapper.map(comment,CommentDto.class);
    }

}
