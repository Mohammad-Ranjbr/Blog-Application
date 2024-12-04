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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    // CascadeType.REMOVE : It will be deleted when no other post uses it
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    // Set -> No repetition: Set automatically prevents duplicate elements from entering. If we want to ensure that each post is in the user's saved list only once, using Set is a better option.
    // Performance: Search, add, and delete operations on a HashSet (which is an implementation of Set) have an average execution time of O(1). This optimized function can improve the performance of the application in cases where the data is large.
    // List -> Specific order: If the order of elements is important to us (for example, the chronological order of saving posts), List is a more suitable option. List preserves the order in which elements are entered.
    // Index access: If we need to access elements based on index, List is a better option. List allows us to access elements using index.
    // Repetition allowed: If repetition of elements is allowed (for example, a post may be saved by a user multiple times), List is a more suitable option.
    // CascadeType.REMOVE is not needed here, as this type of operation results in the actual removal of the Post data from the database.
    // Given that posts saved by different users may be shared, actually deleting a post from the database may result in the loss of information that other users may need.
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "user_saved_posts",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> savedByUsers  = new HashSet<>();

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
