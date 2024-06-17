package com.blog_application.config.mapper;

import com.blog_application.dto.category.CategoryCreateDto;
import com.blog_application.dto.category.CategoryGetDto;
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


    public Category toEntity(CategoryGetDto categoryGetDto){
        return modelMapper.map(categoryGetDto,Category.class);
    }

    public Category toEntity(CategoryCreateDto categoryCreateDto){
        return modelMapper.map(categoryCreateDto,Category.class);
    }

    public CategoryGetDto toCategoryGetDto(Category category){
        return modelMapper.map(category,CategoryGetDto.class);
    }

}
