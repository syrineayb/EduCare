package com.pfe.elearning.user.dto;

import com.pfe.elearning.genre.entity.Genre;
import com.pfe.elearning.role.Role;
import com.pfe.elearning.role.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter

public class UserRequest {
    private Integer id;
    @NotNull(message = "Firstname is mandatory")
    private String firstname;
    @NotNull(message = "Lastname is mandatory")
    private String lastname;
    @NotNull(message = "Email name is mandatory")
    @Email(message = "Email is not valid")
    private String email;
    @NotNull(message = "Password is mandatory")
    @Size(min = 4, max = 16, message = "Password should be between 4 and 16 chars")
    private String password;
    private Genre genre;
    private RoleType role;
}
