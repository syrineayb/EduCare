package com.pfe.elearning.resource.controller;

import com.pfe.elearning.UserResourceCompletion.UserLessonProgressResponse;
import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.file.services.FileService;
import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.lesson.repository.LessonRepository;
import com.pfe.elearning.resource.dto.CountResourceResponse;
import com.pfe.elearning.resource.dto.ResourceRequest;
import com.pfe.elearning.resource.dto.ResourceResponse;
import com.pfe.elearning.resource.service.ResourceService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/resourcess")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final LessonRepository lessonRepository;
    @Autowired
    @Qualifier("CourseImageServiceImpl")
    private FileService fileService;

    @GetMapping("/lesson/resources")
    public ResponseEntity<PageResponse<ResourceResponse>>getResourcesByLessonIdWithPagination(
            @RequestParam int lessonId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ResourceResponse> resources = resourceService.getResourcesByLessonIdWithPagination(lessonId, page, size);
        return ResponseEntity.ok(resources);
    }
    @GetMapping("/resources/findAll")
    public ResponseEntity<PageResponse <ResourceResponse>> findAllResources(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "1", required = false) int size) {
        return ResponseEntity.ok(resourceService.findAll(page, size));
    }

    @GetMapping("/resources")
    public ResponseEntity<PageResponse<ResourceResponse>> getResourcesInBatch(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size) {
        PageResponse<ResourceResponse> resourcesPage = resourceService.findResourcesInBatch(page, size);
        return ResponseEntity.ok(resourcesPage);
    }

    @GetMapping("/lesson/{lessonId}/progress")
    public ResponseEntity<UserLessonProgressResponse> getUserLessonProgress(@PathVariable Integer lessonId,
                                                                            @AuthenticationPrincipal UserDetails userDetails) {
        String candidateUsername = userDetails.getUsername();
        int progress = resourceService.getUserLessonProgress(candidateUsername, lessonId);
        return ResponseEntity.ok(new UserLessonProgressResponse(progress));
    }
    @PutMapping("/markAsCompleted")
    public ResponseEntity<Void> markResourceAsCompleted(@RequestParam Integer resourceId,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        String candidateUsername = userDetails.getUsername();
        resourceService.markResourceAsCompleted(resourceId, candidateUsername);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/count/videos/{lessonId}")
    public ResponseEntity<CountResourceResponse> countVideosByLessonId(@PathVariable Integer lessonId) {
        int count = resourceService.countVideosByLessonId(lessonId);
        return new ResponseEntity<>(new CountResourceResponse(count), HttpStatus.OK);
    }

    @GetMapping("/count/images/{lessonId}")
    public ResponseEntity<CountResourceResponse> countImagesByLessonId(@PathVariable Integer lessonId) {
        int count = resourceService.countImagesByLessonId(lessonId);
        return new ResponseEntity<>(new CountResourceResponse(count), HttpStatus.OK);
    }

    @GetMapping("/count/pdfs/{lessonId}")
    public ResponseEntity<CountResourceResponse> countPDFsByLessonId(@PathVariable Integer lessonId) {
        int count = resourceService.countPDFsByLessonId(lessonId);
        return new ResponseEntity<>(new CountResourceResponse(count), HttpStatus.OK);
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceResponse> getResourceById(@PathVariable Integer resourceId) {
        ResourceResponse response = resourceService.getResourceById(resourceId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create/{lessonId}")
    public ResponseEntity<ResourceResponse> createResource(@Valid ResourceRequest request,
                                                           @PathVariable Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with ID: " + lessonId));

        // Initialize URLs for image, video, and PDF files
        String imageUrl = null;
        String videoUrl = null;
        String pdfUrl = null;

        // Check if the files are present in the request
        if (request.getImageFile() != null) {
            MultipartFile imageFile = request.getImageFile();
            String imageFileName = fileService.save(imageFile);
            imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/courses/downloadCourseImage/")
                    .path(imageFileName)
                    .toUriString();
        }

        if (request.getVideoFile() != null) {
            MultipartFile videoFile = request.getVideoFile();
            String videoFileName = fileService.save(videoFile);
            videoUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/courses/downloadCourseImage/")
                    .path(videoFileName)
                    .toUriString();
        }

        if (request.getPdfFile() != null) {
            MultipartFile pdfFile = request.getPdfFile();
            String pdfFileName = fileService.save(pdfFile);
            pdfUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/courses/downloadCourseImage/")
                    .path(pdfFileName)
                    .toUriString();
        }

        // Create the resource using ResourceService
        ResourceResponse response = resourceService.createResource(request, lessonId, imageUrl, videoUrl, pdfUrl);

        // Return the response
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/downloadCourseImage/{filename:.+}")
    public ResponseEntity<Resource> downloadCourseImage(@PathVariable String filename) {
        Resource fileResource = fileService.load(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }

    @PutMapping("/update/{resourceId}")
    public ResponseEntity<ResourceResponse> updateResource(@PathVariable("resourceId") Integer resourceId,
                                                           @Valid @ModelAttribute ResourceRequest request) {
        // Initialize URLs for image, video, and PDF files
        String imageUrl = null;
        String videoUrl = null;
        String pdfUrl = null;

        // Check if the files are present in the request
        if (request.getImageFile() != null) {
            MultipartFile imageFile = request.getImageFile();
            String imageFileName = fileService.save(imageFile);
            imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/courses/downloadCourseImage/")
                    .path(imageFileName)
                    .toUriString();
        }

        if (request.getVideoFile() != null) {
            MultipartFile videoFile = request.getVideoFile();
            String videoFileName = fileService.save(videoFile);
            videoUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/courses/downloadCourseImage/")
                    .path(videoFileName)
                    .toUriString();
        }

        if (request.getPdfFile() != null) {
            MultipartFile pdfFile = request.getPdfFile();
            String pdfFileName = fileService.save(pdfFile);
            pdfUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/courses/downloadCourseImage/")
                    .path(pdfFileName)
                    .toUriString();
        }
        ResourceResponse updateResource = resourceService.updateResource(resourceId, request,imageUrl,pdfUrl,videoUrl);
updateResource.setImageFile(imageUrl);
updateResource.setVideoFile(videoUrl);
updateResource.setPdfFile(pdfUrl);

        return ResponseEntity.accepted().body(updateResource);
    }

    @DeleteMapping("/delete/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable Integer resourceId) {
        resourceService.deleteResource(resourceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<ResourceResponse>> getAllResourcesByLessonId(@PathVariable Integer lessonId) {
        List<ResourceResponse> responses = resourceService.getAllResourcesByLessonId(lessonId);
        return ResponseEntity.ok(responses);
    }
}
