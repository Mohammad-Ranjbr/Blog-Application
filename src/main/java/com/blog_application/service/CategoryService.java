package com.blog_application.service;

import com.blog_application.dto.category.CategoryCreateDto;
import com.blog_application.dto.category.CategoryGetDto;
import com.blog_application.dto.category.CategoryUpdateDto;

import java.util.List;

public interface CategoryService {

    List<CategoryGetDto> getAllCategories();
    void deleteCategory(Long categoryId);
    CategoryGetDto getCategoryById(Long categoryId);
    CategoryGetDto createCategory(CategoryCreateDto categoryDto);
    CategoryGetDto updateCategory(CategoryUpdateDto categoryUpdateDto, Long categoryId);

}
