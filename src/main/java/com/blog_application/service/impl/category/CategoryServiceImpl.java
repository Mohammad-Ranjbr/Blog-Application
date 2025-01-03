package com.blog_application.service.impl.category;

import com.blog_application.config.mapper.category.CategoryMapper;
import com.blog_application.dto.category.CategoryBasicInfoDto;
import com.blog_application.dto.category.CategoryCreateDto;
import com.blog_application.dto.category.CategoryGetDto;
import com.blog_application.dto.category.CategoryUpdateDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.category.Category;
import com.blog_application.repository.category.CategoryRepository;
import com.blog_application.repository.post.PostRepository;
import com.blog_application.service.category.CategoryService;
import com.blog_application.util.responses.PaginatedResponse;
import com.blog_application.util.utils.SortHelper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final PostRepository postRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,CategoryMapper categoryMapper, PostRepository postRepository){
        this.postRepository = postRepository;
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
    }
    @Override
    @Transactional
    public CategoryGetDto createCategory(CategoryCreateDto categoryCreateDto) {
        logger.info("Creating category with title : {}",categoryCreateDto.getTitle());
        Category category = categoryMapper.toEntity(categoryCreateDto);
        try {
            Category savedCategory = categoryRepository.save(category);
            logger.info("Category created successfully with title : {}",savedCategory.getTitle());
            return categoryMapper.toCategoryGetDto(savedCategory);
        } catch (Exception exception){
            logger.error("Error occurred while creating category, Error: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public CategoryBasicInfoDto getCategoryBasicInfoById(Long categoryId) {
        logger.info("Fetching category with ID: {}", categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            logger.warn("Category with ID {} not found, Get category basic info operation not performed",categoryId);
            return new ResourceNotFoundException("Category","ID",String.valueOf(categoryId),"Get Category basic info operation not performed");
        });
        logger.info("Category found with ID : {}",categoryId);
        CategoryBasicInfoDto categoryBasicInfoDto = categoryMapper.toCategoryBasicInfoDto(category);
        logger.info("CategoryBasicInfo created: {}", categoryBasicInfoDto);
        return categoryBasicInfoDto;
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        logger.info("Fetching category with ID : {}",categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            logger.warn("Category with ID {} not found, Get category operation not performed",categoryId);
            return new ResourceNotFoundException("Category","ID",String.valueOf(categoryId),"Get Category operation not performed");
        });
        logger.info("Category found with ID : {}",categoryId);
        return category;
    }

    @Override
    public CategoryGetDto fetchCategoryById(Long categoryId) {
        logger.info("Fetching category with ID : {}",categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            logger.warn("Category with ID {} not found, Get category operation not performed",categoryId);
            return new ResourceNotFoundException("Category","ID",String.valueOf(categoryId),"Get Category operation not performed");
        });
        logger.info("Category found with ID : {}",categoryId);
        return categoryMapper.toCategoryGetDto(category);
    }

    @Override
    public Category getCategoryByTitle(String title) {
        logger.info("Fetching category with Title : {}",title);
        Category category = categoryRepository.findByTitle(title).orElseThrow(() -> {
            logger.warn("Category with Title {} not found, Get category operation not performed",title);
            return new ResourceNotFoundException("Category","Title",String.valueOf(title),"Get Category operation not performed");
        });
        Optional<Category> optionalCategory = categoryRepository.findByTitle(title);
        Consumer<Category> printCategoryDetails = foundCategory -> System.out.println("Category Found : " + foundCategory);
        optionalCategory.ifPresent(printCategoryDetails);
        logger.info("Category found with ID : {}",title);
        return category;
    }

    @Override
    public PaginatedResponse<CategoryBasicInfoDto> getAllCategoryBasicInfo(int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching all category basic info");

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        try{
            Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
            Page<Category> categoryPage = categoryRepository.findAll(pageable);

            List<Category> categories = categoryPage.getContent();
            List<CategoryBasicInfoDto> categoryGetDtoList = categories.stream()
                    .map(categoryMapper::toCategoryBasicInfoDto)
                    .toList();

            PaginatedResponse<CategoryBasicInfoDto> paginatedResponse = new PaginatedResponse<>(
                    categoryGetDtoList,categoryPage.getSize(),categoryPage.getNumber(),categoryPage.getTotalPages(),categoryPage.getTotalElements(),categoryPage.isLast());
            logger.info("Total category basic info found : {}",categories.size());
            return paginatedResponse;
        } catch (Exception exception){
            logger.error("Error occurred while get all basic category info, Error: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    @Transactional
    public CategoryGetDto updateCategory(CategoryUpdateDto categoryUpdateDto, Long categoryId) {
        logger.info("Updating category with ID : {}",categoryId);
        try{
            Category updatedCategory = categoryRepository.findById(categoryId).map(category -> {
                category.setTitle(categoryUpdateDto.getTitle());
                category.setDescription(categoryUpdateDto.getDescription());
                Category savedCategory = categoryRepository.save(category);
                logger.info("Category with ID {} updated successfully",categoryId);
                return savedCategory;
            }).orElseThrow(() -> {
                logger.warn("Category with ID {} not found, Update category operation not performed",categoryId);
                return new ResourceNotFoundException("Category","ID",String.valueOf(categoryId),"Update Category not performed");
            });
            return categoryMapper.toCategoryGetDto(updatedCategory);
        } catch (Exception exception){
            logger.error("Error occurred while updating category, Error: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public PaginatedResponse<CategoryGetDto> getAllCategories(int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching all categories");

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        try {
            Page<Category> categoryPage = categoryRepository.findAll(pageable);

            List<Category> categories = categoryPage.getContent();
            List<CategoryGetDto> categoryGetDtoList = categories.stream()
                    .map(categoryMapper::toCategoryGetDto)
                    .toList();

            PaginatedResponse<CategoryGetDto> paginatedResponse = new PaginatedResponse<>(
                    categoryGetDtoList,categoryPage.getSize(),categoryPage.getNumber(),categoryPage.getTotalPages(),categoryPage.getTotalElements(),categoryPage.isLast());
            logger.info("Total categories found : {}",categories.size());
            return paginatedResponse;
        } catch (Exception exception){
            logger.error("Error occurred while get all categories info, Error: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        logger.info("Deleting category with ID : {}",categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            logger.warn("Category with ID {} not found, Delete category operation not performed",categoryId);
            return new ResourceNotFoundException("Category","ID",String.valueOf(categoryId),"Delete Category not performed");
        });
        try {
            Category defaultCategory = getCategoryByTitle("Uncategorized");
            postRepository.updateCategoryForPosts(defaultCategory.getId(), categoryId);
            categoryRepository.delete(category);
            logger.info("Category with ID {} deleted successfully",categoryId);
        } catch (Exception exception){
            logger.error("Error occurred while deleting category. Category ID: {}, Error: {}", categoryId, exception.getMessage(), exception);
            throw exception;
        }
    }

}
