        package com.pfe.elearning.course.service.serviceImpl;
        import com.pfe.elearning.CourseEnrollment.entity.CourseEnrollment;
        import com.pfe.elearning.CourseEnrollment.repository.CourseEnrollmentRepository;
        import com.pfe.elearning.common.PageResponse;
        import com.pfe.elearning.course.dto.CandidateCourseResponse;
        import com.pfe.elearning.course.dto.CourseRequest;
        import com.pfe.elearning.course.dto.CourseResponse;
        import com.pfe.elearning.course.entity.Course;
        import com.pfe.elearning.course.repository.CourseRepository;
        import com.pfe.elearning.course.dto.CourseMapper;
        import com.pfe.elearning.course.service.CourseService;
        import com.pfe.elearning.exception.ObjectValidationException;
        import com.pfe.elearning.exception.UnauthorizedAccessException;
        import com.pfe.elearning.topic.entity.Topic;
        import com.pfe.elearning.topic.repository.TopicRepository;
        import com.pfe.elearning.user.entity.User;
        import com.pfe.elearning.user.repository.UserRepository;
        import com.pfe.elearning.user.service.UserService;
        import com.pfe.elearning.validator.ObjectsValidator;
        import jakarta.persistence.EntityNotFoundException;
        import jakarta.transaction.Transactional;
        import jakarta.validation.ValidationException;
        import lombok.RequiredArgsConstructor;
        import org.springframework.data.domain.Page;
        import org.springframework.data.domain.PageRequest;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.security.core.userdetails.UserDetails;
        import org.springframework.stereotype.Service;
        import java.util.Collections;
        import java.util.HashSet;
        import java.util.List;
        import java.util.Set;
        import java.util.stream.Collectors;
        @Service
        @RequiredArgsConstructor
        @Transactional
        public class CourseServiceImpl implements CourseService {
        
            private final CourseRepository courseRepository;
            private final TopicRepository topicRepository;
            private final UserRepository userRepository;
            // private final EnrollmentRepository enrollmentRepository;
            private final CourseMapper courseMapper;
            private final UserService userService;
            private final ObjectsValidator<CourseRequest> validator;
        private final CourseEnrollmentRepository courseEnrollmentRepository;
        
            @Override
            public long countUsersEnrolledInCourse(Integer courseId) {
                try {
                    // Get the course by its ID
                    Course course = courseRepository.findById(courseId)
                            .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
        
                    // Return the count of enrolled candidates for the course
                    return course.getEnrolledCandidates().size();
                } catch (EntityNotFoundException ex) {
                    throw ex; // Re-throw EntityNotFoundException
                } catch (Exception ex) {
                    throw new RuntimeException("Error occurred while counting users enrolled in the course: " + ex.getMessage(), ex);
                }
            }
        
            @Override
            public boolean isUserEnrolledInCourse(Integer userId, Integer courseId) {
                // Check if the user exists
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        
                // Check if the course exists
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
        
                // Check if the user is enrolled in the course
                return course.getEnrolledCandidates().contains(user);
            }
        
            @Override
            public PageResponse<CourseResponse> findCoursesInBatch(int page, int size) {
                Page<Course> coursePage = courseRepository.findAll(PageRequest.of(page, size));
                List<CourseResponse> courseResponses = coursePage.getContent().stream()
                        .map(courseMapper::toCourseResponse)
                        .collect(Collectors.toList());
                return PageResponse.<CourseResponse>builder()
                        .content(courseResponses)
                        .totalPages(coursePage.getTotalPages())
                        .totalElements(coursePage.getTotalElements())
                        .build();
            }
        
        
        
        
            @Override
            public List<CourseResponse> getCoursesForCandidate(Integer candidateId) {
                // Find the candidate by ID
                User candidate = userRepository.findById(candidateId)
                        .orElseThrow(() -> new EntityNotFoundException("Candidate not found with ID: " + candidateId));
        
                // Get the courses enrolled by the candidate
                List<Course> enrolledCourses = candidate.getEnrolledCourses();
        
                // Map the enrolled courses to CourseResponse objects
                return enrolledCourses.stream()
                        .map(courseMapper::toCourseResponse)
                        .collect(Collectors.toList());
            }

            @Override
            public List<CourseResponse> getCoursesByPublisher(String publisherUsername) {
                // Implement the logic to retrieve courses by publisher username
                List<Course> courses = courseRepository.findByPublisherUsername(publisherUsername);

                return courses.stream()
                        .map(courseMapper::toCourseResponse)
                        .collect(Collectors.toList());
             }

            @Override
            public CourseResponse createCourse(CourseRequest courseRequest, String publisherUsername, String imageUrl) {
                try {
                    validator.validate(courseRequest);
                    User publisher = getUserByEmailOrThrowException(publisherUsername);
        
                    Course course = mapToCourse(courseRequest);
                    course.setPublisher(publisher);
                    course.setImageFile(imageUrl);
        
                    // Set the value of isFree based on the checkbox selection
                /*    if (courseRequest.isFree()) {
                        course.setFree(true);
                        course.setPrice(null);
                    } else {
                        course.setFree(false);
                        course.setPrice(courseRequest.getPrice()); // Set the price if the course is not free
                    }

                 */
        
                    Course savedCourse = courseRepository.save(course);
                    return courseMapper.toCourseResponse(savedCourse);
                } catch (ObjectValidationException ex) {
                    throw new ValidationException("Validation error occurred: " + ex.getMessage(), ex);
                } catch (EntityNotFoundException ex) {
                    throw new EntityNotFoundException("Error creating course: " + ex.getMessage(), ex);
                } catch (Exception ex) {
                    throw new RuntimeException("Error occurred while creating course: " + ex.getMessage(), ex);
                }
            }
        
        
            private void validateCourseRequest(CourseRequest courseRequest) {
                try {
                    validator.validate(courseRequest);
                } catch (ObjectValidationException ex) {
                    throw new ValidationException(ex.getMessage(), ex);
                }
            }
        
            private User getUserByEmailOrThrowException(String publisherUsername) {
                User publisher = userService.getUserByEmail(publisherUsername);
                if (publisher == null) {
                    throw new EntityNotFoundException("Publisher not found with username: " + publisherUsername);
                }
                return publisher;
            }
        
            private Course mapToCourse(CourseRequest courseRequest) {
                return courseMapper.toCourse(courseRequest);
            }
        
        
        
            @Override
            public CourseResponse getCourseById(Integer courseId) {
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
                return courseMapper.toCourseResponse(course);
            }
            @Override
            @Transactional
            public CandidateCourseResponse enrollInCourse(Integer userId, Integer courseId) {
                try {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        
                    Course course = courseRepository.findById(courseId)
                            .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
        
                    // Create a new CourseEnrollment entity
                    CourseEnrollment enrollment = new CourseEnrollment();
                    enrollment.setPublisher(course.getPublisher());
                    enrollment.setCandidate(user);
                    enrollment.setCourse(course);
                    enrollment.setCourseName(course.getTitle());
        
                    // Save the enrollment
                    courseEnrollmentRepository.save(enrollment);
        
                    // Adding the user to the enrolled candidates list
                    course.getEnrolledCandidates().add(user);
                    courseRepository.save(course);
        
                    // Return a response object with the user and course IDs, and the list of enrolled courses
                    return CandidateCourseResponse.builder()
                            .userId(userId)
                            .courseId(courseId)
                            .build();
                } catch (EntityNotFoundException e) {
                    // Log the exception or handle it accordingly
                    throw e; // Re-throw the exception for the controller to handle
                } catch (Exception e) {
                    // Log the exception or handle it accordingly
                    throw new RuntimeException("Error occurred while enrolling in course: " + e.getMessage(), e);
                }
            }
        
        
        
            @Override
            public void unenrollFromCourse(Integer userId, Integer courseId) {
                try {
                    // Find the user by ID
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        
                    // Find the course by ID
                    Course course = courseRepository.findById(courseId)
                            .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
        
                    // Check if the user is enrolled in the course
                    if (!course.getEnrolledCandidates().contains(user)) {
                        throw new IllegalStateException("User is not enrolled in the course.");
                    }
        
                    // Remove the user from the enrolled candidates list of the course
                    course.getEnrolledCandidates().remove(user);
        
                    // Save the course to update the changes
                    courseRepository.save(course);
                } catch (EntityNotFoundException ex) {
                    throw ex; // Re-throw EntityNotFoundException
                } catch (IllegalStateException ex) {
                    throw ex; // Re-throw IllegalStateException
                } catch (Exception ex) {
                    throw new RuntimeException("Error occurred while unenrolling from the course: " + ex.getMessage(), ex);
                }
            }
        
        
            /*@Override
            public void updateCourse(Integer courseId, CourseRequest courseRequest) {
                Course existingCourse = courseRepository.findById(courseId)
                        .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
        
                // Update existingCourse with data from courseRequest
                existingCourse.setTitle(courseRequest.getTitle());
                existingCourse.setDescription(courseRequest.getDescription());
                existingCourse.setDuration(courseRequest.getDuration());
                existingCourse.setImageUrl(courseRequest.getImageUrl());
        
                // Get the topic associated with the provided topicId
                Topic topic = topicRepository.findById(courseRequest.getTopicId())
                        .orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        
                // Set the topic for the existing course
                existingCourse.setTopic(topic);
        
                // Save the updated course
                Course updatedCourse = courseRepository.save(existingCourse);
        
                // Map the updated course to response
                courseMapper.toCourseResponse(updatedCourse);
            }
        
             */
            @Override
            @Transactional
            public CourseResponse updateCourse(Integer courseId, CourseRequest courseRequest, String imageUrl) {
                Course existingCourse = courseRepository.findById(courseId)
                        .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
        
                // Check if the imageUrl is null
                if (imageUrl == null) {
                    // If imageUrl is null, retain the existing image URL
                    imageUrl = existingCourse.getImageFile();
                }
        
                existingCourse.setTitle(courseRequest.getTitle());
                existingCourse.setDescription(courseRequest.getDescription());
                existingCourse.setImageFile(imageUrl); // Set the image URL
                existingCourse.setStartDate(courseRequest.getStartDate());
                existingCourse.setEndDate(courseRequest.getEndDate());
                //existingCourse.setFree(courseRequest.isFree());
                existingCourse.setTimeCommitment(courseRequest.getTimeCommitment());
                //existingCourse.setPrice(courseRequest.getPrice());
        
                courseRepository.save(existingCourse);
        
                return courseMapper.toCourseResponse(existingCourse);
            }
        
            @Override
            public void deleteCourse(Integer courseId) {
                try{
                    deleteEnrollmentsByCourseId(courseId);
                    Course course = courseRepository.findById(courseId)
                            .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
                    courseRepository.delete(course);
                } catch (EntityNotFoundException ex) {
                    throw ex; // Re-throw EntityNotFoundException
                } catch (Exception ex) {
                    throw new RuntimeException("Error occurred while deleting a course: " + ex.getMessage(), ex);
                }
            }
            private void deleteEnrollmentsByCourseId(Integer courseId) {
                courseEnrollmentRepository.deleteByCourseId(courseId);
            }
        
            @Override
            public PageResponse<CourseResponse> findAll(int page, int size) {
                try {
                    var pageResult = this.courseRepository.findAll(PageRequest.of(page, size));
                    return PageResponse.<CourseResponse>builder()
                            .content(
                                    pageResult.getContent()
                                            .stream()
                                            .map(courseMapper::toCourseResponse)
                                            .toList()
                            )
                            .totalPages(pageResult.getTotalPages())
                            .build();
                } catch (Exception ex) {
                    throw new RuntimeException("Error occurred while finding all courses: " + ex.getMessage(), ex);
                }
            }
            @Override
            public List<CourseResponse> getAllCourses() {
                try{
                    List<Course> courses = courseRepository.findAll();
                    return courses.stream()
                            .map(courseMapper::toCourseResponse)
                            .collect(Collectors.toList());
                } catch (Exception ex) {
                    throw new RuntimeException("Error occurred while finding all courses: " + ex.getMessage(), ex);
                }
            }
        
            @Override
            public boolean existsById(Integer id) {
                return courseRepository.existsById(id);
            }
        
            @Override
            public List<CourseResponse> findCourseByTitleContaining(String keyword) {
                List<Course> courses = courseRepository.findByTitleContaining(keyword);
                if (courses.isEmpty()) {
                    throw new EntityNotFoundException("No courses found containing keyword: " + keyword);
                }
        
                return courses.stream()
                        .map(courseMapper::toCourseResponse)
                        .collect(Collectors.toList());
            }
            @Override
            public List<CourseResponse> getCoursesByTopic(Integer topicId) {
                Topic topic = topicRepository.findById(topicId)
                        .orElseThrow(() -> new EntityNotFoundException("Topic not found with ID: " + topicId));
        
                List<Course> courses = courseRepository.findByTopic(topic);
                if (courses.isEmpty()) {
                    throw new EntityNotFoundException("No courses found for topic ID: " + topicId);
                }
        
                return courses.stream()
                        .map(courseMapper::toCourseResponse)
                        .collect(Collectors.toList());
            }
            @Override
            public PageResponse<CourseResponse> getCoursesByTopic(Integer topicId, int page, int size) {
                try {
                    // Fetch the topic entity from the repository
                    Topic topic = topicRepository.findById(topicId)
                            .orElseThrow(() -> new EntityNotFoundException("Topic not found with ID: " + topicId));

                    // Fetch courses using the updated repository method
                    Page<Course> coursePage = courseRepository.findByTopicId(topicId, PageRequest.of(page, size));

                    // Map the fetched courses to CourseResponse objects
                    List<CourseResponse> courseResponses = coursePage.getContent()
                            .stream()
                            .map(courseMapper::toCourseResponse)
                            .collect(Collectors.toList());

                    // Create a PageResponse object with the fetched courses
                    return PageResponse.<CourseResponse>builder()
                            .content(courseResponses)
                            .totalPages(coursePage.getTotalPages())
                            .build();
                } catch (Exception ex) {
                    throw new RuntimeException("Error occurred while finding courses: " + ex.getMessage(), ex);
                }
            }




            @Override
            public List<CourseResponse> getAllCoursesByInstructor(String instructorUsername) {
                try {
                    // Retrieve the user (instructor) by their username
                    User instructor = userService.getUserByEmail(instructorUsername);
        
                    // If the instructor is not found, return an empty list
                    if (instructor == null) {
                        return Collections.emptyList();
                    }
        
                    // Retrieve all courses created by the instructor
                    List<Course> courses = courseRepository.findByPublisher(instructor);
        
                    // Map the courses to CourseResponse objects
                    return courses.stream()
                            .map(courseMapper::toCourseResponse)
                            .collect(Collectors.toList());
                } catch (Exception ex) {
                    throw new RuntimeException("Error occurred while fetching courses by instructor: " + ex.getMessage(), ex);
                }
            }
        
            @Override
            public PageResponse<CourseResponse> getAllCoursesByInstructor(String instructorUsername, int page, int size) {
                try {
                    // Retrieve the user (instructor) by their username
                    User instructor = userService.getUserByEmail(instructorUsername);
        
                    // If the instructor is not found, return an empty list
                    if (instructor == null) {
                        return PageResponse.<CourseResponse>builder()
                                .content(Collections.emptyList())
                                .totalPages(0)
                                .totalElements(0L)
                                .build();
                    }
        
                    // Retrieve courses for the specified page and size
                    Page<Course> coursesPage = courseRepository.findByPublisher(instructor, PageRequest.of(page, size));
        
                    // Map the courses to CourseResponse objects
                    List<CourseResponse> courseResponses = coursesPage.getContent().stream()
                            .map(courseMapper::toCourseResponse)
                            .collect(Collectors.toList());
        
                    // Create and return the PageResponse object
                    return PageResponse.<CourseResponse>builder()
                            .content(courseResponses)
                            .totalPages(coursesPage.getTotalPages())
                            .totalElements(coursesPage.getTotalElements())
                            .build();
                } catch (Exception ex) {
                    throw new RuntimeException("Error occurred while fetching courses by instructor: " + ex.getMessage(), ex);
                }
            }
        
        
            @Override
            public List<CourseResponse> findInstructorCourseByTitleContaining(String keyword) {
                try {
                    // Retrieve all courses of the current logged-in instructor
                    List<CourseResponse> instructorCourses = getAllCoursesByInstructor(getCurrentLoggedInUser().getUsername());
        
                    // Filter the courses by title containing the provided keyword
                    List<CourseResponse> filteredCourses = instructorCourses.stream()
                            .filter(course -> course.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                            .collect(Collectors.toList());
        
                    return filteredCourses;
                } catch (Exception ex) {
                    throw new RuntimeException("Error occurred while fetching instructor courses by title containing keyword: " + ex.getMessage(), ex);
                }
            }
            public UserDetails getCurrentLoggedInUser() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                    return (UserDetails) authentication.getPrincipal();
                }
                return null; // Or throw an exception if user is not authenticated
            }
        
                @Override
                public PageResponse<CourseResponse> getRecentlyAddedCoursesByInstructor(String instructorUsername, int page, int size) {
                    try {
                        // Find the instructor by email
                        User instructor = userService.getUserByEmail(instructorUsername);
        
                        // Handle case where user is not found
                        if (instructor == null) {
                            throw new EntityNotFoundException("Instructor not found with email: " + instructorUsername);
                        }
        
                        // Fetch recently added courses by the instructor
                        Page<Course> coursePage = courseRepository.findByPublisherOrderByCreatedAtDesc(instructor, PageRequest.of(page, size));
        
                        // Map the courses to CourseResponse objects
                        List<CourseResponse> courseResponses = coursePage.getContent().stream()
                                .map(courseMapper::toCourseResponse)
                                .collect(Collectors.toList());
        
                        // Return the page response object
                        return PageResponse.<CourseResponse>builder()
                                .content(courseResponses)
                                .totalPages(coursePage.getTotalPages())
                                .totalElements(coursePage.getTotalElements())
                                .build();
                    } catch (EntityNotFoundException ex) {
                        throw ex; // Re-throw EntityNotFoundException
                    } catch (Exception ex) {
                        throw new RuntimeException("Error occurred while fetching recently added courses by instructor: " + ex.getMessage(), ex);
                    }
                }
        
            @Override
            public PageResponse<CourseResponse> getLatestCoursesForCandidates(int page, int size) {
                try {
                    // Fetch the latest added courses
                    Page<Course> coursePage = courseRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
        
                    // Map the courses to CourseResponse objects
                    List<CourseResponse> courseResponses = coursePage.getContent().stream()
                            .map(courseMapper::toCourseResponse)
                            .collect(Collectors.toList());
        
                    // Return the page response object
                    return PageResponse.<CourseResponse>builder()
                            .content(courseResponses)
                            .totalPages(coursePage.getTotalPages())
                            .totalElements(coursePage.getTotalElements())
                            .build();
                } catch (Exception ex) {
                    throw new RuntimeException("Error occurred while fetching latest courses for candidates: " + ex.getMessage(), ex);
                }
            }
            @Override
            public long countEnrolledCandidatesByInstructorForCourse(String instructorUsername, Integer courseId) {
                // Retrieve the user (instructor) by their username
                User instructor = userService.getUserByEmail(instructorUsername);
        
                // If the instructor is not found, return 0 as there are no enrolled candidates
                if (instructor == null) {
                    return 0;
                }
        
                // Retrieve the course by ID
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
        
                // Check if the course belongs to the instructor
                if (!course.getPublisher().equals(instructor)) {
                    throw new UnauthorizedAccessException("This course does not belong to the instructor: " + instructorUsername);
                }
        
                // Return the count of enrolled candidates for the course
                return course.getEnrolledCandidates().size();
            }

            @Override
            public String getCourseTitleById(Integer courseId) {
                // Retrieve the currently logged-in user
                UserDetails userDetails = getCurrentLoggedInUser();

                // Check if the user is authenticated
                if (userDetails != null) {
                    // Check if the user is an instructor
                    if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"))) {
                        // Retrieve the course by its ID
                        Course course = courseRepository.findById(courseId)
                                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));

                        // Check if the course publisher matches the authenticated instructor
                        if (course.getPublisher().getEmail().equals(userDetails.getUsername())) {
                            // Return the title of the course
                            return course.getTitle();
                        } else {
                            throw new UnauthorizedAccessException("You are not authorized to access this course title.");
                        }
                    } else {
                        throw new UnauthorizedAccessException("Only instructors are allowed to access course titles.");
                    }
                } else {
                    throw new UnauthorizedAccessException("User is not authenticated.");
                }
            }
            @Override
            public long countCoursesByInstructor(String instructorUsername) {
                // Retrieve the user (instructor) by their username
                User instructor = userService.getUserByEmail(instructorUsername);
        
                // If the instructor is not found, return 0 as there are no courses
                if (instructor == null) {
                    return 0;
                }
                List<Course> courses = courseRepository.findByPublisher(instructor);
        
                // Return the count of courses by the instructor's username
                return courses.size();
            }
            @Override
            public long countAllEnrolledCandidatesForAllCoursesByInstructor(String instructorUsername) {
                // Retrieve the user (instructor) by their username
                User instructor = userService.getUserByEmail(instructorUsername);
        
                // If the instructor is not found, return 0 as there are no enrolled candidates
                if (instructor == null) {
                    return 0;
                }
        
                // Retrieve all courses taught by the instructor
                List<Course> courses = courseRepository.findByPublisher(instructor);
        
                // Initialize a HashSet to store distinct candidate IDs
                Set<Integer> distinctCandidateIds = new HashSet<>();
        
                // Iterate through each course and collect distinct candidate IDs
                for (Course course : courses) {
                    distinctCandidateIds.addAll(course.getEnrolledCandidates().stream()
                            .map(candidate -> candidate.getId())
                            .collect(Collectors.toSet()));
                }
                // Return the count of distinct candidate IDs
                return distinctCandidateIds.size();
            }
        
        
        }
        
        
