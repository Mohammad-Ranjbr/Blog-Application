package com.blog_application.service.impl;

import com.blog_application.dto.CategoryDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.Category;
import com.blog_application.repository.CategoryRepository;
import com.blog_application.service.CategoryService;
import org.modelmapper.ModelMapper;
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

    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,ModelMapper modelMapper){
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
    }
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        logger.info("Creating category : {}",categoryDto.getTitle());
        Category category = this.categoryDtoToCategory(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        logger.info("Category created successfully : {}",savedCategory.getTitle());
        return this.categoryToCategoryDto(savedCategory);
    }

    @Override
    public CategoryDto getCategoryById(Long category_id) {
        logger.info("Fetching category with ID : {}",category_id);
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> {
            logger.warn("Category with ID {} not found, get category not performed",category_id);
            return new ResourceNotFoundException("Category","ID",String.valueOf(category_id),"Get Category not performed");
        });
        Optional<Category> optionalCategory = categoryRepository.findById(category_id);
        Consumer<Category> printCategoryDetails = foundCategory -> System.out.println("Category Found : " + foundCategory);
        optionalCategory.ifPresent(printCategoryDetails);
        logger.info("Category found with ID : {}",category_id);
        return this.categoryToCategoryDto(category);
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
            logger.warn("Category with ID {} not found, update category not performed",category_id);
            return new ResourceNotFoundException("Category","ID",String.valueOf(category_id),"Update Category not performed");
        });
        return this.categoryToCategoryDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        logger.info("Fetching all categories");
        List<Category> categories = categoryRepository.findAll();
        logger.info("Total categories found : {}",categories.size());
        return categories.stream().map(this::categoryToCategoryDto).collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(Long category_id) {
        logger.info("Deleting category with ID : {}",category_id);
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> {
            logger.warn("Category with ID {} not found, delete category not performed",category_id);
            return new ResourceNotFoundException("Category","ID",String.valueOf(category_id),"Delete Category not performed");
        });
        logger.info("Category with ID {} deleted successfully",category_id);
        categoryRepository.delete(category);
    }

    @Override
    public Category categoryDtoToCategory(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto,Category.class);
    }

    @Override
    public CategoryDto categoryToCategoryDto(Category category) {
        return modelMapper.map(category,CategoryDto.class);
    }

}
