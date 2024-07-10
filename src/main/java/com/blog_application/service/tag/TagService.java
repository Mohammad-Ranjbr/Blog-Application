package com.blog_application.service.tag;

import com.blog_application.dto.tag.TagBasicInfoDto;
import com.blog_application.dto.tag.TagCreateDto;
import com.blog_application.dto.tag.TagGetDto;
import com.blog_application.dto.tag.TagUpdateDto;
import com.blog_application.util.responses.PaginatedResponse;

public interface TagService {

    void deleteTag(Long tagId);
    TagGetDto getTagById(Long tagId);
    TagGetDto getTagByName(String tagName);
    TagGetDto createTag(TagCreateDto tagCreateDto);
    TagBasicInfoDto getTagBasicInfoById(Long tagId);
    TagGetDto updateTag(TagUpdateDto tagUpdateDto, Long tagId);
    PaginatedResponse<TagGetDto> getAllTags(int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginatedResponse<TagBasicInfoDto> getAllTagBasicInfo(int pageNumber, int pageSize, String sortBy, String sortDir);

}
