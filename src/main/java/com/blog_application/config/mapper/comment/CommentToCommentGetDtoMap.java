package com.blog_application.config.mapper.comment;

import com.blog_application.dto.comment.CommentGetDto;
import com.blog_application.model.comment.Comment;
import org.modelmapper.PropertyMap;

// PropertyMap is an important concept in the ModelMapper library that allows you to precisely control how data is transformed from one model to another.
// In ModelMapper terms, a PropertyMap allows you to define specific rules for mapping one model to another.
// Using PropertyMap, you can specify which fields should be converted and what actions should be taken during the mapping process.
public class CommentToCommentGetDtoMap extends PropertyMap<Comment, CommentGetDto> {

    @Override
    protected void configure() {
        map().setParent(source.getParent().getId());
    }

}
