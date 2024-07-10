package com.blog_application.service.impl.tag;

import com.blog_application.config.mapper.tag.TagMapper;
import com.blog_application.dto.tag.TagBasicInfoDto;
import com.blog_application.dto.tag.TagCreateDto;
import com.blog_application.dto.tag.TagGetDto;
import com.blog_application.dto.tag.TagUpdateDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.tag.Tag;
import com.blog_application.repository.tag.TagRepository;
import com.blog_application.service.tag.TagService;
import com.blog_application.util.responses.PaginatedResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;

@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final TagRepository tagRepository;
    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

    @Autowired
    public TagServiceImpl(TagMapper tagMapper, TagRepository tagRepository){
        this.tagMapper = tagMapper;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public void deleteTag(Long tagId) {
        logger.info("Deleting tag with ID : {}",tagId);
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> {
            logger.warn("Tag with ID {} not found, Delete Tag operation not performed",tagId);
           return new ResourceNotFoundException("Tag","ID",String.valueOf(tagId),"Delete Tag operation not performed");
        });
        logger.info("Tag with ID {} deleted successfully",tagId);
        tagRepository.delete(tag);
    }

    @Override
    public TagGetDto getTagById(Long tagId) {
        logger.info("Fetching tag with ID : {}",tagId);
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> {
            logger.warn("Tag with ID {} not found, Get Tag operation not performed",tagId);
           return new ResourceNotFoundException("Tag","ID",String.valueOf(tagId),"Get Tag operation not performed");
        });
        Optional<Tag> optionalTag = tagRepository.findById(tagId);
        Consumer<Tag> printTagDetails = findTag -> System.out.println("Tag Found : " + findTag);
        optionalTag.ifPresent(printTagDetails);
        logger.info("Tag found with ID : {}",tagId);
        return tagMapper.toTagGetDto(tag);
    }

    @Override
    @Transactional
    public TagGetDto createTag(TagCreateDto tagCreateDto) {
        logger.info("Creating tag with name : {}",tagCreateDto.getName());
        Tag tag = tagMapper.toEntity(tagCreateDto);
        Tag savedTag = tagRepository.save(tag);
        logger.info("Tag created successfully with name : {}",savedTag.getName());
        return tagMapper.toTagGetDto(savedTag);
    }

    @Override
    public TagBasicInfoDto getTagBasicInfoById(Long tagId) {
        logger.info("Fetching tag with ID: {}", tagId);
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> {
            logger.warn("Tag with ID {} not found, Get tag basic info operation not performed",tagId);
            return new ResourceNotFoundException("Tag","ID",String.valueOf(tagId),"Get Tag basic info operation not performed");
        });
        logger.info("Tag found with ID : {}",tagId);
        TagBasicInfoDto tagBasicInfoDto = tagMapper.toTagBasicInfoDto(tag);
        logger.info("TagBasicInfo created: {}", tagBasicInfoDto);
        return tagBasicInfoDto;
    }

    @Override
    @Transactional
    public TagGetDto updateTag(TagUpdateDto tagUpdateDto, Long tagId) {
        logger.info("Updating tag with ID : {}",tagId);
        Tag updatedTag = tagRepository.findById(tagId).map(tag -> {
            tag.setName(tagUpdateDto.getName());
            tag.setDescription(tagUpdateDto.getDescription());
            Tag savedTag =  tagRepository.save(tag);
            logger.info("Tag with ID {} updated successfully",tagId);
            return savedTag;
        }).orElseThrow(() -> {
            logger.warn("Tag with ID {} not found, Update tag operation not performed",tagId);
            return new ResourceNotFoundException("Tag","ID",String.valueOf(tagId),"Update Tag operation not performed");
        });
        return tagMapper.toTagGetDto(updatedTag);
    }

    @Override
    public PaginatedResponse<TagGetDto> getAllTags(int pageNumber, int pageSize, String sortBy, String sortDir) {
        return null;
    }

    @Override
    public PaginatedResponse<TagBasicInfoDto> getAllTagBasicInfo(int pageNumber, int pageSize, String sortBy, String sortDir) {
        return null;
    }

}
