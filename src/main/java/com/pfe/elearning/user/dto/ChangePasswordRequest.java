package com.pfe.elearning.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordRequest {

    @NotNull(message = "Password is mandatory")
    @Size(min = 4, max = 16, message = "Password should be between 4 and 16 chars")
    private String currentPassword;
    @NotNull(message = "Password is mandatory")
    @Size(min = 4, max = 16, message = "Password should be between 4 and 16 chars")
    private String newPassword;
    @NotNull(message = "Password is mandatory")
    @Size(min = 4, max = 16, message = "Password should be between 4 and 16 chars")
    private String confirmationPassword;
}