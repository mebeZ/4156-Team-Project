package com.example.imaging.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// Inspiration from: https://spring.io/guides/gs/accessing-data-jpa/
@Entity
public class Image {
    @Id
    private String imagePath;
    private String eyeColor;
    //private String poseAngle;
    //private String isCentered;

    //protected ImageModel() {}

    public Image(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }

    @Override
    public String toString() {
        return String.format("Image[pathToImage='%s', eyeColor='%s']", imagePath, eyeColor);
    }
}
