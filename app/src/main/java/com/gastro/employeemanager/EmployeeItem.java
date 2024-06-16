package com.gastro.employeemanager;

import java.util.HashMap;
import java.util.Map;

public class EmployeeItem {

    String name;
    String key;
    String UID;
    Map<String, String> arbeitsZeiten = new HashMap<>();

    public EmployeeItem() {
    }

    public EmployeeItem(String key, String name) {
        this.name = name;
        this.key = key;
    }

    public EmployeeItem(String key, String name, String UID) {
        this.name = name;
        this.key = key;
        this.UID = UID;
    }

    public EmployeeItem(String key, String name, String UID, Map<String, String> arbeitsZeiten) {
        this.name = name;
        this.key = key;
        this.arbeitsZeiten = arbeitsZeiten;
        this.UID = UID;
    }
    public EmployeeItem(String key, String name, Map<String, String> arbeitsZeiten) {
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