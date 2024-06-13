package com.blog_application.repository;

import com.blog_application.model.Category;
import com.blog_application.model.Post;
import com.blog_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findAllByUser(User user);
    List<Post> findAllByCategory(Category category);
    //Query Methods
    //Indicates that we want to find records whose title contains the input value.
    List<Post> findByTitleContaining(String title);

}
