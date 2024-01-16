package com.example.login;

import com.google.android.gms.maps.GoogleMap;
public class MapHolder {
    private static MapHolder instance;
    private GoogleMap googleMap;

    private MapHolder() {}

    public static MapHolder getInstance() {
        if (instance == null) {
            instance = new MapHolder();
        }
        return instance;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }
}
