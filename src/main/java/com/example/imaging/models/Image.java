package com.example.imaging.models;

import com.example.imaging.controllers.EyeColorController;
import com.example.imaging.controllers.PoseController;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
//import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Inspiration from: https://spring.io/guides/gs/accessing-data-jpa/
@Entity
@Table(name = "ImageData")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String type;

    @Lob
    @Column(name = "imageData", length = 1000)
    private byte[] imageData;

    /*
    private String imagePath;
    private String eyeColor;
    private double poseAngle;
    //private String isCentered;
    //@ManyToOne
    //private Client client;

    //protected ImageModel() {}

    public Image(String imagePath, String accessToken) throws Exception {
        this.imagePath = imagePath;
        setEyeColor(accessToken);
        //setPoseAngle(accessToken);
    }

    // Getters and setters
    public String getImagePath() {
        return this.imagePath;
    }

    public String getEyeColor() {
        return this.eyeColor;
    }

    public void setEyeColor(String accessToken) throws Exception {
        this.eyeColor = EyeColorController.getEyeColor(imagePath, accessToken).eyeColor();
    }

    public double getPoseAngle() {
        return this.poseAngle;
    }

    public void setPoseAngle(String accessToken) throws Exception {
        this.poseAngle = PoseController.getPoseAngle(imagePath, accessToken).yawAngle();
    }

    @Override
    public String toString() {
        return String.format("Image[pathToImage='%s', eyeColor='%s']", imagePath, eyeColor);
        //return String.format("Image[pathToImage='%s', eyeColor='%s', poseAngle='%s']", imagePath, eyeColor, poseAngle);
    }
    */
}
