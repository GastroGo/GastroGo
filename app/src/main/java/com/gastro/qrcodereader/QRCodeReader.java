package com.gastro.qrcodereader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.gastro.login.BaseActivity;
import com.gastro.login.R;
import com.gastro.utility.AnimationUtil;
import com.gastro.utility.DropdownManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QRCodeReader extends BaseActivity {

    private final List<String> allIds = new ArrayList<>();
    public String idTable;
    List<String> allGerichte = new ArrayList<>();
    List<Dish> dishList = new ArrayList<>();
    FloatingActionButton back;
    int index;
    private int tasksCompleted = 0;
    private int zutatenTasksCompleted = 0;
    private final ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(this, R.string.cancelled, Toast.LENGTH_SHORT).show();
        } else {
            String idScanned = result.getContents().substring(0, result.getContents().length() - 3);
            idTable = result.getContents().substring(result.getContents().length() - 3);
            getAllGerichte(idScanned);
        }
    });
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showCamera();
                } else {
                    Toast.makeText(this, R.string.cam_permission, Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_reader);
        initViews();
        TextView headerText = findViewById(R.id.text);
        headerText.setText(R.string.scan_table_qr_code);

        back = findViewById(R.id.btn_back);
        back.setOnClickListener(v -> {
            onBackPressed();
        });

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();
    }

    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);
        qrCodeLauncher.launch(options);
    }

    private void initViews() {
        FloatingActionButton btn = findViewById(R.id.fab);
        AnimationUtil.applyButtonAnimation(btn, this, () -> {
            checkPermissionAndShowActivity(this);
        });
    }

    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            showCamera();
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(context, R.string.lukas_hater, Toast.LENGTH_SHORT).show();
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        }
    }

    private void getAllGerichte(String id) {
        DatabaseReference dbGerichte = FirebaseDatabase.getInstance().getReference("Restaurants").child(id).child("speisekarte");

        dbGerichte.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String gericht = snapshot.getKey();
                    allGerichte.add(gericht);
                }
                getDataGericht(id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading IDs from Firebase", databaseError.toException());
            }
        });
    }

    private void getDataGericht(String id) {

        Dish[] dish = new Dish[allGerichte.size()];
        index = 0;

        for (String gerichtSelected : allGerichte) {

            DatabaseReference dbGerichte = FirebaseDatabase.getInstance().getReference("Restaurants").child(id).child("speisekarte").child(gerichtSelected);
            dbGerichte.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {


                        dish[index] = new Dish();  // Initialisierung eines neuen Gericht-Objekts
                        dish[index].setGerichtName(snapshot.child("gericht").getValue(String.class));
                        dish[index].setPreis(snapshot.child("preis").getValue(Double.class));
                        dish[index].setId(gerichtSelected);

                        getDataZutaten(id, gerichtSelected, dish[index], new Callback() {
                            @Override
                            public void onComplete() {
                                index++;
                                zutatenTasksCompleted++;
                                if (index == allGerichte.size() && zutatenTasksCompleted == allGerichte.size()) {
                                    activityAufruf(id);
                                }
                            }
                        });
                        dishList.add(dish[index]);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void getDataAllergien(String id, String gerichtSelected, Dish dish, Callback callback) {
        DatabaseReference dbAllergien = FirebaseDatabase.getInstance()
                .getReference("Restaurants").child(id).child("speisekarte")
                .child(gerichtSelected).child("allergien");

        dbAllergien.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> allAllergien = new ArrayList<>();
                    for (DataSnapshot snapshotAl : snapshot.getChildren()) {
                        String allergie = snapshotAl.getKey();
                        boolean allergieValue = snapshotAl.getValue(Boolean.class);

                        if (allergieValue) {
                            allAllergien.add(allergie);
                        }
                    }
                    dish.setAllergien(allAllergien);
                    //callback.onComplete();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error reading allergens from Firebase", error.toException());
            }
        });
    }

    private void getDataZutaten(String id, String gerichtSelceted, Dish dish, Callback callback) {
        DatabaseReference dbZutaten = FirebaseDatabase.getInstance().getReference("Restaurants").child(id).child("speisekarte").child(gerichtSelceted).child("zutaten");
        dbZutaten.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> allZutaten = new ArrayList<>();
                    for (DataSnapshot snapshotAl : snapshot.getChildren()) {
                        String zutaten = snapshotAl.getKey();
                        boolean zutatenValue = snapshotAl.getValue(Boolean.class);

                        if (zutatenValue) {
                            allZutaten.add(zutaten);
                        }
                    }
                    if (allZutaten.isEmpty()){
                        allZutaten.add(" ");
                    }
                    dish.setZutaten(allZutaten);

                    callback.onComplete();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error reading zutaten from Firebase", error.toException());
            }
        });
    }

    private void activityAufruf(String id) {
        Intent intent = new Intent(QRCodeReader.this, OrderManager.class);
        intent.putExtra(getString(R.string.dishes), (Serializable) dishList);
        intent.putExtra("idTable", idTable);
        intent.putExtra("id", id);
        intent.putExtra("user", "client");
        startActivity(intent);
    }

    interface Callback {
        void onComplete();
    }
}