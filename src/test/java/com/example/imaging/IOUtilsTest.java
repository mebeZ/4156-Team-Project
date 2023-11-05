package com.example.imaging;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.boot.test.context.SpringBootTest;

import nu.pattern.OpenCV;

@SpringBootTest
public class IOUtilsTest {
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
		assertThrows(FileNotFoundException.class, () -> IOUtils.getPathToFile("foo.txt")); // foo.txt is an invalid file
	}

	@Test 
	void loadImageFromEmptyString() {
		assertThrows(FileNotFoundException.class, () -> IOUtils.getPathToFile(""));
	}

	@Test
	void loadImageFromNULL() {
		assertThrows(FileNotFoundException.class, () -> IOUtils.getPathToFile(null));
	}

	@Test
	void loadValidImageFile() {
        Mat result = IOUtils.loadFileAsMat("samantha");
        Mat expected = Imgcodecs.imread( "src/main/resources/static/images/samantha-green.jpeg");
        Assertions.assertEquals(expected.rows(), result.rows());
        Assertions.assertEquals(expected.cols(), result.cols());
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

}
