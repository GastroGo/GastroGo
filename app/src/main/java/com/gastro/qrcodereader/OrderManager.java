package com.gastro.qrcodereader;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gastro.login.BaseActivity;
import com.gastro.login.R;
import com.gastro.orderview.OrderView;
import com.gastro.utility.DropdownManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class OrderManager extends BaseActivity implements AmountChangeListener {

    List<Dish> selectedGerichte = new ArrayList<>();

    private Dialog currentDialog;
    FloatingActionButton back;
    String user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);



        back = findViewById(R.id.btn_back);
        back.setOnClickListener(v -> {
            onBackPressed();
        });

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        Intent intent = getIntent();
        List<Dish> dishList = (List<Dish>) getIntent().getSerializableExtra(getString(R.string.dishes));
        user = intent.getStringExtra("user");

        Button btnAdd = findViewById(R.id.btnAdd);
        TextView text = findViewById(R.id.tableNumber);
        text.setText("");
        String idRestaurant = getIntent().getStringExtra("id");
        String idSelectedTable = getIntent().getStringExtra("idTable");

        TextView headerText = findViewById(R.id.text);
        headerText.setText(getString(R.string.table_number) + String.valueOf(idSelectedTable));


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DishAdapter dishAdapter = new DishAdapter(dishList);
        recyclerView.setAdapter(dishAdapter);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGerichte(dishList);
                copyAmount(selectedGerichte);
                showDialog(selectedGerichte, idRestaurant, idSelectedTable, dishList);
            }
        });

    }

    @Override
    public void onAmountChanged() {
        updateTotalPrice(currentDialog);
    }


    private void setGerichte(List<Dish> dishList) {
        selectedGerichte.clear();
        for (Dish dish : dishList) {
            if (dish.isSelected() && dish.getAmount() > 0) {
                selectedGerichte.add(dish);
            }
        }
    }


    private void copyAmount(List<Dish> selectedGerichte) {
        for (Dish dish : selectedGerichte) {
            dish.setFinalAmount(dish.getAmount());
        }
    }

    private void showDialog(List<Dish> selectedGerichte, String idRestaurant, String idSelectedTable, List<Dish> dishList) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_window);

        RecyclerView recyclerDialogView = dialog.findViewById(R.id.recyclerDialogView);

        recyclerDialogView.setLayoutManager(new LinearLayoutManager(this));
        OrderAdapter orderAdapter = new OrderAdapter(selectedGerichte);
        recyclerDialogView.setAdapter(orderAdapter);

        for (Dish dish : selectedGerichte) {
            dish.setAmountChangeListener(this);
        }

        int dialogHeight = calculateDialogHeight(selectedGerichte);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);

        currentDialog = dialog;

        if (selectedGerichte.size() > 0) {
            updateTotalPrice(dialog);
            dialog.show();
        } else {
            Toast.makeText(this, R.string.no_dishes_addad, Toast.LENGTH_SHORT).show();
            dialog.dismiss();  // Schließe den Dialog, wenn die Liste leer ist
        }

        Button orderBtn = dialog.findViewById(R.id.orderBtn);
        if (orderBtn != null) {
            orderBtn.setOnClickListener(v -> {
                String tableId = String.format("T%s", idSelectedTable);
                setOrders(idRestaurant, tableId, selectedGerichte);
                if (user.equalsIgnoreCase("client")){
                    Intent intent = new Intent(OrderManager.this, OrderView.class);
                    intent.putExtra("restaurantId", idRestaurant);
                    intent.putExtra("tableId", tableId);
                    Log.i("Orded<fgrManager", "Starting OrderView activity with restaurantId: " + idRestaurant + ", tableId: " + tableId);
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                }

            });
        }

        orderAdapter.setOnListEmptyListener(dialog::dismiss);
    }


    private int calculateDialogHeight(List<Dish> selectedGerichte) {
        // Hier können Sie die Höhe des Dialogs basierend auf der Anzahl der Gerichte anpassen.
        // Beispiel: Hier wird die Höhe auf 400dp plus 100dp für jeden ausgewählten Artikel festgelegt.
        int itemHeight = 150;
        int minHeight = 200;
        int totalHeight = minHeight + (selectedGerichte.size() + 2) * itemHeight;
        return Math.min(totalHeight, getResources().getDisplayMetrics().heightPixels);
    }

    private void setOrders(@NonNull String idRestaurant, @NonNull String idSelectedTable, @NonNull List<Dish> dishList) {
        DatabaseReference bestellungenRef = FirebaseDatabase.getInstance()
                .getReference("Restaurants")
                .child(idRestaurant)
                .child("tische")
                .child(idSelectedTable)
                .child("bestellungen");
        for (Dish dish : dishList) {
            String formattedIndex = dish.getId();
            bestellungenRef.child(formattedIndex).setValue(dish.getFinalAmount());
        }

        bestellungenRef = FirebaseDatabase.getInstance()
                .getReference("Restaurants")
                .child(idRestaurant)
                .child("tische")
                .child(idSelectedTable);
        bestellungenRef.child("letzteBestellung").setValue(getCurrentTime());
        bestellungenRef.child("status").setValue((long) 1);

    }


    private void updateTotalPrice(Dialog dialog) {
        TextView totalPrice = dialog.findViewById(R.id.totalPrice);

        double totalPriceValue = 0;

        for (Dish dish : selectedGerichte) {
            totalPriceValue += dish.getPreis() * dish.getFinalAmount();
        }

        // Aktualisieren Sie das totalPrice TextView
        if (!selectedGerichte.isEmpty()) {
            totalPrice.setText(String.format("%.2f€", totalPriceValue));
        } else {
            totalPrice.setText("");
        }
    }

    public String getCurrentTime(){
        ZonedDateTime now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = ZonedDateTime.now(ZoneId.of("Europe/Berlin"));
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        }
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentTime = now.format(formatter);
        }
        return currentTime;
    }

    @Override
    protected void onDestroy() {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }
        super.onDestroy();
    }

}

