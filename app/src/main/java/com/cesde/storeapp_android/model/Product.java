package com.cesde.storeapp_android.model;

import java.util.List;

public class Product {
    private int id;
    private String title;
    private String description;
    private double price;
    private List<String> images;
    private List<Review> reviews; // Cambia esto a List<Review>
    private double averageRating; // Cambia el tipo de averageRating de String a double
    private int categoryId;

    // Constructor vacío
    public Product() {}

    // Constructor con todos los campos
    public Product(int id, String title, String description, double price, List<String> images, List<Review> reviews, double averageRating, int categoryId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.images = images;
        this.reviews = reviews;
        this.averageRating = averageRating;
        this.categoryId = categoryId;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
