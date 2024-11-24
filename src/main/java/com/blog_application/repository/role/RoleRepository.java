package com.blog_application.repository.role;

import com.blog_application.model.role.Role;

import java.util.Optional;

public interface RoleRepository {

    Optional<Role> findByName(String mame);

}
