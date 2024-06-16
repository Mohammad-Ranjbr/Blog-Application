package com.blog_application.service;

import com.blog_application.dto.CategoryDto;
import com.blog_application.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);
    CategoryDto getCategoryById(Long category_id);
    CategoryDto updateCategory(CategoryDto categoryDto,Long category_id);
    List<CategoryDto> getAllCategories();
    void deleteCategory(Long category_id);

}
