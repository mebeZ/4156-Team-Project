package com.example.imaging.controllers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.imaging.models.Client;
import com.example.imaging.models.Image;
import com.example.imaging.models.data.ClientRepository;
import com.example.imaging.models.data.ImageRepository;
import com.example.imaging.services.IOService;
import com.google.common.collect.Iterables;


@Controller
public class UploadController {

    @Autowired
    private ImageRepository imageDao;

    @Autowired
    private ClientRepository clientDao;

    // Inject image upload directory location from application.properties
    @Value("${upload.dir}")
    private String uploadDir;

    /*
     * Helper function which transfers the Multipartfile @file to the specified directory @uploadDir
     * Relies on vodoo filepath operations
     * @returns: Root path to the image: /images/face-images/{file_name.jpeg}
     */
    private String transferFile(MultipartFile file, String uploadDir) throws Exception {
        // Get current working directory
        String currDir = System.getProperty("user.dir");

        // Create absolute path for saving the image file
        Path uploadPath = Paths.get(currDir, uploadDir);

        // Create the directory if it doesn't yet exist
        if (uploadPath.toFile().exists()) {
            uploadPath.toFile().mkdirs();
        }

        // Get the original filename of the uploaded file
        String imgName = file.getOriginalFilename();

        // Create the absolute path for the new file
        Path filePath = uploadPath.resolve(imgName);

        // Transfer the file to the specified path
        file.transferTo(filePath.toFile());

        // Get the substring after 'static' as the image url (i.e /images/face-images/{file_name})
        String pathString = filePath.toString();
        int index = pathString.indexOf("static");
        if (index < 0) {
            throw new Exception("'static' not found in filePath: " + pathString);
        }
        return pathString.substring(index + "static".length());
    }

    /*
     * Given a legitimate access token, render the HTML page which lets a client upload an image to the service
     */
    @GetMapping("/upload")
    public String renderUploadPage(Model model, @RequestParam(name="accessToken") String token) {
        model.addAttribute("accessToken", token);
        // Bind images to the model
        Iterable<Image> images = imageDao.findAll();
        model.addAttribute("images", images);
        return "upload";
    }

    /*
     * Given a Multipartfile and a legitimate access token, upload the Multipartfile to the service
     * @returns: ResponseEntity indicating whether the file upload was successful
     */
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(Model model, @RequestParam("file") MultipartFile file, @RequestParam("token") String token) throws Exception {
        // Find the client with the specified token
        Optional<Client> tclient = clientDao.findById(token);
        if (!tclient.isPresent()) {
            throw new Exception("No client found with accessToken=" + token);
        }
        Client client = tclient.get();

        // Make sure that the uploaded file is not empty
        if (file.isEmpty()) {
            throw new Exception("File cannot be empty");
        }

        // Move the Multipartfile to inside the static resources 
        String imgUrl = transferFile(file, uploadDir);
        System.out.println("Image URL: " + imgUrl);

        // Create an Image object representing the image and save it to the Images db
        //String filepath = imageFile.toString();
        imageDao.save(new Image(imgUrl));
        
        // Update the Clients DB with the name of the image that the client uploaded
        client.setImagePath(imgUrl);
        clientDao.save(client);

        // Return an HTTP response indicating upload was successful
        return new ResponseEntity<>("Upload successful", HttpStatus.OK);
    }

    @GetMapping("/getImage")
    public ResponseEntity<byte []> getImage(@RequestParam(name="selectedImageName") String imageName) throws Exception {
        byte[] imageBytes = IOService.loadFileAsBinary(imageName);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
