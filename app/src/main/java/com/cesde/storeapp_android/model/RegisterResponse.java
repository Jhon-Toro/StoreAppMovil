package com.cesde.storeapp_android.model;

public class RegisterResponse {
    private String username;
    private String email;
    private int id;
    private boolean is_admin;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public boolean isAdmin() {
        return is_admin;
    }
}
