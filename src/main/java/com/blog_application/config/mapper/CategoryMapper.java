package com.blog_application.config.mapper;

import com.blog_application.dto.CategoryDto;
import com.blog_application.model.Category;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public CategoryMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public CategoryDto toDto(Category category){
        return modelMapper.map(category,CategoryDto.class);
    }

    public Category toEntity(CategoryDto categoryDto){
        return modelMapper.map(categoryDto,Category.class);
    }

}
