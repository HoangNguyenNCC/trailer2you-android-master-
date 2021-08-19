package com.applutions.t2y.data.response.booking;

import java.io.Serializable;
import java.util.List;

public class Loc implements Serializable {
    String id;
    List<Double> coordinates;
    String text;
    String type;
    String pincode;

    public String getId() {
        return id;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public String getPincode() {
        return pincode;
    }
}
