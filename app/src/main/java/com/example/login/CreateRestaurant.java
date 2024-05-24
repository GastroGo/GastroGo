package com.example.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateRestaurant extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextName, editTextPlace, editTextZip, editTextStreet, editTextHousenr;
    Button buttonReg;
    FloatingActionButton buttonLocation;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    InputValidator inputValidator;
    FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_create_restaurant);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("Restaurants");
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextName = findViewById(R.id.name);
        editTextPlace = findViewById(R.id.place);
        editTextZip = findViewById(R.id.zip);
        editTextStreet = findViewById(R.id.street);
        editTextHousenr = findViewById(R.id.housenr);
        buttonReg = findViewById(R.id.registerButton);
        buttonLocation = findViewById(R.id.locationButton);

        inputValidator = new InputValidator(this);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());
                String name = String.valueOf(editTextName.getText());
                String place = String.valueOf(editTextPlace.getText());
                String zipString = String.valueOf(editTextZip.getText());
                String street = String.valueOf(editTextStreet.getText());
                String housenrString = String.valueOf(editTextHousenr.getText());

                if (!inputValidator.validateInput(editTextEmail, "Email eingeben") ||
                        !inputValidator.isPasswordValid(editTextPassword) ||
                        !inputValidator.validateInput(editTextName, "Name eingeben") ||
                        !inputValidator.validateInput(editTextPlace, "Ort eingeben") ||
                        !inputValidator.validateInput(editTextZip, "Postleitzahl eingeben") ||
                        !inputValidator.validateInput(editTextStreet, "Straße eingeben") ||
                        !inputValidator.validateInput(editTextHousenr, "Hausnummer eingeben") ||
                        !inputValidator.isNumeric(zipString, "Postleitzahl muss eine Zahl sein") ||
                        !inputValidator.isNumeric(housenrString, "Hausnummer muss eine Zahl sein")) {
                    return;
                }

                int zip = Integer.parseInt(zipString);
                int housenr = Integer.parseInt(housenrString);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String uid = mAuth.getCurrentUser().getUid();
                                    Toast.makeText(CreateRestaurant.this, "Account erstellt", Toast.LENGTH_SHORT).show();
                                    createRestaurant(name, place, zip, street, housenr, uid);
                                    Intent intent = new Intent(getApplication(), Login.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        editTextEmail.setError("Diese E-Mail ist bereits registriert");
                                    } else {
                                        Log.w("AuthError", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(CreateRestaurant.this, "Authentifizierungsfehler: " + task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(CreateRestaurant.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateRestaurant.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(CreateRestaurant.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        Geocoder geocoder = new Geocoder(CreateRestaurant.this, Locale.getDefault());
                                        try {
                                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                            if (!addresses.isEmpty()) {
                                                Address address = addresses.get(0);
                                                editTextPlace.setText(address.getLocality());
                                                editTextStreet.setText(address.getThoroughfare());
                                                editTextZip.setText(address.getPostalCode());
                                                editTextHousenr.setText(address.getSubThoroughfare());
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                }
            }
        });

    }

    public void createRestaurant(String name, String place, int zip, String street, int housnr, String uid) {
        String restaurantId = dbRef.push().getKey(); //Schlüssel erstellen

        Data data = new Data();
        data.setHausnr(housnr);
        data.setId(restaurantId);
        data.setName(name);
        data.setOrt(place);
        data.setPlz(zip);
        data.setSpeisekarte(false);
        data.setStrasse(street);
        data.setUid(uid);

        Map<String, Speisekarte> speisekarte = new HashMap<>();
        speisekarte.put("G001", new Speisekarte("1", 0.0, new HashMap<String, Boolean>() {{
            put("gluten", true);
            put("nüsse", true);
        }}, new HashMap<String, Boolean>() {{
            put("eier", true);
            put("fleisch", true);
            put("milch", true);
        }}));
        speisekarte.put("G002", new Speisekarte("2", 0.0, new HashMap<String, Boolean>() {{
            put("gluten", true);
            put("nüsse", true);
        }}, new HashMap<String, Boolean>() {{
            put("eier", true);
            put("fleisch", true);
            put("milch", true);
        }}));

        Map<String, Integer> ordersMap = new HashMap<String, Integer>() {{
            put("G001", 0);
            put("G002", 0);
        }};

        Map<String, Tisch> tische = new HashMap<>();

        tische.put("T001", new Tisch(ordersMap, new HashMap<>(ordersMap), 0));
        tische.put("T002", new Tisch(ordersMap, new HashMap<>(ordersMap), 0));

        Restaurant restaurant = new Restaurant(data, speisekarte, tische); //ruft Standardkonstruktor auf in Restaurant Klasse
        dbRef.child(restaurantId).setValue(restaurant); //fügt gesetzte Restaurantklasse in Datenbank ein unter der erstellten Id
    }
}