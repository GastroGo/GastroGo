package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class EditMenu extends AppCompatActivity {
    TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);
        back = findViewById(R.id.textViewBack);

        back.setOnClickListener(v -> {
            finish();
        });
    }
}