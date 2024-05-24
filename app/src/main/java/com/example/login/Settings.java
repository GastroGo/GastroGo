package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity {

    private EditText schluesselEingabe;
    private Switch benachrichtigungen, darkmode;
    private Spinner spinner_languages;
    FloatingActionButton back;
    Homepage sRef = new Homepage();
    boolean inEmployee;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        schluesselEingabe = findViewById(R.id.schluesselEingabe);
        benachrichtigungen = findViewById(R.id.benachrichtigungen);
        darkmode = findViewById(R.id.darkmode);
        spinner_languages = findViewById(R.id.spinner_languages);



        inEmployee = getIntent().getBooleanExtra("inEmployee", false);
        if (inEmployee) {
            findViewById(R.id.mitarbeiterLogin).setVisibility(View.GONE);
        }

        back = findViewById(R.id.btn_back);

        back.setOnClickListener(v -> {
            finish();
        });

        TextView headerText = findViewById(R.id.text);
        headerText.setText("Einstellungen");

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu_settings, R.id.imageMenu);
        dropdownManager.setupDropdown();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_languages.setAdapter(adapter);

        setupListeners();
        loadModelData();
    }

    private void setupListeners() {
        schluesselEingabe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                findViewById(R.id.mitarbeiterLogin).setEnabled(!editable.toString().isEmpty());
                saveSchluessel();
            }
        });

        findViewById(R.id.mitarbeiterLogin).setOnClickListener(view -> {
            saveSchluessel();
            schluesselAbgleichen();
        });
        darkmode.setOnCheckedChangeListener(this::onDarkModeChanged);
        benachrichtigungen.setOnCheckedChangeListener(this::onBenachrichtigungenChanged);
        spinner_languages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                saveLanguage(adapterView.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadModelData() {
        SettingsModel model = SettingsModel.getInstance();
        model.load(this);

        benachrichtigungen.setChecked(model.getBenachrichtigungen() == 1);
        darkmode.setChecked(model.getDarkmode());
        spinner_languages.setSelection(model.getLanguage());
        schluesselEingabe.setText(model.getSchluessel());
    }

    private void saveSchluessel() {
        SettingsModel model = SettingsModel.getInstance();
        model.setSchluessel(schluesselEingabe.getText().toString());
        model.save(this);
    }

    private void schluesselAbgleichen() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schluessel");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String inputKey = schluesselEingabe.getText().toString();
                boolean keyFound = false;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                        for (DataSnapshot greatGrandChildSnapshot : grandChildSnapshot.getChildren()) {
                            String firebaseKey;
                            try {
                                firebaseKey = greatGrandChildSnapshot.getValue(String.class);
                            } catch (Exception e){
                                break;
                            }
                            if (inputKey.equals(firebaseKey)) {

                                DatabaseReference grandChildRef = greatGrandChildSnapshot.getRef().getParent();
                                if (grandChildRef != null) {

                                    DatabaseReference childRef = grandChildRef.getParent();
                                    if (childRef != null) {
                                        String childKey = grandChildRef.getKey();
                                        String parentKey = childRef.getKey();
                                        String uid = sRef.getUserId();
                                        DataSnapshot snap = dataSnapshot.child(parentKey).child(childKey).child("UID");
                                        String idc = snap.getValue(String.class);
                                        if (idc.equals("")) {
                                            Intent intent = new Intent(getApplicationContext(), EmployeesView.class);
                                            intent.putExtra("restaurantId", parentKey);
                                            startActivity(intent);
                                            ref.child(parentKey).child(childKey).child("UID").setValue(uid);
                                        } else if (idc.equals(uid)) {
                                            Intent intent = new Intent(getApplicationContext(), EmployeesView.class);
                                            intent.putExtra("restaurantId", parentKey);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(Settings.this, "Schlüssel bereits verwendet", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                keyFound = true;
                                break;
                            }
                        }

                        if (keyFound) {
                            break;
                        }
                    }

                    if (keyFound) {
                        break;
                    }
                }

                if (!keyFound) {
                    Toast.makeText(Settings.this, "Schlüssel falsch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void onDarkModeChanged(CompoundButton buttonView, boolean isChecked) {
        SettingsModel model = SettingsModel.getInstance();
        model.setDarkmode(isChecked);
        model.save(this);
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void onBenachrichtigungenChanged(CompoundButton buttonView, boolean isChecked) {
        SettingsModel model = SettingsModel.getInstance();
        model.setBenachrichtigungen(isChecked ? 1 : 0);
        model.save(this);
    }

    private void saveLanguage(int language) {
        SettingsModel model = SettingsModel.getInstance();
        model.setLanguage(language);
        model.save(this);
    }


}