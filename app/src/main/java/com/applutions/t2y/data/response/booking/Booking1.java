package com.applutions.t2y.data.response.booking;

import com.applutions.t2y.data.items.BookingUpsell;
import com.applutions.t2y.ui.notifications.response.RentalObj;

import java.io.Serializable;
import java.util.ArrayList;

public class Booking1 implements Serializable {
    int __v;
    String _id;
    String bookingType;
    String cancellationCharges;
    String customerId;
    Loc customerLocation;
    String dlrCharges;
    String endDate;
    boolean doChargeDLR;
    boolean hasCompletetedPayment;
    boolean isActive;
    boolean isApproved;
    boolean isPickup;
    double lateFeePerDay;
    RentalObj.Charges charges;
    String licenseeId, prevRevision, startDate, taxes, trailerCharges, trailerId, upsellCharges;
    ArrayList<BookingUpsell> upsellItems;

    public RentalObj.Charges getCharges() {
        return charges;
    }

    public int get__v() {
        return __v;
    }

    public String get_id() {
        return _id;
    }

    public String getBookingType() {
        return bookingType;
    }

    public String getCancellationCharges() {
        return cancellationCharges;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Loc getLocation() {
        return customerLocation;
    }

    public String getDlrCharges() {
        return dlrCharges;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean getHasCompletetedPayment() {
        return hasCompletetedPayment;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public boolean getIsApproved() {
        return isApproved;
    }

    public boolean getIsPickup() {
        return isPickup;
    }

    public double getLateFeePerDay() {
        return lateFeePerDay;
    }

    public String getLicenseeId() {
        return licenseeId;
    }

    public String getPrevRevision() {
        return prevRevision;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getTaxes() {
        return taxes;
    }

    public String getTrailerCharges() {
        return trailerCharges;
    }

    public String getTrailerId() {
        return trailerId;
    }

    public String getUpsellCharges() {
        return upsellCharges;
    }

    public ArrayList<BookingUpsell> getUpsellItems() {
        return upsellItems;
    }

    public boolean isDoChargeDLR() {
        return doChargeDLR;
    }

    public boolean isActive() {
        return isActive;
    }
}