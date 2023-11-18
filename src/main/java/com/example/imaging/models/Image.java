package com.example.imaging.models;

import com.example.imaging.controllers.EyeColorController;
import com.example.imaging.controllers.PoseController;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// Inspiration from: https://spring.io/guides/gs/accessing-data-jpa/
@Entity
public class Image {
    @Id
    private String imagePath;
    private String eyeColor;
    private double poseAngle;
    //private String isCentered;

    //protected ImageModel() {}

    public Image(String imagePath) throws Exception {
        this.imagePath = imagePath;
        setEyeColor();
        //setPoseAngle();
    }

    // Getters and setters
    public String getImagePath() {
        return this.imagePath;
    }

    public String getEyeColor() {
        return this.eyeColor;
    }

    public void setEyeColor() throws Exception {
        this.eyeColor = EyeColorController.getEyeColor(imagePath).eyeColor();
    }

    public double getPoseAngle() {
        return this.poseAngle;
    }

    public void setPoseAngle() throws Exception {
        this.poseAngle = PoseController.getYawAngle(imagePath);
    }

    @Override
    public String toString() {
        return String.format("Image[pathToImage='%s', eyeColor='%s']", imagePath, eyeColor);
        //return String.format("Image[pathToImage='%s', eyeColor='%s', poseAngle='%s']", imagePath, eyeColor, poseAngle);
    }
}
