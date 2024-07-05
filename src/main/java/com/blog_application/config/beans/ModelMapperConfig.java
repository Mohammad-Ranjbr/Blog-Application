package com.blog_application.config.beans;

import com.blog_application.config.mapper.comment.CommentToCommentGetDtoMap;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper getModelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new CommentToCommentGetDtoMap());
        return modelMapper;
    }

}
