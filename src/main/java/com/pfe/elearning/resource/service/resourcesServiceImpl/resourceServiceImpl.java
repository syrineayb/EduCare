package com.pfe.elearning.resource.service.resourcesServiceImpl;

import com.pfe.elearning.UserResourceCompletion.UserCourseCompletion.UserCourseCompletion;
import com.pfe.elearning.UserResourceCompletion.UserCourseCompletion.UserCourseCompletionRepository;
import com.pfe.elearning.UserResourceCompletion.UserResourceCompletion;
import com.pfe.elearning.UserResourceCompletion.UserResourceCompletionRepository;
import com.pfe.elearning.UserResourceCompletion.UserlessonCompletion.UserLessonCompletion;
import com.pfe.elearning.UserResourceCompletion.UserlessonCompletion.UserlessonCompletionRepository;
import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.lesson.repository.LessonRepository;
import com.pfe.elearning.resource.dto.ResourceMapper;
import com.pfe.elearning.resource.dto.ResourceRequest;
import com.pfe.elearning.resource.dto.ResourceResponse;
import com.pfe.elearning.resource.entity.Resourcee;
import com.pfe.elearning.resource.repository.ResourceRepository;
import com.pfe.elearning.resource.service.ResourceService;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.repository.UserRepository;
import com.pfe.elearning.user.service.UserService;
import com.pfe.elearning.validator.ObjectsValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class resourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final LessonRepository lessonRepository;
    private final ResourceMapper resourceMapper;
    private final ObjectsValidator<ResourceRequest> validator;
    private final UserService userService;
    private final UserResourceCompletionRepository userResourceCompletionRepository;
    private final UserRepository userRepository;
    private final UserCourseCompletionRepository userCourseCompletionRepository;
    private final UserlessonCompletionRepository userlessonCompletionRepository;
    private final UserlessonCompletionRepository userLessonCompletionRepository;
    @Override
    public ResourceResponse createResource(ResourceRequest request, Integer lessonId, String imageUrl, String videoUrl, String pdfUrl) {
        // Retrieve the lesson by its ID
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with ID: " + lessonId));

        // Map the request to a Resource entity
        Resourcee newResource = resourceMapper.toResource(request, lesson);
        newResource.setImageFile(imageUrl);
        newResource.setVideoFile(videoUrl);
        newResource.setPdfFile(pdfUrl);
        Resourcee savedResource = resourceRepository.save(newResource);
        return resourceMapper.toResourceResponse(savedResource);

    }



    @Override
    public long countCompletedResourcesByUserAndLessonId(String candidateUsername, Integer lessonId) {
        User user = userService.getUserByEmail(candidateUsername);
        return userResourceCompletionRepository.countByUserAndResource_Lesson_IdAndCompleted(user, lessonId, true);
    }

    @Override
    public void markResourceAsCompleted(Integer resourceId, String candidateUsername) {
        User user = userService.getUserByEmail(candidateUsername);

        Resourcee resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + resourceId));

        UserResourceCompletion completion = userResourceCompletionRepository.findByUserAndResource(user, resource)
                .orElse(new UserResourceCompletion(null, user, resource, false));

        completion.setCompleted(true);
        userResourceCompletionRepository.save(completion);

        // Check if all resources in the lesson are completed
        long completedCount = userResourceCompletionRepository.countByUserAndResource_Lesson_IdAndCompleted(user, resource.getLesson().getId(), true);
        long totalResources = resourceRepository.countByLessonId(resource.getLesson().getId());

        if (completedCount == totalResources) {
            UserLessonCompletion lessonCompletion = userLessonCompletionRepository.findByUserAndLesson(user, resource.getLesson())
                    .orElse(new UserLessonCompletion(null, user, resource.getLesson(), false));
            lessonCompletion.setCompleted(true);
            userLessonCompletionRepository.save(lessonCompletion);


            // Check if all lessons in the course are completed
            long completedLessonsCount = userlessonCompletionRepository.countByUserAndLesson_Course_IdAndCompleted(user, resource.getLesson().getCourse().getId(), true);
            long totalLessons = lessonRepository.countByCourseId(resource.getLesson().getCourse().getId());

            if (completedLessonsCount == totalLessons) {
                UserCourseCompletion courseCompletion = userCourseCompletionRepository.findByUserAndCourse(user, resource.getLesson().getCourse())
                        .orElse(new UserCourseCompletion(null, user, resource.getLesson().getCourse(), false));
                courseCompletion.setCompleted(true);
                userCourseCompletionRepository.save(courseCompletion);
            }
        }
    }
    @Override
    public int getUserLessonProgress(String candidateUsername, Integer lessonId) {
        User user = userService.getUserByEmail(candidateUsername);
        long completedCount = userResourceCompletionRepository.countByUserAndResource_Lesson_IdAndCompleted(user, lessonId, true);
        long totalResources = resourceRepository.countByLessonId(lessonId);

        if (totalResources == 0) {
            return 0;
        }

        // Correctly perform the division as double and round the result to the nearest integer
        double progress = ((double) completedCount / totalResources) * 100;
        return (int) Math.round(progress);
    }


    private long countResourcesByLessonId(Integer lessonId) {
        return resourceRepository.countByLessonId(lessonId);
    }

    public int countVideosByLessonId(Integer lessonId) {
        return resourceRepository.countByLessonIdAndVideoFileIsNotNull(lessonId);
    }

    @Override
    public int countImagesByLessonId(Integer lessonId) {
        return resourceRepository.countByLessonIdAndImageFileIsNotNull(lessonId);
    }

    @Override
    public int countPDFsByLessonId(Integer lessonId) {
        return resourceRepository.countByLessonIdAndPdfFileIsNotNull(lessonId);
    }


