package com.applutions.t2y.data.items;

import java.io.Serializable;

public class RatingItem implements Serializable {

    private String name;
    private String dates;
    private PhotoObj photo;
    private String invoiceId;
    private String status;
    private String licensee;

    public RatingItem(String name, String dates, PhotoObj photo, String invoiceId, String status, String licensee) {
        this.name = name;
        this.dates = dates;
        this.photo = photo;
        this.invoiceId = invoiceId;
        this.status = status;
        this.licensee = licensee;
    }

    public String getName() {
        return name;
    }

    public String getDates() {
        return dates;
    }

    public PhotoObj getPhoto() {
        return photo;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getStatus() {
        return status;
    }

    public String getLicensee() {
        return licensee;
    }
}
