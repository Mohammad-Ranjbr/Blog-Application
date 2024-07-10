package com.blog_application.config.mapper.tag;

import com.blog_application.dto.tag.TagBasicInfoDto;
import com.blog_application.dto.tag.TagCreateDto;
import com.blog_application.dto.tag.TagGetDto;
import com.blog_application.model.tag.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public TagMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public Tag toEntity(TagCreateDto tagCreateDto){
        return modelMapper.map(tagCreateDto,Tag.class);
    }

    public Tag toEntity(TagGetDto tagGetDto){
        return modelMapper.map(tagGetDto,Tag.class);
    }

    public TagGetDto toTagGetDto(Tag tag){
        return modelMapper.map(tag,TagGetDto.class);
    }

    public TagBasicInfoDto toTagBasicInfoDto(Tag tag){
        return modelMapper.map(tag,TagBasicInfoDto.class);
    }

}
