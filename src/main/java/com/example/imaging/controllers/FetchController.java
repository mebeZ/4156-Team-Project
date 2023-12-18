package com.example.imaging.controllers;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.imaging.models.Image;
import com.example.imaging.models.data.ImageRepository;
import com.example.imaging.services.IOService;

@Controller
public class FetchController {

    @Autowired
    private ImageRepository imageDao;

    // Create mapping to test fetch.html template
    @GetMapping("/fetch")
    public String renderFetchTemplate(Model model) {
        Iterable<Image> images = imageDao.findAll();
        model.addAttribute("images", images);
        return "fetch";
    }
    
    @GetMapping("/getImage")
    public ResponseEntity<byte []> getImage(@RequestParam(name="selectedImageName") String imageName) throws Exception {
        byte[] imageBytes = IOService.loadFileAsBinary(imageName);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
