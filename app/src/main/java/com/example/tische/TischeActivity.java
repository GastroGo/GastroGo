package com.example.tische;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bestellungen.BestellungenActivity;
import com.example.datenbank.states;
import com.example.login.DropdownManager;
import com.example.login.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


public class TischeActivity extends AppCompatActivity{
    TablelistModel tableModel = TablelistModel.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();
    String restaurantId;
    String employeeId;
    RecyclerView recyclerView;
    RV_Adapter_Tische adapter;
    Button sortTimerButton;
    Button sortTableButton;
    Calendar calendar = Calendar.getInstance();
    Map<String, String> workData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tische);
        TextView headerText = findViewById(R.id.text);
        headerText.setText("Tische");

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        restaurantId = getIntent().getStringExtra("restaurantId");
        employeeId = getIntent().getStringExtra("employeeId");

        FloatingActionButton returnButton = findViewById(R.id.btn_back);
        returnButton.setOnClickListener(view -> {
            if (employeeId != null){
                showEndWorkDialog();
                Log.i("dialog", "true");
            } else {
                finish();
            }
        });

        sortTimerButton = findViewById(R.id.btn_sortByTimer);
        sortTableButton = findViewById(R.id.btn_sortByNumber);

        sortTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableModel.curState = states.SORTTIMER;
                tableModel.setTableNumAndTimerMap(tableModel.curState == states.SORTNUMBER ? sortWithNumber(tableModel.getTableNumAndTimerMap()) : sortWithTimer(tableModel.getTableNumAndTimerMap()));
                setupAdapter();
                updateStyle();
            }
        });

        sortTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableModel.curState = states.SORTNUMBER;
                tableModel.setTableNumAndTimerMap(tableModel.curState == states.SORTNUMBER ? sortWithNumber(tableModel.getTableNumAndTimerMap()) : sortWithTimer(tableModel.getTableNumAndTimerMap()));
                setupAdapter();
                updateStyle();
            }
        });

        dbRef.child("/Restaurants/" + restaurantId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Map<String, Object>> values = (Map<String, Map<String, Object>>) snapshot.child("/tische").getValue();

                Map<String, String> tableNumAndLetzteBestellung = new TreeMap<>();
                Map<String, Long> tableNumAndStatus = new TreeMap<>();
                for (Map.Entry<String, Map<String, Object>> entry : values.entrySet()) {
                    String tableNum = entry.getKey();
                    Map<String, Object> tableProperties = entry.getValue();
                    String letzteBestellung = (String) tableProperties.get("letzteBestellung");
                    long status = (long) tableProperties.get("status");
                    tableNumAndLetzteBestellung.put(tableNum, letzteBestellung);
                    tableNumAndStatus.put(tableNum, status);
                }
                tableModel.setTableNumAndTimerMap(tableModel.curState == states.SORTNUMBER ? sortWithNumber(tableNumAndLetzteBestellung) : sortWithTimer(tableNumAndLetzteBestellung));
                tableModel.setTableNumAndStatus(tableNumAndStatus);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setupAdapter();
    }

    private Map<String, String> sortWithNumber(Map<String, String> values){

        Map<String, String> tableNumAndLetzteBestellung = new TreeMap<>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String tableNum = entry.getKey();
            String letzteBestellung = entry.getValue();
            tableNumAndLetzteBestellung.put(tableNum, letzteBestellung);
        }

        return tableNumAndLetzteBestellung;
    }

    private Map<String, String> sortWithTimer(Map<String, String> values){
        Map<String, String> tableNumAndLetzteBestellung = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String tableNum = entry.getKey();
            String letzteBestellung = entry.getValue();
            tableNumAndLetzteBestellung.put(tableNum, letzteBestellung);
        }

        // Sort the map by values in ascending order, with "-" at the bottom
        tableNumAndLetzteBestellung = tableNumAndLetzteBestellung.entrySet().stream()
                .sorted((entry1, entry2) -> {
                    String value1 = entry1.getValue();
                    String value2 = entry2.getValue();
                    if ("-".equals(value1)) {
                        return 1;
                    } else if ("-".equals(value2)) {
                        return -1;
                    } else {
                        return value1.compareTo(value2);
                    }
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        return tableNumAndLetzteBestellung;
    }


    private void setupAdapter(){
        recyclerView = findViewById(R.id.mRecyclerView);
        adapter = new RV_Adapter_Tische(restaurantId, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void updateStyle(){
        if (tableModel.curState == states.SORTNUMBER){
            sortTableButton.setTextColor(Color.WHITE);
            sortTimerButton.setTextColor(Color.BLACK);
            sortTableButton.setBackgroundResource(R.drawable.roundstyle);
            sortTimerButton.setBackgroundColor(Color.TRANSPARENT);
        } else if (tableModel.curState == states.SORTTIMER) {
            sortTimerButton.setTextColor(Color.WHITE);
            sortTableButton.setTextColor(Color.BLACK);
            sortTimerButton.setBackgroundResource(R.drawable.roundstyle);
            sortTableButton.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    public void onItemClick(int tableNumber) {
        Intent intent = new Intent(this, BestellungenActivity.class);
        intent.putExtra("TableNr", tableNumber);
        intent.putExtra("restaurantId", restaurantId);

        startActivity(intent);
    }

    private void showEndWorkDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.RoundedDialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_delete_dish, null);
        builder.setView(view);

        Button confirmButton = view.findViewById(R.id.delete_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        TextView text = view.findViewById(R.id.confirm);

        text.setText("Arbeitstag beenden?");
        text.setGravity(Gravity.CENTER);
        confirmButton.setText("beenden");

        AlertDialog dialog = builder.create();

        confirmButton.setOnClickListener(view1 -> {
            getWorkdays();
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(view1 -> dialog.cancel());

        dialog.show();
    }

    private void getWorkdays(){
        DatabaseReference arbeitsZeitenRef = dbRef.child("/Schluessel/" + restaurantId + "/" + employeeId + "/arbeitsZeiten");
        arbeitsZeitenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                workData = (Map<String, String>) dataSnapshot.getValue();
                String date = String.format("%02d%02d%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
                String workday = workData.get(date);
                workday = String.format("%s%02d:%02d", workday.substring(0, 6), calendar.get(Calendar.HOUR_OF_DAY) + 2, calendar.get(Calendar.MINUTE));
                workData.put(date, workday);
                arbeitsZeitenRef.setValue(workData);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
