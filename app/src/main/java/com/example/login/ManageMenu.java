package com.example.login;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageMenu extends AppCompatActivity {

    private DishAdapter dishAdapter;
    private List<Speisekarte> dishes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_menu);

        dishes = new ArrayList<>();
        dishAdapter = new DishAdapter(dishes);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dishAdapter);

        loadDishes();
    }

    private void loadDishes() {
        String restaurantId = getIntent().getStringExtra("restaurantId");
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Restaurants").child(restaurantId).child("speisekarte");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dishes.clear();
                for (DataSnapshot dishSnapshot : dataSnapshot.getChildren()) {
                    Speisekarte dish = dishSnapshot.getValue(Speisekarte.class);
                    dishes.add(dish);
                }
                dishAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ManageMenu.this, "Failed to load dishes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}