package com.example.login;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyLocationListener implements LocationListener {
    Startseite startseite;
    GoogleMap gMap; // Add this line
    Context context; // Add this line

    public MyLocationListener(Startseite startseite, GoogleMap gMap, Context context) {
        this.startseite = startseite;
        this.gMap = gMap; // Modify this line
        this.context = context; // Add this line
    }

    public MyLocationListener() {}

    @Override
    public void onLocationChanged(Location loc) {
        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 10));

        // Vorherigen Marker entfernen, wenn vorhanden
        if (startseite.currentLocationMarker != null) {
            startseite.currentLocationMarker.remove();
        }

        // Neuen Marker hinzufügen und Referenz speichern
        startseite.currentLocationMarker = gMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(200))
                .position(latLng)
                .title("Sie befinden sich hier"));
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void getLastKnownLocation(LocationManager locationManager) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            onLocationChanged(lastKnownLocation);
        } else if(lastKnownLocation == null) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownLocation != null) {
                onLocationChanged(lastKnownLocation);
            }
        }
    }
}