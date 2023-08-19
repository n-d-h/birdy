package com.newbies.birdy.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FirebaseStorageService {

    String uploadFile(MultipartFile multipartFile) throws IOException;

    ResponseEntity<Object> downloadFile(String fileName, HttpServletRequest request) throws Exception;

    String getImageUrl(String name);

    void deleteFile(String fileName) throws IOException;
}
