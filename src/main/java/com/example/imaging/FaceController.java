package com.example.imaging;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.sh0nk.matplotlib4j.NumpyUtils;
//import com.github.sh0nk.matplotlib4j.Plot;
//import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.File;
import java.io.FileNotFoundException;
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
//import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

@RestController
public class FaceController {
	// Used to name RGB histogram files sequentially
	//private static int imgNum = 1;

	public static Mat detectEye(Mat imgMatrix) {
		//Mat imgMatrix = Imgcodecs.imread(fpath);
		//if (imgMatrix == null) {
		//	throw new FileNotFoundException(fpath + "is an invalid file path");
		//}
		
		// Resize the image such that it can later be displayed by HighGUI
		//Imgproc.resize(imgMatrix, imgMatrix, new Size(1000, 1000));
		//System.out.println("Size of the resized face image: " + imgMatrix.size());

		CascadeClassifier faceCascade = new CascadeClassifier("src/main/resources/templates/haarcascades/haarcascade_frontalface_default.xml");
		CascadeClassifier eyesCascade = new CascadeClassifier("src/main/resources/templates/haarcascades/haarcascade_eye.xml");
		
		MatOfRect faces = new MatOfRect();
		MatOfRect eyes = new MatOfRect();

		faceCascade.detectMultiScale(imgMatrix, faces);
		eyesCascade.detectMultiScale(imgMatrix, eyes);
		int borderThickness = 5;

		// Concatenate the bounding rectangles for the faces and the eyes such that they can be drawn in one for loop rather than two
		List<Rect> rects = Stream.concat(faces.toList().stream(), eyes.toList().stream()).toList();

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

		Rect eyeROI = eyes.toList().get(0);
		Mat leftEyeMat = new Mat(imgMatrix, eyeROI);
		System.out.println("Size of eye region: " + leftEyeMat.size());

		return leftEyeMat;
	}

	public static double[] detectIris(Mat eyeImage) throws Exception {
		/*
		HighGui.imshow("Color eye image", eyeImage);
		HighGui.waitKey();
		*/ 

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
				throw new Exception("detectIris failedd: Number of detected circles should be 1 (i.e. the iris). However, " + irisCircles.cols() + " were detected"); 
			// If more than 1 circles are detected, tweak the parameters such that less false positives are detected
			} else if (irisCircles.cols() > 1 && minDist <= (double)grayEyeImage.rows()/16) {
				minDist *= 1.5;
				p2 *= 1.5;
			// If we've tweaked the parameters, and false positives are still detected, then we cannot discern the iris in the image, so throw an exception
			} else if (irisCircles.cols() > 1) {
				throw new Exception("detectIris failed: Number of detected circles should be 1 (i.e. the iris). However, " + irisCircles.cols() + " were detected"); 
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

		// Make sure only one circle is detected for the iris
		if (irisCircles.cols() != 1) {
			throw new Exception("detectIris failed: Number of detected circles should be 1 (i.e. the iris). However, " + irisCircles.cols() + " were detected");
		}
	
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
		double blue_avg_intensity = getAvgIntensity(blue_counts, intensity_range);
		double green_avg_intensity = getAvgIntensity(green_counts, intensity_range);
		
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
	public static double getAvgIntensity(List<Double> hist, int[] range) {
		int numerator = 0;
		int denominator = 0;
		for (int i = range[0]; i <= range[1]; i++) {
			numerator += i * hist.get(i);
			denominator += hist.get(i);
		}
		return numerator / denominator;
	}

	/**
	 * Locates a person's image file and load it into memory. If there are multiple valid image files containing a person's name, only the first one is loaded.
	 * 
	 * @param name is a person's name - must be a valid substring in at least one image file in resources/static/images
	 * @exception FileNotFoundException if name does not appear in at least one image file 
	 * @returns: On success, a matrix representing the loaded image file; on failure, a FileNotFoundException
	 */
	public static Mat loadImageFile(String name) throws FileNotFoundException {
		File imagesFolder = new File("src/main/resources/static/images");
		File[] imageFiles = imagesFolder.listFiles();
		for (int i = 0; i < imageFiles.length; i++) {
			if (imageFiles[i].isFile()) {
				String filename = imageFiles[i].getName();
				System.out.println(filename);
				if (name == null || name.equals("")){
					throw new FileNotFoundException("No image file containing name = " + name);
				}
				System.out.println(filename.indexOf(name));
				if (filename.indexOf(name) >= 0) {
					String filepath = imageFiles[i].getAbsolutePath();
					System.out.println("Filepath = " + filepath);
					Mat imgMatrix = Imgcodecs.imread(filepath);
					System.out.println("Size of the original loaded image: " + imgMatrix.size());
					//System.out.println("Number of rows: " + imgMatrix.rows() + " Number of cols: " + imgMatrix.cols());
					//Imgproc.resize(imgMatrix, imgMatrix, new Size(1000, 1000));
					return imgMatrix;
				}
			}
		}
		throw new FileNotFoundException("No image file containing name = " + name);
	}

	// A GET request to /eye-color binds to the calling of method 'getEyeColor'
	// @RequestParam binds the value of the query parameter name to the value of parameter name in the method
	// localhost:8080/eye-color?name=carl
	@GetMapping("/eye-color")
	public static FaceInfo getEyeColor(@RequestParam(value="name") String name) {
		Mat faceImage = null;
		try {
			System.out.println("Loading image file...");
			faceImage = loadImageFile(name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		System.out.println("Image file " + name + " successfully loaded");
		Mat eyeImage = detectEye(faceImage);
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
