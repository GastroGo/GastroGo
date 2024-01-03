package com.gastrogo.sortierte_bestellungen_2.Tische;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.gastrogo.sortierte_bestellungen_2.DBKlassen.Gericht;
import com.gastrogo.sortierte_bestellungen_2.DBKlassen.GerichteModel;
import com.gastrogo.sortierte_bestellungen_2.DBKlassen.TablelistModel;
import com.gastrogo.sortierte_bestellungen_2.DBKlassen.Tische;
import com.gastrogo.sortierte_bestellungen_2.Bestellungen.BestellungenActivity;
import com.gastrogo.sortierte_bestellungen_2.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.mRecyclerView);
        RV_Adapter_Tische adapterTische = new RV_Adapter_Tische(this);
        recyclerView.setAdapter(adapterTische);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        dbRef.child("-NnBQCxgwtnxnvuBu0tW").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NumberOfTables = (int) snapshot.child("tische").getChildrenCount();
                NumberOfGerichte = (int) snapshot.child("speisekarte").getChildrenCount();

                tableListO.setup(NumberOfTables);
                gerichteListO.setup(NumberOfGerichte);

                for(int x = 0; x < NumberOfTables; x++){
                    String xString = "T" + String.format("%03d", (x + 1));
                    tableListO.getTischeArray()[x] = snapshot.child("tische").child(xString).getValue(Tische.class);
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
        startActivity(intent);
    }
}
