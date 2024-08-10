package com.pfe.elearning.profile.dto;

import com.pfe.elearning.profile.entity.Profile;
import com.pfe.elearning.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class ProfileMapper {

    public ProfileResponse toProfileResponse(Profile profile) {
        int enrolledCandidatesCount = profile.getUser().getEnrolledCourses().size();

        return ProfileResponse.builder()
                .profileId(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .profileImage((profile.getProfileImage()))
                .description(profile.getDescription())
                .phoneNumber(profile.getPhoneNumber())
                .country(profile.getCountry())
                .currentJob(profile.getCurrentJob())
                .experience(profile.getExperience())
                .githubUrl(profile.getGithubUrl())
                .twitterUrl(profile.getTwitterUrl())
                .linkedInUrl(profile.getLinkedInUrl())
                .gender(profile.getGender())
                .createdAt(profile.getCreatedAt())
                .enrolledCandidatesCount(enrolledCandidatesCount)

                .build();
    }

}