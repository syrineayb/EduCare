package com.pfe.elearning.profile.dto;

import com.pfe.elearning.genre.entity.Genre;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;
@Getter
@Setter
public class ProfileRequest {
   @NotNull(message = "First name is mandatory")
   @NotBlank(message = "First name is mandatory")
   private String firstName;

     @NotNull(message = "Last name is mandatory")
     @NotBlank(message = "Last name is mandatory")
     private String lastName;
    @NotNull(message = "Last Email is mandatory")
    @NotBlank(message = "Last Email is mandatory")
    @Email
    private String email;
    private String description;
    private String phoneNumber;
    private Date dateOfBirth;
    private String country;
    private String currentJob;
    private int experience;
    private String linkedInUrl;
    private String githubUrl;
    private String twitterUrl;
    private Genre gender;
}