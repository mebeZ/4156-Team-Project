import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class FaceEyeDetectionExample {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Load the pre-trained Haar Cascade classifiers
        String faceCascadeFile = "lbpcascade_frontalface_improved.xml";
        String eyeCascadeFile = "haarcascade_eye.xml";

        CascadeClassifier faceCascade = new CascadeClassifier();
        CascadeClassifier eyeCascade = new CascadeClassifier();

        faceCascade.load(faceCascadeFile);
        eyeCascade.load(eyeCascadeFile);

        // Load an image
        String imagePath = "path_to_image.jpg";
        Mat image = Imgcodecs.imread(imagePath);

        // Convert the image to grayscale for detection
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Perform face detection
        MatOfRect faceDetections = new MatOfRect();
        faceCascade.detectMultiScale(gray, faceDetections);

        // Loop through the detected faces and draw rectangles
        for (Rect faceRect : faceDetections.toArray()) {
            Imgproc.rectangle(image, faceRect.tl(), faceRect.br(), new Scalar(0, 255, 0), 2);

            // Region of interest (ROI) for eye detection within the detected face region
            Mat faceROI = gray.submat(faceRect);
            
            // Perform eye detection
            MatOfRect eyeDetections = new MatOfRect();
            eyeCascade.detectMultiScale(faceROI, eyeDetections);

            // Loop through the detected eyes and draw rectangles
            for (Rect eyeRect : eyeDetections.toArray()) {
                // Convert eye coordinates to global coordinates within the image
                eyeRect.x += faceRect.x;
                eyeRect.y += faceRect.y;
                Imgproc.rectangle(image, eyeRect.tl(), eyeRect.br(), new Scalar(255, 0, 0), 2);
            }
        }

        // Display the image with detected faces and eyes
        Imgcodecs.imwrite("output_image.jpg", image);
    }
}
