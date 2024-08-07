package com.blog_application.config.mapper.post;

import com.blog_application.dto.post.PostCreateDto;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.model.post.Post;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    private final ModelMapper modelMapper;

    //By default, ModelMapper map object with attribute`s name
    @Autowired
    public PostMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public Post toEntity(PostCreateDto postCreateDto){
        return modelMapper.map(postCreateDto,Post.class);
    }

    public Post toEntity(PostGetDto postGetDto){
        return modelMapper.map(postGetDto,Post.class);
    }

    public PostGetDto toPostGetDto(Post post){
        return modelMapper.map(post,PostGetDto.class);
    }

}
