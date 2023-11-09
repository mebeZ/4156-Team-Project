package com.example.imaging;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.boot.test.context.SpringBootTest;

import nu.pattern.OpenCV;

import javax.imageio.ImageIO;

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

	/*Start of unit tests for method loadFileAsMat
	*
	* Test invalid file input
	* */
    @Test
	void loadInvalidImageFile() {
		assertThrows(FileNotFoundException.class, () -> IOUtils.getPathToFile("foo.txt")); // foo.txt is an invalid file
	}

	/*Start of unit tests for method loadFileAsMat
	 *
	 * Test empty input
	 * */
	@Test 
	void loadImageFromEmptyString() {
		assertThrows(FileNotFoundException.class, () -> IOUtils.getPathToFile(""));
	}

	/*Start of unit tests for method loadFileAsMat
	 *
	 * Test null input
	 * */
	@Test
	void loadImageFromNULL() {
		assertThrows(FileNotFoundException.class, () -> IOUtils.getPathToFile(null));
	}

	/*Start of unit tests for method loadFileAsMat
	 *
	 * Test valid input
	 * */
	@Test
	void loadValidImageFile() {
        Mat result = IOUtils.loadFileAsMat("samantha");
        Mat expected = Imgcodecs.imread( "src/main/resources/static/plain-images/samantha-green.jpeg");
        Assertions.assertEquals(expected.rows(), result.rows());
        Assertions.assertEquals(expected.cols(), result.cols());
	}

	/*Start of unit tests for method loadFileAsMat
	 *
	 * Test multiple valid inputs but one read the first one
	 * */
	@Test
	void multipleValidImageFile() {
		File mockImage = new File("src/main/resources/static/images/mock-images/");
		mockImage.mkdirs();

		//copy samantha-green.jpeg to mock-images folder
		Path sourcePath = Paths.get("src/main/resources/static/plain-images/samantha-green.jpeg");
		Path destinationPath = Paths.get("src/main/resources/static/images/mock-images/samantha-green.jpeg");
		try {
			Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("File copied successfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}

		//copy carl-blue.jpeg to mock-images folder and rename as samantha-blue.jpeg
		sourcePath = Paths.get("src/main/resources/static/plain-images/carl-blue.jpeg");
		destinationPath = Paths.get("src/main/resources/static/images/mock-images/samantha-blue.jpeg");
		try {
			Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("File copied successfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}

		//test loadFileAsMat can read the samantha-green as the first input between samantha-blue and samantha-green in mock-images folder
		Mat result = IOUtils.loadFileAsMat("samantha");
		Mat expected = Imgcodecs.imread( "src/main/resources/static/images/mock-images/samantha-green.jpeg");

		Assertions.assertEquals(expected.rows(), result.rows());
		Assertions.assertEquals(expected.cols(), result.cols());
		mockImage.delete();
		//File foo = new File("src/main/resources/static/images/mock-images/foo/");
		//File bar = new File("src/main/resources/static/images/mock-images/bar/");
		//foo.mkdirs();
		//bar.mkdirs();
		//foo.delete();
		//bar.delete();
	}

	/*Start of unit tests for method loadFileAsBufferedImage
	 *
	 * Test invalid file input
	 * */
	@Test
	void loadInvalidBufferedImageFile() {
		assertThrows(FileNotFoundException.class, () -> IOUtils.getPathToFile("foo.txt"));
	}

	/*Start of unit tests for method loadFileAsBufferedImage
	 *
	 * Test empty file input
	 * */
	@Test
	void loadBufferedImageFromEmptyString() {
		assertThrows(FileNotFoundException.class, () -> IOUtils.getPathToFile(""));

	}

	/*Start of unit tests for method loadFileAsBufferedImage
	 *
	 * Test null file input
	 * */
	@Test
	void loadBufferedImageFromNULL() {
		assertThrows(FileNotFoundException.class, () -> IOUtils.getPathToFile(null));
	}

	/*Helper function for test valid file input for loadFileAsBufferedImage
	 *
	 * Compare two BufferedImage object and check equals for their width, height and RGB
	 * */
	boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
		if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
			for (int x = 0; x < img1.getWidth(); x++) {
				for (int y = 0; y < img1.getHeight(); y++) {
					if (img1.getRGB(x, y) != img2.getRGB(x, y))
						return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	/*Start of unit tests for method loadFileAsBufferedImage
	 *
	 * Test valid file input
	 * */
	@Test
	void loadValidBufferedImageFile() {
		File file = new File("src/main/resources/static/plain-images/samantha-green.jpeg");
		BufferedImage ExpectedBufferedImage = null;
		BufferedImage result = IOUtils.loadFileAsBufferedImage("samantha");
		try {
			ExpectedBufferedImage = ImageIO.read(file);
		} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
		}
		Assertions.assertEquals(bufferedImagesEqual(ExpectedBufferedImage, result), true);
	}

}
