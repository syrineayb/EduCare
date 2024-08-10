package com.pfe.elearning.file.services;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service

public interface FileService {
    public void init();

    String  save(MultipartFile file);

    public Resource load(String filename);




}