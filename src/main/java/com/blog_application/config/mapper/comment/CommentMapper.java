package com.blog_application.config.mapper.comment;

import com.blog_application.dto.comment.CommentCreateDto;
import com.blog_application.dto.comment.CommentGetDto;
import com.blog_application.model.comment.Comment;
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

    public Comment toEntity(CommentGetDto CommentGetDto){
        return modelMapper.map(CommentGetDto,Comment.class);
    }

    public CommentGetDto toCommentGetDto(Comment comment){
        return modelMapper.map(comment,CommentGetDto.class);
    }

}
