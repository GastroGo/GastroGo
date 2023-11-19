package com.example.login;

import android.content.Context;
import android.content.SharedPreferences;

public class Model {
    private static Model instance;
    private String user;
    private String password;
    private static final String PREF_NAME = "UserData";
    private static final String KEY_USER = "user";
    private static final String KEY_PASSWORD = "password";

    private Model() { }

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }



    public void save(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER, user);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }


    public void load(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        user = sharedPref.getString(KEY_USER, null);
        password = sharedPref.getString(KEY_PASSWORD, null);
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
