package com.blog_application.repository.comment;

import com.blog_application.model.comment.Comment;
import com.blog_application.model.comment.CommentReaction;
import com.blog_application.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction,Long> {

    Optional<CommentReaction> findByUserAndComment(User user, Comment comment);

    @Query("select count(cr) from CommentReaction cr where cr.comment = :comment and cr.isLike = true")
    int countLikesByComment(@Param("comment") Comment comment);

    @Query("select count(cr) from CommentReaction cr where cr.comment = :comment and cr.isLike = false ")
    int countDislikesByComment(@Param("comment") Comment comment);

    @Query("SELECT CASE WHEN (COUNT(cr) > 0) THEN true ELSE false END FROM CommentReaction cr " +
            "WHERE cr.comment.id = :comment_id AND cr.user.email = :user_email AND cr.isLike = true ")
    boolean existsLikeByCommentIdAndUserEmail(@Param("comment_id") Long commentId, @Param("user_email") String email);

    @Query("SELECT CASE WHEN (COUNT(cr) > 0) THEN true ELSE false END FROM CommentReaction cr " +
            "WHERE cr.comment.id = :comment_id AND cr.user.email = :user_email AND cr.isLike = false ")
    boolean existsDislikeByCommentIdAndUserEmail(@Param("comment_id") Long commentId, @Param("user_email") String email);

    @Query("SELECT cr.comment.id FROM CommentReaction cr WHERE cr.user.email = :user_email AND cr.isLike = true")
    List<Long> findLikedCommentIdsByUserEmail(@Param("user_email") String userEmail);

    @Query("SELECT cr.comment.id FROM CommentReaction cr WHERE cr.user.email = :user_email AND cr.isLike = false ")
    List<Long> findDislikedCommentIdsByUserEmail(@Param("user_email") String userEmail);


}
