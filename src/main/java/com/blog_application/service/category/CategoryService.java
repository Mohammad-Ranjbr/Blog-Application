package com.blog_application.service.category;

import com.blog_application.dto.category.CategoryBasicInfoDto;
import com.blog_application.dto.category.CategoryCreateDto;
import com.blog_application.dto.category.CategoryGetDto;
import com.blog_application.dto.category.CategoryUpdateDto;
import com.blog_application.model.category.Category;
import com.blog_application.util.responses.PaginatedResponse;

public interface CategoryService {

    void deleteCategory(Long categoryId);
    Category getCategoryByTitle(String title);
    Category getCategoryById(Long categoryId);
    CategoryGetDto fetchCategoryById(Long categoryId);
    CategoryGetDto createCategory(CategoryCreateDto categoryDto);
    CategoryBasicInfoDto getCategoryBasicInfoById(Long categoryId);
    CategoryGetDto updateCategory(CategoryUpdateDto categoryUpdateDto, Long categoryId);
    PaginatedResponse<CategoryGetDto> getAllCategories(int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginatedResponse<CategoryBasicInfoDto> getAllCategoryBasicInfo(int pageNumber, int pageSize, String sortBy, String sortDir);

}
