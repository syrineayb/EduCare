package com.pfe.elearning.profile.dto;

import com.pfe.elearning.genre.entity.Genre;
import com.pfe.elearning.profile.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@AllArgsConstructor
@Builder
public class ProfileResponse {

    private Integer profileId;
    private String firstName;
    private String lastName;
    private String email;
    private String profileImage;
    private String description; // Add description field
    private String phoneNumber; // Add phoneNumber field
    private Date dateOfBirth; // Add dateOfBirth field
    private String country; // Add country field
    private String currentJob; // Add currentJob field
    private int experience; // Add experience field
    private String degreeOfEducation; // Add degreeOfEducation field
    private String certificates;
    private LocalDateTime createdAt; // Add creation date field
    private String linkedInUrl;
    private String githubUrl;
    private String twitterUrl;
    private Genre gender;
    private String newToken;
    private int enrolledCandidatesCount;

    public static ProfileResponse fromProfile(Profile profile, String newToken) {
        return ProfileResponse.builder()
                .profileId(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .profileImage(profile.getProfileImage())
                .description(profile.getDescription())
                .phoneNumber(profile.getPhoneNumber())
                .country(profile.getCountry())
                .currentJob(profile.getCurrentJob())
                .experience(profile.getExperience())
                .createdAt(profile.getCreatedAt())
                .linkedInUrl(profile.getLinkedInUrl())
                .githubUrl(profile.getGithubUrl())
                .twitterUrl(profile.getTwitterUrl())
                .gender(profile.getGender())
                .newToken(newToken)
                .build();
    }
}