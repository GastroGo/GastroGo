package com.gastro.homepage;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MyLocationListener implements LocationListener {
    Homepage homepage;
    GoogleMap gMap;
    Context context;
    LatLng latLng;

    public MyLocationListener(Homepage homepage, GoogleMap gMap, Context context) {
        this.homepage = homepage;
        this.gMap = gMap;
        this.context = context;
        this.latLng = new LatLng(0, 0);
    }

    @Override
    public void onLocationChanged(@NonNull Location loc) {
        if (latLng == null) {
            latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        } else {
            double latitude = loc.getLatitude();
            double longitude = loc.getLongitude();
            latLng = new LatLng(latitude, longitude);
        }
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

        if (homepage.currentLocationMarker != null) {
            homepage.currentLocationMarker.remove();
            homepage.currentLocationMarker = null;
        }
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {}

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void getLastKnownLocation(LocationManager locationManager) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            onLocationChanged(lastKnownLocation);
        } else {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownLocation != null) {
                onLocationChanged(lastKnownLocation);
            }
        }
    }
}