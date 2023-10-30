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
	/*
	 * Tests for loadImageFile method
	 * 1a. If we pass in an invalid file name - which is a non-empty string - we should get a FileNotFoundException
	 * 1b. If we pass in an empty string, we should get a FileNotFoundException
	 * 1c. If we pass in a null value, we should get a FileNotFoundException
	 * 1d. If we pass in a valid file name, we should get a Mat object back, and the size of that Mat object should match the size of the image in static/images 
	 * 2a. In a mock image folder there are multiple valid files. Make sure that only the first one is opened.
	 * 2b. In a mock image folder there are no valid files. This is the same as case 1a. Thus we can ignore this case.
	 * c. In a mock image folder there are only folders (no images), and none of the folders have names which match the parameter name. 
	 * d. In a mock image folder there is one or more folders 
	 * e. 
	 */
	@BeforeAll
	public static void loadLocally(){
		OpenCV.loadLocally();
	}

	@Test
	void loadInvalidImageFile() {
		assertThrows(FileNotFoundException.class, () -> FaceController.loadImageFile("foo.txt")); // foo.txt is an invalid file
	}

	@Test 
	void loadImageFromEmptyString() {
		assertThrows(FileNotFoundException.class, () -> FaceController.loadImageFile(""));
	}

	@Test
	void loadImageFromNULL() {
		assertThrows(FileNotFoundException.class, () -> FaceController.loadImageFile(null));
	}

	@Test
	void loadValidImageFile() {
		try {
			Mat result = FaceController.loadImageFile("samantha");
			Mat EXPECTED = Imgcodecs.imread( "src/main/resources/static/images/samantha-green.jpeg");
			Assertions.assertEquals(EXPECTED.rows(), result.rows());
			Assertions.assertEquals(EXPECTED.cols(), result.cols());
		} catch (FileNotFoundException error) {
			System.out.println(error.getMessage());
		}
	}

	@Test
	void multipleValidImageFile() {
		File mockImage = new File("src/main/resources/static/images/mock-images/");
		mockImage.mkdirs();
		File foo = new File("src/main/resources/static/images/mock-images/foo/");
		File bar = new File("src/main/resources/static/images/mock-images/bar/");
		foo.mkdirs();
		bar.mkdirs();
		//mockImage.delete();
		//foo.delete();
		//bar.delete();
	}


//	@Test
//	void loadMultipleValidImageFile() {
//
//	}
	/**
	 * Tests for getAvgIntensity method
	 * 1a. A histogram (hist) with one or more negative values should throw an Exception because there cannot be negative pixel counts.
	 * 1b. A histogram with all values equal to zero should throw an exception because the average brightness is undefined in this case. 
	 * 1c. A histogram in which all non-zero values are outside of the range (i.e. hist = {0, 0, 0, 3, 3}, range = {0,2}) should throw an exception because the average brightness is undifined in this case.
	 * 1d. A null histogram should throw an exception
	 * 1e. A histogram which is an empty list should throw an exception
	 * 2a. If range is incorrectly formatted with the start value below zero, then the method should throw an exception
	 * 2b. If range is incorrectly formatted with the end value greater than hist.length - 1, then the method should throw an exception
	 * 2c. If range is incorrectly formatted with both the start value and end value being invalid then the method should throw an exception. 
	 * 2d. If range is null, the method should through an exception
	 * 2e. If range has less than two elements, the method should throw an exception 
	 * 2f. If range has more than two elements, the method should throw an exception
	 * 3a. If hist only has one element, and the range is {0, 0}, then the return value should be 0 (i.e. the index of the one element of the hist)
	 * 3b. If hist only has one non-zero element and it is within the range, then the return value should be the index of that element
	 * 3c. If hist has two or more non-zero elements, the return value should be the weighted average of the indices of those elements. For example, hist={2,8} and range={0,1} should return 0.8. 
	 */


	
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
	void testSingleElementHistAndRangeZeroToZero() {
		List<Double> hist = Arrays.asList(3.0);
		int[] range = {0, 0};
		int avgIntensity = (int) FaceController.getAvgIntensity(hist, range);
		assertEquals(0, avgIntensity);
	}
	
	//	 3b. If hist has more than one element
	@Test
	void testMultipleElementHist() {
		List<Double> hist = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
		int[] range = {1, 4};
		// Average intensity calculation: (2*1 + 3*2 + 4*3 + 5*4) / (1 + 2 + 3 + 4)
		double avgIntensity = FaceController.getAvgIntensity(hist, range);
		assertEquals(3.0, avgIntensity, 0.001); // Using a delta for double comparison
	}
	//


}
