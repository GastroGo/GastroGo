package com.example.login;

import android.content.Context;
import android.content.SharedPreferences;

public class Model {
    private static Model instance;
    private int darkmode;
    private int benachrichtigungen;
    private int language;
    private String schluessel;

    private static final String PREF_NAME = "UserData";
    private static final int KEY_DARKMODE = 0;
    private static final int KEY_BENACHRICHTIGUNGEN = 0;
    private static final int KEY_LANGUAGE = 0;
    private static final String KEY_SCHLUESSEL = null;

    private Model() {
    }

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    public void save(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("KEY_DARKMODE", darkmode); // Use "KEY_DARKMODE" instead of String.valueOf(KEY_DARKMODE)
        editor.putInt("KEY_BENACHRICHTIGUNGEN", benachrichtigungen); // Similarly update other keys if needed
        editor.putInt("KEY_LANGUAGE", language);
        editor.putString("KEY_SCHLUESSEL", schluessel);
        editor.apply();
    }


    public void load(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        darkmode = sharedPref.getInt("KEY_DARKMODE", KEY_DARKMODE);
        benachrichtigungen = sharedPref.getInt("KEY_BENACHRICHTIGUNGEN", KEY_BENACHRICHTIGUNGEN);
        language = sharedPref.getInt("KEY_LANGUAGE", KEY_LANGUAGE);
        schluessel = sharedPref.getString("KEY_SCHLUESSEL", KEY_SCHLUESSEL);
    }

    public void setDarkmode(int darkmode) {
        this.darkmode = darkmode;
    }

    public int getDarkmode() {
        return darkmode;
    }

    public void setBenachrichtigungen(int benachrichtigungen) {
        this.benachrichtigungen = benachrichtigungen;
    }

    public int getBenachrichtigungen() {
        return benachrichtigungen;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public int getLanguage() {
        return language;
    }

    public void setSchluessel(String schluessel) {
        this.schluessel = schluessel;
    }

    public String getSchluessel() {
        return schluessel;
    }

}
