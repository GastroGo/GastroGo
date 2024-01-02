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
import android.widget.Toast;

import com.example.login.Model;
import com.example.login.R;
import com.example.login.Startseite;
import com.example.mitarbeiterverwaltung.MitarbeiterVerwalten;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Einstellungen extends AppCompatActivity {

    private EditText schluesselEingabe;
    private Switch benachrichtigungen, darkmode;
    private Spinner spinner_languages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);
        schluesselEingabe = findViewById(R.id.schluesselEingabe);
        benachrichtigungen = findViewById(R.id.benachrichtigungen);
        darkmode = findViewById(R.id.darkmode);
        spinner_languages = findViewById(R.id.spinner_languages);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_languages.setAdapter(adapter);

        setupListeners();
        loadModelData();
    }

    private void setupListeners() {
        schluesselEingabe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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
        findViewById(R.id.zurueck).setOnClickListener(view -> finish());

        darkmode.setOnCheckedChangeListener(this::onDarkModeChanged);
        benachrichtigungen.setOnCheckedChangeListener(this::onBenachrichtigungenChanged);
        spinner_languages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                saveLanguage(adapterView.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadModelData() {
        Model model = Model.getInstance();
        model.load(this);

        benachrichtigungen.setChecked(model.getBenachrichtigungen() == 1);
        darkmode.setChecked(model.getDarkmode() == 1);
        spinner_languages.setSelection(model.getLanguage());
        schluesselEingabe.setText(model.getSchluessel());
    }

    private void saveSchluessel() {
        Model model = Model.getInstance();
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
                            String firebaseKey = greatGrandChildSnapshot.getValue(String.class);

                            if (inputKey.equals(firebaseKey)) {
                                // Get the parent of the greatGrandChildSnapshot
                                DatabaseReference grandChildRef = greatGrandChildSnapshot.getRef().getParent();
                                if (grandChildRef != null) {
                                    // Get the parent of the grandChildSnapshot
                                    DatabaseReference childRef = grandChildRef.getParent();
                                    if (childRef != null) {
                                        String parentKey = childRef.getKey();
                                       // Intent intent = new Intent(getApplicationContext(), MitarbeiterSeiteOderSoKeinPlanSoWeitBinIchNochNicht.class);
                                       // intent.putExtra("restaurantId", parentKey); // Pass the restaurant ID to CreateMenu activity
                                       // startActivity(intent);
                                        Toast.makeText(Einstellungen.this, "Restaurant: "+parentKey, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Einstellungen.this, "Schlüssel falsch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void onDarkModeChanged(CompoundButton buttonView, boolean isChecked) {
        Model model = Model.getInstance();
        model.setDarkmode(isChecked ? 1 : 0);
        model.save(this);
    }

    private void onBenachrichtigungenChanged(CompoundButton buttonView, boolean isChecked) {
        Model model = Model.getInstance();
        model.setBenachrichtigungen(isChecked ? 1 : 0);
        model.save(this);
    }

    private void saveLanguage(int language) {
        Model model = Model.getInstance();
        model.setLanguage(language);
        model.save(this);
    }

}