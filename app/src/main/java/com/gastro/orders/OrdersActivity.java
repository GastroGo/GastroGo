package com.gastro.orders;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gastro.bill.BillActivity;
import com.gastro.database.Dish;
import com.gastro.database.States;
import com.gastro.login.BaseActivity;
import com.gastro.login.R;
import com.gastro.qrcodereader.OrderManager;
import com.gastro.qrcodereader.QRCodeReader;
import com.gastro.utility.DropdownManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersActivity extends BaseActivity {

    DishModel dishModel = DishModel.getInstance();
    int tableNum;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("Restaurants");
    String restaurantId;
    RecyclerView recyclerView;
    RV_Adapter_Orders adapter;

    Button openOrdersButton, closedOrdersButton, newOrderButton, billButton;

    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tableNum = getIntent().getIntExtra("TableNr", -1);
        restaurantId = getIntent().getStringExtra("restaurantId");
        setContentView(R.layout.activity_tisch_bestellungen);
        TextView headerText = findViewById(R.id.text);
        headerText.setText("Table " + tableNum);

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        FloatingActionButton returnButton = findViewById(R.id.btn_back);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dishModel.curState = States.OPEN;
                finish();
                closeOrderEnd();
            }
        });
        setupAdapter();

        openOrdersButton = findViewById(R.id.btn_bestellungen_offen);
        closedOrdersButton = findViewById(R.id.btn_bestellungen_geschl);
        newOrderButton = findViewById(R.id.newOrderButton);
        billButton = findViewById(R.id.billButton);

        //Button Listeners
        openOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dishModel.curState = States.OPEN;
                updateStyle();
                loadOrdersOnce();
                adapter.notifyDataSetChanged();
            }
        });

        closedOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dishModel.curState = States.CLOSED;
                updateStyle();
                loadOrdersOnce();
                adapter.notifyDataSetChanged();
            }
        });

        billButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tableId = String.format("T%03d", tableNum);
                Intent intent = new Intent(OrdersActivity.this, BillActivity.class);
                intent.putExtra("restaurantId", restaurantId);
                intent.putExtra("tableId", tableId);
                startActivity(intent);
            }
        });

        newOrderButton.setOnClickListener(view -> addOrderActivityStart());

        //Database
        loadDishNames();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Long> orderValues = (Map<String, Long>) snapshot.child("/tische/" + String.format("T%03d", tableNum) + "/" + (dishModel.curState == States.OPEN ? "bestellungen" : "geschlosseneBestellungen")).getValue();
                orderValues.values().removeIf(value -> value == 0L);

                dishModel.setOrders(orderValues);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        dbRef.child(restaurantId).addValueEventListener(valueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove the listener when the activity is destroyed
        if (valueEventListener != null) {
            dbRef.removeEventListener(valueEventListener);
        }
    }

    private void setupAdapter(){
        recyclerView = findViewById(R.id.BestellungenRecyclerView);
        adapter = new RV_Adapter_Orders(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateStyle() {
        if (dishModel.curState == States.OPEN){
            openOrdersButton.setTextColor(Color.WHITE);
            closedOrdersButton.setTextColor(Color.BLACK);
            openOrdersButton.setBackgroundResource(R.drawable.roundstyle);
            closedOrdersButton.setBackgroundColor(Color.TRANSPARENT);
        } else if (dishModel.curState == States.CLOSED) {
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
        dbRef.child(restaurantId).child("/tische/" + String.format("T%03d", tableNum) + "/" + (dishModel.curState == States.OPEN ? "bestellungen" : "geschlosseneBestellungen"))
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

        if(dishModel.curState == States.OPEN) {
            if (dishModel.getClosingDishes().contains(dish)) {
                dishModel.removeClosingDish(dish);
            } else {
                dishModel.addClosingDish(dish);
            }
            adapter.notifyDataSetChanged();
        } else if (dishModel.curState == States.CLOSED) {
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

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Get number of openOrders to set the letzeBestellungen parameter to "-" when no open orders are left
                int numberOfOpenOrders = 0;

                Map<String, Long> openOrders = (Map<String, Long>) snapshot.child("bestellungen").getValue();
                for (String dish : openOrders.keySet()){
                    if (openOrders.get(dish) != 0 && !dishModel.getClosingDishes().contains(dish)){
                        numberOfOpenOrders ++;
                    }
                }

                //
                Map<String, Long> closedOrders = (Map<String, Long>) snapshot.child("geschlosseneBestellungen").getValue();

                for (String dish : dishModel.getClosingDishes()) {
                    Long closedOrderCount = closedOrders != null && closedOrders.containsKey(dish) ? closedOrders.get(dish) : 0L;
                    Long orderCount = dishModel.getOrders().containsKey(dish) ? dishModel.getOrders().get(dish) : 0L;

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("geschlosseneBestellungen/" + dish, closedOrderCount + orderCount);
                    updates.put("bestellungen/" + dish, 0);

                    if (numberOfOpenOrders == 0){
                        updates.put("letzteBestellung", "-");
                    }

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

    public void deleteOrder(String dishId){
        dbRef.child(restaurantId + "/tische/" + String.format("T%03d", tableNum) + "/" + (dishModel.curState == States.OPEN ? "bestellungen" : "geschlosseneBestellungen") + "/" + dishId).setValue(0);
    }

    private void addOrderActivityStart(){
        dbRef.child(restaurantId + "/speisekarte").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Dish> dishMap = new HashMap<>();

                for(int x = 1; x <= snapshot.getChildrenCount(); x++){
                    String dishId = String.format("G%03d", x);
                    dishMap.put(dishId, snapshot.child(dishId).getValue(Dish.class));
                }

                List<com.gastro.qrcodereader.Dish> dishList = new ArrayList<>();

                dishMap.forEach((key, value) -> {
                    List<String> allZutaten = new ArrayList<>();
                    Map<String, Boolean> ZutatenMap = value.getZutaten();
                    ZutatenMap.forEach((k, v) -> {
                        if (v == true) {
                            allZutaten.add(k);
                        }
                    });
                    if (allZutaten.isEmpty()){
                        allZutaten.add(" ");
                    }
                    dishList.add(new com.gastro.qrcodereader.Dish(key, value.getPreis(), value.getGericht(), allZutaten));
                });

                Intent intent = new Intent(OrdersActivity.this, OrderManager.class);
                intent.putExtra(getString(R.string.dishes), (Serializable) dishList);
                intent.putExtra("idTable", String.format("%03d", tableNum));
                intent.putExtra("id", restaurantId);
                intent.putExtra("user", "employee");
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
