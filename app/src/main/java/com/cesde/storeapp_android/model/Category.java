package com.cesde.storeapp_android.model;

import com.google.gson.annotations.SerializedName;

public class Category {
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private String imageUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
