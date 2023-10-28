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
	 * 1a. A histogram (hist) with one or more negative values should throw an Exception because there cannot be negative pixel counts.
	 * 1b. A histogram with all values equal to zero should throw an exception because the average brightness is in this case. 
	 * 1c. A null histogram should throw an exception
	 * 1d. A histogram which is an empty list should throw an exception
	 * 2a. If range is incorrectly formatted with the start value below zero, then the method should throw an exception
	 * 2b. If range is incorrectly formatted with the end value greater than hist.length - 1, then the method should throw an exception
	 * 2c. If range is incorrectly formatted with both the start value and end value being invalid then the method should throw an exception. 
	 * 2d. If range is null, the method should through an exception
	 * 2e. If range has less than two elements, the method should throw an exception 
	 * 2f. If range has more than two elements, the method should throw an exception
	 * 3a. If hist only has one element, and the range is {0, 0}, then the return value should be 0 (i.e. the index of the one element of the hist)
	 * 3b. If hist has more than one element
	 */

}
