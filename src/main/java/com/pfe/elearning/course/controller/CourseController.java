package com.pfe.elearning.course.controller;

import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.course.dto.CandidateCourseResponse;
import com.pfe.elearning.course.dto.CourseRequest;
import com.pfe.elearning.course.dto.CourseResponse;
import com.pfe.elearning.course.repository.CourseRepository;
import com.pfe.elearning.course.service.CourseService;
import com.pfe.elearning.exception.*;
import com.pfe.elearning.file.services.FileService;
import com.pfe.elearning.question.dto.QuestionResponse;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    // Autowire the FileService for handling file operations
    private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    @Qualifier("CourseImageServiceImpl")
    private FileService fileService;


    // Endpoint to download course image file
    @GetMapping("/downloadCourseImage/{filename:.+}")
    public ResponseEntity<Resource> downloadCourseImage(@PathVariable String filename) {
        Resource fileResource = fileService.load(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }

    // Endpoint to create a new course
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Specify consumes for multipart requests
    public ResponseEntity<CourseResponse> createCourse(@Valid CourseRequest courseRequest,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        String fileImageDownloadUrl = null; // Declare fileImageDownloadUrl here
        if (courseRequest.getImageFile() != null) {
            String filename = fileService.save(courseRequest.getImageFile());
            fileImageDownloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/courses/downloadCourseImage/")
                    .path(filename) // Use filename here instead of imageUrl
                    .toUriString();
        }
        // Get the publisher's username from the authenticated user's details
        String publisherUsername = userDetails.getUsername();
        System.out.println("publisherUsername: " + publisherUsername);

        CourseResponse createdCourse = courseService.createCourse(courseRequest, publisherUsername, fileImageDownloadUrl);
        System.out.println("created course ----");

        // Create the course with the provided request and publisher's username

        // Return the ID of the newly created course
        return ResponseEntity.accepted().body(createdCourse);
    }

    // Endpoint to get a course by ID
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Integer courseId) {
        CourseResponse courseResponse = courseService.getCourseById(courseId);
        return ResponseEntity.ok(courseResponse);
    }
    @GetMapping("/by-publisher")
    public ResponseEntity<List<CourseResponse>> getCoursesByPublisher(@RequestParam String publisherUsername) {
        List<CourseResponse> courses = courseService.getCoursesByPublisher(publisherUsername);
        return ResponseEntity.ok(courses);
    }


    @GetMapping("/{courseId}/title")
    public ResponseEntity<?> getCourseTitleById(@PathVariable Integer courseId) {
        String courseTitle = courseService.getCourseTitleById(courseId);
        return ResponseEntity.ok(courseTitle);
    }

    // Endpoint to get all courses with pagination
    @GetMapping("/findAll")
    public ResponseEntity<PageResponse<CourseResponse>> findAll(
            @RequestParam(name = "page", defaultValue = "1", required = false) int page,
            @RequestParam(name = "size", defaultValue = "4", required = false) int size) {
        return ResponseEntity.ok(courseService.findAll(page, size));
    }

    // Endpoint to get all courses without pagination
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    // Endpoint to get all courses of the logged-in instructor
    @GetMapping("/instructor-courses")// Adding a specific path for instructor courses
    public ResponseEntity<List<CourseResponse>> getInstructorCourses(@AuthenticationPrincipal UserDetails userDetails) {
        String instructorUsername = userDetails.getUsername();
        List<CourseResponse> courses = courseService.getAllCoursesByInstructor(instructorUsername);
        return ResponseEntity.ok(courses);
    }

    // Endpoint to get paginated courses of the logged-in instructor
    @GetMapping("/instructor-courses/paginated") // Adding a specific path for paginated instructor courses
    public ResponseEntity<PageResponse<CourseResponse>> getPaginatedInstructorCourses(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String instructorUsername = userDetails.getUsername();
        PageResponse<CourseResponse> coursesPage = courseService.getAllCoursesByInstructor(instructorUsername, page, size);
        return ResponseEntity.ok(coursesPage);
    }

    // Endpoint to search instructor courses by title containing a keyword
    @GetMapping("/instructor-courses/search")
    public ResponseEntity<List<CourseResponse>> searchInstructorCoursesByTitleContaining(@RequestParam String keyword) {
        List<CourseResponse> instructorCourses = courseService.findInstructorCourseByTitleContaining(keyword);
        return ResponseEntity.ok(instructorCourses);
    }

        /*   @PutMapping("/{courseId}")
        public ResponseEntity<String> updateCourse(
                @PathVariable Integer courseId,
                @Valid @RequestBody CourseRequest courseRequest) {
            courseService.updateCourse(courseId, courseRequest);
            return ResponseEntity.ok("Course " + courseId + " successfully updated.");
        }

      */

    // Endpoint to update a course
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PutMapping("/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable("courseId") Integer courseId,
                                          @Valid @ModelAttribute CourseRequest courseRequest) {
        try {
            String imageUrl = null; // Initialize imageUrl

            // If a new image file is provided, save it and get the image URL
            if (courseRequest.getImageFile() != null) {
                String filename = fileService.save(courseRequest.getImageFile());
                imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/courses/downloadCourseImage/")
                        .path(filename)
                        .toUriString();
            }

            CourseResponse updatedCourse = courseService.updateCourse(courseId, courseRequest, imageUrl);
            updatedCourse.setImageFile(imageUrl);

            return ResponseEntity.accepted().body(updatedCourse);
        } catch (CourseNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CourseValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Endpoint to delete a course
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable Integer courseId) {
        //        if (courseService.existsById(courseId)) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }


    // Endpoint to search courses by title containing a keyword
    @GetMapping("/search")
    public List<CourseResponse> findCourseByTitleContaining(@RequestParam String keyword) {
        return courseService.findCourseByTitleContaining(keyword);
    }

    @GetMapping("/in-batch")
    public ResponseEntity<PageResponse<CourseResponse>> findCoursesInBatch(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
        PageResponse<CourseResponse> coursesPage = courseService.findCoursesInBatch(page, size);
        return ResponseEntity.ok(coursesPage);
    }

    @GetMapping("/{courseId}/enrolled-users/count")
    public ResponseEntity<Long> countUsersEnrolledInCourse(@PathVariable("courseId") Integer courseId) {
        try {
            // Call the service method to count users enrolled in the course
            long count = courseService.countUsersEnrolledInCourse(courseId);
            return ResponseEntity.ok(count);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{courseId}/enrolled-candidates/count")
    public ResponseEntity<Long> countEnrolledCandidatesForCourseByInstructor(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer courseId) {
        String instructorUsername = userDetails.getUsername();
        long enrolledCandidatesCount = courseService.countEnrolledCandidatesByInstructorForCourse(instructorUsername, courseId);
        return ResponseEntity.ok(enrolledCandidatesCount);
    }
    @GetMapping("/enrolled-candidates/count")
    public ResponseEntity<Long> countAllEnrolledCandidatesForAllCoursesByInstructor(
            @AuthenticationPrincipal UserDetails userDetails) {
        String instructorUsername = userDetails.getUsername();
        long totalEnrolledCandidatesCount = courseService.countAllEnrolledCandidatesForAllCoursesByInstructor(instructorUsername);
        return ResponseEntity.ok(totalEnrolledCandidatesCount);
    }
    @GetMapping("/instructor-courses/count")
    public ResponseEntity<Long> countInstructorCourses(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String instructorUsername = userDetails.getUsername();
        long courseCount = courseService.countCoursesByInstructor(instructorUsername);
        return ResponseEntity.ok(courseCount);
    }



    // Endpoint to get courses by topic ID
    @GetMapping("/by-topic/{topicId}")
    public List<CourseResponse> getCoursesByTopic(@PathVariable Integer topicId) {
        return courseService.getCoursesByTopic(topicId);
    }

    @GetMapping("/pagable/topic/{topicId}")
    public ResponseEntity<?> getCoursesByTopic(
            @PathVariable Integer topicId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "4", required = false) int size) {

        try {
            PageResponse<CourseResponse> coursePage = courseService.getCoursesByTopic(topicId, page, size);
            return ResponseEntity.ok(coursePage);
        } catch (Exception ex) {
            // Log the exception and return an error response
            String errorMessage = "Error occurred while retrieving courses: " + ex.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMessage);
        }
    }


    @GetMapping("/forCandidate/{candidateId}")
    public ResponseEntity<List<CourseResponse>> getCoursesForCandidate(@PathVariable Integer candidateId) {
        List<CourseResponse> courses = courseService.getCoursesForCandidate(candidateId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/my-courses")
    public ResponseEntity<List<CourseResponse>> getMyCourses() {
        // Extract the candidate's username from the authentication token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Retrieve the candidate from the database using the username
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Retrieve the courses for the candidate
        List<CourseResponse> courses = courseService.getCoursesForCandidate(user.getId());
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}/users/{userId}/enrollment")
    public ResponseEntity<Boolean> isUserEnrolledInCourse(@PathVariable Integer userId, @PathVariable Integer courseId) {
        boolean isEnrolled = courseService.isUserEnrolledInCourse(userId, courseId);
        return ResponseEntity.ok(isEnrolled);
    }

    @PostMapping("/courses/enroll")
    public ResponseEntity<CandidateCourseResponse> enrollInCourse(@RequestParam Integer userId, @RequestParam Integer courseId) {
        try {
            // Enroll the user in the course
            courseService.enrollInCourse(userId, courseId);

            // Build the response containing enrolled courses
            CandidateCourseResponse response = CandidateCourseResponse.builder()
                    .userId(userId)
                    .courseId(courseId)
                    .build();

            // Return the response
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CandidateCourseResponse());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CandidateCourseResponse());
        }
    }

    // Endpoint to get recently added courses by the logged-in instructor
    @GetMapping("/instructors/recently-added-courses")
    public ResponseEntity<PageResponse<CourseResponse>> getRecentlyAddedCoursesByLoggedInInstructor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        // Get the username of the authenticated instructor
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String instructorUsername = authentication.getName();

        // Call the service method to fetch the recent courses for the instructor
        PageResponse<CourseResponse> recentlyAddedCourses = courseService.getRecentlyAddedCoursesByInstructor(instructorUsername, page, size);

        // Return the response with the fetched courses
        return ResponseEntity.ok(recentlyAddedCourses);
    }


    @GetMapping("/latest-courses")
    public ResponseEntity<PageResponse<CourseResponse>> getLatestCoursesForCandidates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<CourseResponse> latestCourses = courseService.getLatestCoursesForCandidates(page, size);
        return ResponseEntity.ok(latestCourses);
    }

    @DeleteMapping("/{courseId}/unenroll")
    public ResponseEntity<String> unenrollFromCourse(@PathVariable Integer courseId,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the user ID from the authentication principal
            String username = userDetails.getUsername();
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            // Call the service method to unenroll the user from the course
            courseService.unenrollFromCourse(user.getId(), courseId);

            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}



