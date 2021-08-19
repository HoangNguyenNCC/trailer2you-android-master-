package com.applutions.t2y.data.response.search;

import androidx.annotation.NonNull;

import java.sql.Array;
import java.util.ArrayList;

public class SearchTrailerBody {
    String count;
    String skip;
    Double[] location;
    ArrayList dates;
    ArrayList times;
    String delivery;
    Array[] type;

    public SearchTrailerBody(Double[] location, ArrayList dates, ArrayList times) {
        this.count = "5";
        this.skip = "0";
        this.location = location;
        this.dates = dates;
        this.times = times;
     }

    @NonNull
    @Override
    public String toString() {
        return "location: "+ location + " dates: " + dates + " times: " + times + " delivery: " + delivery;
    }
}
