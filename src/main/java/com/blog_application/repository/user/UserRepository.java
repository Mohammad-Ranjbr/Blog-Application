package com.blog_application.repository.user;

import com.blog_application.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Boolean existsByUserName(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findByUserNameOrEmail(String username, String email);

}
