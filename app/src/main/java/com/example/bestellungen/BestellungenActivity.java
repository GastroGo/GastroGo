package com.example.bestellungen;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datenbank.states;
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

public class BestellungenActivity extends AppCompatActivity {

    DishModel dishModel = DishModel.getInstance();
    int tableNum;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("Restaurants");
    String restaurantId;
    RecyclerView recyclerView;
    RV_Adapter_Bestellungen adapter;

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
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dishModel.curState = states.OPEN;
                finish();
                closeOrderEnd();
            }
        });
        setupAdapter();

        openOrdersButton = findViewById(R.id.btn_bestellungen_offen);
        closedOrdersButton = findViewById(R.id.btn_bestellungen_geschl);

        //Button Listeners
        openOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dishModel.curState = states.OPEN;
                updateStyle();
                loadOrdersOnce();
                adapter.notifyDataSetChanged();
            }
        });

        closedOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dishModel.curState = states.CLOSED;
                updateStyle();
                loadOrdersOnce();
                adapter.notifyDataSetChanged();
            }
        });

        //Database
        loadDishNames();

        dbRef.child(restaurantId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Long> orderValues = (Map<String, Long>) snapshot.child("/tische/" + String.format("T%03d", tableNum) + "/" + (dishModel.curState == states.OPEN ? "bestellungen" : "geschlosseneBestellungen")).getValue();
                orderValues.values().removeIf(value -> value == 0L);

                dishModel.setOrders(orderValues);

                Log.i("Data", "" + orderValues);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupAdapter(){
        recyclerView = findViewById(R.id.BestellungenRecyclerView);
        adapter = new RV_Adapter_Bestellungen(this);
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


    private void loadDishNames(){
        dbRef.child(restaurantId).child("/speisekarte").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Map<String, Object>> dishNamesValues = (Map<String, Map<String, Object>>) snapshot.getValue();

                Map<String, String> dishNames = new HashMap<>();
                if (dishNamesValues != null) {
                    for (Map.Entry<String, Map<String, Object>> entry : dishNamesValues.entrySet()) {
                        String dishCode = entry.getKey();
                        Map<String, Object> dishDetails = entry.getValue();
                        String dishName = (String) dishDetails.get("gericht");
                        dishNames.put(dishCode, dishName);
                    }
                }

                dishModel.setDishNames(dishNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void loadOrdersOnce(){
        dbRef.child(restaurantId).child("/tische/" + String.format("T%03d", tableNum) + "/" + (dishModel.curState == states.OPEN ? "bestellungen" : "geschlosseneBestellungen"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Long> orderValues = (Map<String, Long>) snapshot.getValue();
                        if (orderValues != null) {
                            orderValues.values().removeIf(value -> value == 0L);
                            dishModel.setOrders(orderValues);
                            Log.i("Data", "" + orderValues);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    public void closeOpenOrders(String dish){
        String formatedTableNum = String.format("T%03d", tableNum);

        if(dishModel.curState == states.OPEN) {
            if (dishModel.getClosingDishes().contains(dish)) {
                dishModel.removeClosingDish(dish);
            } else {
                dishModel.addClosingDish(dish);
            }
            adapter.notifyDataSetChanged();
        } else if (dishModel.curState == states.CLOSED) {
                DatabaseReference openRef = dbRef.child(restaurantId + "/tische/" + formatedTableNum);
                openRef.child("/bestellungen/" + dish).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Long currentOrders = snapshot.getValue(Long.class);
                        if (currentOrders == null) {
                            currentOrders = 0L;
                        }

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("geschlosseneBestellungen/" + dish, 0);
                        updates.put("bestellungen/" + dish, currentOrders + dishModel.getOrders().get(dish));

                        openRef.updateChildren(updates);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
    }


    public void closeOrderEnd() {

        Log.i("order", dishModel.getClosingDishes() + "");

        String formattedTableNum = String.format("T%03d", tableNum);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Restaurants/" + restaurantId + "/tische/" + formattedTableNum);

        ref.child("geschlosseneBestellungen").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Long> closedOrders = (Map<String, Long>) snapshot.getValue();

                for (String dish : dishModel.getClosingDishes()) {
                    Long closedOrderCount = closedOrders != null && closedOrders.containsKey(dish) ? closedOrders.get(dish) : 0L;
                    Long orderCount = dishModel.getOrders().containsKey(dish) ? dishModel.getOrders().get(dish) : 0L;

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("geschlosseneBestellungen/" + dish, closedOrderCount + orderCount);
                    updates.put("bestellungen/" + dish, 0);

                    ref.updateChildren(updates);
                    dishModel.resetClosingDishes();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error here
            }
        });



    }

}