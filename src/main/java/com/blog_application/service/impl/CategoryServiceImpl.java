package com.blog_application.service.impl;

import com.blog_application.config.mapper.CategoryMapper;
import com.blog_application.dto.category.CategoryCreateDto;
import com.blog_application.dto.category.CategoryDto;
import com.blog_application.dto.category.CategoryGetDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.Category;
import com.blog_application.repository.CategoryRepository;
import com.blog_application.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,CategoryMapper categoryMapper){
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
    }
    @Override
    public CategoryGetDto createCategory(CategoryCreateDto categoryCreateDto) {
        logger.info("Creating category with title : {}",categoryCreateDto.getTitle());
        Category category = categoryMapper.toEntity(categoryCreateDto);
        Category savedCategory = categoryRepository.save(category);
        logger.info("Category created successfully with title : {}",savedCategory.getTitle());
        return categoryMapper.toCategoryGetDto(savedCategory);
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        logger.info("Fetching category with ID : {}",categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            logger.warn("Category with ID {} not found, Get category operation not performed",categoryId);
            return new ResourceNotFoundException("Category","ID",String.valueOf(categoryId),"Get Category not performed");
        });
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        Consumer<Category> printCategoryDetails = foundCategory -> System.out.println("Category Found : " + foundCategory);
        optionalCategory.ifPresent(printCategoryDetails);
        logger.info("Category found with ID : {}",categoryId);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        logger.info("Updating category with ID : {}",categoryId);
        Category updatedCategory = categoryRepository.findById(categoryId).map(category -> {
            category.setTitle(categoryDto.getTitle());
            category.setDescription(categoryDto.getDescription());
            Category savedCategory = categoryRepository.save(category);
            logger.info("Category with ID {} updated successfully",categoryId);
            return savedCategory;
        }).orElseThrow(() -> {
            logger.warn("Category with ID {} not found, Update category operation not performed",categoryId);
            return new ResourceNotFoundException("Category","ID",String.valueOf(categoryId),"Update Category not performed");
        });
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    public List<CategoryGetDto> getAllCategories() {
        logger.info("Fetching all categories");
        List<Category> categories = categoryRepository.findAll();
        logger.info("Total categories found : {}",categories.size());
        return categories.stream()
                .map(categoryMapper::toCategoryGetDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(Long categoryId) {
        logger.info("Deleting category with ID : {}",categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            logger.warn("Category with ID {} not found, Delete category operation not performed",categoryId);
            return new ResourceNotFoundException("Category","ID",String.valueOf(categoryId),"Delete Category not performed");
        });
        logger.info("Category with ID {} deleted successfully",categoryId);
        categoryRepository.delete(category);
    }

}
