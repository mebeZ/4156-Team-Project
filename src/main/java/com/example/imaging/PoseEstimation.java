package com.example.imaging;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.calib3d.Calib3d;

public class PoseEstimation {

    static {
        OpenCV.loadLocally();
    }

    public static void main(String[] args) {
        // Read Image
        Mat image = Imgcodecs.imread("Photo.jpg");
        Size size = image.size();

        // 2D image points
        MatOfPoint2f imagePoints = new MatOfPoint2f(
                new Point(359, 391), // Nose tip
                new Point(399, 561), // Chin
                new Point(337, 297), // Left eye left corner
                new Point(513, 301), // Right eye right corner
                new Point(345, 465), // Left Mouth corner
                new Point(453, 469)  // Right mouth corner
        );

        // 3D model points
        MatOfPoint3f modelPoints = new MatOfPoint3f(
                new Point3(0.0, 0.0, 0.0),             // Nose tip
                new Point3(0.0, -330.0, -65.0),        // Chin
                new Point3(-225.0, 170.0, -135.0),     // Left eye left corner
                new Point3(225.0, 170.0, -135.0),      // Right eye right corner
                new Point3(-150.0, -150.0, -125.0),    // Left Mouth corner
                new Point3(150.0, -150.0, -125.0)      // Right mouth corner
        );

        // Camera internals
        double focalLength = size.width;
        Point center = new Point(size.width / 2, size.height / 2);
        Mat cameraMatrix = Mat.eye(3, 3, CvType.CV_64FC1);
        cameraMatrix.put(0, 0, focalLength);
        cameraMatrix.put(1, 1, focalLength);
        cameraMatrix.put(0, 2, center.x);
        cameraMatrix.put(1, 2, center.y);

        System.out.println("Camera Matrix:\n" + cameraMatrix.dump());

        // Assuming no lens distortion
        MatOfDouble distCoeffs = new MatOfDouble(0, 0, 0, 0);

        // SolvePnP
        Mat rotationVector = new Mat();
        Mat translationVector = new Mat();
        Calib3d.solvePnP(modelPoints, imagePoints, cameraMatrix, distCoeffs, rotationVector, translationVector);

        System.out.println("Rotation Vector:\n" + rotationVector.dump());
        System.out.println("Translation Vector:\n" + translationVector.dump());

        // Project a 3D point
        MatOfPoint2f noseEndPoint2D = new MatOfPoint2f();
        MatOfPoint3f noseEndPoint3D = new MatOfPoint3f(new Point3(0.0, 0.0, 1000.0));

        Calib3d.projectPoints(noseEndPoint3D, rotationVector, translationVector, cameraMatrix, distCoeffs, noseEndPoint2D);

        // Draw points and line
        for (Point point : imagePoints.toArray()) {
            Imgproc.circle(image, point, 3, new Scalar(0, 0, 255), -1);
        }

        Point p1 = imagePoints.toArray()[0];
        Point p2 = noseEndPoint2D.toArray()[0];
        Imgproc.line(image, p1, p2, new Scalar(255, 0, 0), 2);

        // Save image
        Imgcodecs.imwrite("output2.jpg", image);
    }
}
