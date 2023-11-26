package com.example.imaging.models;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.imaging.models.data.ImageRepository;

@Service
public class StorageService {
    @Autowired
    private ImageRepository imageRepo;

    public String uploadImage(MultipartFile file) throws IOException {
        Image savedImage = imageRepo.save(Image.builder()
            .name(file.getOriginalFilename())
            .type(file.getContentType())
            .imageData(file.getBytes()).build());
        
        if (savedImage != null) {
            return "File uploaded successfully: " + file.getOriginalFilename();
        }
        return null;
    }

}