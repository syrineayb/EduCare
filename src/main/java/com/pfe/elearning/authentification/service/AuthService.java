package com.pfe.elearning.authentification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.elearning.authentification.dto.request.AuthRequest;
import com.pfe.elearning.authentification.dto.request.RegisterRequest;
import com.pfe.elearning.authentification.dto.response.AuthResponse;
import com.pfe.elearning.exception.InvalidRoleException;
import com.pfe.elearning.profile.repository.ProfileRepository;
import com.pfe.elearning.profile.entity.Profile;
import com.pfe.elearning.role.Role;
import com.pfe.elearning.role.RoleRepository;
import com.pfe.elearning.role.RoleType;

import com.pfe.elearning.token.Token;
import com.pfe.elearning.token.TokenType;
import com.pfe.elearning.token.repository.TokenRepository;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.repository.UserRepository;
import com.pfe.elearning.validator.ObjectsValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final ObjectsValidator<RegisterRequest> registrationRequestValid;
    private final ObjectsValidator<AuthRequest> authenticationRequestValidator;
    private final TokenRepository tokenRepository;
    private final Set<RoleType> validRoles = EnumSet.of(RoleType.ROLE_USER, RoleType.ROLE_ADMIN, RoleType.ROLE_CANDIDATE, RoleType.ROLE_INSTRUCTOR, RoleType.ROLE_NO_ROLE);


    @Transactional
    public AuthResponse register(RegisterRequest request) {
        registrationRequestValid.validate(request);
        String requestedRoleName = request.getRole();
        // Validate role
        if (!isValidRole(requestedRoleName)) {
            throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }
        Role userRole = roleRepository.findByName(requestedRoleName)
                .orElseGet(() -> roleRepository.save(new Role(requestedRoleName)));

        User user = User.builder()
                //.firstname(request.getFirstName())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .fullname(request.getFirstname()+" " + request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(List.of(userRole))
                .build();

        User savedUser = userRepository.save(user);

        userRole.setUsers(new ArrayList<>(List.of(savedUser)));
        roleRepository.save(userRole);

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRoles());
        claims.put("active", user.isActive());
        claims.put("username", user.getFullname());
        var jwtToken = jwtService.generateToken(user, claims);
        var refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(savedUser, jwtToken);


        Profile profile = new Profile();
        profile.setUser(user);
        profile.setFirstName(request.getFirstname());
        profile.setLastName(request.getLastname());
        profile.setEmail(request.getEmail());
        profileRepository.save(profile);
        return buildAuthenticationResponse(savedUser, jwtToken, refreshToken);
    }

    private AuthResponse buildAuthenticationResponse(User user, String jwtToken, String refreshToken) {
        return AuthResponse.builder()
                //.username(user.getUsername())
                .fullname(user.getFullname())
                .userId(user.getId())
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin()) // Include lastLogin field
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private boolean isValidRole(String roleName) {
        try {
            RoleType roleType = RoleType.valueOf(roleName);
            if (validRoles.contains(roleType)) {
                return true;
            } else {
                throw new InvalidRoleException("Invalid role: " + roleName);
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("Invalid role: " + roleName);
        }
    }
    public AuthResponse authenticate(AuthRequest request) {
        authenticationRequestValidator.validate(request);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Update last login date
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setLastLogin(LocalDateTime.now());
        user.setActive(true); // Set active status to true

        userRepository.save(user);

        System.out.println("Authentication successful");

        // Generate JWT token
        var claims = new HashMap<String, Object>();
        claims.put("role", user.getRoles());
        claims.put("active", user.isActive());
        claims.put("username", user.getFullname());
        var jwtToken = jwtService.generateToken(user, claims);

        // Generate refresh token
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return buildAuthenticationResponse(user, jwtToken, refreshToken);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }


    }



//     Method to get the role of the logged-in user
// Method to retrieve the role of the currently logged-in user
    public String getUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return null;
        }
    }
    // Method to retrieve the role of the currently logged-in user
    public String getUserRole() {
        // Retrieve the currently logged-in user's email from the security context
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (userEmail != null && !userEmail.isEmpty()) {
            // Use the userEmail to fetch the corresponding user from the repository
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Get the roles associated with the user
            List<Role> roles = user.getRoles();

            // If roles list is not empty, return a comma-separated string of role names
            if (!roles.isEmpty()) {
                return roles.stream()
                        .map(Role::getName)
                        .collect(Collectors.joining(", "));
            }
        }

        return null; // Handle cases where user has no roles or authentication fails
    }


}
