package com.blog_application.repository.post;

import com.blog_application.model.post.Post;
import com.blog_application.model.post.PostReaction;
import com.blog_application.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction,Long> {

    Optional<PostReaction> findByUserAndPost(User user, Post post);

    @Query("select count(pr) from PostReaction pr where pr.post = :post and pr.isLike = true")
    int countLikByPost(@Param("post") Post post);

}
