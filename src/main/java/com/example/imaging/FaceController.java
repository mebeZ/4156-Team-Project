package com.example.imaging;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.sh0nk.matplotlib4j.NumpyUtils;
//import com.github.sh0nk.matplotlib4j.Plot;
//import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
//import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.opencv.core.Core;
// OpenCV imports
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
//import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

@RestController
public class FaceController {
	// Used to name RGB histogram files sequentially
	//private static int imgNum = 1;

	public static CascadeClassifier faceCascade = new CascadeClassifier("src/main/resources/templates/haarcascades/haarcascade_frontalface_default.xml");
	public static CascadeClassifier eyesCascade = new CascadeClassifier("src/main/resources/templates/haarcascades/haarcascade_eye.xml");

	/**
	 * Given an image matrix, detects the eye region.
	 * @imgMatrix: An image matrix (should be of a face)
	 * @returns: A Mat object of the eye
	 * @throws: Exception when a face or the eyes cannot be detected
	 */
	public static Mat detectEye(Mat imgMatrix) throws Exception {
		//Mat imgMatrix = Imgcodecs.imread(fpath);
		if (imgMatrix == null) {
			throw new NullPointerException("imgMatrix cannot be null");
		}
		
		// Resize the image such that it can later be displayed by HighGUI
		//Imgproc.resize(imgMatrix, imgMatrix, new Size(1000, 1000));
		//System.out.println("Size of the resized face image: " + imgMatrix.size());
		
		MatOfRect faces = new MatOfRect();
		MatOfRect eyes = new MatOfRect();

		// Detect all bounding rectangles of faces and eyes in the image
		faceCascade.detectMultiScale(imgMatrix, faces);
		eyesCascade.detectMultiScale(imgMatrix, eyes);

		List<Rect> faceList = faces.toList();
		List<Rect> eyeList = eyes.toList();

		int numFaces = faceList.size();
		int numEyes = eyeList.size();

		System.out.println("Number of eyes: " + numEyes);
		System.out.println("Number of faces: " + numFaces);

		// Check that at least one face is detected (in valid images, sometimes more than one is detected so in these cases we will just pick the first detected face)
		if (numFaces == 0) {
			throw new IOException("No face detected");
		}

		// Check that the face that was detected is not a false positive; if face area is too small, we declare it a false positive (because for non_face.jpg, a false face is returned with a small area)
		double imageArea = imgMatrix.width() * imgMatrix.height();
		double faceArea = faceList.get(0).area();
		System.out.println("Image area = " + imageArea);
		System.out.println("Face area = " + faceArea);
		//if (faceArea < 0.025 * imageArea) {
		//	throw new IOException("No face detected");
		//}

		// Make sure both eyes can be detected if the face exists; sometimes more than 2 eyes are detected even for valid images
		if (numEyes < 2) {
			throw new IOException("One or more eyes not detected");
		}
		
		/*
		int borderThickness = 5;
		// Concatenate the bounding rectangles for the faces and the eyes such that they can be drawn in one for loop rather than two
		List<Rect> rects = Stream.concat(faceList.stream(), eyeList.stream()).toList();

		
		// Draw each bounding rectangle (both of the face and the eyes) onto the image
		for (Rect rect : rects) {
			Imgproc.rectangle(
				imgMatrix,
				new Point(rect.x, rect.y),
				new Point(rect.x + rect.width, rect.y + rect.height),
				// Images are stored in BGR order in OpenCV2
				new Scalar(0, 0, 255), // A red outline 
				borderThickness
			);
		}

		System.out.println("Face Detected");
		// Display the detected faces until a key is pressed
		// HighGui.imshow("Detected faces", imgMatrix);
		// HighGui.waitKey();
		*/

		Rect eyeROI = eyes.toList().get(0);
		Mat leftEyeMat = new Mat(imgMatrix, eyeROI);
		System.out.println("Size of eye region: " + leftEyeMat.size());

		return leftEyeMat;
	}

