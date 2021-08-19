package com.applutions.t2y.ui.notifications.response;

import com.applutions.t2y.data.items.PhotoObj;

public class Items {
    private String itemType,itemId,rentedItemType,adminRentalItemId,itemName;
    private int units;
    private PhotoObj itemPhoto;
    private totalCharges totalCharges;

    public Items(String itemName, int units, PhotoObj itemPhoto, Items.totalCharges totalCharges) {
        this.itemName = itemName;
        this.units = units;
        this.itemPhoto = itemPhoto;
        this.totalCharges = totalCharges;
    }

    public Items.totalCharges getTotalCharges() {
        return totalCharges;
    }

    public int getUnits() {
        return units;
    }

    public PhotoObj getItemPhoto() {
        return itemPhoto;
    }

    public void setItemPhoto(PhotoObj itemPhoto) {
        this.itemPhoto = itemPhoto;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getRentedItemType() {
        return rentedItemType;
    }

    public void setRentedItemType(String rentedItemType) {
        this.rentedItemType = rentedItemType;
    }

    public String getAdminRentalItemId() {
        return adminRentalItemId;
    }

    public void setAdminRentalItemId(String adminRentalItemId) {
        this.adminRentalItemId = adminRentalItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public static class totalCharges{
        RentalObj.TrailerCharges charges;
        double payable;
        int quantity;

        public totalCharges(RentalObj.TrailerCharges charges, double payable, int quantity) {
            this.charges = charges;
            this.payable = payable;
            this.quantity = quantity;
        }

        public RentalObj.TrailerCharges getCharges() {
            return charges;
        }

        public double getPayable() {
            return payable;
        }

        public int getQuantity() {
            return quantity;
        }
    }

}

