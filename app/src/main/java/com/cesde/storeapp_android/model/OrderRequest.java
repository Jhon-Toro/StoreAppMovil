package com.cesde.storeapp_android.model;

import java.util.List;

public class OrderRequest {
    private double total_price;
    private List<CartItem> items;

    public OrderRequest(List<CartItem> items) {
        this.items = items;
        this.total_price = calculateTotalPrice(items);
    }

    private double calculateTotalPrice(List<CartItem> items) {
        double total = 0;
        for (CartItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    // Getters y Setters
    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
