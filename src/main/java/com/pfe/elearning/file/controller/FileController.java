package com.pfe.elearning.file.controller;

import com.pfe.elearning.file.services.FileService;
import com.pfe.elearning.profile.entity.Profile;
import com.pfe.elearning.profile.repository.ProfileRepository;
import com.pfe.elearning.topic.dto.TopicRequest;
import com.pfe.elearning.topic.dto.TopicResponse;
import com.pfe.elearning.topic.entity.Topic;
import com.pfe.elearning.topic.repository.TopicRepository;
import com.pfe.elearning.topic.service.TopicService;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {



    private final UserRepository userRepository ;
    private final ProfileRepository profileRepository ;
    @Autowired
    @Qualifier("FileServiceImpl")
    private   FileService fileService;
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile (@RequestParam Integer id,@RequestParam MultipartFile file){

        String fileName = fileService.save(file);
        String fileImageDownloadUrl= ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/downloadProfileImage/")
                .path(fileName)
                .toUriString();
        // tu dois veridier dans la bd que image est egale à http://localhost:8080/api/files/downloadProfileImage/logo.png
            User user = userRepository.findById(id).orElseThrow();
            Profile profileUser = user.getProfile();
            profileUser.setProfileImage(fileImageDownloadUrl);
         // save
        profileRepository.save(profileUser);

       return ResponseEntity.ok().build();
    }



    @GetMapping("/downloadProfileImage/{filename:.+}")
    public ResponseEntity<Resource> downloadProfileImage(@PathVariable String filename) {
        Resource fileResource = fileService.load(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }

}