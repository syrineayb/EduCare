package com.pfe.elearning.file.services.Impl;

import com.pfe.elearning.config.FileStorageProperties;
import com.pfe.elearning.file.services.FileService;
import com.pfe.elearning.handler.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service("MessageFilesServiceImpl")
public class MessageFilesServiceImpl  implements FileService {
    private final Path imageLocation;

    @Autowired
    public MessageFilesServiceImpl(FileStorageProperties fileStorageProperties) {
        this.imageLocation= Paths.get(fileStorageProperties.getUploadFilesMessageDir()).toAbsolutePath().normalize();

    }

    @Override
    public void init() {
        try {
            Files.createDirectories(imageLocation);
        }catch (IOException e){
            throw new RuntimeException("Could not initialize storage");
        }
    }

    @Override
    public String save(MultipartFile file) {
        String fileName= StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")){
                throw new FileStorageException("File name contains invalid path sequence "+fileName);
            }
            Files.copy(file.getInputStream(),this.imageLocation.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        }catch (Exception e){
            throw new RuntimeException("Fail",e);
        }
        return file.getOriginalFilename();
    }

    @Override
    public Resource load(String filename) {
        try {
            Path path = imageLocation.resolve(filename);
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()){
                return resource;
            }else {
                throw new RuntimeException("Fail");
            }
        }catch (MalformedURLException e){
            throw new RuntimeException("Fail");
        }
    }

}