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
    public void deleteTag(Long tagId) {

    }

    @Override
    public TagGetDto getTagById(Long tagId) {
        return null;
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
           return new ResourceNotFoundException("Tag","ID",String.valueOf(tagId),"Get tag basic info operation not performed");
        });
        logger.info("Tag found with ID : {}",tagId);
        TagBasicInfoDto tagBasicInfoDto = tagMapper.toTagBasicInfoDto(tag);
        logger.info("TagBasicInfo created: {}", tagBasicInfoDto);
        return tagBasicInfoDto;
    }

    @Override
    public TagGetDto updateTag(TagUpdateDto tagUpdateDto, Long tagId) {
        return null;
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
