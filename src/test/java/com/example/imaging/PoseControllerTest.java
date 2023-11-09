package com.example.imaging;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.opencv.core.*;
import org.springframework.boot.test.context.SpringBootTest;
import nu.pattern.OpenCV;

@SpringBootTest
public class PoseControllerTest {
    @BeforeAll
	public static void loadLocally(){
		OpenCV.loadLocally();
	}

    // 1a. Test for a forward-facing face, yaw angle should be near 0
    @Test
    void testForwardFacingFace() throws Exception {
        String imageName = "forward_facing.jpg"; // This image should be of a person facing forward
        double yawAngle = PoseController.getYawAngle(imageName);
        assertTrue(Math.abs(yawAngle) < 15.0, "Expected yaw angle to be less than 15 degrees for a forward-facing face");
    }

    // 1b. Test for a non-forward-facing face, yaw angle should be significant
    @Test
    void testNonForwardFacingFace() throws Exception {
        String imageName = "profile_facing.jpg"; // This image should be of a person facing sideways
        double yawAngle = PoseController.getYawAngle(imageName);
        assertFalse(Math.abs(yawAngle) < 15.0, "Expected yaw angle to be greater than 15 degrees for a non-forward-facing face");
    }
}
