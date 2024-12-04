package com.blog_application.repository.user;

import com.blog_application.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Override
    @Query("select u from User u where u.id = :id and u.isActive = true and u.softDelete = false ")
    Optional<User> findById(@Param("id") UUID uuid);
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Boolean existsByUserName(String username);
    @Query("select u from User u  where (u.userName = :username or u.email = :email) and u.isActive = true and u.softDelete = false")
    Optional<User> findByUserNameOrEmail(@Param("username") String username, @Param("email") String email);

}
