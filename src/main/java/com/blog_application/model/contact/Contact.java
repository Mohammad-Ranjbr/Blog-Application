package com.blog_application.model.contact;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "contact_message")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contactName;
    private String contactEmail;
    private String subject;
    private String message;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

}
