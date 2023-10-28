package com.example.imaging;

import org.springframework.boot.SpringApplication;
//import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import nu.pattern.OpenCV;

@SpringBootApplication
public class RestService {
    public static void main(String[] args) {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadLocally();
        FaceController.getEyeColor("samantha");
        //SpringApplication.run(RestService.class, args);
    }
}
