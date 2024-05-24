package com.example.employeemanager;

public class EmployeeModel {
    private static final String KEY_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int KEY_LENGTH = 6;


    public String getKeyCharacters() {
        return KEY_CHARACTERS;
    }

    public int getKeyLength() {
        return KEY_LENGTH;
    }
}
