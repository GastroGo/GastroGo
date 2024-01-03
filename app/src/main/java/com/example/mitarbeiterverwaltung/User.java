package com.example.mitarbeiterverwaltung;

public class User {

    String name, key;

    public User() {
    }

    public User(String key, String name) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }
}