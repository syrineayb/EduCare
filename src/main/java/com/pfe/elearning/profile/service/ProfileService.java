package com.pfe.elearning.profile.service;

import com.pfe.elearning.authentification.dto.response.AuthResponse;
import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.profile.dto.ProfileRequest;
import com.pfe.elearning.profile.dto.ProfileResponse;
import com.pfe.elearning.profile.entity.Profile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.springframework.data.domain.Pageable;

import java.util.List;
public interface ProfileService {
    ProfileResponse getUserProfile(Integer userId);
    PageResponse<ProfileResponse> getInstructorProfiles(Pageable pageable);
    //ProfileResponse updateUserProfile(Integer userId, ProfileRequest profileRequest);
    ProfileResponse updateUserProfile(Long userId, ProfileRequest profileRequest);
    ProfileResponse getCurrentUserProfile();
    ProfileResponse updateProfile(ProfileRequest profileRequest) ;
    AuthResponse updateUserEmail(String oldEmail, String newEmail);
    Profile getById(Long id);

}