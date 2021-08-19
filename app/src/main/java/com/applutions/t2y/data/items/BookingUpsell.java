package com.applutions.t2y.data.items;

import java.io.Serializable;

public class BookingUpsell implements Serializable {
    String id;
    String price;
    int quantity;

    public BookingUpsell(String id, int count, String price) {
        this.id = id;
        this.quantity = count;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
