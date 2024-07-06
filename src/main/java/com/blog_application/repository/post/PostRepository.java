package com.blog_application.repository.post;

import com.blog_application.model.category.Category;
import com.blog_application.model.post.Post;
import com.blog_application.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    Page<Post> findAllByUser(User user, Pageable pageable);
    Page<Post> findAllByCategory(Category category, Pageable pageable);
    //Query Methods
    //Indicates that we want to find records whose title contains the input value.
    Page<Post> findByTitleContaining(String title, Pageable pageable);
    @Query("select p from Post p where p.title like :key")
    Page<Post> searchByTitle(@Param("key") String title,Pageable pageable);

}
