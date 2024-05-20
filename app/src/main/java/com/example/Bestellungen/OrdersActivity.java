package com.example.Bestellungen;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import com.example.DBKlassen.DishModel;
import com.example.DBKlassen.states;
import com.example.Tische.RV_Adapter_Tische;
import com.example.login.DropdownManager;
import com.example.login.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class OrdersActivity extends AppCompatActivity {

    DishModel dishModel = DishModel.getInstance();
    int tableNum;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("Restaurants");
    String restaurantId;
    RecyclerView recyclerView;
    RV_Adapter_Orders adapter;

    Button openOrdersButton;
    Button closedOrdersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tableNum = getIntent().getIntExtra("TableNr", -1);
        restaurantId = getIntent().getStringExtra("restaurantId");
        setContentView(R.layout.activity_orders);
        TextView headerText = findViewById(R.id.text);
        headerText.setText("Table " + tableNum);

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        FloatingActionButton returnButton = findViewById(R.id.btn_back);
        returnButton.setOnClickListener(view -> finish());
        setupAdapter();

        openOrdersButton = findViewById(R.id.btn_bestellungen_offen);
        closedOrdersButton = findViewById(R.id.btn_bestellungen_geschl);

        openOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dishModel.curState = states.OPEN;
                updateStyle();
                adapter.notifyDataSetChanged();
            }
        });

        closedOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dishModel.curState = states.CLOSED;
                updateStyle();
                adapter.notifyDataSetChanged();
            }
        });


        dbRef.child(restaurantId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Long> orderValues = (Map<String, Long>) snapshot.child("/tische/" + String.format("T%03d", tableNum) + "/" + (dishModel.curState == states.OPEN ? "bestellungen" : "geschlosseneBestellungen")).getValue();
                orderValues.values().removeIf(value -> value == 0L);

                Map<String, Map<String, Object>> dishNamesValues = (Map<String, Map<String, Object>>) snapshot.child("/speisekarte").getValue();

                Map<String, String> dishNames = new HashMap<>();
                for (Map.Entry<String, Map<String, Object>> entry : dishNamesValues.entrySet()) {
                    String dishCode = entry.getKey();
                    Map<String, Object> dishDetails = entry.getValue();
                    String dishName = (String) dishDetails.get("gericht");
                    dishNames.put(dishCode, dishName);
                }

                dishModel.setOrders(orderValues);
                dishModel.setDishNames(dishNames);

                Log.i("Data", "" + orderValues);
                Log.i("Data", "" + dishNamesValues);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupAdapter(){
        recyclerView = findViewById(R.id.BestellungenRecyclerView);
        adapter = new RV_Adapter_Orders();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateStyle(){
        if (dishModel.curState == states.OPEN){
            openOrdersButton.setTextColor(Color.WHITE);
            closedOrdersButton.setTextColor(Color.BLACK);
            openOrdersButton.setBackgroundResource(R.drawable.roundstyle);
            closedOrdersButton.setBackgroundColor(Color.TRANSPARENT);
        } else if (dishModel.curState == states.CLOSED) {
            closedOrdersButton.setTextColor(Color.WHITE);
            openOrdersButton.setTextColor(Color.BLACK);
            closedOrdersButton.setBackgroundResource(R.drawable.roundstyle);
            openOrdersButton.setBackgroundColor(Color.TRANSPARENT);
        }
    }

}