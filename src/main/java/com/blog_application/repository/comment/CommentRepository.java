package com.blog_application.repository.comment;

import com.blog_application.model.comment.Comment;
import com.blog_application.model.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findByPost(Post post);
    List<Comment> findByParentId(Long parentId);

}
