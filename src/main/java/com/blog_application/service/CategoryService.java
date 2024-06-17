package com.blog_application.service;

import com.blog_application.dto.category.CategoryDto;
import com.blog_application.dto.category.CategoryGetDto;

import java.util.List;

public interface CategoryService {

    List<CategoryGetDto> getAllCategories();
    void deleteCategory(Long categoryId);
    CategoryDto getCategoryById(Long categoryId);
    CategoryDto createCategory(CategoryDto categoryDto);
    CategoryDto updateCategory(CategoryDto categoryDto,Long categoryId);

}
