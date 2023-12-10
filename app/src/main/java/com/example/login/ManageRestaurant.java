package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import java.util.Map;

public class ManageRestaurant extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference dbRef;
    TextView name;
    Button menu;
    Button delete;
    Daten restaurantDaten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_restaurant);
        name = findViewById(R.id.text);
        menu = findViewById(R.id.buttonMenu);
        delete = findViewById(R.id.buttonDelete);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            getData(user.getUid());
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menuAccount) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.menuHome) {
                    Intent intent = new Intent(getApplicationContext(), ManageRestaurant.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManageMenu.class);
                startActivity(intent);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRestaurant(user.getUid());
            }
        });
    }

    private void getData(String uid) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Restaurants");
        dbRef.orderByChild("daten/uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot restaurantSnapshot: dataSnapshot.getChildren()) {
                        Restaurant restaurant = restaurantSnapshot.getValue(Restaurant.class);
                        restaurantDaten = restaurant.getDaten();
                        Map<String, String> schluessel = restaurant.getSchluessel();
                        Map<String, Speisekarte> speisekarte = restaurant.getSpeisekarte();
                        Map<String, Tisch> tische = restaurant.getTische();
                    }
                    displayData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void displayData() {
        if (restaurantDaten != null) {
            name.setText(restaurantDaten.getName());
        }
    }

    private void deleteRestaurant(String uid) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Restaurants");
        dbRef.orderByChild("daten/uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot restaurantSnapshot: dataSnapshot.getChildren()) {
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
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}