//    @Override
//    public void markResourceAsCompleted(Integer resourceId) {
//        // Retrieve the resource from the database
//        Resourcee resource = resourceRepository.findById(resourceId)
//                .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + resourceId));
//
//        // Mark the resource as completed
//        resource.setCompleted(true);
//        resourceRepository.save(resource);
//    }



    @Override
    public Resourcee findById(Integer resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + resourceId));
    }






    @Override
    public ResourceResponse updateResource(Integer resourceId, ResourceRequest request, String imageUrl, String pdfUrl, String videoUrl) {
        Resourcee existingResource= resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Ressource not found with id: " + resourceId));

// Check if the imageUrl is null
        if (imageUrl == null) {
            // If imageUrl is null, retain the existing image URL
            imageUrl = existingResource.getImageFile();
        }
        if (videoUrl == null) {
            // If imageUrl is null, retain the existing image URL
            videoUrl = existingResource.getVideoFile();
        }
        if (pdfUrl == null) {
            // If imageUrl is null, retain the existing image URL
            pdfUrl = existingResource.getPdfFile();
        }
        existingResource.setImageFile(imageUrl);
        existingResource.setPdfFile(pdfUrl);
        existingResource.setVideoFile(videoUrl);
        resourceRepository.save(existingResource);
        return resourceMapper.toResourceResponse(existingResource);
    }

    @Override
    public ResourceResponse getResourceById(Integer resourceId) {
        Resourcee resource = findById(resourceId);
        return mapToResourceResponse(resource);
    }

    @Override
    public void deleteResource(Integer resourceId) {
        resourceRepository.deleteById(resourceId);
    }

    @Override
    public List<ResourceResponse> getAllResourcesByLessonId(Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with ID: " + lessonId));

        List<Resourcee> resources = lesson.getResources();

        return resources.stream()
                .map(this::mapToResourceResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<ResourceResponse> getResourcesByLessonIdWithPagination(int lessonId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Resourcee> resourcePage = (Page<Resourcee>) resourceRepository.findByLessonId(lessonId, pageable);

        List<ResourceResponse> resourceResponses = resourcePage.getContent().stream()
                .map(resourceMapper::toResourceResponse)
                .collect(Collectors.toList());

        return PageResponse.<ResourceResponse>builder()
                .content(resourceResponses)
                .totalPages(resourcePage.getTotalPages())
                .totalElements(resourcePage.getTotalElements())
                .build();
    }

    @Override
    public PageResponse<ResourceResponse>findAll(int page, int size) {
        Page<Resourcee> resourcePage = resourceRepository.findAll(PageRequest.of(page, size));
        List<ResourceResponse>resourceResponses = resourcePage.getContent().stream()
                .map(resourceMapper::toResourceResponse)
                .collect(Collectors.toList());
        return PageResponse.<ResourceResponse>builder()
                .content(resourceResponses)
                .totalPages(resourcePage.getTotalPages())
                .totalElements(resourcePage.getTotalElements())
                .build();
    }

    @Override
    public List<ResourceResponse> getAllResources() {
        List<Resourcee> resources = resourceRepository.findAll();
        return resources.stream()
                .map(resourceMapper::toResourceResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<ResourceResponse> findResourcesInBatch(int page, int size) {
        Page<Resourcee> resourcePage = resourceRepository.findAll(PageRequest.of(page, size));
        List<ResourceResponse> resourceResponses = resourcePage.getContent().stream()
                .map(resourceMapper::toResourceResponse)
                .collect(Collectors.toList());
        return PageResponse.<ResourceResponse>builder()
                .content(resourceResponses)
                .totalPages(resourcePage.getTotalPages())
                .totalElements(resourcePage.getTotalElements())
                .build();
    }
    // Helper method to map Resource to ResourceResponse
    private ResourceResponse mapToResourceResponse(Resourcee resource) {
        ResourceResponse response = new ResourceResponse();
        response.setId(resource.getId());
        response.setPdfFile(resource.getPdfFile());
        response.setVideoFile(resource.getVideoFile());
        response.setImageFile(resource.getImageFile());
        response.setLessonTitle(resource.getLesson().getTitle());
        // Map other resource properties as needed
        return response;
    }
}
