package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ManageMenu extends AppCompatActivity {
    TextView back;
    Button buttonCreate, buttonEdit, buttonScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_menu);
        buttonCreate = findViewById(R.id.buttonCreate);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonScan = findViewById(R.id.buttonScan);
        back = findViewById(R.id.textViewBack);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavigationManager.setupBottomNavigationView(bottomNavigationView, this);

        back.setOnClickListener(v -> {
            finish();
        });

        buttonCreate.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CreateMenu.class);
            startActivity(intent);
        });
        buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditMenu.class);
            startActivity(intent);
        });
        /*buttonScan.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ScanMenu.class);
            startActivity(intent);
            finish();
        }); */

    }
}