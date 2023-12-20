import com.example.imaging.services.IOService;

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

import com.example.imaging.services.IOService;
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

	@Test
	void loadInvalidImageFile() throws FileNotFoundException {
		assertThrows(FileNotFoundException.class, () -> IOService.getPathToFile("foo.txt")); // foo.txt is an invalid file
	}

	@Test
	void loadImageFromEmptyString() throws FileNotFoundException {
		assertThrows(FileNotFoundException.class, () -> IOService.getPathToFile(""));
	}

	@Test
	void loadImageFromNULL() throws FileNotFoundException {
		assertThrows(FileNotFoundException.class, () -> IOService.getPathToFile(null));
	}

	@Test
	void loadValidImageFile() throws FileNotFoundException {
		Mat result = IOService.loadFileAsMat("samantha");
		Mat expected = Imgcodecs.imread("src/main/resources/static/plain-images/samantha-green.jpeg");
		Assertions.assertEquals(expected.rows(), result.rows());
		Assertions.assertEquals(expected.cols(), result.cols());
	}

	@Test
	void loadValidBufferedImageFile() throws IOException {
		File file = new File("src/main/resources/static/plain-images/samantha-green.jpeg");
		BufferedImage expectedBufferedImage = ImageIO.read(file);
		BufferedImage result = IOService.loadFileAsBufferedImage("samantha");
		Assertions.assertTrue(bufferedImagesEqual(expectedBufferedImage, result));
	}




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


	//Test cases of getFileName()
	// a. If we pass in a valid file name, we should get the correct image name and check its correctness.
	//@Test
	//void testFileNameWithValidNameAndCheckCorrectness() {
		//String imageName = IOService.getImageName("carl");
		//Assertions.assertTrue(imageName.contains("carl"));
		//Assertions.assertTrue(imageName.endsWith(".jpeg"));
	//}

	// b. If we pass in a non-existent name, we should get an IllegalArgumentException.
	//@Test
	//void testFileNameWithNonExistentName() {
	//	assertThrows(IllegalArgumentException.class, () -> IOService.getImageName("nonexistentname"));
	//}

	// c. If we pass in an empty string, we should get an IllegalArgumentException.
	//@Test
	//void testFileNameFromEmptyString() {
		//assertThrows(IllegalArgumentException.class, () -> IOService.getImageName(""));
	//}

	// d. If we pass in a null value, we should get an IllegalArgumentException.
	//@Test
	//void testFileNameWithNull() {
		//assertThrows(IllegalArgumentException.class, () -> IOService.getImageName(null));
	//}

	// e. If we pass in a valid name, we should get the correct image name.
	@Test
	void testFileNameWithValidName() {
		String name = "samantha";
		String expectedName = "samantha-green.jpeg";
		String result = IOService.getImageName(name);

		// Extract the file name from the full path
		// resultFileName = Paths.get(result).getFileName().toString();

		Assertions.assertEquals(expectedName, result);
	}

	// f. If we pass in a valid name, we should get the correct image name, and the associated image file should exist.
	@Test
	void testValidNameReturnsFile() {
		String imageName = IOUtils.getImageName("samantha");
		String imagePath = "src/main/resources/static/plain-images/" + imageName;
		File imageFile = new File(imagePath);
		Assertions.assertTrue(imageFile.exists());
	}


//	@Test
//	void getImageNameMultipleValidFiles() {
//		// Create a mock image folder with multiple valid files
//		File mockImageFolder = new File("src/main/resources/static/images/mock-images");
//		mockImageFolder.mkdirs();
//
//		// Copy valid image files to the mock folder
//		Path sourcePath1 = Paths.get("src/main/resources/static/plain-images/samantha-green.jpeg");
//		Path destinationPath1 = Paths.get("src/main/resources/static/images/mock-images/samantha-green.jpeg");
//		try {
//			Files.copy(sourcePath1, destinationPath1, StandardCopyOption.REPLACE_EXISTING);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		Path sourcePath2 = Paths.get("src/main/resources/static/plain-images/carl-blue.jpeg");
//		Path destinationPath2 = Paths.get("src/main/resources/static/images/mock-images/sasha-blue.jpeg");
//		try {
//			Files.copy(sourcePath2, destinationPath2, StandardCopyOption.REPLACE_EXISTING);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//
//		String imageName = IOUtils.getImageName("samantha");
//		Assertions.assertEquals("samantha-green.jpeg", imageName); // Verify that the first valid file is used
//
//		mockImageFolder.delete();
//
//	}


}
