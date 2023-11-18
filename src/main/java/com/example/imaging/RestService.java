package com.example.imaging;

import org.apache.tomcat.util.http.fileupload.UploadContext;
import org.springframework.boot.SpringApplication;
//import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.imaging.controllers.UploadController;

import nu.pattern.OpenCV;

@SpringBootApplication
public class RestService {
    public static void main(String[] args) throws Exception {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadLocally();
        //UploadController.uploadImage("/Users/ZMan/Desktop/2023-24/ASE/local_face_images/carl-blue.jpeg");
        SpringApplication.run(RestService.class, args);
    }
}
