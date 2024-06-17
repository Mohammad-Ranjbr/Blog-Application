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
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    @Column(nullable = false , length = 1000)
    private String about;
    private String gender;
    private String password;
    private String userName;
    private String phoneNumber;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
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

    //When we want to fetch the user, it also fetches the user's posts and comments from the database and tries
    //to print it with the toString method, which becomes a loop and causes stack overflow.

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", about='" + about + '\'' +
                ", gender='" + gender + '\'' +
                ", password='" + password + '\'' +
                ", userName='" + userName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
