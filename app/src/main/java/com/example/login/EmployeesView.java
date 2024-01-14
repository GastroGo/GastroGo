package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.Tische.TischeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EmployeesView extends AppCompatActivity {

    ConstraintLayout settings, user, work;
    FloatingActionButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees_view);

        settings = findViewById(R.id.settingButtonEmployees);
        user = findViewById(R.id.toUserPage);
        work = findViewById(R.id.toWorkersPage);
        back = findViewById(R.id.btn_back);

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        TextView headerText = findViewById(R.id.text);
        headerText.setText("Mitarbeiter");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Einstellungen.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

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