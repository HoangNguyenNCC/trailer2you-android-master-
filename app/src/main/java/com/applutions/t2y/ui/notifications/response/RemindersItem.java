package com.applutions.t2y.ui.notifications.response;

import com.applutions.t2y.data.items.PhotoObj;

import java.util.ArrayList;

public class RemindersItem {
    private String name,licenseeName,reminderType,reminderText,reminderColor,invoiceId;
    private boolean isTracking;
    private int isApproved;

    private ArrayList<Items> rentedItems=new ArrayList<>();

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
        this.isApproved = isApproved;
    }


    public ArrayList<Items> getItems() {
        return rentedItems;
    }

    public void setItems(ArrayList<Items> items) {
        this.rentedItems = items;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public boolean isTracking() {
        return isTracking;
    }

    public void setTracking(boolean tracking) {
        isTracking = tracking;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getLicenseeName() {
        return licenseeName;
    }

    public void setLicenseeName(String licenseeName) {
        this.licenseeName = licenseeName;
    }

    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    public String getReminderText() {
        return reminderText;
    }

    public void setReminderText(String reminderText) {
        this.reminderText = reminderText;
    }

    public String getReminderColor() {
        return reminderColor;
    }

    public void setReminderColor(String reminderColor) {
        this.reminderColor = reminderColor;
    }
}
