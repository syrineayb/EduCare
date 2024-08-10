package com.pfe.elearning.profile.controller;

import com.pfe.elearning.authentification.dto.request.RegisterRequest;
import com.pfe.elearning.authentification.dto.response.AuthResponse;
import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.profile.dto.ProfileRequest;
import com.pfe.elearning.profile.dto.ProfileResponse;
import com.pfe.elearning.profile.service.ProfileService;
import com.pfe.elearning.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponse> getUserProfile(@PathVariable Integer userId) {
        ProfileResponse response = profileService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }
/*
    @PutMapping("/{userId}/profile")
    @PreAuthorize("hasAnyRole('CANDIDATE')")
    public ResponseEntity<ProfileResponse> updateUserProfile(@PathVariable Long candidateId,
                                                                  @RequestBody ProfileRequest profileRequest) {
        ProfileResponse response = userProfileService.updateUserProfile(userId, profileRequest);
        return ResponseEntity.ok(response);
    }
 */

@PutMapping("/{userId}")
public ResponseEntity<ProfileResponse> updateUserProfile(
        @PathVariable Long userId,
        @RequestBody ProfileRequest profileRequest) {

    // Assuming you have a service method to update the profile
    ProfileResponse updatedProfile = profileService.updateUserProfile(userId, profileRequest);

    // Check if the update was successful
    if (updatedProfile != null) {
        return ResponseEntity.ok(updatedProfile);
    } else {
        return ResponseEntity.notFound().build();
    }
}
    @GetMapping("/instructors")
    public PageResponse<ProfileResponse> getInstructorProfiles(Pageable pageable) {
        return profileService.getInstructorProfiles(pageable);
    }
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getCurrentUserProfile() {
        ProfileResponse profile = profileService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/update")
    public ResponseEntity<ProfileResponse> updateProfile(@RequestBody ProfileRequest profileRequest) {
        try {
            // Pass both the file and profile request to the service for updating the profile
            ProfileResponse updatedProfile = profileService.updateProfile(profileRequest);
            return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/profileId")
    public ResponseEntity<Integer> getCurrentId() {
        // Retrieve the authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Cast the principal object to your User class
        User user = (User) authentication.getPrincipal();

        // Extract the user ID
        Integer userId = user.getId();
        System.out.println("Retrieved user ID: " + userId);

        return ResponseEntity.ok(userId);

    }


}