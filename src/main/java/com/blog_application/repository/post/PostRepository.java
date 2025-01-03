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

import java.util.List;
import java.util.UUID;

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
    //@Query(value = "SELECT p.id FROM posts p JOIN user_saved_posts usp ON p.id = usp.post_id JOIN users u ON usp.user_id = u.id WHERE u.email = :user_email", nativeQuery = true)
    @Query(value = "SELECT usp.post_id FROM user_saved_posts usp JOIN users u ON usp.user_id = u.id AND u.email = :user_email", nativeQuery = true)
    List<Long> findSavedPostIdsByUser(@Param("user_email") String userEmail);

    @Query(value = "SELECT CASE WHEN COUNT(usp) > 0 THEN true ELSE false END FROM" +
            " user_saved_posts usp JOIN users u ON usp.user_id = u.id WHERE usp.post_id = :post_id AND u.email = :user_email", nativeQuery = true)
    boolean existSavedByCurrentUser(@Param("post_id") Long postId, @Param("user_email") String userEmail);

    @Query(value = "SELECT p.* FROM posts p JOIN user_followers uf ON p.user_id = uf.user_id " +
            "WHERE uf.follower_id = :current_user_id ORDER BY p.creation_date DESC", nativeQuery = true)
    List<Post> findHomePosts(@Param("current_user_id") UUID currentUserId);

    @Query("SELECT p FROM Post p WHERE p.user.id != :current_user_id ORDER BY p.creationDate DESC")
    List<Post> findExplorePosts(@Param("current_user_id") UUID currentUserId);

}
