package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.Tische.TischeActivity;

public class EmployeesView extends AppCompatActivity {

    Button settings, user, work;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees_view);

        settings = findViewById(R.id.BackButtonEmployees);

        user = findViewById(R.id.toUserPage);

        work = findViewById(R.id.toWorkersPage);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Einstellungen.class);
                startActivity(intent);
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Startseite.class);
                startActivity(intent);
            }
        });

        work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TischeActivity.class);
                intent.putExtra("restaurantId", getIntent().getStringExtra("restaurantId"));
                startActivity(intent);
            }
        });

    }
}