package com.blog_application.config.mapper;

import com.blog_application.dto.post.PostDto;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.model.Post;
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
        modelMapper.createTypeMap(Post.class, PostDto.class)
                .addMapping(Post::getCategory,PostDto::setCategoryDto)
                .addMapping(Post::getUser,PostDto::setUserDto)
                .addMapping(Post::getComments,PostDto::setCommentDtos);
    }
    public PostDto toDto(Post post){
        return modelMapper.map(post,PostDto.class);
    }

    public Post toEntity(PostDto postDto){
        return modelMapper.map(postDto,Post.class);
    }

    public Post toEntity(PostGetDto postGetDto){
        return modelMapper.map(postGetDto,Post.class);
    }

    public PostGetDto toPostGetDto(Post post){
        return modelMapper.map(post,PostGetDto.class);
    }

}
