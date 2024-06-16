package com.gastro.bill;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gastro.login.BaseActivity;
import com.gastro.login.R;
import com.gastro.utility.DropdownManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class BillActivity extends BaseActivity {

    Button downloadBillButton;
    TextView totalCostText;
    RecyclerView recyclerView;
    String restaurantId, tableId;
    BillAdapter adapter;
    BillPDFGenerator pdfGen;
    DatabaseReference dbRef;
    BillModel billModel = BillModel.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        TextView headerText = findViewById(R.id.text);
        headerText.setText(R.string.bill);

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        FloatingActionButton returnButton = findViewById(R.id.btn_back);
        returnButton.setOnClickListener(view -> finish());

        restaurantId = getIntent().getStringExtra("restaurantId");
        tableId = getIntent().getStringExtra("tableId");

        pdfGen = new BillPDFGenerator(restaurantId, tableId, this);

        downloadBillButton = findViewById(R.id.btnDownloadBillPDF);
        totalCostText = findViewById(R.id.billTotalCostTV);

        dbRef = FirebaseDatabase.getInstance().getReference("Restaurants/" + restaurantId);

        downloadBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfGen.saveBillPDF();
            }
        });

        setupAdapter();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Map<String, Object>> dishNamesValues = (Map<String, Map<String, Object>>) snapshot.child("speisekarte").getValue();

                Map<String, String> dishNames = new HashMap<>();
                if (dishNamesValues != null) {
                    for (Map.Entry<String, Map<String, Object>> entry : dishNamesValues.entrySet()) {
                        String dishCode = entry.getKey();
                        Map<String, Object> dishDetails = entry.getValue();
                        String dishName = (String) dishDetails.get("gericht");
                        double dishCost = ((Number) dishDetails.get("preis")).doubleValue();
                        billModel.dishCosts.put(dishCode, dishCost);
                        dishNames.put(dishCode, dishName);
                    }
                }

                billModel.dishNames = dishNames;

                Map<String, Long> openOrders = (Map<String, Long>) snapshot.child("tische/" + tableId + "/bestellungen").getValue();
                Map<String, Long> closedOrders = (Map<String, Long>) snapshot.child("tische/" + tableId + "/geschlosseneBestellungen").getValue();

                Map<String, Long> orders = new HashMap<>(openOrders);

                closedOrders.forEach((key, value) ->
                        orders.merge(key, value, Long::sum)
                );


                Log.i("o", orders + "");

                orders.values().removeIf(val -> val == 0);

                billModel.orders = orders;

                adapter.notifyDataSetChanged();

                double[] totalCosts = {0.0};

                orders.forEach((key, value) -> {
                    totalCosts[0] += billModel.dishCosts.get(key) * value;
                });

                totalCostText.setText(" " + String.valueOf(totalCosts[0]));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupAdapter(){
        recyclerView = findViewById(R.id.billRecyclerView);
        adapter = new BillAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}