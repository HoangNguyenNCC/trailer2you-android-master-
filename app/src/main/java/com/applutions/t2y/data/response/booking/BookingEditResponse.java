package com.applutions.t2y.data.response.booking;


import java.io.Serializable;

public class BookingEditResponse implements Serializable {
    String actionRequired;

    Booking1 booking;
    String message;
    String newBookingId, priceDiff;
    boolean success;
    String stripeClientSecret;

    public String getStripeClientSecret() {
        return stripeClientSecret;
    }

    public String getActionRequired() {
        return actionRequired;
    }

    public Booking1 getBooking() {
        return booking;
    }

    public String getMessage() {
        return message;
    }

    public String getNewBookingId() {
        return newBookingId;
    }

    public String getPriceDiff() {
        return priceDiff;
    }

    public boolean getSuccess() {
        return success;
    }
}
