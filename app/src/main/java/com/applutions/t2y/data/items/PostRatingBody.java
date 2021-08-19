package com.applutions.t2y.data.items;

public class PostRatingBody {
    private String invoiceId;
    private int rating;
    private String review;

    public PostRatingBody(String invoiceId, int rating, String review) {
        this.invoiceId = invoiceId;
        this.rating = rating;
        this.review = review;
    }
}
