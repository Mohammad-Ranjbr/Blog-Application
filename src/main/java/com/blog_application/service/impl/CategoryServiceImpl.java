package com.blog_application.service.impl;

import com.blog_application.config.mapper.CategoryMapper;
import com.blog_application.dto.CategoryDto;
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
    public CategoryDto createCategory(CategoryDto categoryDto) {
        logger.info("Creating category with title : {}",categoryDto.getTitle());
        Category category = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        logger.info("Category created successfully with title : {}",savedCategory.getTitle());
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public CategoryDto getCategoryById(Long category_id) {
        logger.info("Fetching category with ID : {}",category_id);
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> {
            logger.warn("Category with ID {} not found, Get category operation not performed",category_id);
            return new ResourceNotFoundException("Category","ID",String.valueOf(category_id),"Get Category not performed");
        });
        Optional<Category> optionalCategory = categoryRepository.findById(category_id);
        Consumer<Category> printCategoryDetails = foundCategory -> System.out.println("Category Found : " + foundCategory);
        optionalCategory.ifPresent(printCategoryDetails);
        logger.info("Category found with ID : {}",category_id);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long category_id) {
        logger.info("Updating category with ID : {}",category_id);
        Category updatedCategory = categoryRepository.findById(category_id).map(category -> {
            category.setTitle(categoryDto.getTitle());
            category.setDescription(categoryDto.getDescription());
            Category savedCategory = categoryRepository.save(category);
            logger.info("Category with ID {} updated successfully",category_id);
            return savedCategory;
        }).orElseThrow(() -> {
            logger.warn("Category with ID {} not found, Update category operation not performed",category_id);
            return new ResourceNotFoundException("Category","ID",String.valueOf(category_id),"Update Category not performed");
        });
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        logger.info("Fetching all categories");
        List<Category> categories = categoryRepository.findAll();
        logger.info("Total categories found : {}",categories.size());
        return categories.stream().map(categoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(Long category_id) {
        logger.info("Deleting category with ID : {}",category_id);
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> {
            logger.warn("Category with ID {} not found, Delete category operation not performed",category_id);
            return new ResourceNotFoundException("Category","ID",String.valueOf(category_id),"Delete Category not performed");
        });
        logger.info("Category with ID {} deleted successfully",category_id);
        categoryRepository.delete(category);
    }

}
