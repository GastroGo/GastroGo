package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.button.MaterialButton;

public class Startseite extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseAuth auth;
    FirebaseUser user;
    MaterialButton qrButton;
    MaterialButton zoomOutButton, zoomInButton;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap gMap;


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
        zoomOutButton = findViewById(R.id.zOut_button);
        zoomInButton = findViewById(R.id.zIn_button);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
        mapFragment.getMapAsync(this);

        zoomOutButton.setOnClickListener(v -> {
            gMap.animateCamera(CameraUpdateFactory.zoomOut());
        });
        zoomInButton.setOnClickListener(v -> {
            gMap.animateCamera(CameraUpdateFactory.zoomIn());
        });

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
        gMap = googleMap;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener locationListener = new MyLocationListener(this, gMap);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        locationListener.getLastKnownLocation(locationManager);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(gMap);
            } else {
                //Bastard wer ablehnt
            }
        }
    }
}