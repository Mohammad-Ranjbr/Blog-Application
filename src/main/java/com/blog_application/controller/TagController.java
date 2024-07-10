package com.blog_application.controller;

import com.blog_application.dto.tag.TagCreateDto;
import com.blog_application.dto.tag.TagGetDto;
import com.blog_application.service.tag.TagService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return new ResponseEntity<>(createdTag, HttpStatus.OK);
    }

}
