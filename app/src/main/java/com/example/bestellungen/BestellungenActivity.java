package com.example.bestellungen;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datenbank.Gericht;
import com.example.datenbank.GerichteModel;
import com.example.datenbank.TablelistModel;
import com.example.login.DropdownManager;
import com.example.login.Tisch;
import com.example.login.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BestellungenActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("Restaurants");
    TablelistModel tableListO = TablelistModel.getInstance();

    Button btnClosed;
    Button btnOpen;

    GerichteModel gerichteListO = GerichteModel.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tisch_bestellungen);
        String restaurantId = getIntent().getStringExtra("restaurantId");   //Übergabe der Restaurant ID
        TextView title = findViewById(R.id.text);

        btnClosed = findViewById(R.id.btn_bestellungen_geschl);
        btnOpen = findViewById(R.id.btn_bestellungen_offen);

        tableListO.setBestellungsFilter((byte) 1);

        FloatingActionButton returnButton = findViewById(R.id.btn_back);
        returnButton.setOnClickListener(view -> finish());

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        int tischNr = getIntent().getIntExtra("TableNr", -1);

        RecyclerView recyclerView = findViewById(R.id.BestellungenRecyclerView);
        RV_Adapter_Bestellungen adapterBestellungen = new RV_Adapter_Bestellungen(tischNr, restaurantId);
        recyclerView.setAdapter(adapterBestellungen);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        title.setText("Tisch " + tischNr);

        dbRef.child(restaurantId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int NumberOfTables = (int) snapshot.child("tische").getChildrenCount();
                int NumberOfGerichte = (int) snapshot.child("speisekarte").getChildrenCount();

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

                adapterBestellungen.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableListO.setBestellungsFilter((byte) 1);
                updateStyle();
                adapterBestellungen.notifyDataSetChanged();
            }
        });

        btnClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableListO.setBestellungsFilter((byte) 2);
                updateStyle();
                adapterBestellungen.notifyDataSetChanged();
            }
        });


    }

    private void updateStyle(){
        if (tableListO.getBestellungsFilter() == 1){
            btnOpen.setTextColor(Color.WHITE);
            btnClosed.setTextColor(getResources().getColor(R.color.text_gray));
            btnOpen.setBackgroundResource(R.drawable.modern_button_click_effect);
            btnClosed.setBackgroundResource(R.drawable.modern_button_unselected_click_effect);
        } else if (tableListO.getBestellungsFilter() == 2) {
            btnClosed.setTextColor(Color.WHITE);
            btnOpen.setTextColor(getResources().getColor(R.color.text_gray));
            btnClosed.setBackgroundResource(R.drawable.modern_button_click_effect);
            btnOpen.setBackgroundResource(R.drawable.modern_button_unselected_click_effect);
        }
    }

}