package com.example.imaging;

import static org.junit.Assert.assertThrows;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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
	@Test
	void loadInvalidImageFile() {
		assertThrows(FileNotFoundException.class, () -> FaceController.loadImageFile("foo.txt")); // foo.txt is an invalid file
	}

	@Test 
	void loadImageFromEmptyString() {
		assertThrows(FileNotFoundException.class, () -> FaceController.loadImageFile(""));
	}

	@Test
	void loadImageFromInteger() {
		assertThrows(FileNotFoundException.class, () -> FaceController.loadImageFile(null));
	}

	/**
	 * Tests for getAvgIntensity method
	 * 1. 
	 */

}
