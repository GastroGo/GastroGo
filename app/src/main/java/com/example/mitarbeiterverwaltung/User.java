package com.example.mitarbeiterverwaltung;

import java.util.Map;

public class User {

    String name;
    String key;
    String UID;
    Map<String, String> arbeitsZeiten;

    public User() {
    }

    public User(String key, String name) {
        this.name = name;
        this.key = key;
    }

    public User(String key, String name, String UID) {
        this.name = name;
        this.key = key;
        this.UID = UID;
    }

    public User(String key, String name, Map<String, String> arbeitsZeiten) {
        this.name = name;
        this.key = key;
        this.arbeitsZeiten = arbeitsZeiten;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public Map<String, String> getArbeitsZeiten() {
        return arbeitsZeiten;
    }

    public void setArbeitsZeiten(Map<String, String> arbeitsZeiten) {
        this.arbeitsZeiten = arbeitsZeiten;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }


}