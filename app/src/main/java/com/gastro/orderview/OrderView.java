package com.gastro.orderview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gastro.bill.BillAdapter;
import com.gastro.bill.BillModel;
import com.gastro.homepage.Homepage;
import com.gastro.homepage.NavigationManager;
import com.gastro.login.BaseActivity;
import com.gastro.login.R;
import com.gastro.qrcodereader.QRCodeReader;
import com.gastro.settings.SettingsModel;
import com.gastro.utility.AnimationUtil;
import com.gastro.utility.DropdownManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderView extends BaseActivity {

    private AlertDialog callWaiterDialog;
    Button callWaiter, pay;
    FloatingActionButton back;
    DatabaseReference status, dbRef;
    String restaurantId, tableId, totalPrice;
    BillModel billModel = BillModel.getInstance();
    RecyclerView recyclerView;
    BillAdapter adapter;
    TextView noOrders, tvTotalPrice;
    FloatingActionButton backButton, qrCodeButton;

    @SuppressLint({"CutPasteId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);
        qrCodeButton = findViewById(R.id.fabQrCode);

        Intent qrCodeIntent = new Intent(OrderView.this, QRCodeReader.class);
        AnimationUtil.applyButtonAnimation(qrCodeButton, this, () -> startActivity(qrCodeIntent));
        backButton = findViewById(R.id.btn_back);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        NavigationManager.setupBottomNavigationView(bottomNavigationView, this);


        noOrders = findViewById(R.id.tvNoOrder);
        pay = findViewById(R.id.pay);
        callWaiter = findViewById(R.id.call_waiter);
        qrCodeButton = findViewById(R.id.fabQrCode);
        TextView headerText = findViewById(R.id.text);
        headerText.setText(R.string.orderView);

        back = findViewById(R.id.btn_back);
        back.setOnClickListener(v -> finish());

        SettingsModel model = SettingsModel.getInstance();
        model.load(this);

        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        restaurantId = getIntent().getStringExtra("restaurantId");
        tableId = getIntent().getStringExtra("tableId");
        totalPrice = getIntent().getStringExtra("totalPrice");
        model.setRestaurantId(restaurantId);
        model.setTableNr(tableId);
        model.save(this);

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        //noinspection deprecation
        backButton.setOnClickListener(v -> onBackPressed());

        if (restaurantId != null && !restaurantId.isEmpty() && tableId != null && !tableId.isEmpty()) {
            noOrders.setVisibility(View.GONE);

            tvTotalPrice.setText(this.getString(R.string.total_cost)+totalPrice);

            callWaiter.setOnClickListener(v -> callWaiter());
            pay.setOnClickListener(v -> {
                model.load(this);
                model.setRestaurantId("");
                model.setTableNr("");
                model.save(this);
                Intent intent = new Intent(OrderView.this, Homepage.class);
                startActivity(intent);
                finish();
            });

            status = FirebaseDatabase.getInstance().getReference("Restaurants").child(restaurantId).child("tische").child(tableId).child("status");
            dbRef = FirebaseDatabase.getInstance().getReference("Restaurants/" + restaurantId);

            status.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //noinspection DataFlowIssue
                    int statusValue = dataSnapshot.getValue(Integer.class);
                    if (statusValue == 2) {
                        callWaiter.setEnabled(false);
                        callWaiter.setText(R.string.waiter_called);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors.
                }
            });

            setupAdapter();
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String, Map<String, Object>> dishNamesValues = (Map<String, Map<String, Object>>) snapshot.child("speisekarte").getValue();

                    Map<String, String> dishNames = new HashMap<>();
                    if (dishNamesValues != null) {
                        for (Map.Entry<String, Map<String, Object>> entry : dishNamesValues.entrySet()) {
                            String dishCode = entry.getKey();
                            Map<String, Object> dishDetails = entry.getValue();
                            String dishName = (String) dishDetails.get("gericht");
                            double dishCost = ((Number) Objects.requireNonNull(dishDetails.get("preis"))).doubleValue();
                            billModel.dishCosts.put(dishCode, dishCost);
                            dishNames.put(dishCode, dishName);
                        }
                    }

                    billModel.dishNames = dishNames;

                    Map<String, Long> openOrders = (Map<String, Long>) snapshot.child("tische/" + tableId + "/bestellungen").getValue();
                    Map<String, Long> closedOrders = (Map<String, Long>) snapshot.child("tische/" + tableId + "/geschlosseneBestellungen").getValue();

                    Map<String, Long> orders = new HashMap<>(openOrders);

                    assert closedOrders != null;
                    closedOrders.forEach((key, value) ->
                            orders.merge(key, value, Long::sum)
                    );


                    Log.i("o", orders + "");

                    orders.values().removeIf(val -> val == 0);

                    billModel.orders = orders;

                    adapter.notifyDataSetChanged();

                    double[] totalCosts = {0.0};

                    orders.forEach((key, value) -> totalCosts[0] += billModel.dishCosts.get(key) * value);

                    tvTotalPrice.setText(getString(R.string.total_cost)+ totalCosts[0]+"€");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            recyclerView = findViewById(R.id.orderRecyclerView);
            recyclerView.setVisibility(View.GONE);
            callWaiter.setVisibility(View.GONE);
            pay.setVisibility(View.GONE);
            tvTotalPrice.setVisibility(View.GONE);
        }

    }
    private void setupAdapter(){
        recyclerView = findViewById(R.id.orderRecyclerView);
        adapter = new BillAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void callWaiter() {
        callWaiterDialog = createCallWaiterDialog();
        callWaiterDialog.show();
    }

    private android.app.AlertDialog createCallWaiterDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OrderView.this, R.style.RoundedDialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_call_waiter, null);
        builder.setView(view);

        Button confirm = view.findViewById(R.id.confirm_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(v2 -> callWaiterDialog.dismiss());
        confirm.setOnClickListener(v2 -> {
            callWaiterDialog.dismiss();
            status.setValue(2);
        });

        return builder.create();
    }

}