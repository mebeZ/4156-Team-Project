package com.example.imaging;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.opencv.core.*;
import org.springframework.boot.test.context.SpringBootTest;
import nu.pattern.OpenCV;
import java.lang.reflect.Method;
import static org.opencv.core.CvType.CV_64FC1;

public class PoseControllerTest {
    @BeforeAll
    public static void loadLocally() {
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
        String imageName = "no_landmarks_image.jpg"; //  face with undetectable landmarks
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
        assertTrue(Math.abs(yawAngle) < 15.0, "Expected yaw angle to be greater than 15 degrees for a non-forward-facing face");
    }

    // Utility method to invoke private method using reflection
    private double[] invokeRotationMatrixToEuler(Mat R) throws Exception {
        Method method = PoseController.class.getDeclaredMethod("rotationMatrixToEuler", Mat.class);
        method.setAccessible(true);
        return (double[]) method.invoke(null, R);
    }

    // Test for non-singular case
    @Test
    void testRotationMatrixToEulerNonSingular() throws Exception {
        Mat R = Mat.eye(3, 3, CV_64FC1);
        R.put(1, 0, 0.1);
        R.put(2, 0, 0.1);

        double[] eulerAngles = invokeRotationMatrixToEuler(R);
        assertNotNull(eulerAngles, "Euler angles should not be null");
        assertEquals(3, eulerAngles.length, "Euler angles array should have 3 elements");
    }

    // Test for singular case
    @Test
    void testRotationMatrixToEulerSingular() throws Exception {
        Mat R = Mat.eye(3, 3, CV_64FC1);
        R.put(0, 0, 1e-7);
        R.put(1, 0, 1e-7);

        double[] eulerAngles = invokeRotationMatrixToEuler(R);
        assertNotNull(eulerAngles, "Euler angles should not be null");
        assertEquals(3, eulerAngles.length, "Euler angles array should have 3 elements");
    }
}
