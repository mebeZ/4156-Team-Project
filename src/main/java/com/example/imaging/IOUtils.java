package com.example.imaging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class IOUtils {
    /**
     * Locates a person's image file and load it into memory. If there are multiple valid image files containing a person's name, only the first one is loaded.
     *
     * @param name is a person's name - must be a valid substring in at least one image file in resources/static
     * @exception FileNotFoundException if name does not appear in at least one image file
     * @returns: On success, a matrix representing the loaded image file; on failure, a FileNotFoundException
     */
    // loadImageFile
    public static String getPathToFile(String name) throws FileNotFoundException {
        // TODO: If name is null, change the exception that is thrown and change the unit tests
        if (name == null || name.equals("")){
            throw new FileNotFoundException("No image file containing name = " + name);
        }
        
        File imagesFolder = new File("src/main/resources/static");
        // TODO: Refactor getPathToFile so that it searches recursively starting from a sub-directory
        for (File folder : imagesFolder.listFiles()) {
            for (File file : folder.listFiles()) {
                if (file.isFile()) {
                    String filename = file.getName();
                    System.out.println(filename);
                    
                    System.out.println(filename.indexOf(name));
                    if (filename.indexOf(name) >= 0) {
                        String filepath = file.getAbsolutePath();
                        return filepath;
                    }
                }
            }
        }
        throw new FileNotFoundException("No image file containing name = " + name);
    }

    /**
     * Given a substring of the name of an image, get the full image name
     * @param name: The substring of the name which we use to lookup the image (i.e. samantha)
     * @return: The full image name (i.e. samantha-green.jpg)
     */
    public static String getImageName(String name) throws FileNotFoundException {
        String fp = getPathToFile(name);

        //return fp.substring(fp.lastIndexOf("\\") + 1);
        // Use Paths.get(fp).getFileName() to extract only the filename without the path
        return Paths.get(fp).getFileName().toString();
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
            File file = null;
            if (name.contains("/")) {
                file = new File(name);
            } else {
                filepath = getPathToFile(name);
                file = new File(filepath);
            }
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return bufferedImage;
    }

    /*
     * Locates the image located at localpath and moves it to static/face-images
     */
    public static void uploadLocalImage(String localPath) throws Exception {
        Mat img = Imgcodecs.imread(localPath);
        String name = getImageName(localPath);
        Imgcodecs.imwrite("src/main/resources/static/face-images/" + name, img);
    }
}