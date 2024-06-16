package com.gastro.settings;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.gastro.homepage.Homepage;
import com.gastro.login.BaseActivity;
import com.gastro.login.R;
import com.gastro.login.SplashScreen;
import com.gastro.manage.EmployeesView;
import com.gastro.utility.DropdownManager;
import com.gastro.utility.LocaleHelper;
import com.gastro.utility.UserCache;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Settings extends BaseActivity {
    private static boolean languageChanged = false;
    FloatingActionButton back;
    Homepage sRef = new Homepage();
    boolean inEmployee;
    private EditText schluesselEingabe;
    private SwitchMaterial benachrichtigungen, darkmode;
    private Spinner spinner_languages;
    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        schluesselEingabe = findViewById(R.id.schluesselEingabe);
        benachrichtigungen = findViewById(R.id.benachrichtigungen);
        darkmode = findViewById(R.id.darkmode);
        spinner_languages = findViewById(R.id.spinner_languages);
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        if (user == null) {
            findViewById(R.id.login).setVisibility(View.VISIBLE);
            findViewById(R.id.mTitel).setVisibility(View.VISIBLE);
        } else {
            String cachedUserId = UserCache.getInstance().getUserId();
            if (cachedUserId != null && cachedUserId.equals(user.getUid())) {
                findViewById(R.id.login).setVisibility(View.GONE);
                findViewById(R.id.mTitel).setVisibility(View.GONE);
            }
        }

        inEmployee = getIntent().getBooleanExtra("inEmployee", false);
        if (inEmployee) {
            findViewById(R.id.mitarbeiterLogin).setVisibility(View.GONE);
        }

        back = findViewById(R.id.btn_back);
        back.setOnClickListener(v ->
                onBackPressed());

        TextView headerText = findViewById(R.id.text);
        headerText.setText(R.string.settings);

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu_settings, R.id.imageMenu);
        dropdownManager.setupDropdown();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_languages.setAdapter(adapter);

        setupListeners();
        loadModelData();
    }

    /** @noinspection deprecation*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (languageChanged) {
            languageChanged = false;
            Intent intent = new Intent(this, SplashScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        finish();
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
            if (!schluesselEingabe.getText().toString().trim().isEmpty()) {
                schluesselAbgleichen();
            }
        });
        darkmode.setOnCheckedChangeListener(this::onDarkModeChanged);
        benachrichtigungen.setOnCheckedChangeListener(this::onBenachrichtigungenChanged);

        spinner_languages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedLanguage = adapterView.getSelectedItem().toString();
                String currentLanguage = Locale.getDefault().getLanguage();
                String newLanguageCode = "";

                switch (selectedLanguage) {
                    case "Deutsch":
                    case "German":
                        newLanguageCode = "de";
                        break;
                    case "Englisch":
                    case "English":
                        newLanguageCode = "en";
                        break;
                }

                if (!currentLanguage.equals(newLanguageCode)) {
                    saveLanguage(adapterView.getSelectedItemPosition());
                    LocaleHelper.setLocale(Settings.this, newLanguageCode);
                    recreate();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadModelData() {
        SettingsModel model = SettingsModel.getInstance();
        model.load(this);

        benachrichtigungen.setChecked(model.getBenachrichtigungen() == 1);
        darkmode.setChecked(model.getDarkmode());
        spinner_languages.setSelection(model.getLanguage());
        String schluessel = model.getSchluessel();
        if (schluessel != null) {
            schluesselEingabe.setText(schluessel.trim());
        } else {
            schluesselEingabe.setText("");
        }
    }

    private void saveSchluessel() {
        SettingsModel model = SettingsModel.getInstance();
        model.setSchluessel(schluesselEingabe.getText().toString().trim());
        model.save(this);
    }

    private void schluesselAbgleichen() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schluessel");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String inputKey = schluesselEingabe.getText().toString().trim();
                boolean keyFound = false;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                        for (DataSnapshot greatGrandChildSnapshot : grandChildSnapshot.getChildren()) {
                            String firebaseKey;
                            try {
                                firebaseKey = greatGrandChildSnapshot.getValue(String.class);
                            } catch (Exception e) {
                                firebaseKey = "";
                            }
                            if (inputKey.equals(firebaseKey)) {

                                DatabaseReference grandChildRef = greatGrandChildSnapshot.getRef().getParent();
                                if (grandChildRef != null) {

                                    DatabaseReference childRef = grandChildRef.getParent();
                                    if (childRef != null) {
                                        String childKey = grandChildRef.getKey();
                                        String parentKey = childRef.getKey();
                                        String uid = sRef.getUserId();
                                        assert parentKey != null;
                                        assert childKey != null;
                                        DataSnapshot snap = dataSnapshot.child(parentKey).child(childKey).child("UID");
                                        String idc = snap.getValue(String.class);
                                        assert idc != null;
                                        if (idc.isEmpty()) {
                                            Intent intent = new Intent(getApplicationContext(), EmployeesView.class);
                                            intent.putExtra("restaurantId", parentKey);
                                            intent.putExtra("employeeId", grandChildSnapshot.getKey());
                                            startActivity(intent);
                                            ref.child(parentKey).child(childKey).child("UID").setValue(uid);
                                        } else if (idc.equals(uid)) {
                                            Intent intent = new Intent(getApplicationContext(), EmployeesView.class);
                                            intent.putExtra("restaurantId", parentKey);
                                            intent.putExtra("employeeId", grandChildSnapshot.getKey());
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(Settings.this, R.string.key_used, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Settings.this, R.string.wrong_key, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void onDarkModeChanged(CompoundButton buttonView, boolean isChecked) {
        SettingsModel model = SettingsModel.getInstance();
        if (model.getDarkmode() != isChecked) {
            model.setDarkmode(isChecked);
            model.save(this);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                this.recreate();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                this.recreate();
            }
        }
    }

    private void onBenachrichtigungenChanged(CompoundButton buttonView, boolean isChecked) {
        SettingsModel model = SettingsModel.getInstance();
        model.setBenachrichtigungen(isChecked ? 1 : 0);
        model.save(this);
    }

    private void saveLanguage(int language) {
        SettingsModel model = SettingsModel.getInstance();
        languageChanged = model.getLanguage() != language;
        model.setLanguage(language);
        model.save(this);
    }
}