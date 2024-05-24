package com.example.mitarbeiterverwaltung;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.DropdownManager;
import com.example.login.R;
import com.example.tables.RV_Adapter_Tables;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Employee extends AppCompatActivity {

    String restaurantID;
    String employeeID;
    FloatingActionButton back;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();
    RecyclerView recyclerView;
    EmployeeModel employeeModel = EmployeeModel.getInstance();
    RV_Adapter_EmployeeWorkDay adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mitarbeiter_verwalten);

        restaurantID = getIntent().getStringExtra("restaurantID");
        employeeID = getIntent().getStringExtra("employeeID");

        TextView headerText = findViewById(R.id.text);

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        back = findViewById(R.id.btn_back);

        back.setOnClickListener(view -> finish());

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User employee = snapshot.child("/Schluessel/" + restaurantID + "/" + employeeID).getValue(User.class);
                headerText.setText(employee.name);
                employeeModel.setEmployee(employee);
                adapter.notifyDataSetChanged();
                Log.i("zeiten", employeeModel.employee.getArbeitsZeiten() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setupAdapter();

    }

    private void setupAdapter(){
        recyclerView = findViewById(R.id.mListe);
        adapter = new RV_Adapter_EmployeeWorkDay(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void deleteWorkday(String day){
        dbRef.child("/Schluessel/" + restaurantID + "/" + employeeID + "/arbeitsZeiten/" + day).removeValue();
    }

    public void showEditDialog(){

    }
}