package com.gastro.settings;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

public class SettingsModel {
    private static final boolean KEY_DARKMODE = false;
    private static final int KEY_BENACHRICHTIGUNGEN = 0;
    private static final int KEY_LANGUAGE = 0;
    private static final String KEY_SCHLUESSEL = null;
    private static final String KEY_RESTAURANT_ID = null;
    private static final String KEY_TABLE_NR = null;
    private static final String KEY_TOTAL_PRICE = null;
    private static SettingsModel instance;
    private boolean darkmode;
    private int benachrichtigungen;
    private int language;
    private String schluessel;
    private String restaurantId;
    private String tableNr;

    private SettingsModel() {
    }

    public static SettingsModel getInstance() {
        if (instance == null) {
            instance = new SettingsModel();
        }
        return instance;
    }

    public void save(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("KEY_DARKMODE", darkmode);
        editor.putInt("KEY_BENACHRICHTIGUNGEN", benachrichtigungen);
        editor.putInt("KEY_LANGUAGE", language);
        editor.putString("KEY_SCHLUESSEL", schluessel);
        editor.putString("KEY_RESTAURANT_ID", restaurantId);
        editor.putString("KEY_TABLE_NR", tableNr);

        editor.apply();
    }

    public void load(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);

        darkmode = sharedPreferences.getBoolean("KEY_DARKMODE", KEY_DARKMODE);
        benachrichtigungen = sharedPreferences.getInt("KEY_BENACHRICHTIGUNGEN", KEY_BENACHRICHTIGUNGEN);
        language = sharedPreferences.getInt("KEY_LANGUAGE", KEY_LANGUAGE);
        schluessel = sharedPreferences.getString("KEY_SCHLUESSEL", KEY_SCHLUESSEL);
        restaurantId = sharedPreferences.getString("KEY_RESTAURANT_ID", KEY_RESTAURANT_ID);
        tableNr = sharedPreferences.getString("KEY_TABLE_NR", KEY_TABLE_NR);
    }

    public boolean getDarkmode() {
        return this.darkmode;
    }

    public void setDarkmode(boolean darkmode) {
        this.darkmode = darkmode;
    }

    public int getBenachrichtigungen() {
        return benachrichtigungen;
    }

    public void setBenachrichtigungen(int benachrichtigungen) {
        this.benachrichtigungen = benachrichtigungen;
    }

    public String getRestaurantId() {
        return this.restaurantId;
    }
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getTableNr() {
        return this.tableNr;
    }
    public void setTableNr(String tableNr) {
        this.tableNr = tableNr;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public String getSchluessel() {
        return schluessel;
    }

    public void setSchluessel(String schluessel) {
        this.schluessel = schluessel;
    }

    public String getLanguageCode() {
        switch (language) {
            case 0:
                return "de";
            case 1:
                return "en";
            default:
                return Locale.getDefault().getLanguage();
        }
    }
}
