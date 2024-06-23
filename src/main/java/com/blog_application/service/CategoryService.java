package com.blog_application.service;

import com.blog_application.dto.category.CategoryBasicInfoDto;
import com.blog_application.dto.category.CategoryCreateDto;
import com.blog_application.dto.category.CategoryGetDto;
import com.blog_application.dto.category.CategoryUpdateDto;
import com.blog_application.util.responses.PaginatedResponse;

import java.util.List;

public interface CategoryService {

    void deleteCategory(Long categoryId);
    CategoryGetDto getCategoryById(Long categoryId);
    List<CategoryBasicInfoDto> getAllCategoryBasicInfo();
    CategoryGetDto createCategory(CategoryCreateDto categoryDto);
    CategoryBasicInfoDto getCategoryBasicInfoById(Long categoryId);
    CategoryGetDto updateCategory(CategoryUpdateDto categoryUpdateDto, Long categoryId);
    PaginatedResponse<CategoryGetDto> getAllCategories(int pageNumber, int pageSize, String sortBy, String sortDir);

}
