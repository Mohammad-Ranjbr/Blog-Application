package com.blog_application.model.post;

import com.blog_application.model.tag.Tag;
import com.blog_application.model.user.User;
import com.blog_application.model.category.Category;
import com.blog_application.model.comment.Comment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,length = 100)
    private String title;
    @Column(length = 1000)
    private String content;
    private String imageName;
    @CreationTimestamp
    @Column(updatable = false)
    //LocalDateTime is newer than Date , and hase newer and powerful api than Date
    //not save information about zone
    private LocalDateTime creationDate;
    @UpdateTimestamp
    private LocalDateTime updatedDate;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    // When orphanRemoval = true is set, if a child is removed from the parent collection,
    // JPA automatically removes that child from the database as well. This behavior is called "orphan removal".
    // newArrayList<>() : Because of the cascade="all-delete-orphan" setting you need to make sure that collections are properly updated and orphaned entities are properly deleted.
    private User user;
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<PostReaction> postReactions = new ArrayList<>();
    private int likes;
    // CascadeType.PERSIST: When a new entity is added to the database (EntityManager.persist is called), the related entities are automatically added.
    // For example: if you create a new post and add new tags to it, the new tags are automatically saved in the database.
    // CascadeType.MERGE: When an existing entity is updated (EntityManager.merge is called), related entities are automatically updated as well.
    // For example: if you update an existing post and make changes to it, the changes will be automatically updated in the database.
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", imageName='" + imageName + '\'' +
                ", creationDate=" + creationDate +
                ", updatedDate=" + updatedDate +
                '}';
    }

}
