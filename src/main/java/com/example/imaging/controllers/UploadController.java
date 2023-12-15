package com.example.imaging.controllers;

import java.io.IOException;
import java.util.Optional;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.imaging.DBUtils;
import com.example.imaging.IOUtils;
import com.example.imaging.models.Image;
import com.example.imaging.models.StorageService;
import com.example.imaging.models.data.ClientRepository;
import com.example.imaging.models.data.ImageRepository;

import io.micrometer.core.ipc.http.HttpSender.Response;

@RestController
public class UploadController {

    @Autowired
    private ImageRepository imageDao;

    @Autowired
    private ClientRepository clientDao;

    @Autowired
    private StorageService storageService;

    /*
    @PostMapping("/upload-image")
    public void uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        storageService.uploadImage(file);
    }*/
    
    // Given a Multipartfile representing an Image, add this image to the Image table of the database; a wrapper for StorageService.uploadImage
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            String result = storageService.uploadImage(file);
            if (result == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Upload failed.\"}");
            }
            return ResponseEntity.ok("{\"message\":\"File uploaded successfully: " + file.getOriginalFilename() + "\"}");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    /*
    @GetMapping("/upload-image")
    public static Image uploadImage(@RequestParam(value="localPath") String path, @RequestParam(value="accessToken") String token) throws Exception {
        // TODO: Make sure user has permission to access the API
		//DBUtils.checkAccessToken(token);

        Mat img = IOUtils.loadFileAsMat(path);
        String imgName = IOUtils.getImageName(path);
        String filepath = "src/main/resources/static/face-images/" + imgName;
        Imgcodecs.imwrite(filepath, img);
        Image faceImage = new Image(filepath, token);
        //System.out.println(faceImage);
        return faceImage;
    }
    */

    // Given an image id (which can be viewed in the Image table of the db), return a ResponseEntity with the corresponding image in the response body and image metadata in the response headers  
    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable Long id) {
        Optional<Image> image = imageDao.findById(String.valueOf(id));

        return image.map(img -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(img.getType()));
            headers.setContentLength(img.getImageData().length);
            return new ResponseEntity<>(img.getImageData(), headers, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