	/**
	 * Detects the location and radius of the iris from an image of an eye
	 * @eyeImage: The image of the eye
	 * @returns: An array containing information about the iris: [center_x, center_y, iris_radius]
	 * @throws: Exception if a unique iris cannot be located in the eye image
	 */
	public static double[] detectIris(Mat eyeImage) throws Exception {
		/*
		HighGui.imshow("Color eye image", eyeImage);
		HighGui.waitKey();
		*/
		if (eyeImage == null) {
			throw new NullPointerException("eyeImage cannot be null");
		}

		Mat grayEyeImage = new Mat();
		Imgproc.cvtColor(eyeImage, grayEyeImage, Imgproc.COLOR_BGR2GRAY);
		/*
		HighGui.imshow("Gray eye image", grayEyeImage);
		HighGui.waitKey();
		*/ 

		// Apply a median blur to the image to avoid false positives for circle detection
		
		Imgproc.medianBlur(grayEyeImage, grayEyeImage, 5);
		/*
		HighGui.imshow("Blurred gray eye image", grayEyeImage);
		HighGui.waitKey();
		*/

		/*
		Mat thresholdEyeImage = new Mat();
		Imgproc.threshold(grayEyeImage, thresholdEyeImage, 30, 255, Imgproc.THRESH_BINARY_INV);
		*/

		// Find circles using Hough transform
		Mat irisCircles = new Mat();
		// The min and max radius of iris circles to look for based on empirical observation: i.e. the size of the eye bounding rectangle compared to the size of the iris
		int minIrisRadius = grayEyeImage.rows() / 7;
		int maxIrisRadius = grayEyeImage.rows() / 4; 
		double minDist = (double)grayEyeImage.rows()/16;
		double p1 = 100;
		double p2 = 30;

		while (irisCircles.cols() != 1) {
			Imgproc.HoughCircles(grayEyeImage, irisCircles, Imgproc.HOUGH_GRADIENT, 1.0,
			minDist,
			p1, p2, minIrisRadius, maxIrisRadius);
			System.out.println("minIrisRadius = " + minIrisRadius);
			System.out.println("maxIrisRadius = " + maxIrisRadius);
			// If no circles are detected, increase the max radius
			if (irisCircles.cols() < 1 && maxIrisRadius <= (grayEyeImage.rows() / 4)) {
				maxIrisRadius *= 2;
			// if we've increased the max radius, and still no circles are detected, then there likely is not an iris in the image, so throw an exception
			} else if (irisCircles.cols() < 1) {
				throw new Exception("detectIris failed: " + irisCircles.cols() + " circles were detected"); 
			// If more than 1 circles are detected, tweak the parameters such that less false positives are detected
			} else if (irisCircles.cols() > 1 && minDist <= (double)grayEyeImage.rows()/16) {
				minDist *= 1.5;
				p2 *= 1.5;
			// If we've tweaked the parameters, and false positives are still detected, then we cannot discern the iris in the image, so throw an exception
			} else if (irisCircles.cols() > 1) {
				throw new Exception("detectIris failed: More than 1 circle was detected"); 
			}
		}

		/*
		for (int i = 0; i < irisCircles.cols(); i++) {
			double circle[] = irisCircles.get(0, i);
			System.out.println("Iris: Center x: " + circle[0] + " Center y: " + circle[1] + " Radius: " + circle[2]);
			Imgproc.circle(thresholdEyeImage, new Point(circle[0], circle[1]), (int) circle[2], new Scalar(255,255,255), 2);
		}

		HighGui.imshow("Thresholded eye image", thresholdEyeImage);
		HighGui.waitKey();
		*/
	
		double iris_circle[] = irisCircles.get(0, 0);
		double iris_x = iris_circle[0];
		double iris_y = iris_circle[1];
		double iris_radius = iris_circle[2];

		/*
		// Detect the pupil now: For some reason this doesn't work
		int maxPupilRadius = (int) (iris_radius / 1.5); // Assumption: Pupil is at most 2/3 the size of the iris
		Mat pupilCircles = new Mat();
		Imgproc.HoughCircles(grayEyeImage, pupilCircles, Imgproc.HOUGH_GRADIENT, 1.0,
		(double)grayEyeImage.rows()/16, // change this value to detect circles with different distances to each other
		100.0, 30.0, 1, maxPupilRadius);

		// Make sure only one circle is detected for the pupil
		if (pupilCircles.cols() != 1) {
			throw new Exception("Number of detected circles should be 1 (i.e. the pupil). However, " + pupilCircles.cols() + " were detected");
		}

		double pupil_circle[] = irisCircles.get(0, 0);
		double pupil_x = pupil_circle[0];
		double pupil_y = pupil_circle[1];
		double pupil_radius = pupil_circle[2];
		*/

		System.out.println("Iris: Center x: " + iris_x + " Center y: " + iris_y + " Radius: " + iris_radius);
		Imgproc.circle(grayEyeImage, new Point(iris_x, iris_y), (int) iris_radius, new Scalar(0,0,255), 3);

		/*
		System.out.println("Pupil: Center x: " + pupil_x + " Center y: " + pupil_y + " Radius: " + pupil_radius);
		Imgproc.circle(grayEyeImage, new Point(pupil_x, pupil_y), (int) pupil_radius, new Scalar(0,255,0), 2);
		*/

		//System.out.println("Number of detected circles = " + irisCircles.size());
		// HighGui.imshow("Detected Circle (Iris): ", grayEyeImage);
		//HighGui.waitKey();
		return iris_circle;
	}

