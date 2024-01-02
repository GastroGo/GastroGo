package com.example.qrcodegenerator;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class OrderManager extends AppCompatActivity implements AmountChangeListener{

    List <Gericht> selectedGerichte = new ArrayList<>();

    private Dialog currentDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        Intent intent = getIntent();
        List<Gericht> gerichtList = (List<Gericht>) getIntent().getSerializableExtra("Gerichte");

        Button btnAdd = findViewById(R.id.btnAdd);
        String idRestaurant = getIntent().getStringExtra("id");
        String idSelectedTable = getIntent().getStringExtra("idTable");

        TextView tableNumber = findViewById(R.id.tableNumber);
        tableNumber.setText("Tischnummer: " + String.valueOf(idSelectedTable));


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        GerichtAdapter gerichtAdapter = new GerichtAdapter(gerichtList);
        recyclerView.setAdapter(gerichtAdapter);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGerichte(gerichtList);
                copyAmount(selectedGerichte);
                showDialog(selectedGerichte, idRestaurant, idSelectedTable, gerichtList);
            }
        });

    }

    @Override
    public void onAmountChanged() {
        updateTotalPrice(currentDialog);
    }


    private void setGerichte(List<Gericht> gerichtList) {
        selectedGerichte.clear();
        for (Gericht gericht : gerichtList) {
            if (gericht.isSelected() && gericht.getAmount() > 0) {
                selectedGerichte.add(gericht);
            }
        }
    }


    private void copyAmount(List<Gericht> selectedGerichte) {
        for (Gericht gericht : selectedGerichte){
            gericht.setFinalAmount(gericht.getAmount());
        }
    }

    private void showDialog(List<Gericht> selectedGerichte, String idRestaurant, String idSelectedTable, List<Gericht> gerichtList) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_window);

        RecyclerView recyclerDialogView = dialog.findViewById(R.id.recyclerDialogView);

        recyclerDialogView.setLayoutManager(new LinearLayoutManager(this));
        OrderAdapter orderAdapter = new OrderAdapter(selectedGerichte);
        recyclerDialogView.setAdapter(orderAdapter);

        for (Gericht gericht : selectedGerichte) {
            gericht.setAmountChangeListener(this);
        }

        int dialogHeight = calculateDialogHeight(selectedGerichte);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);

        currentDialog = dialog;

        if (selectedGerichte.size() > 0) {
            updateTotalPrice(dialog);
            dialog.show();
        } else {
            Toast.makeText(this, "Keine Gerichte zur Bestellung hinzugefügt", Toast.LENGTH_SHORT).show();
            dialog.dismiss();  // Schließe den Dialog, wenn die Liste leer ist
        }

        Button orderBtn = dialog.findViewById(R.id.orderBtn);
        if (orderBtn != null) {
            orderBtn.setOnClickListener(v -> {
                setOrders(idRestaurant, idSelectedTable, gerichtList);
                dialog.dismiss();
            });
        }

        orderAdapter.setOnListEmptyListener(dialog::dismiss);
    }


    private int calculateDialogHeight(List<Gericht> selectedGerichte) {
        // Hier können Sie die Höhe des Dialogs basierend auf der Anzahl der Gerichte anpassen.
        // Beispiel: Hier wird die Höhe auf 400dp plus 100dp für jeden ausgewählten Artikel festgelegt.
        int itemHeight = 150;
        int minHeight = 200;
        int totalHeight = minHeight + (selectedGerichte.size()+2) * itemHeight;
        return Math.min(totalHeight, getResources().getDisplayMetrics().heightPixels);
    }

    private void setOrders(String idRestaurant, String idSelectedTable, List<Gericht> gerichtList) {
        DatabaseReference bestellungenRef = FirebaseDatabase.getInstance()
                .getReference("Restaurants")
                .child(idRestaurant)
                .child("tische")
                .child("T" + idSelectedTable)
                .child("bestellungen");
        int index = 1;
        for (Gericht gericht : gerichtList) {
            String formattedIndex = String.format("%03d", index);
            bestellungenRef.child("G" + formattedIndex).setValue(gericht.getFinalAmount());
            index++;
        }
    }


    private void updateTotalPrice(Dialog dialog) {
        TextView totalPrice = dialog.findViewById(R.id.totalPrice);

        double totalPriceValue = 0;

        for (Gericht gericht : selectedGerichte) {
            totalPriceValue += gericht.getPreis() * gericht.getFinalAmount();
        }

        // Aktualisieren Sie das totalPrice TextView
        if (!selectedGerichte.isEmpty()) {
            totalPrice.setText(String.format("%.2f€", totalPriceValue));
        } else {
            totalPrice.setText("");
        }
    }

}

