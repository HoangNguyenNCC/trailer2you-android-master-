package com.applutions.t2y.data.response.search;

import com.applutions.t2y.data.items.PhotoObj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class TrailerDetails implements Serializable {
    String licenseeDistance;
    String rentalItemId;
    String name;
    String type;
    String price;
    String description;
    ArrayList<String> features;
    String size;
    String tare;
    String capacity;
    ArrayList<PhotoObj> photo;
    ArrayList<PhotoObj> photos;
    String licenseeId;
    String licenseeName;
    int rating;
    String rentalItemType;
    ArrayList<UpsellItem> upsellItems = new ArrayList<>();

    public TrailerDetails(String licenseeDistance, String rentalItemId, String name, String type, String price,
                          ArrayList<PhotoObj> photo, String licenseeId, String licenseeName, int rating, String rentalItemType,
                          ArrayList<UpsellItem> searchUpsellItems) {
        this.licenseeDistance = licenseeDistance;
        this.rentalItemId = rentalItemId;
        this.name = name;
        this.type = type;
        this.price = price;
        this.photo = photo;
        this.licenseeId = licenseeId;
        this.licenseeName = licenseeName;
        this.rating = rating;
        this.rentalItemType = rentalItemType;
        this.upsellItems = searchUpsellItems;
    }

    public String getRentalItemId() {
        return rentalItemId;
    }

    public void setRentalItemId(String rentalItemId) {
        this.rentalItemId = rentalItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ArrayList<PhotoObj> getPhoto() {
        return photo;
    }

    public void setPhoto(ArrayList<PhotoObj> photo) {
        this.photo = photo;
    }

    public String getLicenseeId() {
        return licenseeId;
    }

    public void setLicenseeId(String licenseeId) {
        this.licenseeId = licenseeId;
    }

    public String getLicenseeName() {
        return licenseeName;
    }

    public void setLicenseeName(String licenseeName) {
        this.licenseeName = licenseeName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getRentalItemType() {
        return rentalItemType;
    }

    public void setRentalItemType(String rentalItemType) {
        this.rentalItemType = rentalItemType;
    }

    public String getLicenseeDistance() {
        return licenseeDistance;
    }

    public ArrayList<UpsellItem> getUpsellItems() {
        return upsellItems;
    }


    public static Comparator<TrailerDetails> priceComparatorAsc = (o1, o2) -> {
        String price1 = o1.price.toUpperCase();
        String price2 = o2.price.toUpperCase();

        return price1.compareTo(price2);
    };
    public static Comparator<TrailerDetails> priceComparatorDsc = (o1, o2) -> {
        String price1 = o1.price.toUpperCase();
        String price2 = o2.price.toUpperCase();

        return price2.compareTo(price1);
    };


    public static Comparator<TrailerDetails> distComparatorAsc = (o1, o2) -> {
        String price1 = o1.licenseeDistance.toUpperCase();
        String price2 = o2.licenseeDistance.toUpperCase();

        return price1.compareTo(price2);
    };
    public static Comparator<TrailerDetails> distComparatorDsc = (o1, o2) -> {
        String price1 = o1.licenseeDistance.toUpperCase();
        String price2 = o2.licenseeDistance.toUpperCase();

        return price2.compareTo(price1);
    };

    public ArrayList<PhotoObj> getPhotos() {
        return photos;
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

    public String getCapacity() {
        return capacity;
    }


}
