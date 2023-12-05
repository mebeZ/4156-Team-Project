package com.example.imaging.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.imaging.models.Client;
import com.example.imaging.models.Image;
import com.example.imaging.models.data.ClientRepository;
import com.example.imaging.models.data.ImageRepository;


@RestController
public class UploadController {

    @Autowired
    private ImageRepository imageDao;

    @Autowired
    private ClientRepository clientDao;

    // Inject upload directory from application.properties
    @Value("${upload.dir}")
    private String uploadDir;

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @PostMapping("/new-upload")
    public String newUploadImage(@RequestParam("file") MultipartFile file) throws IllegalStateException, IOException {
        //System.out.println("File name: " + file.getOriginalFilename());
        Path uploadPath = Paths.get(System.getProperty("user.dir"), uploadDir);
        Path relativePath = uploadPath.resolve(file.getOriginalFilename());
        file.transferTo(relativePath.toFile());
        return "redirect:/index";
    }

    /*
     * Saves the uploaded image to the Images db
     * Modifies the Clients db as follows:
     *  - Find the client object with matching accessToken
     *  - Update that client object's imageName to the name of the uploaded image  
     */
    @PostMapping("/upload-image")
    public Image uploadImage(@RequestParam(value="localPath") String path, @RequestParam(value="accessToken") String token) throws Exception {
        // Find the client with the specified token
        Optional<Client> tclient = clientDao.findById(token);
        if (!tclient.isPresent()) {
            throw new Exception("No client found with accessToken=" + token);
        }
        Client client = tclient.get();

        // Move the image file to the face-images/ folder if found
        Mat img = IOService.loadFileAsMat(path);
        String imgName = IOService.getImageName(path);
        String filepath = "src/main/resources/static/face-images/" + imgName;
        Imgcodecs.imwrite(filepath, img);
        
        // Create an Image object representing the image and save it to the Images db
        Image faceImage = imageDao.save(new Image(filepath));
        
        // Update the Clients DB with the name of the image that the client uploaded
        client.setImageName(imgName);
        clientDao.save(client);
        return faceImage;
    }   
}
