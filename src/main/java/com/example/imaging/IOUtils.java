package com.example.imaging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class IOUtils {
    /**
	 * Locates a person's image file and load it into memory. If there are multiple valid image files containing a person's name, only the first one is loaded.
	 * 
	 * @param name is a person's name - must be a valid substring in at least one image file in resources/static/images
	 * @exception FileNotFoundException if name does not appear in at least one image file 
	 * @returns: On success, a matrix representing the loaded image file; on failure, a FileNotFoundException
	 */
    // loadImageFile
	public static String getPathToFile(String name) throws FileNotFoundException {
		File imagesFolder = new File("src/main/resources/static/plain-images");
		File[] imageFiles = imagesFolder.listFiles();
		for (int i = 0; i < imageFiles.length; i++) {
			if (imageFiles[i].isFile()) {
				String filename = imageFiles[i].getName();
				System.out.println(filename);
				if (name == null || name.equals("")){
					throw new FileNotFoundException("No image file containing name = " + name);
				}
				System.out.println(filename.indexOf(name));
				if (filename.indexOf(name) >= 0) {
					String filepath = imageFiles[i].getAbsolutePath();
                    return filepath;
                }
			}
		}
		throw new FileNotFoundException("No image file containing name = " + name);
	}

    public static String getImageName(String name) {
        String fp = null;
        try {
            fp = getPathToFile(name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return fp.substring(fp.lastIndexOf("/") + 1);
    }

    public static Mat loadFileAsMat(String name) {
        String filepath = null;
        try {
            filepath = getPathToFile(name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("Filepath = " + filepath);
        Mat imgMatrix = Imgcodecs.imread(filepath);
        System.out.println("Size of the original loaded image: " + imgMatrix.size());
        //System.out.println("Number of rows: " + imgMatrix.rows() + " Number of cols: " + imgMatrix.cols());
        //Imgproc.resize(imgMatrix, imgMatrix, new Size(1000, 1000));
        return imgMatrix;
    }

    public static BufferedImage loadFileAsBufferedImage(String name) {
        String filepath = null;
        BufferedImage bufferedImage = null;
        try {
            filepath = getPathToFile(name);
            File file = new File(filepath);
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return bufferedImage;
    }
}