package com.blog_application.service.impl;

import com.blog_application.dto.CategoryDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.Category;
import com.blog_application.repository.CategoryRepository;
import com.blog_application.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,ModelMapper modelMapper){
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = this.categoryDtoToCategory(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return this.categoryToCategoryDto(savedCategory);
    }

    @Override
    public CategoryDto getCategoryById(Long category_id) {
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> new ResourceNotFoundException("Category","ID",String.valueOf(category_id),"Get Category not performed"));
        Optional<Category> optionalCategory = categoryRepository.findById(category_id);
        Consumer<Category> printCategoryDetails = foundCategory -> System.out.println("Category Found : " + foundCategory);
        optionalCategory.ifPresent(printCategoryDetails);
        return this.categoryToCategoryDto(category);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long category_id) {
        Category updatedCategory = categoryRepository.findById(category_id).map(category -> {
            category.setTitle(categoryDto.getTitle());
            category.setDescription(categoryDto.getDescription());
            Category savedCategory = categoryRepository.save(category);
            return savedCategory;
        }).orElseThrow(() -> {
                return new ResourceNotFoundException("Category","ID",String.valueOf(category_id),"Update Category not performed");
        });
        return this.categoryToCategoryDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(this::categoryToCategoryDto).collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(Long category_id) {
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> new ResourceNotFoundException("Category","ID",String.valueOf(category_id),"Delete Category not performed"));
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
