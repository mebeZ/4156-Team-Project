package com.example.imaging.controllers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/upload-image")
    public ResponseEntity<String> newUploadImage(@RequestParam("file") MultipartFile file, @RequestParam("token") String token) throws Exception {
        // Find the client with the specified token
        Optional<Client> tclient = clientDao.findById(token);
        if (!tclient.isPresent()) {
            return new ResponseEntity<>("No client found with accessToken=" + token, HttpStatus.UNAUTHORIZED);
        }
        Client client = tclient.get();

        // Move the uploaded file to the images folder
        String imgName = file.getOriginalFilename();
        Path uploadPath = Paths.get(System.getProperty("user.dir"), uploadDir);
        File imageFile = uploadPath.resolve(imgName).toFile();
        file.transferTo(imageFile);
        
        // Create an Image object representing the image and save it to the Images db
        //String filepath = imageFile.toString();
        String filepath = Paths.get(uploadDir, imgName).toString();
        imageDao.save(new Image(filepath));
        
        // Update the Clients DB with the name of the image that the client uploaded
        client.setImageName(filepath);
        clientDao.save(client);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
