package com.blog_application.controller;

import com.blog_application.dto.category.CategoryBasicInfoDto;
import com.blog_application.dto.category.CategoryCreateDto;
import com.blog_application.dto.category.CategoryGetDto;
import com.blog_application.dto.category.CategoryUpdateDto;
import com.blog_application.service.CategoryService;
import com.blog_application.util.responses.ApiResponse;
import com.blog_application.util.constants.ApplicationConstants;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
    public ResponseEntity<CategoryGetDto> getCategoryById(@PathVariable("id") Long categoryId){
        logger.info("Received request to get category with ID : {}",categoryId);
        CategoryGetDto categoryGetDto = categoryService.getCategoryById(categoryId);
        logger.info("Returning response for get category with ID : {}",categoryId);
        return new ResponseEntity<>(categoryGetDto,HttpStatus.OK);
    }

    //PUT Mapping-Update Category
    @PutMapping("/{id}")
    public ResponseEntity<CategoryGetDto> updateCategory(@Valid @RequestBody CategoryUpdateDto categoryUpdateDto, @PathVariable("id") Long categoryId){
        logger.info("Received request to update category with ID : {}",categoryId);
        CategoryGetDto updatedCategory = categoryService.updateCategory(categoryUpdateDto,categoryId);
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

    //GET Mapping-Get Category Basic Info By ID
    @GetMapping("/basic-info/{id}")
    public ResponseEntity<CategoryBasicInfoDto> getCategoryBasicInfo(@PathVariable("id") Long categoryId){
        logger.info("Received request to get category basic info with ID : {}",categoryId);
        CategoryBasicInfoDto categoryBasicInfoDto = categoryService.getCategoryBasicInfoById(categoryId);
        logger.info("Returning response for get category basic info with ID : {}",categoryId);
        return new ResponseEntity<>(categoryBasicInfoDto,HttpStatus.OK);
    }

    //GET Mapping-Get All Category Basic Info
    @GetMapping("/basic-info/")
    public ResponseEntity<List<CategoryBasicInfoDto>> getAllBasicInfo(){
        logger.info("Received request to get all category basic info");
        List<CategoryBasicInfoDto> categoryBasicInfoDtos = categoryService.getAllCategoryBasicInfo();
        logger.info("Returning response with {} category basic info",categoryBasicInfoDtos.size());
        return new ResponseEntity<>(categoryBasicInfoDtos,HttpStatus.OK);
    }

    //OPTIONS Mapping for all categories
    @RequestMapping(value = "/",method = RequestMethod.OPTIONS)
    public ResponseEntity<?> optionsForAllCategories(){
        logger.info("Received OPTIONS request for all categories");
        HttpHeaders headers = new HttpHeaders();
        headers.add(ApplicationConstants.HEADER_ALLOW,"GET,POST,OPTIONS");
        logger.info("Returning response with allowed methods for all categories");
        return new ResponseEntity<>(headers,HttpStatus.OK);
    }

    //OPTIONS Mapping fo single category
    @RequestMapping(value = "/{id}",method = RequestMethod.OPTIONS)
    public ResponseEntity<?> optionsForSingleCategory(@PathVariable("id") Long categoryId){
        logger.info("Received OPTIONS request for category with ID : {}", categoryId);
        ResponseEntity<?> response = ResponseEntity.ok()
                .allow(HttpMethod.GET,HttpMethod.PUT,HttpMethod.DELETE,HttpMethod.OPTIONS)
                .build();
        logger.info("Returning response with allowed methods for category with ID : {}", categoryId);
        return response;
    }

}
