package com.cesde.storeapp_android.model;

public class AuthResponse {
    private String access_token;
    private String token_type;
    private User user;

    public String getAccessToken() {
        return access_token;
    }

    public User getUser() {
        return user;
    }

    public static class User {
        private String username;
        private boolean isAdmin;

        public String getUsername() {
            return username;
        }

        public boolean isAdmin() {
            return isAdmin;
        }
    }
}
