package com.blog_application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
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
    private String password;
    //mappedBy="user" -> It means that the Foreign Key column is located in the Post table, which points to the User table.
    //cascade = CascadeType.ALL -> This property specifies that various operations (such as saving, updating, deleting, etc.)
    // performed on the User entity will automatically be performed on the associated Post entities.
    //CascadeType.REFRESH -> When we need to update child entities to reflect new data that may have changed in the database.
    //If the post information in the database is changed outside , of the current context, not only the user information, but also the related post information is reloaded and updated from the database.
    //fetch = FetchType.LAZY -> Posts related to a user are loaded only when they are explicitly required. This can help improve
    //performance as it prevents unnecessary data from being loaded.
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Post> posts;

}
