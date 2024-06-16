package com.blog_application.config.mapper;

import com.blog_application.dto.user.UserDto;
import com.blog_application.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public UserMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public UserDto toDto(User user){
        return modelMapper.map(user,UserDto.class);
    }

    public User toEntity(UserDto userDto){
        return modelMapper.map(userDto,User.class);
    }

}
