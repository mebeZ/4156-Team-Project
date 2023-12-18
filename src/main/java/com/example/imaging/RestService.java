package com.example.imaging;

import org.springframework.boot.SpringApplication;
//import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import com.example.imaging.controllers.UploadController;

import com.example.imaging.controllers.EyeColorController;

import nu.pattern.OpenCV;

@SpringBootApplication
public class RestService {
    public static void main(String[] args) throws Exception {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadLocally();
        //String color = EyeColorController.getEyeColor("sarah", "client1");
        //System.out.println("Eye color: " + color);
        SpringApplication.run(RestService.class, args);
    }
}
