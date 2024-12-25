package com.blog_application.repository.post;

import com.blog_application.model.post.Post;
import com.blog_application.model.post.PostReaction;
import com.blog_application.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction,Long> {

    Optional<PostReaction> findByUserAndPost(User user, Post post);

    @Query("select count(pr) from PostReaction pr where pr.post = :post and pr.isLike = true")
    int countLikByPost(@Param("post") Post post);

    @Query("SELECT CASE WHEN (COUNT(pr) > 0) THEN true ELSE false END FROM PostReaction pr WHERE pr.post.id = :post_id AND pr.user.email = :user_email")
    boolean existsLikeByPostIdAndUserEmail(@Param("post_id") Long postId, @Param("user_email") String email);

    @Query("SELECT pr.post.id FROM PostReaction pr WHERE pr.user.email = :user_email AND pr.isLike = true")
    List<Long> findLikedPostIdsByUserEmail(@Param("user_email") String userEmail);

}
