package com.pfe.elearning.forum.Message;


import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.course.service.CourseService;
import com.pfe.elearning.file.services.FileService;
import com.pfe.elearning.lesson.service.LessonService;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService discussionService;
    private final UserRepository userRepository;
    private final CourseService courseService;
    private final LessonService lessonService;
    @Autowired
    @Qualifier("MessageFilesServiceImpl")
    private FileService fileService;


    @PostMapping("/forum/{forumId}/create")
    public ResponseEntity<MessageResponse> createDiscussion(@ModelAttribute MessageRequest discussionRequest,
                                                            @PathVariable Integer forumId,
                                                            @AuthenticationPrincipal UserDetails userDetails) {

        // Get the logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User author = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        // Initialize URLs for image, video, PDF files, and record (if available)
        String imageUrl = null;
        String videoUrl = null;
        String pdfUrl = null;
        String recordUrl = null;

        // Handle file uploads if they exist in the request
        if (discussionRequest.getImageFile() != null && !discussionRequest.getImageFile().isEmpty()) {
            MultipartFile imageFile = discussionRequest.getImageFile();
            String imageFileName = fileService.save(imageFile);
            imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/messages/downloadFile/")
                    .path(imageFileName)
                    .toUriString();
        }

        if (discussionRequest.getVideoFile() != null && !discussionRequest.getVideoFile().isEmpty()) {
            MultipartFile videoFile = discussionRequest.getVideoFile();
            String videoFileName = fileService.save(videoFile);
            videoUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/messages/downloadFile/")
                    .path(videoFileName)
                    .toUriString();
        }

        if (discussionRequest.getPdfFile() != null && !discussionRequest.getPdfFile().isEmpty()) {
            MultipartFile pdfFile = discussionRequest.getPdfFile();
            String pdfFileName = fileService.save(pdfFile);
            pdfUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/messages/downloadFile/")
                    .path(pdfFileName)
                    .toUriString();
        }

        if (discussionRequest.getRecordFile() != null && !discussionRequest.getRecordFile().isEmpty()) {
            MultipartFile recordFile = discussionRequest.getRecordFile();
            String recordFileName = fileService.save(recordFile);
            recordUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/messages/downloadFile/")
                    .path(recordFileName)
                    .toUriString();
        }

        // Call the service method to create the discussion
        MessageResponse response = discussionService.createDiscussion(discussionRequest, imageUrl, videoUrl, pdfUrl, recordUrl, forumId, author.getId());

        // Return the response with HTTP status CREATED
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/downloadFile/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource fileResource = fileService.load(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }
    @GetMapping("/forum/{forumId}")
    public ResponseEntity<PageResponse<MessageResponse>> getAllDiscussionsByForumId(
            @PathVariable int forumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(discussionService.getAllDiscussionsByForumId(forumId, page, size));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<PageResponse<MessageResponse>> getAllDiscussionsByLessonId(
            @PathVariable int lessonId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<MessageResponse> discussions = discussionService.getAllDiscussionsByLessonId(lessonId, page, size);
        return ResponseEntity.ok(discussions);
    }
    @PutMapping("/{id}/update")
    public ResponseEntity<MessageResponse> updateDiscussion(
            @PathVariable Integer id,
            @ModelAttribute MessageRequest discussionRequest) {

        // Get the logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User author = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        // Initialize URLs for image, video, PDF files, and record (if available)
        String imageUrl = null;
        String videoUrl = null;
        String pdfUrl = null;
        String recordUrl = null;

        // Handle file uploads if they exist in the request
        if (discussionRequest.getImageFile() != null && !discussionRequest.getImageFile().isEmpty()) {
            MultipartFile imageFile = discussionRequest.getImageFile();
            String imageFileName = fileService.save(imageFile);
            imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/messages/downloadFile/")
                    .path(imageFileName)
                    .toUriString();
        }

        if (discussionRequest.getVideoFile() != null && !discussionRequest.getVideoFile().isEmpty()) {
            MultipartFile videoFile = discussionRequest.getVideoFile();
            String videoFileName = fileService.save(videoFile);
            videoUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/messages/downloadFile/")
                    .path(videoFileName)
                    .toUriString();
        }

        if (discussionRequest.getPdfFile() != null && !discussionRequest.getPdfFile().isEmpty()) {
            MultipartFile pdfFile = discussionRequest.getPdfFile();
            String pdfFileName = fileService.save(pdfFile);
            pdfUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/messages/downloadFile/")
                    .path(pdfFileName)
                    .toUriString();
        }

        if (discussionRequest.getRecordFile() != null && !discussionRequest.getRecordFile().isEmpty()) {
            MultipartFile recordFile = discussionRequest.getRecordFile();
            String recordFileName = fileService.save(recordFile);
            recordUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/messages/downloadFile/")
                    .path(recordFileName)
                    .toUriString();
        }

        // Call the service method to update the discussion
        MessageResponse response = discussionService.updateDiscussion(id, discussionRequest, imageUrl, videoUrl, pdfUrl, recordUrl, author.getId());

        // Return the response with HTTP status OK
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteDiscussion(@PathVariable Integer id) {
        discussionService.deleteDiscussion(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search/{forumId}")
    public ResponseEntity<PageResponse<MessageResponse>> searchDiscussionsBySubjectContaining(
            @RequestParam String keyword,
            @PathVariable int forumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<MessageResponse> discussions = discussionService.searchDiscussionsBySubjectContaining(forumId,keyword, page, size);
        if (!discussions.getContent().isEmpty()) {
            return ResponseEntity.ok(discussions);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/lesson/search")
    public ResponseEntity<List<MessageResponse>> searchDiscussionsByLessonContaining(@RequestParam String keyword) {
        List<MessageResponse> discussions = discussionService.searchDiscussionsByLessonContaining(keyword);
        if (discussions != null) {
            return ResponseEntity.ok(discussions);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/instructor-lessons-messages")
    public ResponseEntity<PageResponse<MessageResponse>> getInstructorLessonDiscussions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String instructorUsername = userDetails.getUsername();

        // Call the service method to get instructor lesson discussions
        PageResponse<MessageResponse> instructorLessonDiscussions = discussionService.getInstructorLessonDiscussions(instructorUsername, page, size);

        // Return the response
        return ResponseEntity.ok(instructorLessonDiscussions);
    }
}
