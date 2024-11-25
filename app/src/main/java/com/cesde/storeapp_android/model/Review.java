package com.cesde.storeapp_android.model;

public class Review {
    private double rating;
    private String comment;
    private int productId;
    private String username;
    private String createdAt;

    public Review(String comment, int productId, float rating) {
        this.comment = comment;
        this.productId = productId;
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public int getProductId() {
        return productId;
    }

    public String getUsername() {
        return username;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
