package com.gastro.manage;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.gastro.database.Data;
import com.gastro.database.Menu;
import com.gastro.database.Restaurant;
import com.gastro.database.Table;
import com.gastro.login.BaseActivity;
import com.gastro.login.Login;
import com.gastro.login.R;
import com.gastro.qrcodepdf.PdfActivity;
import com.gastro.settings.Settings;
import com.gastro.tables.TablesActivity;
import com.gastro.utility.DropdownManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ManageRestaurant extends BaseActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    TextView name;
    ConstraintLayout menu, schluessel, qrcode, orders;
    Button delete;
    FloatingActionButton back;
    Data restaurantData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_restaurant);
        schluessel = findViewById(R.id.buttonEmployeeKeys);
        name = findViewById(R.id.text);
        menu = findViewById(R.id.buttonMenu);
        delete = findViewById(R.id.buttonDelete);
        qrcode = findViewById(R.id.buttonGenerateQR);
        orders = findViewById(R.id.buttonOrders);
        back = findViewById(R.id.btn_back);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            getData(user.getUid());
        }

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManageMenu.class);
                intent.putExtra("restaurantId", restaurantData.getId());
                startActivity(intent);
            }
        });

        schluessel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmployeeManager.class);
                intent.putExtra("restaurantId", restaurantData.getId());
                startActivity(intent);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.RoundedDialog);
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                View view = inflater.inflate(R.layout.dialog_delete_item, null);
                builder.setView(view);

                Button deleteButton = view.findViewById(R.id.delete_button);
                Button cancelButton = view.findViewById(R.id.cancel_button);

                AlertDialog dialog = builder.create();

                deleteButton.setOnClickListener(v1 -> {
                    dialog.dismiss();
                    deleteRestaurant(user.getUid());
                });
                cancelButton.setOnClickListener(v2 -> dialog.dismiss());
                TextView confirm = view.findViewById(R.id.confirm);
                confirm.setText(R.string.really_delete_restaurant);

                dialog.show();
            }
        });
        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantData != null && restaurantData.getId() != null) {
                    Intent intent = new Intent(ManageRestaurant.this, PdfActivity.class);
                    intent.putExtra("restaurantId", restaurantData.getId());
                    startActivity(intent);
                } else {
                }
            }
        });
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantData != null && restaurantData.getId() != null) {
                    Intent intent = new Intent(getApplicationContext(), TablesActivity.class);
                    intent.putExtra("restaurantId", restaurantData.getId()); // Pass the restaurant ID to TischeActivity activity
                    startActivity(intent);
                } else {
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });
    }

    private void getData(String uid) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Restaurants");
        dbRef.orderByChild("daten/uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot restaurantSnapshot : dataSnapshot.getChildren()) {
                        Restaurant restaurant = restaurantSnapshot.getValue(Restaurant.class);
                        restaurantData = restaurant.getDaten();
                        Map<String, Menu> speisekarte = restaurant.getSpeisekarte();
                        Map<String, Table> tische = restaurant.getTische();
                    }
                    displayData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void displayData() {
        if (restaurantData != null) {
            name.setText(restaurantData.getName());
            FloatingActionButton icon = findViewById(R.id.btn_back);
            icon.setImageResource(R.drawable.ic_manage);
            TextView dashboard = findViewById(R.id.textViewBack);
            dashboard.setText(R.string.dashboard);
        }
    }

    private void deleteRestaurant(String uid) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Restaurants");
        DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference("Schluessel");
        dbRef2.child(restaurantData.getId()).removeValue();
        dbRef.orderByChild("daten/uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot restaurantSnapshot : dataSnapshot.getChildren()) {
                        restaurantSnapshot.getRef().removeValue();
                    }
                    user.delete().addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                        }   //ja würd mir stinken
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}