package com.example.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateRestaurant extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextName, editTextPlace, editTextZip, editTextStreet, editTextHousenr;
    Button buttonReg, buttonBack;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    ProgressBar progressBar;
    Restaurant newRestaurant;
    InputValidator inputValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        buttonReg = findViewById(R.id.btn_register);
        buttonBack = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.progressBar);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), Register.class);
                startActivity(intent);
                finish();
            }
        });
        inputValidator = new InputValidator(this);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
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
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                int zip = Integer.parseInt(zipString);
                int housenr = Integer.parseInt(housenrString);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
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

    }

    public void createRestaurant(String name, String place, int zip, String street, int housnr, String uid) {
        String restaurantId = dbRef.push().getKey(); //Schlüssel erstellen

        Daten daten = new Daten();
        daten.setHausnr(housnr);
        daten.setId(restaurantId);
        daten.setName(name);
        daten.setOrt(place);
        daten.setPlz(zip);
        daten.setSpeisekarte(false);
        daten.setStrasse(street);
        daten.setUid(uid);

        Map<String, String> schluessel = new HashMap<>();
        schluessel.put("M001", "");

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
        Map<String, Tisch> tische = new HashMap<>();

        tische.put("T001", new Tisch(new HashMap<String, Integer>() {{
            put("G001", 0);
            put("G002", 0);
        }}, 0));
        tische.put("T002", new Tisch(new HashMap<String, Integer>() {{
            put("G001", 0);
            put("G002", 0);
        }}, 0));

        Restaurant restaurant = new Restaurant(daten, schluessel, speisekarte, tische); //ruft Standardkonstruktor auf in Restaurant Klasse
        dbRef.child(restaurantId).setValue(restaurant); //fügt gesetzte Restaurantklasse in Datenbank ein unter der erstellten Id
    }
}