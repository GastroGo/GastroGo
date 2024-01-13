package com.example.Bestellungen;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DBKlassen.Gericht;
import com.example.DBKlassen.GerichteModel;
import com.example.DBKlassen.TablelistModel;
import com.example.DBKlassen.Tische;
import com.example.login.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BestellungenActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("Restaurants");
    TablelistModel tableListO = TablelistModel.getInstance();

    GerichteModel gerichteListO = GerichteModel.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tisch_bestellungen);
        String restaurantId = getIntent().getStringExtra("restaurantId");   //Übergabe der Restaurant ID

        TextView title = findViewById(R.id.TischBestellungenTitle);

        Button returnButton = findViewById(R.id.returnButtonBestellungen);

        returnButton.setOnClickListener(view -> finish());

        int tischNr = getIntent().getIntExtra("TableNr", -1);

        RecyclerView recyclerView = findViewById(R.id.BestellungenRecyclerView);
        RV_Adapter_Bestellungen adapterBestellungen = new RV_Adapter_Bestellungen(tischNr, restaurantId);
        recyclerView.setAdapter(adapterBestellungen);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        title.setText("Tisch " + String.format("%03d", tischNr));

        dbRef.child(restaurantId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int NumberOfTables = (int) snapshot.child("tische").getChildrenCount();
                int NumberOfGerichte = (int) snapshot.child("speisekarte").getChildrenCount();

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

                adapterBestellungen.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}