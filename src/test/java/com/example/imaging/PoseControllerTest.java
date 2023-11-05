package com.example.imaging;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

import nu.pattern.OpenCV;

@SpringBootTest
public class PoseControllerTest {
    @BeforeAll
	public static void loadLocally(){
		OpenCV.loadLocally();
	}
    
}
