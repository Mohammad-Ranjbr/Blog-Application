package com.blog_application.controller;

import com.blog_application.dto.CategoryDto;
import com.blog_application.service.CategoryService;
import com.blog_application.util.ApiResponse;
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
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto){
        logger.info("Received request to create category : {}",categoryDto.getTitle());
        CategoryDto createdCategory = categoryService.createCategory(categoryDto);
        logger.info("Returning response for category creation : {}",categoryDto.getTitle());
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    //GET Mapping-Get Category By ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable("id") Long category_id){
        logger.info("Received request to get user with ID : {}",category_id);
        CategoryDto categoryDto = categoryService.getCategoryById(category_id);
        logger.info("Returning response for category with ID : {}",category_id);
        return new ResponseEntity<>(categoryDto,HttpStatus.OK);
    }

    //PUT Mapping-Update Category
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@RequestBody CategoryDto categoryDto,@PathVariable("id") Long category_id){
        logger.info("Received request to update category with ID : {}",category_id);
        CategoryDto updatedCategory = categoryService.updateCategory(categoryDto,category_id);
        logger.info("Returning response for update category with ID : {}",category_id);
        return new ResponseEntity<>(updatedCategory,HttpStatus.OK);
    }

    //DELETE Mapping-Delete Category
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable("id") Long category_id){
        logger.info("Received request to delete category with ID : {}",category_id);
        categoryService.deleteCategory(category_id);
        logger.info("Returning response for delete category with ID : {}",category_id);
        return new ResponseEntity<>(new ApiResponse("Category deleted successfully",true),HttpStatus.OK);
    }

    //GET Mapping-Get All Categories
    @GetMapping("/")
    public ResponseEntity<List<CategoryDto>> getAllCategories(){
        logger.info("Received request to get all categories");
        List<CategoryDto> categories = categoryService.getAllCategories();
        logger.info("Returning response with {} categories",categories.size());
        return new ResponseEntity<>(categories,HttpStatus.OK);
    }

}
