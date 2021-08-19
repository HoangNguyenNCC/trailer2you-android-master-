package com.applutions.t2y.data.items;

import java.io.Serializable;
import java.util.ArrayList;

public class FeaturedDetails implements Serializable {
    String name;
    String type;
    String description;
    ArrayList<String> features;
    String size;
    String tare;
    String capacity;
    ArrayList<PhotoObj> photos;

    public FeaturedDetails(String name, String type, String description, ArrayList<String> features, String size, String tare, ArrayList<PhotoObj> photos, String capacity) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.features = features;
        this.size = size;
        this.tare = tare;
        this.photos = photos;
        this.capacity = capacity;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getFeatures() {
        return features;
    }

    public String getSize() {
        return size;
    }

    public String getTare() {
        return tare;
    }

    public ArrayList<PhotoObj> getPhotos() {
        return photos;
    }
}
