package com.gastro.utility;

import com.gastro.database.Menu;
import com.gastro.manage.DishAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class FirebaseManager {

    private final String restaurantId;
    private final DatabaseReference dbRefDishes;
    private final DatabaseReference dbRefTables;

    public FirebaseManager(String restaurantId) {
        this.restaurantId = restaurantId;
        this.dbRefDishes = FirebaseDatabase.getInstance().getReference("Restaurants").child(restaurantId).child("speisekarte");
        this.dbRefTables = FirebaseDatabase.getInstance().getReference("Restaurants").child(restaurantId).child("tische");
    }

    public void addDish(Menu gericht, List<Menu> dishes, DishAdapter dishAdapter) {
        dbRefDishes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                String dishKey = "G" + String.format(Locale.getDefault(),"%03d", count + 1);

                dbRefDishes.child(dishKey).setValue(gericht);

                dishes.add(gericht);
                dishAdapter.notifyDataSetChanged();

                dbRefTables.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot tableSnapshot : dataSnapshot.getChildren()) {
                            tableSnapshot.getRef()
                                    .child("bestellungen")
                                    .child(dishKey)
                                    .setValue(0);
                            tableSnapshot.getRef()
                                    .child("geschlosseneBestellungen")
                                    .child(dishKey)
                                    .setValue(0);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void loadDishes(List<Menu> dishes, DishAdapter dishAdapter) {
        dbRefDishes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dishes.clear();
                for (DataSnapshot dishSnapshot : dataSnapshot.getChildren()) {
                    Menu dish = dishSnapshot.getValue(Menu.class);
                    dishes.add(dish);
                }
                dishAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void updateDishInFirebase(Menu dish, int position, List<Menu> dishes) {
        String dishKey = "G" + String.format(Locale.getDefault(), "%03d", position + 1);
        dbRefDishes.child(dishKey).setValue(dish);

        dishes.set(position, dish);
    }

    public void deleteDishFromFirebase(int position, List<Menu> dishes) {
        String dishKey = "G" + String.format(Locale.getDefault(), "%03d", position + 1);
        dbRefDishes.child(dishKey).removeValue();

        DatabaseReference dbRefTables = FirebaseDatabase.getInstance().getReference("Restaurants").child(restaurantId).child("tische");
        dbRefTables.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot tableSnapshot : dataSnapshot.getChildren()) {
                    tableSnapshot.getRef().child("bestellungen").child(dishKey).removeValue();
                    tableSnapshot.getRef().child("geschlosseneBestellungen").child(dishKey).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        dishes.remove(position);

        dbRefTables.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot tableSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot ordersSnapshot = tableSnapshot.child("bestellungen");
                    for (int i = position + 1; i <= dishes.size(); i++) {
                        String oldDishKey = "G" + String.format(Locale.getDefault(), "%03d", i + 1);
                        String newDishKey = "G" + String.format(Locale.getDefault(), "%03d", i);
                        Object value = ordersSnapshot.child(oldDishKey).getValue();
                        if (value != null) {
                            tableSnapshot.getRef().child("bestellungen").child(newDishKey).setValue(value);
                            tableSnapshot.getRef().child("bestellungen").child(oldDishKey).removeValue();

                            tableSnapshot.getRef().child("geschlosseneBestellungen").child(newDishKey).setValue(value);
                            tableSnapshot.getRef().child("geschlosseneBestellungen").child(oldDishKey).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        for (int i = position; i < dishes.size(); i++) {
            dbRefDishes.child("G" + String.format(Locale.getDefault(), "%03d", i + 2)).removeValue();
        }

        for (int i = position; i < dishes.size(); i++) {
            String newDishKey = "G" + String.format(Locale.getDefault(), "%03d", i + 1);
            dbRefDishes.child(newDishKey).setValue(dishes.get(i));
        }
    }
}