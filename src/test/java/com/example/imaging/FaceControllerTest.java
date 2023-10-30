package com.example.imaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.FileNotFoundException;

import nu.pattern.OpenCV;
import org.junit.jupiter.api.BeforeAll;
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
			Mat EXPECTED = Imgcodecs.imread( "C:\\Users\\Yonghan Xie\\4156-Team-Project\\src\\main\\resources\\static\\images\\samantha-green.jpeg");
			Assertions.assertEquals(EXPECTED.rows(), result.rows());
			Assertions.assertEquals(EXPECTED.cols(), result.cols());
		} catch (FileNotFoundException error) {
			System.out.println(error.getMessage());
		}
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

}
