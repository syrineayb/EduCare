package com.pfe.elearning.lesson.service.ServiceImpl;


import com.pfe.elearning.UserResourceCompletion.UserCourseCompletion.UserCourseCompletion;
import com.pfe.elearning.UserResourceCompletion.UserCourseCompletion.UserCourseCompletionRepository;
import com.pfe.elearning.UserResourceCompletion.UserResourceCompletionRepository;
import com.pfe.elearning.UserResourceCompletion.UserlessonCompletion.UserlessonCompletionRepository;
import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.course.repository.CourseRepository;
import com.pfe.elearning.forum.entity.Forum;
import com.pfe.elearning.forum.service.ForumService;
import com.pfe.elearning.lesson.dto.LessonMapper;
import com.pfe.elearning.lesson.dto.LessonRequest;
import com.pfe.elearning.lesson.dto.LessonResponse;
import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.lesson.repository.LessonRepository;
import com.pfe.elearning.lesson.service.LessonService;
import com.pfe.elearning.resource.repository.ResourceRepository;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final UserService userService;
    private final ForumService forumService;
    private final ResourceRepository resourceRepository;
    private final UserlessonCompletionRepository userlessonCompletionRepository;
    private final UserCourseCompletionRepository userCourseCompletionRepository;
    private final UserResourceCompletionRepository userResourceCompletionRepository;

    @Override
    public long countLessonsByCourseId(Integer courseId) {

        return lessonRepository.countByCourseId(courseId);
    }

    @Override
    public long countCompletedLessonsByUserAndCourseId(String candidateUsername, Integer courseId) {
        User user = userService.getUserByEmail(candidateUsername);
        return userlessonCompletionRepository.countByUserAndLesson_Course_IdAndCompleted(user, courseId, true);
    }

    @Override
    public void updateCourseCompletionStatus(String candidateUsername, Integer courseId) {
        User user = userService.getUserByEmail(candidateUsername);
        long completedCount = userlessonCompletionRepository.countByUserAndLesson_Course_IdAndCompleted(user, courseId, true);
        long totalLessons = lessonRepository.countByCourseId(courseId);

        if (completedCount == totalLessons) {
            UserCourseCompletion courseCompletion = userCourseCompletionRepository.findByUserAndCourse(user, courseRepository.findById(courseId)
                            .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId)))
                    .orElse(new UserCourseCompletion(null, user, courseRepository.findById(courseId)
                            .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId)), false));
            courseCompletion.setCompleted(true);
            userCourseCompletionRepository.save(courseCompletion);
        }
    }


    @Override
    public int getUserCourseProgress(String candidateUsername, Integer courseId) {
        User user = userService.getUserByEmail(candidateUsername);

        // Fetch all lessons in the course
        List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
        if (lessons.isEmpty()) {
            return 0;
        }

        double totalProgress = 0;
        for (Lesson lesson : lessons) {
            // Calculate the progress of each lesson based on resources
            long completedResources = userResourceCompletionRepository.countByUserAndResource_Lesson_IdAndCompleted(user, lesson.getId(), true);
            long totalResources = resourceRepository.countByLessonId(lesson.getId());

            if (totalResources > 0) {
                double lessonProgress = (double) completedResources / totalResources;
                totalProgress += lessonProgress;
            }
        }

        // Average progress over all lessons
        double overallProgress = totalProgress / lessons.size();
        return (int) Math.round(overallProgress * 100); // Convert to percentage and round to integer
    }


    @Override
    public LessonResponse createLesson(Integer courseId, LessonRequest lessonRequest) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found " + courseId));

        Lesson lesson = lessonMapper.toLesson(lessonRequest, course);
        lesson.setCourse(course);
        Lesson savedLesson = lessonRepository.save(lesson);

        // Create a forum for the lesson
        Forum forum = new Forum();
        forum.setTitle("Forum for Lesson: " + savedLesson.getTitle());
        forum.setLesson(savedLesson);
        forumService.createForumForLesson(forum);
        return lessonMapper.toLessonResponse(savedLesson);
    }


    @Override
    public LessonResponse updateLesson(Integer lessonId, LessonRequest lessonRequest) {
        Lesson existingLesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found for ID: " + lessonId));
        existingLesson.setTitle(lessonRequest.getTitle());
        existingLesson.setDescription(lessonRequest.getDescription());
        Lesson updatedLesson = lessonRepository.save(existingLesson);

        return lessonMapper.toLessonResponse(updatedLesson);
    }

    @Override
    @Transactional
    public void deleteLesson(Integer lessonId) {
        try {
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new EntityNotFoundException("Lesson not found with ID: " + lessonId));
            lessonRepository.delete(lesson);
        } catch (EntityNotFoundException ex) {
            throw ex; // Re-throw EntityNotFoundException
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while deleting a course: " + ex.getMessage(), ex);
        }
    }

    @Override
    public LessonResponse getLessonById(Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + lessonId));
        return lessonMapper.toLessonResponse(lesson);
    }


    @Override
    public PageResponse<LessonResponse> getAllLessonsByCourse(Integer courseId, int page, int size) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        // Adjust page number to start from 0 instead of 1
        Page<Lesson> lessonPage = lessonRepository.findAllByCourse(course, PageRequest.of(page - 1, size));

        List<LessonResponse> lessonResponses = lessonPage.getContent()
                .stream()
                .map(lessonMapper::toLessonResponse)
                .collect(Collectors.toList());

        return PageResponse.<LessonResponse>builder()
                .content(lessonResponses)
                .totalPages(lessonPage.getTotalPages())
                .totalElements(lessonPage.getTotalElements())
                .build();
    }
    @Override
    public List<LessonResponse> getAllLessonsByCourse(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        // Adjust page number to start from 0 instead of 1
        List<Lesson> lessons = lessonRepository.findAllByCourse(course);


        return lessons.stream()
                .map(lessonMapper::toLessonResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LessonResponse> findLessonByTitleContaining(String keyword) {
        List<Lesson> lessons = lessonRepository.findByTitleContaining(keyword);
        return lessons.stream()
                .map(lessonMapper::toLessonResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LessonResponse> getAllLessons() {
        List<Lesson> lessons = lessonRepository.findAll();
        return lessons.stream()
                .map(lessonMapper::toLessonResponse)
                .collect(Collectors.toList());
    }


}

/*
implements LessonService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final CourseRepository courseRepository;
    // private final UserService userService;
    private final ObjectsValidator<LessonRequest> validator;
    private final FileService fileService;
    @Override
    public LessonResponse createLesson(LessonRequest lessonRequest) {
        Lesson lesson = lessonMapper.toLesson(lessonRequest);

        // Save resources and get their URLs
        List<String> resourceUrls = saveResources(lessonRequest.getResources());

        // Create Resource objects for each URL and associate them with the lesson
        List<Resource> resources = resourceUrls.stream()
                .map(url -> {
                    Resource resource = new Resource();
                    resource.setUrl(url);
                    resource.setTitle(resource.getTitle());
                    resource.setCreatedAt(LocalDateTime.now());
                    // Set any other properties of Resource as needed
                    return resource;
                })
                .collect(Collectors.toList());

        // Set the list of resources in the lesson
        lesson.setResources(resources);

        Lesson savedLesson = lessonRepository.save(lesson);
        return lessonMapper.toLessonResponse(savedLesson);
    }

    private List<String> saveResources(List<MultipartFile> resources) {
        return resources.stream()
                .map(fileService::save)
                .collect(Collectors.toList());
    }
    /*@Override
    @Transactional
    public LessonResponse createLesson(@Valid LessonRequest lessonRequest) {
        validator.validate(lessonRequest);

        Lesson lesson = lessonMapper.toLesson(lessonRequest);
        Course course = courseRepository.findById(lessonRequest.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        lesson.setCourse(course);

        Lesson savedLesson = lessonRepository.save(lesson);
        return lessonMapper.toLessonResponse(savedLesson);
    }

     */



  /* deja commenté
    @Override
    @Transactional
    public LessonResponse updateLesson(Integer lessonId, @Valid LessonRequest lessonRequest) {
        validator.validate(lessonRequest);
        Lesson existingLesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + lessonId));

        existingLesson.setTitle(lessonRequest.getTitle());
        existingLesson.setDescription(lessonRequest.getDescription());
        lessonRepository.save(existingLesson);
        return lessonMapper.toLessonResponse(existingLesson);
    }

    @Override
    @Transactional
    public void deleteLesson(Integer lessonId) {
        try {
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new EntityNotFoundException("Lesson not found with ID: " + lessonId));
            lessonRepository.delete(lesson);
        } catch (EntityNotFoundException ex) {
            throw ex; // Re-throw EntityNotFoundException
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while deleting a course: " + ex.getMessage(), ex);
        }
    }

    @Override
    public LessonResponse getLessonById(Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + lessonId));
        return lessonMapper.toLessonResponse(lesson);
    }

    @Override
    public PageResponse<LessonResponse> getAllLessonsByCourse(Integer courseId, int page, int size) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course == null) {
            return PageResponse.<LessonResponse>builder()
                    .content(Collections.emptyList())
                    .totalPages(0)
                    .totalElements(0L)
                    .build();
        }

        Page<Lesson> lessonsPage = lessonRepository.findByCourse(course, PageRequest.of(page, size));

        List<LessonResponse> lessonResponses = lessonsPage.getContent().stream()
                .map(lessonMapper::toLessonResponse)
                .collect(Collectors.toList());

        // Create and return the PageResponse object
        return PageResponse.<LessonResponse>builder()
                .content(lessonResponses)
                .totalPages(lessonsPage.getTotalPages())
                .totalElements(lessonsPage.getTotalElements())
                .build();
    }

    @Override
    public List<LessonResponse> findLessonByTitleContaining(String keyword) {
        List<Lesson> lessons = lessonRepository.findByTitleContaining(keyword);
        return lessons.stream()
                .map(lessonMapper::toLessonResponse)
                .collect(Collectors.toList());
    }
}

   */



/* deja commente
    @Override
    public LessonResponse createLesson(LessonRequest lessonRequest, String publisherUsername) {
        validator.validate(lessonRequest);

        User publisher = userService.getUserByEmail(publisherUsername);

        Course course = courseRepository.findById(lessonRequest.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        Lesson lesson = lessonMapper.toLesson(lessonRequest);
        lesson.setCourse(course);
        Lesson savedLesson = lessonRepository.save(lesson);

        return lessonMapper.toLessonResponse(savedLesson);
    }

    @Override
    public LessonResponse getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .map(lessonMapper::toLessonResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(Long lessonId, LessonRequest lessonRequest, String publisherUsername) {
        Lesson existingLesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + lessonId));

        Course course = existingLesson.getCourse();
        String coursePublisherUsername = course.getPublisher().getEmail();

        existingLesson.setTitle(lessonRequest.getTitle());
        existingLesson.setDescription(lessonRequest.getDescription());
        existingLesson.setCourse(course);

        Lesson updatedLesson = lessonRepository.save(existingLesson);
        return lessonMapper.toLessonResponse(updatedLesson);
    }

    @Override
    public void deleteLesson(Long lessonId, String publisherUsername) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + lessonId));

        Course course = lesson.getCourse();
        String coursePublisherUsername = course.getPublisher().getEmail();

        lessonRepository.delete(lesson);
    }

    @Override
    public PageResponse<LessonResponse> getAllLessons(int page, int size) {
        Page<Lesson> lessonPage = lessonRepository.findAll(PageRequest.of(page, size));
        return new PageResponse<>(
                lessonPage.getContent().stream()
                        .map(lessonMapper::toLessonResponse)
                        .toList(),
                lessonPage.getTotalPages(),
                lessonPage.getTotalElements()
        );
    }

    @Override
    public PageResponse<LessonResponse> findLessonByTitleContaining(String keyword, int page, int size) {
        Page<Lesson> lessonPage = lessonRepository.findByTitleContaining(keyword, PageRequest.of(page, size));
        return new PageResponse<>(
                lessonPage.getContent().stream()
                        .map(lessonMapper::toLessonResponse)
                        .toList(),
                lessonPage.getTotalPages(),
                lessonPage.getTotalElements()
        );
    }


 */

