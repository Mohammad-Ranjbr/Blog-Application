package com.blog_application.repository.post;

import com.blog_application.model.category.Category;
import com.blog_application.model.post.Post;
import com.blog_application.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
    // @Modifying:
    // This annotation tells Spring Data JPA that this query is a data modification operation (such as UPDATE or DELETE) and does not return a result.
    // @Transactional:
    // You need a transaction to ensure that changes are handled properly.
    // If this method is part of another service that is already @Transactional, you can remove it here.
    @Modifying
    @Query("UPDATE Post p SET p.category.id = :defaultCategoryId WHERE p.category.id = :categoryId")
    void updateCategoryForPosts(@Param("defaultCategoryId") Long defaultCategoryId, @Param("categoryId") Long categoryId);

}
