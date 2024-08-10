package com.pfe.elearning.user.dto;

import com.pfe.elearning.role.RoleType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Integer id;
    private String firstname;
    private String lastname;
    private String fullname;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean enabled;
    private boolean active;
    private List<RoleType> roles;
}
