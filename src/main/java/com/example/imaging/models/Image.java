package com.example.imaging.models;

import jakarta.persistence.Column;

//import com.example.imaging.controllers.EyeColorController;
//import com.example.imaging.controllers.PoseController;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Inspiration from: https://spring.io/guides/gs/accessing-data-jpa/
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imagePath;

    public Image(String imagePath) {
        this.imagePath = imagePath;
    }

    // Getters and setters
    public String getImagePath() {
        return this.imagePath;
    }

    @Override
    public String toString() {
        return String.format("Image[Id='%o', eyeColor='%s']", id, imagePath);
    }
}
