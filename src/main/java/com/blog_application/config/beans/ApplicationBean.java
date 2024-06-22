package com.blog_application.config.beans;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBean {

    @Bean
    public ModelMapper getModelMapper(){
        return new ModelMapper();
    }

}
