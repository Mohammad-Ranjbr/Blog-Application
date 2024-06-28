package com.blog_application.repository;

import com.blog_application.model.Comment;
import com.blog_application.model.LikeDislike;
import com.blog_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeDislikeRepository extends JpaRepository<LikeDislike,Long> {

    Optional<LikeDislike> findByUserAndComment(User user, Comment comment);

}
