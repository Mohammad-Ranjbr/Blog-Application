package com.blog_application.controller;

import com.blog_application.dto.category.CategoryCreateDto;
import com.blog_application.dto.category.CategoryDto;
import com.blog_application.dto.category.CategoryGetDto;
import com.blog_application.service.CategoryService;
import com.blog_application.util.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    //POST Mapping-Create Category
    @PostMapping("/")
    public ResponseEntity<CategoryGetDto> createCategory(@Valid @RequestBody CategoryCreateDto categoryCreateDto){
        logger.info("Received request to create category with title : {}",categoryCreateDto.getTitle());
        CategoryGetDto createdCategory = categoryService.createCategory(categoryCreateDto);
        logger.info("Returning response for category creation with title : {}",categoryCreateDto.getTitle());
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    //GET Mapping-Get Category By ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable("id") Long categoryId){
        logger.info("Received request to get category with ID : {}",categoryId);
        CategoryDto categoryDto = categoryService.getCategoryById(categoryId);
        logger.info("Returning response for get category with ID : {}",categoryId);
        return new ResponseEntity<>(categoryDto,HttpStatus.OK);
    }

    //PUT Mapping-Update Category
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto,@PathVariable("id") Long categoryId){
        logger.info("Received request to update category with ID : {}",categoryId);
        CategoryDto updatedCategory = categoryService.updateCategory(categoryDto,categoryId);
        logger.info("Returning response for update category with ID : {}",categoryId);
        return new ResponseEntity<>(updatedCategory,HttpStatus.OK);
    }

    //DELETE Mapping-Delete Category
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable("id") Long categoryId){
        logger.info("Received request to delete category with ID : {}",categoryId);
        categoryService.deleteCategory(categoryId);
        logger.info("Returning response for delete category with ID : {}",categoryId);
        return new ResponseEntity<>(new ApiResponse("Category deleted successfully",true),HttpStatus.OK);
    }

    //GET Mapping-Get All Categories
    @GetMapping("/")
    public ResponseEntity<List<CategoryGetDto>> getAllCategories(){
        logger.info("Received request to get all categories");
        List<CategoryGetDto> categories = categoryService.getAllCategories();
        logger.info("Returning response with {} categories",categories.size());
        return new ResponseEntity<>(categories,HttpStatus.OK);
    }

}
