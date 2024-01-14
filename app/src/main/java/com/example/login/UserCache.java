package com.example.login;

public class UserCache {
    private static UserCache instance;
    private String userId;

    private UserCache() {}

    public static UserCache getInstance() {
        if (instance == null) {
            instance = new UserCache();
        }
        return instance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}