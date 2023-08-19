package com.newbies.birdy.controllers;

import com.newbies.birdy.services.FirebaseStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Firebase Storage API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/firebase")
public class FirebaseStorageController {

    private final FirebaseStorageService firebaseStorageService;

    @PostMapping("")
    public ResponseEntity<?> create(@RequestParam(name = "file") MultipartFile file) {
        String fileName = null;
        String imageUrl ="";
        try{
            fileName = firebaseStorageService.uploadFile(file);
            imageUrl = firebaseStorageService.getImageUrl(fileName);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok(imageUrl);

    }
}
