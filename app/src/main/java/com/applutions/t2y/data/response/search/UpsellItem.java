package com.applutions.t2y.data.response.search;

import com.applutions.t2y.data.items.PhotoObj;

import java.io.Serializable;
import java.util.ArrayList;

public class UpsellItem implements Serializable {
    String rentalItemId;
    String name;
    String type;
    String price;
    ArrayList<PhotoObj> photos;
    String licenseeId;
    String licenseeName;
    int rating;
    String rentalItemType;
    int btnID;

    public int getBtnID() {
        return btnID;
    }

    public void setBtnID(int btnID) {
        this.btnID = btnID;
    }

    public String getRentalItemId() {
        return rentalItemId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getPrice() {
        return price;
    }

    public ArrayList<PhotoObj> getPhotos() {
        return photos;
    }

    public String getLicenseeId() {
        return licenseeId;
    }

    public String getLicenseeName() {
        return licenseeName;
    }

    public int getRating() {
        return rating;
    }

    public String getRentalItemType() {
        return rentalItemType;
    }
}
