package com.applutions.t2y.data.items;

import java.util.ArrayList;

public class ReqCharges {
    String trailerId;
    ArrayList<BookingUpsell> upsellItems;
    String startDate;
    String endDate;
    Boolean isPickup;

    public ReqCharges(String trailerId, ArrayList<BookingUpsell> upsellItems, String startDate, String endDate) {
        this.trailerId = trailerId;
        this.upsellItems = upsellItems;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPickup = false;
    }
}
