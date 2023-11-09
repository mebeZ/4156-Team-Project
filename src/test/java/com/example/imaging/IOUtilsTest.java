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
	 * Tests for getPathToFile method
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
		Mat expected = Imgcodecs.imread( "src/main/resources/static/plain-images/samantha-green.jpeg");
		Assertions.assertEquals(expected.rows(), result.rows());
		Assertions.assertEquals(expected.cols(), result.cols());
	}

	// c. In a mock image folder there are only folders (no images), and none of the folders have names which match the parameter name.
	@Test
	void loadImageFromFolderWithOnlySubfoldersNoMatch() {
		// Create subfolders without matching names
		File subfolder1 = new File("src/main/resources/static/plain-images/subfolder1");
		File subfolder2 = new File("src/main/resources/static/plain-images/subfolder2");
		subfolder1.mkdirs();
		subfolder2.mkdirs();

		try {
			assertThrows(FileNotFoundException.class, () -> IOUtils.getPathToFile("nonexistentname"));
		} finally {
			// Clean up created subfolders
			subfolder1.delete();
			subfolder2.delete();
		}
	}


	// d. In a mock image folder there is one or more folders
	@Test
	void loadImageFromFolderWithSubfoldersAndImagesFirstMatchingSubfolder() {
		File subfolder1 = new File("src/main/resources/static/plain-images/subfolder1");
		File subfolder2 = new File("src/main/resources/static/plain-images/subfolder2");
		subfolder1.mkdirs();
		subfolder2.mkdirs();

		// Create an image file in both subfolders
		Path sourcePath1 = Paths.get("src/main/resources/static/plain-images/samantha-green.jpeg");
		Path destinationPath1 = Paths.get("src/main/resources/static/plain-images/subfolder1/samantha-green.jpeg");
		Path sourcePath2 = Paths.get("src/main/resources/static/plain-images/carl-blue.jpeg");
		Path destinationPath2 = Paths.get("src/main/resources/static/plain-images/subfolder2/carl-blue.jpeg");

		try {
			Files.copy(sourcePath1, destinationPath1, StandardCopyOption.REPLACE_EXISTING);
			Files.copy(sourcePath2, destinationPath2, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String imagePath = null;
		try {
			imagePath = IOUtils.getPathToFile("samantha");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		Assertions.assertNotNull(imagePath);
		Assertions.assertTrue(imagePath.endsWith("samantha-green.jpeg"));

		// Clean up: delete the created folders and files
		deleteFolder(subfolder1);
		deleteFolder(subfolder2);
	}
	// Helper method to delete the mock folders and files
	private void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteFolder(file);
				} else {
					file.delete();
				}
			}
		}
		folder.delete();
	} // end of unit tests for the getPathToFile()


	/*
	 * Start of unit tests for the loadFileAsMat() method in the IOUtils class.
	 */

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

	@Test
	void loadInvalidBufferedImageFile() {
		assertThrows(FileNotFoundException.class, () -> IOUtils.getPathToFile("foo.txt"));
	}

	@Test
	void loadBufferedImageFromEmptyString() {
		assertThrows(FileNotFoundException.class, () -> IOUtils.getPathToFile(""));

	}
	@Test
	void loadBufferedImageFromNULL() {
		assertThrows(FileNotFoundException.class, () -> IOUtils.getPathToFile(null));
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
	} // end of unit tests for the loadFileAsMat()


	/*
	 * Start of unit tests for the getImageName method in the IOUtils class.
	 *
	 * Test cases:
	 * a. If we pass in a valid file name, we should get the correct image name and check its correctness.
	 *    This test ensures that when a valid file name is provided, the resulting image name
	 *    contains the specified name and ends with the ".jpeg" extension.
	 *
	 * b. If we pass in a non-existent name, we should get an IllegalArgumentException.
	 *    This test checks if an IllegalArgumentException is thrown when a non-existent name is passed.
	 *
	 * c. If we pass in an empty string, we should get an IllegalArgumentException.
	 *    This test verifies that an IllegalArgumentException is thrown when an empty string is used as input.
	 *
	 * d. If we pass in a null value, we should get an IllegalArgumentException.
	 *    This test checks if the method throws an IllegalArgumentException when a null value is provided.
	 *
	 * e. If we pass in a valid name, we should get the correct image name.
	 *    This test ensures that the correct image name is obtained when a valid name is passed.
	 *
	 * f. If we pass in a valid name, we should get the correct image name, and the associated image file should exist.
	 *    This test checks both the correctness of the image name and the existence of the associated image file
	 *    when a valid name is provided.
	 */

	// a. If we pass in a valid file name, we should get the correct image name and check its correctness.
	@Test
	void testFileNameWithValidNameAndCheckCorrectness() {
		String imageName = IOUtils.getImageName("carl");
		Assertions.assertTrue(imageName.contains("carl"));
		Assertions.assertTrue(imageName.endsWith(".jpeg"));
	}

	// b. If we pass in a non-existent name, we should get an IllegalArgumentException.
	@Test
	void testFileNameWithNonExistentName() {
		assertThrows(IllegalArgumentException.class, () -> IOUtils.getImageName("nonexistentname"));
	}

	// c. If we pass in an empty string, we should get an IllegalArgumentException.
	@Test
	void testFileNameFromEmptyString() {
		assertThrows(IllegalArgumentException.class, () -> IOUtils.getImageName(""));
	}

	// d. If we pass in a null value, we should get an IllegalArgumentException.
	@Test
	void testFileNameWithNull() {
		assertThrows(IllegalArgumentException.class, () -> IOUtils.getImageName(null));
	}

	// e. If we pass in a valid name, we should get the correct image name.
	@Test
	void testFileNameWithValidName() {
		String name = "samantha";
		String expectedName = "samantha-green.jpeg";
		String result = IOUtils.getImageName(name);

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
	} // end of unit tests for the getImageName()


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
//		Path destinationPath2 = Paths.get("src/main/resources/static/images/mock-images/samantha-blue.jpeg");
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