package com.example.imaging.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
public class Client {
    @Id
    private String accessToken;
    private String imageName;

    protected Client() {}

    public Client(String accessToken) {
        this.accessToken = accessToken;
        this.imageName = "";
    }

    // Getter and setter methods
    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    // Getter methods
    public String getAccessToken() {
        return accessToken;
    }

    // String representation of Client object
    @Override
    public String toString() {
        return String.format("Client[accessToken='%s', imagePath='%s']", accessToken, imageName);
    }
}
