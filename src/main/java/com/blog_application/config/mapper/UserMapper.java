package com.blog_application.config.mapper;

import com.blog_application.dto.user.UserBasicInfoDto;
import com.blog_application.dto.user.UserCreateDto;
import com.blog_application.dto.user.UserGetDto;
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

    public User toEntity(UserCreateDto userCreateDto){
        return modelMapper.map(userCreateDto,User.class);
    }

    public User toEntity(UserGetDto userGetDto){
        return modelMapper.map(userGetDto,User.class);
    }

    public UserGetDto toUserGetDto(User user){
        return modelMapper.map(user,UserGetDto.class);
    }

    public UserBasicInfoDto toUserBasicInfoDto(User user){
        return modelMapper.map(user,UserBasicInfoDto.class);
    }

}
