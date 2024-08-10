package com.pfe.elearning.course.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
public class CourseRequest {
    //  @Column(unique = true)
    //  @NotBlank(message = "Title cannot be blank")
    //  @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    //  @NotBlank(message = "Description cannot be blank")
    //  @Size(min = 2, message = "Description must be at least 2 characters")
    private String description;

    //   @NotNull(message = "Topic ID cannot be null")
    private Integer topicId;

    private MultipartFile imageFile; // Add image file field

    //   @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    //   @NotNull(message = "End date cannot be null")
    private LocalDate endDate;

    /*   // @NotNull(message = "Free status cannot be null")
       private boolean free;

       private Float price; // Add price attribute for the course
   */
    //  @NotBlank(message = "Time commitment cannot be blank")
    private String timeCommitment;
}

    /*public boolean getIsFree()    .......................................................................................................................... {
        return isFree();
    }
}

     */
//@NotBlank(message = "Publisher name cannot be blank")
//private String publisherName;