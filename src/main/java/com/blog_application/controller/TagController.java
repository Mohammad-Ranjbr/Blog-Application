package com.blog_application.controller;

import com.blog_application.dto.tag.TagBasicInfoDto;
import com.blog_application.dto.tag.TagCreateDto;
import com.blog_application.dto.tag.TagGetDto;
import com.blog_application.dto.tag.TagUpdateDto;
import com.blog_application.service.tag.TagService;
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
    public ResponseEntity<TagGetDto> createTag(@Valid @RequestBody TagCreateDto tagCreateDto){
        logger.info("Received request to create tag with name : {}",tagCreateDto.getName());
        TagGetDto createdTag = tagService.createTag(tagCreateDto);
        logger.info("Returning response for tag creation with name : {}",tagCreateDto.getName());
        return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
    }

    //GET Mapping-Get Tag Basic Info By ID
    @GetMapping("/basic-info/{id}")
    public ResponseEntity<TagBasicInfoDto> getTagBasicInfoById(@PathVariable("id") Long tagId){
        logger.info("Received request to get tag basic info with ID : {}",tagId);
        TagBasicInfoDto tagBasicInfoDto = tagService.getTagBasicInfoById(tagId);
        logger.info("Returning response for get tag basic info with ID : {}",tagId);
        return new ResponseEntity<>(tagBasicInfoDto,HttpStatus.OK);
    }

    //GET Mapping-Get Tag By ID
    @GetMapping("/{id}")
    public ResponseEntity<TagGetDto> getTagById(@PathVariable("id") Long tagId){
        logger.info("Received request to get tag with ID : {}",tagId);
        TagGetDto tagGetDto = tagService.getTagById(tagId);
        logger.info("Returning response for get tag with ID : {}",tagId);
        return new ResponseEntity<>(tagGetDto,HttpStatus.OK);
    }

    //PUT Mapping-Update Tag
    @PutMapping("/{id}")
    public ResponseEntity<TagGetDto> updateTag(@Valid @RequestBody TagUpdateDto tagUpdateDto, @PathVariable("id") Long tagId){
        logger.info("Received request to update tag with ID : {}",tagId);
        TagGetDto updatedTag = tagService.updateTag(tagUpdateDto,tagId);
        logger.info("Returning response for update tag with ID : {}",tagId);
        return new ResponseEntity<>(updatedTag,HttpStatus.OK);
    }


}
