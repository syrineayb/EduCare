package com.pfe.elearning.user.dto;

import com.pfe.elearning.role.Role;
import com.pfe.elearning.role.RoleRepository;
import com.pfe.elearning.role.RoleType;
import com.pfe.elearning.user.entity.User;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserMapper {
    private final RoleRepository roleRepository;
    public User toUser(UserRequest request) {
        User user = new User();
        user.setId(request.getId());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setFullname(request.getFirstname()+" "+request.getLastname());
        user.setGenre(request.getGenre());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setGenre(request.getGenre());
        user.setRoles(getRolesFromType(request.getRole())); // Convert RoleType to Role objects
        user.setActive(true);
        user.setEnabled(true);
        // Set other properties as needed
        return user;
    }

    private List<Role> getRolesFromType(RoleType requestedRole) {
        if (requestedRole == null) {
            // Handle the situation where requestedRole is null
            // You can throw an exception, return an empty list, or handle it based on your requirements
            throw new IllegalArgumentException("Invalid role: null");
        }
        // Check if the role exists in the database
        Role role = roleRepository.findByName(requestedRole.name())
                .orElseGet(() -> {
                    // If the role doesn't exist, create a new role and save it to the database
                    Role newRole = new Role();
                    newRole.setName(requestedRole.name());
                    return roleRepository.save(newRole);
                });
        return Collections.singletonList(role);
    }


    public UserResponse toUserResponse(User user) {
        List<RoleType> roles = user.getRoles().stream()
                .map(Role::getName)
                .map(RoleType::valueOf)
                .collect(Collectors.toList());
        return UserResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .password(user.getPassword())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .enabled(user.isEnabled())
                .active(user.isActive())
                .roles(roles)
                .build();
    }
}