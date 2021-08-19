package com.applutions.t2y.data.response.search;

import java.util.ArrayList;

public class SearchResponse {
    Boolean success;
    String message;
    ArrayList<TrailerDetails> trailers;

    public void setTrailers(ArrayList<TrailerDetails> trailers) {
        this.trailers = trailers;
    }

    public ArrayList<TrailerDetails> getTrailers() {
        return trailers;
    }

    public SearchResponse(ArrayList<TrailerDetails> trailers) {
        this.trailers = trailers;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
