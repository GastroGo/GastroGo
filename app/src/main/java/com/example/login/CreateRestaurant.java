package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class CreateRestaurant extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextStreet;
    Button buttonReg, buttonLog;
    FirebaseAuth mAuth;
    DatabaseReference dbRef; //Firebase verbinden
    ProgressBar progressBar;
    TextView textView1, textView2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_restaurant);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextStreet = findViewById(R.id.street);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView1 = findViewById(R.id.loginNow);
        textView2 = findViewById(R.id.registerNow);
    }
}