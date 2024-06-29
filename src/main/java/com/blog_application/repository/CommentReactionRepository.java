package com.blog_application.repository;

import com.blog_application.model.Comment;
import com.blog_application.model.CommentReaction;
import com.blog_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction,Long> {

    Optional<CommentReaction> findByUserAndComment(User user, Comment comment);

    @Query("select count(cr) from CommentReaction cr where cr.comment = :comment and cr.isLike = true")
    int countLikesByComment(@Param("comment") Comment comment);

    @Query("select count(cr) from CommentReaction cr where cr.comment = :comment and cr.isLike = false ")
    int countDislikesByComment(@Param("comment") Comment comment);


}
