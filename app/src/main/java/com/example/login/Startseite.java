package com.example.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class Startseite extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    FirebaseAuth auth;
    FirebaseUser user;
    MaterialButton qrButton, zoomOutButton, zoomInButton;
    GoogleMap gMap;
    Marker currentLocationMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startseite);
        qrButton = findViewById(R.id.qr_button);
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            checkUserInDatabase(user.getUid());
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavigationManager.setupBottomNavigationView(bottomNavigationView, this);

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), com.example.qrcodegenerator.QRCodeReader.class);
                startActivity(intent);
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }
    public String getUserId() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        return user.getUid();
    }

    private void checkUserInDatabase(String uid) {  //überprüft ob es sich um Restaurant handelt
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Restaurants");
        dbRef.orderByChild("daten/uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {    //Restaurant Ansicht wird gestartet
                    Intent intent = new Intent(getApplicationContext(), ManageRestaurant.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { //würd mir stinken
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        SupportMapFragment mMapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener locationListener = new MyLocationListener(this, gMap, this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        locationListener.getLastKnownLocation(locationManager);
        addRestaurantsOnMap();
        int padding = 100; // replace with desired padding in pixels
        gMap.setPadding(0, padding, 0, 0);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(gMap);
            } else {
                Toast.makeText(this, "Bastard", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addRestaurantsOnMap() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Restaurants");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot restaurantSnapshot : dataSnapshot.getChildren()) {
                        Restaurant restaurant = restaurantSnapshot.getValue(Restaurant.class);
                        Daten daten = restaurant.getDaten();
                        String address = daten.getStrasse() + " " + daten.getHausnr() + ", " + daten.getPlz() + " " + daten.getOrt();
                        LatLng latLng = getLatLngFromAddress(address);
                        if (latLng != null && currentLocationMarker != null) {
                            float[] results = new float[1];
                            Location.distanceBetween(currentLocationMarker.getPosition().latitude, currentLocationMarker.getPosition().longitude,
                                    latLng.latitude, latLng.longitude, results);
                            int distanceInMeters = (int) results[0];
                            gMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(daten.getName())
                                    .snippet("Entfernung: "+ distanceInMeters/1000 + "km"));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        LatLng latLng = null;

        try {
            addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0);
                latLng = new LatLng(returnedAddress.getLatitude(), returnedAddress.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return latLng;
    }
}