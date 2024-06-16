package com.gastro.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.gastro.settings.SettingsModel;
import com.gastro.utility.LocaleHelper;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsModel model = SettingsModel.getInstance();
        model.load(this);
        if (model.getDarkmode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        if(model.getLanguage() == 0) {
            LocaleHelper.setLocale(this, "de");
        } else if(model.getLanguage() == 1) {
            LocaleHelper.setLocale(this, "en");
        }
        super.onCreate(savedInstanceState);
    }
}