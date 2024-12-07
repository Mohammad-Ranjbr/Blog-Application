package com.blog_application.controller;

import com.blog_application.dto.tag.TagBasicInfoDto;
import com.blog_application.dto.tag.TagCreateDto;
import com.blog_application.dto.tag.TagGetDto;
import com.blog_application.dto.tag.TagUpdateDto;
import com.blog_application.service.tag.TagService;
import com.blog_application.util.constants.ApplicationConstants;
import com.blog_application.util.responses.ApiResponse;
import com.blog_application.util.responses.PaginatedResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;
    private final static Logger logger = LoggerFactory.getLogger(TagController.class);

    @Autowired
    public TagController(TagService tagService){
        this.tagService = tagService;
    }

    //POST Mapping-Create Tag
    @PostMapping("/")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<TagGetDto> createTag(@Valid @RequestBody TagCreateDto tagCreateDto){
        logger.info("Received request to create tag with name : {}",tagCreateDto.getName());
        TagGetDto createdTag = tagService.createTag(tagCreateDto);
        logger.info("Returning response for tag creation with name : {}",tagCreateDto.getName());
        return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
    }

    //GET Mapping-Get Tag Basic Info By ID
    @GetMapping("/basic-info/{id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<TagBasicInfoDto> getTagBasicInfoById(@PathVariable("id") Long tagId){
        logger.info("Received request to get tag basic info with ID : {}",tagId);
        TagBasicInfoDto tagBasicInfoDto = tagService.getTagBasicInfoById(tagId);
        logger.info("Returning response for get tag basic info with ID : {}",tagId);
        return new ResponseEntity<>(tagBasicInfoDto,HttpStatus.OK);
    }

    //GET Mapping-Get Tag By ID
    @GetMapping("/id/{id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<TagGetDto> getTagById(@PathVariable("id") Long tagId){
        logger.info("Received request to get tag with ID : {}",tagId);
        TagGetDto tagGetDto = tagService.getTagById(tagId);
        logger.info("Returning response for get tag with ID : {}",tagId);
        return new ResponseEntity<>(tagGetDto,HttpStatus.OK);
    }

    //PUT Mapping-Update Tag
    @PutMapping("/{id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<TagGetDto> updateTag(@Valid @RequestBody TagUpdateDto tagUpdateDto, @PathVariable("id") Long tagId){
        logger.info("Received request to update tag with ID : {}",tagId);
        TagGetDto updatedTag = tagService.updateTag(tagUpdateDto,tagId);
        logger.info("Returning response for update tag with ID : {}",tagId);
        return new ResponseEntity<>(updatedTag,HttpStatus.OK);
    }

    //DELETE Mapping-Delete Tag
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<ApiResponse> deleteTag(@PathVariable("id") Long tagId){
        logger.info("Received request to delete tag with ID : {}",tagId);
        tagService.deleteTag(tagId);
        logger.info("Returning response for delete tag with ID : {}",tagId);
        return new ResponseEntity<>(new ApiResponse("Tag deleted successfully",true),HttpStatus.OK);
    }

    //GET Mapping-Get All Categories
    @GetMapping("/")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<PaginatedResponse<TagGetDto>> getAllTags(
            @RequestParam(value = ApplicationConstants.PAGE_NUMBER,defaultValue = ApplicationConstants.DEFAULT_PAGE_NUMBER,required = false) int pageNumber,
            @RequestParam(value = ApplicationConstants.PAGE_SIZE,defaultValue = ApplicationConstants.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = ApplicationConstants.SORT_BY,defaultValue = ApplicationConstants.DEFAULT_TAG_SORT_BY,required = false) String sortBy,
            @RequestParam(value = ApplicationConstants.SORT_DIR,defaultValue = ApplicationConstants.DEFAULT_SORT_DIR,required = false) String sortDir){

        logger.info("Received request to get all tags");
        PaginatedResponse<TagGetDto> tags = tagService.getAllTags(pageNumber,pageSize,sortBy,sortDir);
        logger.info("Returning response all tags");
        return new ResponseEntity<>(tags,HttpStatus.OK);
    }

    //GET Mapping-Get All Tag Basic Info
    @GetMapping ("/basic-info/")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<PaginatedResponse<TagBasicInfoDto>> getAllBasicInfo(
            @RequestParam(value = ApplicationConstants.PAGE_NUMBER,defaultValue = ApplicationConstants.DEFAULT_PAGE_NUMBER,required = false) int pageNumber,
            @RequestParam(value = ApplicationConstants.PAGE_SIZE,defaultValue = ApplicationConstants.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = ApplicationConstants.SORT_BY,defaultValue = ApplicationConstants.DEFAULT_TAG_SORT_BY,required = false) String sortBy,
            @RequestParam(value = ApplicationConstants.SORT_DIR,defaultValue = ApplicationConstants.DEFAULT_SORT_DIR,required = false) String sortDir
    ){

        logger.info("Received request to get all tag basic info");
        PaginatedResponse<TagBasicInfoDto> tagBasicInfoDtos = tagService.getAllTagBasicInfo(pageNumber,pageSize,sortBy,sortDir);
        logger.info("Returning response all tag basic info");
        return new ResponseEntity<>(tagBasicInfoDtos,HttpStatus.OK);
    }

    //GET Mapping-Get Tag By Name
    @GetMapping("/name/{name}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<TagGetDto> getTagByName(@PathVariable("name") String tagName){
        logger.info("Received request to get tag with Name : {}",tagName);
        TagGetDto tagGetDto = tagService.getTagByName(tagName);
        logger.info("Returning response for get tag with Name : {}",tagName);
        return new ResponseEntity<>(tagGetDto,HttpStatus.OK);
    }

}
