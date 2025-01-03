package com.blog_application.model.user;

import com.blog_application.model.comment.Comment;
import com.blog_application.model.comment.CommentReaction;
import com.blog_application.model.post.Post;
import com.blog_application.model.post.PostReaction;
import com.blog_application.model.role.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
public class User {

    //In some projects, it might be not possible to jump from JPA specification 2.x to JPA (or Jakarta) specification 3.1.0. However,
    //if we have Hibernate version 4 or 5, we still have the ability to generate UUIDs. For that, we have two approaches.
    //@GeneratedValue(generator = "uuid-hibernate-generator") //Approach 1
    //@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    //@UuidGenerator //Approach 2
    //Furthermore, when specifying @UuidGenerator, we can choose the concrete version of UUID to generate. This is defined by the style parameter. Let’s see the values that this parameter can take:
    //
    //RANDOM – generate UUID based on random numbers (version 4 in RFC)
    //TIME – generate time-based UUID (version 1 in RFC)
    //AUTO – this is the default option and is the same as RANDOM
    //@UuidGenerator(style = UuidGenerator.Style.TIME)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) //Approach 3
    private UUID id;
    private String name;
    @Column(unique = true)
    private String email;
    private String about;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Column(unique = true)
    private String userName;
    @Column(unique = true)
    private String phoneNumber;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<CommentReaction> commentReactions = new ArrayList<>();
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<PostReaction> postReactions = new ArrayList<>();

    //mappedBy="user" -> It means that the Foreign Key column is located in the Post table, which points to the User table.
    //cascade = CascadeType.ALL -> This property specifies that various operations (such as saving, updating, deleting, etc.)
    // performed on the User entity will automatically be performed on the associated Post entities.
    //CascadeType.REFRESH -> When we need to update child entities to reflect new data that may have changed in the database.
    //If the post information in the database is changed outside , of the current context, not only the user information, but also the related post information is reloaded and updated from the database.
    //fetch = FetchType.LAZY -> Posts related to a user are loaded only when they are explicitly required. This can help improve
    //performance as it prevents unnecessary data from being loaded.
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Comment> comments;

    @ManyToMany(mappedBy = "savedByUsers")
    private Set<Post> savedPosts  = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private List<User> followers = new ArrayList<>();

    @ManyToMany(mappedBy = "followers")
    private List<User> following = new ArrayList<>();

    private int followersCount = 0;
    private int followingCount = 0;
    private int postsCount = 0;

    private boolean isActive = true;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
                joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
                inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    private boolean softDelete;
    private String imageName;

    //When we want to fetch the user, it also fetches the user's posts and comments from the database and tries
    //to print it with the toString method, which becomes a loop and causes stack overflow.

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", about='" + about + '\'' +
                ", password='" + password + '\'' +
                ", userName='" + userName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", creationDate=" + createdAt +
                ", updatedDate=" + updatedAt +
                '}';
    }

}
