package com.example.imaging;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.opencv.calib3d.Calib3d;

import com.emaraic.jdlib.Jdlib;
import com.emaraic.utils.FaceDescriptor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

@RestController
public class PoseController {

    static {
        OpenCV.loadLocally();
    }

    public static double getYawAngle(String name) throws Exception {
        Jdlib jdlib = new Jdlib("shape_predictor_68_face_landmarks.dat");
        
        BufferedImage bufferedImage = IOUtils.loadFileAsBufferedImage(name);
        
        // BufferedImage bufferedImage = ImageIO.read(new File("headPose.jpg"));
        ArrayList<Rectangle> faces = jdlib.detectFace(bufferedImage);
        if (faces.isEmpty()) {
            throw new Exception("No faces detected!");
        }

        //Rectangle faceRectangle = faces.get(0);
        ArrayList<FaceDescriptor> faceDescriptors = jdlib.getFaceLandmarks(bufferedImage);

        // Ensure that we have at least one face descriptor to work with
        if (faceDescriptors.isEmpty()) {
            throw new Exception("No face descriptors; getFaceLandMarks() failed");
        }

        // Assuming we are only working with the first face descriptor for simplicity
        FaceDescriptor firstFaceDescriptor = faceDescriptors.get(0);
        ArrayList<java.awt.Point> facialLandmarks = firstFaceDescriptor.getFacialLandmarks();

        // Convert AWT Points to OpenCV Points
        org.opencv.core.Point[] opencvPoints = facialLandmarks.stream()
                .map(awtPoint -> new org.opencv.core.Point(awtPoint.x, awtPoint.y))
                .toArray(org.opencv.core.Point[]::new);

        // Now use opencvPoints for OpenCV operations, ensure you're using the correct indices
        MatOfPoint2f imagePoints = new MatOfPoint2f(
                opencvPoints[30], // Nose tip
                opencvPoints[8],  // Chin
                opencvPoints[36], // Left eye left corner
                opencvPoints[45], // Right eye right corner
                opencvPoints[48], // Left Mouth corner
                opencvPoints[54]  // Right mouth corner
        );

        MatOfPoint3f modelPoints = new MatOfPoint3f(
                new Point3(0.0, 0.0, 0.0),             // Nose tip
                new Point3(0.0, -330.0, -65.0),        // Chin
                new Point3(-225.0, 170.0, -135.0),     // Left eye left corner
                new Point3(225.0, 170.0, -135.0),      // Right eye right corner
                new Point3(-150.0, -150.0, -125.0),    // Left Mouth corner
                new Point3(150.0, -150.0, -125.0)      // Right mouth corner
        );

        Mat image = bufferedImageToMat(bufferedImage);
        Size size = image.size();

        double focalLength = size.width;
        org.opencv.core.Point center = new org.opencv.core.Point(size.width / 2, size.height / 2);
        Mat cameraMatrix = Mat.eye(3, 3, CvType.CV_64FC1);
        cameraMatrix.put(0, 0, focalLength);
        cameraMatrix.put(1, 1, focalLength);
        cameraMatrix.put(0, 2, center.x);
        cameraMatrix.put(1, 2, center.y);

        MatOfDouble distCoeffs = new MatOfDouble(0, 0, 0, 0);

        Mat rotationVector = new Mat();
        Mat translationVector = new Mat();
        Calib3d.solvePnP(modelPoints, imagePoints, cameraMatrix, distCoeffs, rotationVector, translationVector);

        Mat rotationMatrix = new Mat();
        Calib3d.Rodrigues(rotationVector, rotationMatrix);

        double[] eulerAngles = rotationMatrixToEuler(rotationMatrix);
        double[] eulerAnglesInDegrees = {
                Math.toDegrees(eulerAngles[0]), // Roll
                Math.toDegrees(eulerAngles[1]), // Pitch
                Math.toDegrees(eulerAngles[2])  // Yaw
        };

        System.out.println("Euler Angles (Pitch, Yaw, Roll) in degrees: "
                + eulerAnglesInDegrees[0] + ", "
                + eulerAnglesInDegrees[1] + ", "
                + eulerAnglesInDegrees[2]);

        double yawAngle = eulerAngles[1];
        double yawThreshold = 15.0;
        if (Math.abs(Math.toDegrees(yawAngle)) < yawThreshold) {
            System.out.println("Face is oriented forward");
        } else {
            System.out.println("Face is not oriented forward");
        }
        
        // face centered
        double faceCenterX = facialLandmarks.stream().mapToDouble(p -> p.x).average().orElse(Double.NaN);
        double faceCenterY = facialLandmarks.stream().mapToDouble(p -> p.y).average().orElse(Double.NaN);

        // Define separate thresholds for x and y
        double xThreshold = 0.1; // 10% for x-axis
        double yThreshold = 0.1;  // 10% for y-axis

        // Image center
        double centerX = size.width / 2;
        double centerY = size.height / 2;

        // Check if the face is centered in the image
        boolean isCenteredX = Math.abs(faceCenterX - centerX) <= xThreshold * size.width;
        boolean isCenteredY = Math.abs(faceCenterY - centerY) <= yThreshold * size.height;

        if (isCenteredX && isCenteredY) {
          System.out.println("Face is centered in the image");
        } else {
          System.out.println("Face is not centered in the image");
        }
        
        MatOfPoint2f noseEndPoint2D = new MatOfPoint2f();
        MatOfPoint3f noseEndPoint3D = new MatOfPoint3f(new Point3(0.0, 0.0, 1000.0));

        Calib3d.projectPoints(noseEndPoint3D, rotationVector, translationVector, cameraMatrix, distCoeffs, noseEndPoint2D);

        for (org.opencv.core.Point point : imagePoints.toArray()) {
            Imgproc.circle(image, point, 3, new Scalar(0, 0, 255), -1);
        }

        org.opencv.core.Point p1 = imagePoints.toArray()[0];
        org.opencv.core.Point p2 = noseEndPoint2D.toArray()[0];
        Imgproc.line(image, p1, p2, new Scalar(255, 0, 0), 2);

        String imageName = IOUtils.getImageName(name);
        String writePath = "src/main/resources/static/labeled-images/" + imageName; 
        Imgcodecs.imwrite(writePath, image);

        return yawAngle;
    }

    private static Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    private static double[] rotationMatrixToEuler(Mat R) {
        double sy = Math.sqrt(R.get(0, 0)[0] * R.get(0, 0)[0] +  R.get(1, 0)[0] * R.get(1, 0)[0]);
        boolean singular = sy < 1e-6;

        double x, y, z;
        if (!singular) {
            x = Math.atan2(R.get(2, 1)[0], R.get(2, 2)[0]);  // Roll
            y = Math.atan2(-R.get(2, 0)[0], sy);             // Pitch
            z = Math.atan2(R.get(1, 0)[0], R.get(0, 0)[0]);  // Yaw
        } else {
            x = Math.atan2(-R.get(1, 2)[0], R.get(1, 1)[0]); // Roll
            y = Math.atan2(-R.get(2, 0)[0], sy);             // Pitch
            z = 0;                                          // Yaw
        }
        return new double[]{x, y, z};
    }

    @GetMapping("/pose-angle")
    public static PoseInfo getPoseAngle(@RequestParam(value="name") String name) {
        double yawAngle = 0.0;
        try {
            yawAngle = getYawAngle(name);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return new PoseInfo(name, yawAngle);
    }
}

