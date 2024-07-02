package com.blog_application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
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
    private User user;
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Comment> comments;
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<PostReaction> postReactions;
    private int likes;

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
