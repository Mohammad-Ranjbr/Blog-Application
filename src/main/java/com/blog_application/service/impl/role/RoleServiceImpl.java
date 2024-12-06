package com.blog_application.service.impl.role;

import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.role.Role;
import com.blog_application.repository.role.RoleRepository;
import com.blog_application.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final static Logger logger = LoggerFactory.getLogger(RoleService.class);

    @Override
    public Role getRoleByName(String name) {
        logger.info("Fetching role with Name : {}", name);
        Role role = roleRepository.findByName(name).orElseThrow(() -> {
            logger.warn("Role with Name {} not found, Get role operation not performed", name);
            return new ResourceNotFoundException("Role","Name",name,"Get Role operation not performed");
        });
        logger.info("Role found with Name : {}", name);
        return role;
    }

}
