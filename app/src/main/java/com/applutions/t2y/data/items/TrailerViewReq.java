package com.applutions.t2y.data.items;

import java.util.ArrayList;

public class TrailerViewReq {
    String id;
    ArrayList<String> dates;
    ArrayList<String> times;
    ArrayList<Double> location;
    String delivery;

    public TrailerViewReq(String id, ArrayList<String> dates, ArrayList<String> times, ArrayList<Double> location, String delivery) {
        this.id = id;
        this.dates = dates;
        this.times = times;
        this.location = location;
        this.delivery = delivery;
    }
}
