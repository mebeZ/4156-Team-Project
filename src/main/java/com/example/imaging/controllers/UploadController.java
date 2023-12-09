package com.example.imaging.controllers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import com.example.imaging.models.Client;
import com.example.imaging.models.Image;
import com.example.imaging.models.data.ClientRepository;
import com.example.imaging.models.data.ImageRepository;


@Controller
public class UploadController {

    @Autowired
    private ImageRepository imageDao;

    @Autowired
    private ClientRepository clientDao;

    // Inject image upload directory location from application.properties
    @Value("${upload.dir}")
    private String uploadDir;

    @GetMapping("/upload")
    public String uploadForm() {
        return "upload";
    }

    /*
     * Transfer the Multipartfile @file to the specified directory @uploadDir
     * Relies on vodoo filepath operations
     * @returns: Root path to the image: /face-images/{file_name.jpeg}
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

        // Return /face-images/{file_name} from absolute path
        // https://stackoverflow.com/questions/14316487/java-getting-a-substring-from-a-string-starting-after-a-particular-character
        String segments[] = filePath.toString().split("/");
        if (segments.length < 2) {
            throw new Exception("Upload directory must have at least two '/'; instead: upload.dir = " + uploadDir);
        }
        return "/" + segments[segments.length-2] + "/" + segments[segments.length-1];
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("token") String token, RedirectAttributes redirectAttributes) throws Exception {
        // Find the client with the specified token
        Optional<Client> tclient = clientDao.findById(token);
        if (!tclient.isPresent()) {
            throw new Exception("No client found with accessToken=" + token);
        }
        Client client = tclient.get();

        // Make sure that the uploaded file is not empty
        if (file.isEmpty()) {
            return "redirect:/upload";
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

        // Redirect to display template in order to show the uploaded image
        redirectAttributes.addAttribute("imageUrl", imgUrl);
        return "redirect:/display";
    }

    @GetMapping("/display")
    public String displayImage(@RequestParam(name="imageUrl") String imageUrl, Model model) {
        model.addAttribute("imageUrl", imageUrl);
        return "display";
    }
}
