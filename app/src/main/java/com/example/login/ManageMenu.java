package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManageMenu extends AppCompatActivity {
    TextView back;
    Button buttonAdd;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_menu);
        back = findViewById(R.id.textViewBack);
        buttonAdd = findViewById(R.id.buttonAdd);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavigationManager.setupBottomNavigationView(bottomNavigationView, this);
        String restaurantId = getIntent().getStringExtra("restaurantId");

        back.setOnClickListener(v -> {
            finish();
        });

        buttonAdd.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ManageMenu.this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_add_dish, null);
            builder.setView(view);

            EditText dishName = view.findViewById(R.id.dish_name);
            EditText dishPrice = view.findViewById(R.id.dish_price);
            Button addDishButton = view.findViewById(R.id.add_dish_button);

            AlertDialog dialog = builder.create();

            addDishButton.setOnClickListener(v1 -> {
                String name = dishName.getText().toString();
                double price = Double.parseDouble(dishPrice.getText().toString());
                Speisekarte gericht = new Speisekarte(name, price);

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Restaurants").child(restaurantId).child("speisekarte");
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() { //Anzahl Gerichte
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();
                        String dishKey = "G00" + (count + 1);

                        dbRef.child(dishKey).setValue(gericht);

                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle possible errors.
                    }
                });
            });

            dialog.show();
        });

    }
}