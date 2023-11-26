package com.example.imaging;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.opencv.core.*;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.imaging.controllers.PoseController;

import nu.pattern.OpenCV;

@SpringBootTest
public class PoseControllerTest {
    @BeforeAll
	public static void loadLocally(){
		OpenCV.loadLocally();
	}
    // 1a. If the image does not contain any faces, an exception should be thrown
    @Test
    void testNoFacesDetected() {
        String imageName = "no_faces_image.jpg"; //  no faces
        assertThrows(Exception.class, () -> PoseController.getYawAngle(imageName));
    }

    // 1b. If the face landmarks cannot be detected, an exception should be thrown
    @Test
    void testNoFaceDescriptors() {
        String imageName = "no_landmarks_image.jpg"; //  face  with undetectable landmarks
        assertThrows(Exception.class, () -> PoseController.getYawAngle(imageName));
    }

    // 2a. Test for a forward-facing face, yaw angle should be near 0
    @Test
    void testForwardFacingFace() throws Exception {
        String imageName = "forward_facing.jpg"; //  facing forward
        double yawAngle = PoseController.getYawAngle(imageName);
        assertTrue(Math.abs(yawAngle) < 15.0, "Expected yaw angle to be less than 15 degrees for a forward-facing face");
    }

    // 2b. Test for a non-forward-facing face, yaw angle should be significant
    @Test
    void testNonForwardFacingFace() throws Exception {
        String imageName = "profile_facing.jpg"; //  facing sideways
        double yawAngle = PoseController.getYawAngle(imageName);
        assertFalse(Math.abs(yawAngle) < 15.0, "Expected yaw angle to be greater than 15 degrees for a non-forward-facing face");
    }
}
