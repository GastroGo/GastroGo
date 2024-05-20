package com.example.Tische;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DBKlassen.TablelistModel;
import com.example.DBKlassen.sortState;
import com.example.login.DropdownManager;
import com.example.login.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;


public class TischeActivity extends AppCompatActivity {
    TablelistModel tableModel = TablelistModel.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("Restaurants");
    String restaurantId;
    RecyclerView recyclerView;
    RV_Adapter_Tische adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tische);
        TextView headerText = findViewById(R.id.text);
        headerText.setText("Tische");

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        FloatingActionButton returnButton = findViewById(R.id.btn_back);
        returnButton.setOnClickListener(view -> finish());

        restaurantId = getIntent().getStringExtra("restaurantId");

        dbRef.child(restaurantId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Map<String, Object>> values = (Map<String, Map<String, Object>>) snapshot.child("/tische").getValue();

                Map<String, String> tableNumAndLetzteBestellung = new TreeMap<>();
                for (Map.Entry<String, Map<String, Object>> entry : values.entrySet()) {
                    String tableNum = entry.getKey();
                    Map<String, Object> tableProperties = entry.getValue();
                    String letzteBestellung = (String) tableProperties.get("letzteBestellung");
                    tableNumAndLetzteBestellung.put(tableNum, letzteBestellung);
                }
                tableModel.setTableNumAndTimerMap(tableModel.curState == sortState.SORTNUMBER ? sortWithNumber(values) : sortWithTimer(values));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setupAdapter();

    }

    private Map<String, String> sortWithNumber(Map<String, Map<String, Object>> values){

        Map<String, String> tableNumAndLetzteBestellung = new TreeMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : values.entrySet()) {
            String tableNum = entry.getKey();
            Map<String, Object> tableProperties = entry.getValue();
            String letzteBestellung = (String) tableProperties.get("letzteBestellung");
            tableNumAndLetzteBestellung.put(tableNum, letzteBestellung);
        }

        return tableNumAndLetzteBestellung;
    }

    private Map<String, String> sortWithTimer(Map<String, Map<String, Object>> values){
        Map<String, String> tableNumAndLetzteBestellung = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : values.entrySet()) {
            String tableNum = entry.getKey();
            Map<String, Object> tableProperties = entry.getValue();
            String letzteBestellung = (String) tableProperties.get("letzteBestellung");
            tableNumAndLetzteBestellung.put(tableNum, letzteBestellung);
        }

        return tableNumAndLetzteBestellung;
    }

    private void setupAdapter(){
        recyclerView = findViewById(R.id.mRecyclerView);
        adapter = new RV_Adapter_Tische(restaurantId);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

}
