package com.gastro.homepage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import com.gastro.database.Data;
import com.gastro.database.Restaurant;
import com.gastro.login.BaseActivity;
import com.gastro.login.Login;
import com.gastro.login.R;
import com.gastro.manage.EmployeesView;
import com.gastro.manage.ManageRestaurant;
import com.gastro.qrcodereader.QRCodeReader;
import com.gastro.utility.AnimationUtil;
import com.gastro.utility.DropdownManager;
import com.gastro.utility.UserCache;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Homepage extends BaseActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    FirebaseAuth auth;
    FirebaseUser user;
    GoogleMap gMap;
    Marker currentLocationMarker;
    FloatingActionButton searchButton, qrCodeButton,  changeMap;
    SearchView searchView;
    View header;
    List<Marker> allMarkers = new ArrayList<>();
    static boolean employee = true;
    boolean isMapLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        authenticateUser();
        initializeUIComponents();
        setupEventListeners();
    }

    private void initializeUIComponents() {
        searchView = findViewById(R.id.searchView);
        searchButton = findViewById(R.id.search);
        qrCodeButton = findViewById(R.id.fabQrCode);
        changeMap = findViewById(R.id.fab);
        header = findViewById(R.id.header);
        employee = getIntent().getBooleanExtra("employee", true);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        NavigationManager.setupBottomNavigationView(bottomNavigationView, this);
    }

    private void setupEventListeners() {
        Intent qrCodeIntent = new Intent(Homepage.this, QRCodeReader.class);
        AnimationUtil.applyButtonAnimation(qrCodeButton, this, () -> startActivity(qrCodeIntent));

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        searchButton.setOnClickListener(v1 -> {
            String searchText = searchView.getQuery().toString();
            searchRestaurants(searchText);
        });

        changeMap.setOnClickListener(v2 -> {
            if (GoogleMap.MAP_TYPE_NORMAL == gMap.getMapType()) {
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            } else {
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
    }

    private void authenticateUser() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            String cachedUserId = UserCache.getInstance().getUserId();
            if (cachedUserId != null && cachedUserId.equals(user.getUid())) {
                Intent intent = new Intent(getApplicationContext(), ManageRestaurant.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            } else {
                checkUserInDatabase(user.getUid());
            } if (employee) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                String inputKey = sharedPreferences.getString("KEY_SCHLUESSEL", "");
                if (!inputKey.isEmpty()) {
                    checkKey();
                }
            }
        }
    }

    private void loadMap() {
        if (!isMapLoaded) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) mapFragment.getMapAsync(this);
        }
    }

    private void checkKey() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schluessel");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String inputKey = sharedPreferences.getString("KEY_SCHLUESSEL", "");
                boolean keyFound = false;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                        for (DataSnapshot greatGrandChildSnapshot : grandChildSnapshot.getChildren()) {
                            String firebaseKey;
                            try {
                                firebaseKey = greatGrandChildSnapshot.getValue(String.class);
                            } catch (Exception e) {
                                firebaseKey = "";
                            }
                            if (inputKey.equals(firebaseKey)) {
                                DatabaseReference grandChildRef = greatGrandChildSnapshot.getRef().getParent();
                                if (grandChildRef != null) {

                                    DatabaseReference childRef = grandChildRef.getParent();
                                    if (childRef != null) {
                                        String childKey = grandChildRef.getKey();
                                        String parentKey = childRef.getKey();
                                        String uid = getUserId();
                                        assert parentKey != null;
                                        assert childKey != null;
                                        DataSnapshot snap = dataSnapshot.child(parentKey).child(childKey).child("UID");
                                        String idc = snap.getValue(String.class);
                                        assert idc != null;
                                        if (idc.isEmpty()) {
                                            Intent intent = new Intent(getApplicationContext(), EmployeesView.class);
                                            intent.putExtra("restaurantId", parentKey);
                                            intent.putExtra("employeeId", grandChildSnapshot.getKey());
                                            startActivity(intent);
                                            ref.child(parentKey).child(childKey).child("UID").setValue(uid);
                                        } else if (idc.equals(uid)) {
                                            employee = false;
                                            Intent intent = new Intent(getApplicationContext(), EmployeesView.class);
                                            intent.putExtra("restaurantId", parentKey);
                                            intent.putExtra("employeeId", grandChildSnapshot.getKey());
                                            startActivity(intent);
                                        }
                                    }
                                }
                                keyFound = true;
                                break;
                            }
                        }

                        if (keyFound) {
                            break;
                        }
                    }

                    if (keyFound) {
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void searchRestaurants(String searchText) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Restaurants");
        dbRef.orderByChild("daten/name").equalTo(searchText).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String restaurantName = snapshot.child("daten/name").getValue(String.class);

                    for (Marker marker : allMarkers) {
                        if (Objects.equals(marker.getTitle(), restaurantName)) {
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }    //mmm
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        if (bottomNavigationView.getSelectedItemId() != R.id.menuHome) {
            bottomNavigationView.setSelectedItemId(R.id.menuHome);
        }
    }

    public String getUserId() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        return user.getUid();
    }

    private void checkUserInDatabase(String uid) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Restaurants");
        dbRef.orderByChild("daten/uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserCache.getInstance().setUserId(uid);
                    Intent intent = new Intent(getApplicationContext(), ManageRestaurant.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                } else {
                    loadMap();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        isMapLoaded = true;
        GoogleMapOptions options = new GoogleMapOptions();
        options.liteMode(false);
        getSupportFragmentManager().findFragmentById(R.id.map);
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener locationListener = new MyLocationListener(this, gMap, this);
        LatLng preLoadLatLng = new LatLng(47.795044385251785, 9.48099664856038); // Beispielkoordinaten
        float preLoadZoomLevel = 12;
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(preLoadLatLng, preLoadZoomLevel));

        checkDarkMode();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            locationListener.getLastKnownLocation(locationManager);
            gMap.setMyLocationEnabled(true);
        }
        header.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int headerHeight = header.getMeasuredHeight();

        addRestaurantsOnMap();
        gMap.setPadding(0, headerHeight, 0, 150);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        }

        if (mapFragment == null) {
        }
    }

    private void checkDarkMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            try {
                boolean success = gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.dark_map_style));
                if (!success) {
                    Log.e("MapsActivity", "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("MapsActivity", "Can't find style. Error: ", e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(gMap);
            } else {
                Log.e("Homepage", "User denied location permission");
            }
        }
    }

    private void addRestaurantsOnMap() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Restaurants");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot restaurantSnapshot : dataSnapshot.getChildren()) {
                        Restaurant restaurant = restaurantSnapshot.getValue(Restaurant.class);
                        assert restaurant != null;
                        Data data = restaurant.getDaten();
                        String address = data.getStrasse() + " " + data.getHausnr() + ", " + data.getPlz() + " " + data.getOrt();
                        LatLng latLng = getLatLngFromAddress(address);
                        if (latLng != null) {
                            Marker marker = gMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(data.getName()));
                            allMarkers.add(marker);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private LatLng getLatLngFromAddress(String address) {
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
            Log.e("Homepage", "Failed to get location from address", e);
        }

        return latLng;
    }
}