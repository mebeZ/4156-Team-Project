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
	 * 2. Mock image folder 
	 * a. Multiple valid files - make sure only the first one is opened
	 * b. No valid files is the same as case 1a - thus we can ignore this case
	 * c. Only folders with no matching names 
	 * d. Only folders with one matching name
	 * e. 
	 */
	@BeforeAll
	public static void loadLocally(){
		OpenCV.loadLocally();
	}

	@Test
	void loadInvalidImageFile() {
		FaceControllerTest.loadLocally();
		assertThrows(FileNotFoundException.class, () -> FaceController.loadImageFile("foo.txt")); // foo.txt is an invalid file
	}

	@Test 
	void loadImageFromEmptyString() {
		FaceControllerTest.loadLocally();
		assertThrows(FileNotFoundException.class, () -> FaceController.loadImageFile(""));
	}

	@Test
	void loadImageFromNULL() {
		FaceControllerTest.loadLocally();
		assertThrows(FileNotFoundException.class, () -> FaceController.loadImageFile(null));
	}

	@Test
	void loadValidImageFile() {
		FaceControllerTest.loadLocally();
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
	 * 1. 
	 */

}
