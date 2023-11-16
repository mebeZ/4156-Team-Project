package com.example.imaging;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.FileNotFoundException;

import nu.pattern.OpenCV;
import org.junit.jupiter.api.BeforeAll;

// OpenCV imports
import org.opencv.core.Mat;

import org.opencv.imgcodecs.Imgcodecs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nu.pattern.OpenCV;

@SpringBootTest
class FaceControllerTest {
	
	@BeforeAll
	public static void loadLocally(){
		OpenCV.loadLocally();
	}

	/**
	 * Tests for detectEye method
	 * 1a. If imgMatrix is null, a NullPointException should be thrown
	 * 1b. If imgMatrix input does not contain a picture of a face, an IOException should be thrown w/ message "No Face Detected"
	 * 1c. If imgMatrix contains more than one picture of a face, an IOException should be thrown w/ message "More than one face detected"
	 * 2. If a face exists, but two eyes cannot be detected, an IOException should be thrown w/ message "One or more eyes not detected"
	 */

	@Test
	void testNullImage() {
		assertThrows(NullPointerException.class, () -> FaceController.detectEye(null));
	}

	@Test
	void testNonFaceImage() {
		// Load an image without a face
		String filepath = "src/main/resources/static/invalid-images/non_face.jpeg";
		Mat testImg = Imgcodecs.imread(filepath);
		// Ensure that detectEye detects no face
		Exception e = assertThrows(IOException.class, () -> FaceController.detectEye(testImg));
		assertEquals("No face detected", e.getMessage());
	}

	@Test
	void testMultipleFaceImage() {
		// Load an image with multiple faces
		String filepath = "src/main/resources/static/invalid-images/two_faces.jpg";
		Mat testImg = Imgcodecs.imread(filepath);
		// Ensure that detectEye detects two faces
		Exception e = assertThrows(IOException.class, () -> FaceController.detectEye(testImg));
		assertEquals("Multiple faces detected", e.getMessage());
	}

	@Test
	void testSidewaysFaceImage() {
		String filepath = "src/main/resources/static/invalid-images/sideways_face.webp";
		Mat testImg = Imgcodecs.imread(filepath);
		// Ensure that detectEye cannot detect both eyes and thus recognizes the face image is not oriented properly
		Exception e = assertThrows(IOException.class, () -> FaceController.detectEye(testImg));
		assertEquals("One or more eyes not detected", e.getMessage());
	}
	
	//	 Tests for getAvgIntensity method
	//	 1a. A histogram (hist) with one or more negative values should throw an Exception because there cannot be negative pixel counts.
	@Test
	void testNegativeValuesInHistogram() {
		List<Double> hist = Arrays.asList(-1.0, 2.0, 3.0);
		int[] range = {0, 2};
		assertThrows(Exception.class, () -> FaceController.getAvgIntensity(hist, range));
	}
	
	//	 1b. A histogram with all values equal to zero should throw an exception because the average brightness is in this case.
	@Test
	void testAllZeroHistogram() {
		List<Double> hist = Arrays.asList(0.0, 0.0, 0.0);
		int[] range = {0, 2};
		assertThrows(Exception.class, () -> FaceController.getAvgIntensity(hist, range));
	}
	
	//	 1c. A null histogram should throw an exception
	@Test
	void testNullHistogram() {
		List<Double> hist = null;
		int[] range = {0, 2};
		assertThrows(Exception.class, () -> FaceController.getAvgIntensity(hist, range));
	}
	
	
	//	 1d. A histogram which is an empty list should throw an exception
	@Test
	void testEmptyHistogram() {
		List<Double> hist = new ArrayList<>();
		int[] range = {0, 2};
		assertThrows(Exception.class, () -> FaceController.getAvgIntensity(hist, range));
	}
	
	//	 2a. If range is incorrectly formatted with the start value below zero, then the method should throw an exception
	@Test
	void testInvalidStartValueInRange() {
		List<Double> hist = Arrays.asList(1.0, 2.0, 3.0);
		int[] range = {-1, 2};
		assertThrows(Exception.class, () -> FaceController.getAvgIntensity(hist, range));
	}
	
	//	 2b. If range is incorrectly formatted with the end value greater than hist.length - 1, then the method should throw an exception
	@Test
	void testInvalidEndValueInRange() {
		List<Double> hist = Arrays.asList(1.0, 2.0, 3.0);
		int[] range = {0, 3};
		assertThrows(Exception.class, () -> FaceController.getAvgIntensity(hist, range));
	}
	
	//	 2c. If range is incorrectly formatted with both the start value and end value being invalid then the method should throw an exception.
	@Test
	void testInvalidStartAndEndValuesInRange() {
		List<Double> hist = Arrays.asList(1.0, 2.0, 3.0);
		int[] range = {-1, 3};
		assertThrows(Exception.class, () -> FaceController.getAvgIntensity(hist, range));
	}
	
	//	 2d. If range is null, the method should through an exception
	@Test
	void testNullRange() {
		List<Double> hist = Arrays.asList(1.0, 2.0, 3.0);
		int[] range = null;
		assertThrows(Exception.class, () -> FaceController.getAvgIntensity(hist, range));
	}
	
	
	//	 2e. If range has less than two elements, the method should throw an exception
	@Test
	void testRangeWithLessThanTwoElements() {
		List<Double> hist = Arrays.asList(1.0, 2.0, 3.0);
		int[] range = {0};
		assertThrows(Exception.class, () -> FaceController.getAvgIntensity(hist, range));
	}
	
	//	 2f. If range has more than two elements, the method should throw an exception
	@Test
	void testRangeWithMoreThanTwoElements() {
		List<Double> hist = Arrays.asList(1.0, 2.0, 3.0);
		int[] range = {0, 2, 3};
		assertThrows(Exception.class, () -> FaceController.getAvgIntensity(hist, range));
	}
	
	//	 3a. If hist only has one element, and the range is {0, 0}, then the return value should be 0 (i.e. the index of the one element of the hist)
	@Test
	void testSingleElementHistAndRangeZeroToZero() throws Exception {
		List<Double> hist = Arrays.asList(3.0);
		int[] range = {0, 0};
		int avgIntensity = (int) FaceController.getAvgIntensity(hist, range);
		assertEquals(0, avgIntensity);
	}
	
	//	 3b. If hist has more than one element
	@Test
	void testMultipleElementHist() throws Exception {
		List<Double> hist = Arrays.asList(3.0, 14.0, 6.0);
		int[] range = {0, 2};
		// Average intensity calculation: (2*1 + 3*2 + 4*3 + 5*4) / (1 + 2 + 3 + 4)
		double avgIntensity = FaceController.getAvgIntensity(hist, range);
		assertEquals(1.0, avgIntensity, 0.001); // Using a delta for double comparison
	}
	
	/**
	 * Remaining tests for getAvgIntensity method
	 * 3a. If hist only has one element, and the range is {0, 0}, then the return value should be 0 (i.e. the index of the one element of the hist)
	 * 3b. If hist only has one non-zero element and it is within the range, then the return value should be the index of that element
	 * 3c. If hist has two or more non-zero elements, the return value should be the weighted average of the indices of those elements. For example, hist={2,8} and range={0,1} should return 0.8. 
	 */

}
