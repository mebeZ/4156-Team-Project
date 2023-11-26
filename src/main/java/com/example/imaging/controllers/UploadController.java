package com.example.imaging.controllers;

import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.imaging.DBUtils;
import com.example.imaging.IOUtils;
import com.example.imaging.models.Image;
import com.example.imaging.models.StorageService;
import com.example.imaging.models.data.ClientRepository;
import com.example.imaging.models.data.ImageRepository;

import io.micrometer.core.ipc.http.HttpSender.Response;

@RestController
public class UploadController {

    @Autowired
    private ImageRepository imageDao;

    @Autowired
    private ClientRepository clientDao;

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload-image")
    public void uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        storageService.uploadImage(file);
    }

    /*
    @GetMapping("/upload-image")
    public static Image uploadImage(@RequestParam(value="localPath") String path, @RequestParam(value="accessToken") String token) throws Exception {
        // TODO: Make sure user has permission to access the API
		//DBUtils.checkAccessToken(token);

        Mat img = IOUtils.loadFileAsMat(path);
        String imgName = IOUtils.getImageName(path);
        String filepath = "src/main/resources/static/face-images/" + imgName;
        Imgcodecs.imwrite(filepath, img);
        Image faceImage = new Image(filepath, token);
        //System.out.println(faceImage);
        return faceImage;
    }
    */

    
}
