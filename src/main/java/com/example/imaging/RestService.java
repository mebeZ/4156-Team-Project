package com.example.imaging;

import org.springframework.boot.SpringApplication;
//import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import nu.pattern.OpenCV;

@SpringBootApplication
public class RestService {
    public static void main(String[] args) throws Exception {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadLocally();
        //FaceController.getEyeColor("sideways_face");
        SpringApplication.run(RestService.class, args);
    }
}
