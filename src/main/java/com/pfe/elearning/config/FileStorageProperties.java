package com.pfe.elearning.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String uploadImgUsersDir;
    private String uploadImgTopicsDir;

    private String uploadImgCoursesDir;
    private String uploadFilesMessageDir;
    //

    public String getUploadImgCoursesDir() {
        return uploadImgCoursesDir;
    }

    public void setUploadImgCoursesDir(String uploadImgCoursesDir) {
        this.uploadImgCoursesDir = uploadImgCoursesDir;
    }
    public String getUploadImgTopicsDir() {
        return uploadImgTopicsDir;
    }

    public void setUploadImgTopicsDir(String uploadImgTopicDir) {
        this.uploadImgTopicsDir = uploadImgTopicDir;
    }
    public String getUploadImgUsersDir() {
        return uploadImgUsersDir;
    }

    public void setUploadImgUsersDir(String uploadImgUsersDir) {
        this.uploadImgUsersDir = uploadImgUsersDir;
    }  public String getUploadFilesMessageDir() {
        return uploadFilesMessageDir;
    }

    public void setUploadFilesMessageDir(String uploadFilesMessageDir) {
        this.uploadFilesMessageDir = uploadFilesMessageDir;
    }
}
