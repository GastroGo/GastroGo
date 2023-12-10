package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ManageMenu extends AppCompatActivity {
    TextView back;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_menu);
        back = findViewById(R.id.textViewBack);

        back.setOnClickListener(v -> {
            finish();
        });

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
                } /* else if (id == R.id.menuSettings) {
                    Intent intent = new Intent(getApplicationContext(), Settings.class);
                    startActivity(intent);
                    finish();
                } */
                //Weitere If Anweisungen für andere Icons
                return false;
            }
        });
    }
}