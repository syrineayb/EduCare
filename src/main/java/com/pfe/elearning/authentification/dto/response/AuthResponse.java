// AuthResponse.java
package com.pfe.elearning.authentification.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private  int userId;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private LocalDateTime createdAt;
    private String fullname;
    private LocalDateTime lastLogin;

}
