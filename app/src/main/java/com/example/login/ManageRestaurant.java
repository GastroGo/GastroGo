package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mitarbeiterverwaltung.MitarbeiterVerwalten;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ManageRestaurant extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference dbRef;
    TextView name;
    Button delete, schluessel, qrcode, menu, orders;
    Daten restaurantDaten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_restaurant);
        schluessel = findViewById(R.id.buttonEmployeeKeys);
        name = findViewById(R.id.text);
        menu = findViewById(R.id.buttonMenu);
        delete = findViewById(R.id.buttonDelete);
        qrcode = findViewById(R.id.buttonGenerateQR);
        orders = findViewById(R.id.buttonOrders);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavigationManager.setupBottomNavigationView(bottomNavigationView, this);
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            getData(user.getUid());
        }

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManageMenu.class);
                intent.putExtra("restaurantId", restaurantDaten.getId());
                startActivity(intent);
            }
        });

        schluessel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MitarbeiterVerwalten.class);
                intent.putExtra("restaurantId", restaurantDaten.getId());
                startActivity(intent);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRestaurant(user.getUid());
            }
        });
        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), com.example.qrcodepdf.PdfActivity.class);
                startActivity(intent);
            }
        });
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantDaten != null && restaurantDaten.getId() != null) {
                    Intent intent = new Intent(getApplicationContext(), com.example.Tische.TischeActivity.class);
                    intent.putExtra("restaurantId", restaurantDaten.getId()); // Pass the restaurant ID to TischeActivity activity
                    startActivity(intent);
                } else {}
            }
        });
    }

    private void getData(String uid) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Restaurants");
        dbRef.orderByChild("daten/uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot restaurantSnapshot : dataSnapshot.getChildren()) {
                        Restaurant restaurant = restaurantSnapshot.getValue(Restaurant.class);
                        restaurantDaten = restaurant.getDaten();
                        Map<String, Speisekarte> speisekarte = restaurant.getSpeisekarte();
                        Map<String, Tisch> tische = restaurant.getTische();
                    }
                    displayData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void displayData() {
        if (restaurantDaten != null) {
            name.setText(restaurantDaten.getName());
        }
    }

    private void deleteRestaurant(String uid) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Restaurants");
        DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference("Schluessel");
        dbRef2.child(restaurantDaten.getId()).removeValue();
        dbRef.orderByChild("daten/uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot restaurantSnapshot : dataSnapshot.getChildren()) {
                        restaurantSnapshot.getRef().removeValue();
                    }
                    // Delete the user from Firebase Authentication
                    user.delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // User deleted successfully, navigate to login screen
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Handle failure to delete the user
                            // ...
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}