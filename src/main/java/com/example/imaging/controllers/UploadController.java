package com.example.imaging.controllers;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import com.example.imaging.IOUtils;
import com.example.imaging.models.Client;
import com.example.imaging.models.Image;

@RestController
public class UploadController {

    @GetMapping("/upload-image")
    public static Image uploadImage(@RequestParam(value="localPath") String path) throws Exception {
        Mat img = IOUtils.loadFileAsMat(path);
        String imgName = IOUtils.getImageName(path);
        String filepath = "src/main/resources/static/face-images/" + imgName;
        Imgcodecs.imwrite(filepath, img);
        Image faceImage = new Image(filepath);
        //System.out.println(faceImage);
        return faceImage;
    }
}
