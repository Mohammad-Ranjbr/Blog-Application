package com.blog_application.repository.user;

import com.blog_application.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Boolean existsByEmail(String email);
    Boolean existsByUserName(String username);

    @Override
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isActive = true AND u.softDelete = false ")
    Optional<User> findById(@Param("id") UUID uuid);
    @Query("SELECT u.id FROM User u WHERE u.email = :email AND  u.isActive = true AND u.softDelete = false")
    UUID getUserIdByEmail(@Param("email") String email);
    @Query("SELECT u FROM User u WHERE u.email = :email AND  u.isActive = true AND u.softDelete = false")
    Optional<User> findByEmail(@Param("email") String email);
    @Query("SELECT u FROM User u  WHERE (u.userName = :username or u.email = :email) AND u.isActive = true AND u.softDelete = false")
    Optional<User> findByUserNameOrEmail(@Param("username") String username, @Param("email") String email);
    @Modifying
    @Query("UPDATE User u SET u.isActive = :isActive WHERE u.id = :userId")
    void updateUserStatusById(@Param("isActive") boolean isActive, UUID userId);
    @Query("SELECT u From User u WHERE u.id = :id AND u.isActive = false")
    Optional<User> findInactiveUserById(@Param("id") UUID userid);
    @Query(value = "SELECT uf.user_id FROM user_followers uf JOIN users u ON uf.follower_id = u.id AND u.email = :user_email", nativeQuery = true)
    List<UUID> findFollowedUserIdsByUser(@Param("user_email") String email);
    @Query(value = "SELECT CASE WHEN COUNT(uf) > 0 THEN true ELSE false END FROM " +
            "user_followers uf JOIN users u ON uf.follower_id = u.id WHERE u.email = :user_email AND uf.user_id = :user_id", nativeQuery = true)
    boolean existFollowedByCurrentUser(@Param("user_id") UUID userId, @Param("user_email") String userEmail);

}
