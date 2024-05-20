package com.example.tische;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bestellungen.BestellungenActivity;
import com.example.datenbank.Gericht;
import com.example.datenbank.GerichteModel;
import com.example.datenbank.TablelistModel;
import com.example.login.DropdownManager;
import com.example.login.R;
import com.example.login.Tisch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class TischeActivity extends AppCompatActivity implements RV_Adapter_Tische.OnItemClickListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("Restaurants");

    TablelistModel tableListO = TablelistModel.getInstance();
    GerichteModel gerichteListO = GerichteModel.getInstance();
    RecyclerView recyclerView;

    int NumberOfTables = 20;
    int NumberOfGerichte;
    String restaurantId;

    private Handler handler;
    private Runnable runnable;
    private RV_Adapter_Tische adapterTische;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tische);
        restaurantId = getIntent().getStringExtra("restaurantId");
        TextView headerText = findViewById(R.id.text);
        headerText.setText("Tische");

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        recyclerView = findViewById(R.id.mRecyclerView);
        RV_Adapter_Tische adapterTische = new RV_Adapter_Tische(this, restaurantId);
        recyclerView.setAdapter(adapterTische);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        FloatingActionButton returnButton = findViewById(R.id.btn_back);



        returnButton.setOnClickListener(view -> finish());

        dbRef.child(restaurantId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NumberOfTables = (int) snapshot.child("tische").getChildrenCount();
                NumberOfGerichte = (int) snapshot.child("speisekarte").getChildrenCount();
                adapterTische.setNumberOfGerichte(NumberOfGerichte);

                tableListO.setup(NumberOfTables);
                gerichteListO.setup(NumberOfGerichte);

                for(int x = 0; x < NumberOfTables; x++){
                    String xString = "T" + String.format("%03d", (x + 1));
                    tableListO.getTischeArray()[x] = snapshot.child("tische").child(xString).getValue(Tisch.class);
                }

                for(int x = 0; x < NumberOfGerichte; x++){
                    String xString = "G" + String.format("%03d", (x + 1));
                    gerichteListO.getGerichte()[x] = snapshot.child("speisekarte").child(xString).getValue(Gericht.class);
                }

                adapterTische.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onItemClick(int tableNumber) {
        Intent intent = new Intent(this, BestellungenActivity.class);
        intent.putExtra("TableNr", tableNumber);
        intent.putExtra("restaurantId", restaurantId);

        startActivity(intent);
    }
}
