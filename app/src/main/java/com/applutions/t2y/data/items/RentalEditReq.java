package com.applutions.t2y.data.items;

public class RentalEditReq {
    String rentalId;
    String bookingId;
    String newStartDate;
    String newEndDate;
    String type;

    public RentalEditReq(String rentalId, String bookingId, String newStartDate, String newEndDate, String type) {
        this.rentalId = rentalId;
        this.bookingId = bookingId;
        this.newStartDate = newStartDate;
        this.newEndDate = newEndDate;
        this.type = type;
    }
}