	public static String predictEyeColor(Mat eyeRegion) {
		double iris_circle[] = null;
		try {
			iris_circle = detectIris(eyeRegion);
			if (iris_circle == null) {
				throw new Exception("detectIris failed: iris_circle is null");
			} else if (iris_circle.length != 3) {
				throw new Exception("detectIris failed: iris_circle does not have length of 3");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		System.out.println("Successfully detected iris from eye image");
		double c_x = iris_circle[0];
		double c_y = iris_circle[1];
		double r = iris_circle[2];

		// roi is roughly the region under the pupil of the eye: from observation this region accurately reflects the color of the iris and does not have 'light splotches'
		Point p1 = new Point((int)(c_x-0.5*r), (int)(c_y+0.5*r));
		Point p2 = new Point((int)(c_x+0.5*r), (int)(c_y+0.75*r));
		Rect roi = new Rect(p1, p2);
		
		/*
		Imgproc.rectangle(eyeRegion, p1, p2, new Scalar(0,0,255), 3);
		HighGui.imshow("Iris ROI", eyeRegion);
		HighGui.waitKey();
		*/
		Mat iris_roi = new Mat(eyeRegion, roi);
		//HighGui.imshow("Iris ROI", iris_roi);
		//HighGui.waitKey();

		int numChannels = eyeRegion.channels();
		System.out.println("Number of channels: " + numChannels);

		// eyeChannels stores the blue, green, and red channels of iris_roi respectively
		List<Mat> eyeChannels = new ArrayList<Mat>(numChannels);
		Core.split(iris_roi, eyeChannels);
		
		// Initialize histograms to store the distribution of intensities for each channel of the eye image
		Mat blue_hist = new Mat();
		Mat green_hist = new Mat();
		Mat red_hist = new Mat();
		
		/* histSize: The number of buckets of the histogram? We want 1 bucket per brightness value which ranges from 0-255 in BGR channels, so we select 256 buckets for the histogram
		ranges: The range of values in our histogram - should be from 0 to 255, capturing all possible intensities
		*/
		int intensity_max = 256;
		float brightness_range[] = {0, intensity_max};
		MatOfInt histSize = new MatOfInt(intensity_max);
		MatOfFloat ranges = new MatOfFloat(brightness_range);

		// Compute the distribution of intensities for the blue channel of the eye image
		Imgproc.calcHist(eyeChannels, new MatOfInt(0), new Mat(), blue_hist, histSize, ranges);
		
		// Same for green channel
		Imgproc.calcHist(eyeChannels, new MatOfInt(1), new Mat(), green_hist, histSize, ranges);

		// Same for blue channel
		Imgproc.calcHist(eyeChannels, new MatOfInt(2), new Mat(), red_hist, histSize, ranges);
		
		// size(blue_hist) = (1, 256)
		//System.out.println("Size of each hist: " + blue_hist.size());
		//double test[] = blue_hist.get(0, 0);
		//System.out.println("Length of test[] = " + test.length);
		//System.out.println("Number of bins: " + blue_hist.rows());

		// Plot the blue color histogram
		List<Double> intensities = NumpyUtils.linspace(0, intensity_max, intensity_max-1);
		List<Double> blue_counts = new ArrayList<Double>();
		List<Double> green_counts = new ArrayList<Double>();
		List<Double> red_counts = new ArrayList<Double>();

		for (int i = 0; i < intensity_max; i++) {
			//System.out.println("Count of bin " + i + " = " + blue_hist.get(i, 0)[0]);
			blue_counts.add(blue_hist.get(i,0)[0]);
			green_counts.add(green_hist.get(i, 0)[0]);
			red_counts.add(red_hist.get(i, 0)[0]);
		}

		/*
		Plot plt = Plot.create();
		plt.plot().add(intensities, blue_counts, "b");
		plt.plot().add(intensities, green_counts, "g");
		plt.plot().add(intensities, red_counts, "r");

		plt.xlabel("Intensity level");
		plt.ylabel("Intensity frequency");
		plt.title("Color Image Histogram");
		
		try {
			plt.show();
		} catch (IOException | PythonExecutionException e) {
			e.printStackTrace();
		}
		plt.savefig("src/main/resources/static/images/hist" + imgNum + ".jpeg");
		imgNum++;
		*/
		
		// We do not include the edge intensities of 0 and 255 because their counts were empirically found to be quite large and could throw of the brightness avg calculation
		int intensity_range[] = {1, intensity_max-1};
		double blue_avg_intensity = 0.0;
		double green_avg_intensity = 0.0;

		try {
			blue_avg_intensity = getAvgIntensity(blue_counts, intensity_range);
			green_avg_intensity = getAvgIntensity(green_counts, intensity_range);
		} catch (Exception e) {
			e.getMessage();
			System.exit(0);
		}
		
		// Purely based on empirical observation
		if (blue_avg_intensity > green_avg_intensity) {
			return "blue";
		} else {
			return "green";
		}
	}

	/**
	 * This method returns the average intensity of the histogram, where only values from 'range' are included in the calculation. 
	 * @param hist is a list of counts for each intensity value. For example hist={3, 4, 5} would mean that there are three pixels in the image with an intensity of 0 (technically between 0 and 1), four pixels with an intensity of 1 and five pixels with an intensity of 2.
	 * @param range is the span of intensities that we include in our calculation. range = [lowest_intensity, highest_intensity]. For example, specifying range={1,254} would tell us to exclude pixels with brightness of 0 and 255. Note: The first value in range cannot be less than the first index of hist (0) and the second value in range cannot be greater than the last index of hist (hist.length - 1). 
	 * @returns a double representing the weighted average intensity of the pixels whose brightness falls within our specified range. 
	 */
	public static double getAvgIntensity(List<Double> hist, int[] range) throws Exception {
		int numerator = 0;
		int denominator = 0;
		if (range.length != 2) {
			throw new Exception("@range does not have length equal to 2");
		}

		for (int i = range[0]; i <= range[1]; i++) {
			if (hist.get(i) < 0) {
				throw new Exception("No hist element can be negative");
			}
			numerator += i * hist.get(i);
			denominator += hist.get(i);
		}
		return numerator / denominator;
	}



	// A GET request to /eye-color binds to the calling of method 'getEyeColor'
	// @RequestParam binds the value of the query parameter name to the value of parameter name in the method
	// localhost:8080/eye-color?name=carl
	@GetMapping("/eye-color")
	public static FaceInfo getEyeColor(@RequestParam(value="name") String name) {
		System.out.println("Loading image file...");
		Mat faceImage = IOUtils.loadFileAsMat(name);
		
		System.out.println("Image file " + name + " successfully loaded");
		Mat eyeImage = null;
		try {
			eyeImage = detectEye(faceImage);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Detected " + name + "'s eye...");
		String eyeColor = predictEyeColor(eyeImage);
		System.out.println(name + "'s eye color: " + eyeColor);
		
		// For now, mock the value of eye color
		// String eyeColor = "green";
		// detectEye("src/main/resources/static/images/img1.jpeg");
		// Use Haar Cascades to detect the eye color from an image
		return new FaceInfo(name, eyeColor);
	}
}
