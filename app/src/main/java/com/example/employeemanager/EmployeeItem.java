package com.example.employeemanager;

public class EmployeeItem {

    String name, key;

    public EmployeeItem() {
    }

    public EmployeeItem(String key, String name) {
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