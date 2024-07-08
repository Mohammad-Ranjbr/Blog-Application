package com.blog_application.repository.tag;

import com.blog_application.model.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag,Long> {

    Optional<Tag> findByName(String name);

}
