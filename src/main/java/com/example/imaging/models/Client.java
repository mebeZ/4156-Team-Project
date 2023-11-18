package com.example.imaging.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Client {
    @Id
    private String accessToken;
    //private List<Image> imageList;

    protected Client() {}

    public Client(String accessToken) {
        this.accessToken = accessToken;
        //imageList = new ArrayList<Image>();
    }

    /*
    public void addImage(Image img) {
        imageList.add(img);
    }
    */

    // Getter and setter methods
    public String getAccessToken() {
        return this.accessToken;
    }

    @Override
    public String toString() {
        /*
        List<String> imageStrings = new ArrayList<String>();
        for (Image img : imageList) {
            imageStrings.add(img.toString());
        }
        String imageList = String.join(",", imageStrings);
        */
        return String.format("Client['%s']", accessToken);
    }
}
