package com.gastro.manage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gastro.employeemanager.Employee;
import com.gastro.employeemanager.EmployeeAdapter;
import com.gastro.employeemanager.EmployeeItem;
import com.gastro.employeemanager.EmployeeModel;
import com.gastro.login.BaseActivity;
import com.gastro.login.R;
import com.gastro.utility.DropdownManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;

public class EmployeeManager extends BaseActivity {

    public long count;
    RecyclerView recyclerView;
    DatabaseReference database;
    EmployeeAdapter myadapter;
    ArrayList<EmployeeItem> list;
    HashSet<String> keysSet;
    FloatingActionButton mAnlegen;
    FloatingActionButton back;
    String restaurantId;
    ValueEventListener valueEventListener;
    private AlertDialog addEmployeeDialog;

    public static String generateKey() {
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder();
        EmployeeModel model = new EmployeeModel();

        for (int i = 0; i < model.getKeyLength(); i++) {
            int randomIndex = random.nextInt(model.getKeyCharacters().length());
            char randomChar = model.getKeyCharacters().charAt(randomIndex);
            key.append(randomChar);
        }

        return key.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_employees);
        restaurantId = getIntent().getStringExtra("restaurantId");

        TextView headerText = findViewById(R.id.text);
        headerText.setText(R.string.employee);

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        back = findViewById(R.id.btn_back);
        mAnlegen = findViewById(R.id.mAnlegen);
        recyclerView = findViewById(R.id.mListe);
        database = FirebaseDatabase.getInstance().getReference("Schluessel").child(restaurantId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        back.setOnClickListener(v -> finish());

        mAnlegen.setOnClickListener(v -> addEmployee());

        list = new ArrayList<>();
        keysSet = new HashSet<>();
        myadapter = new EmployeeAdapter(this, list, restaurantId);
        recyclerView.setAdapter(myadapter);

        valueEventListener = new  ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = snapshot.getChildrenCount();
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EmployeeItem user = dataSnapshot.getValue(EmployeeItem.class);
                    list.add(user);
                }
                myadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        database.addValueEventListener(valueEventListener);


    }

    private void addEmployee() {
        addEmployeeDialog = createAddEmployeeDialog();
        addEmployeeDialog.show();
    }

    private AlertDialog createAddEmployeeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeManager.this, R.style.RoundedDialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_employee, null);
        builder.setView(view);

        EditText employeeName = view.findViewById(R.id.etName);
        Button mErstellen = view.findViewById(R.id.mErstellen);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        mErstellen.setEnabled(false);

        employeeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mErstellen.setEnabled(!s.toString().trim().isEmpty());
            }
        });

        mErstellen.setOnClickListener(v1 -> {
            @SuppressLint("DefaultLocale") String nM = "M" + String.format("%03d", count + 1);
            database.child(nM).child("name").setValue(employeeName.getText().toString());
            database.child(nM).child("key").setValue(generateKey());
            database.child(nM).child("UID").setValue("");
            addEmployeeDialog.dismiss();
            employeeName.setText("");
        });

        cancelButton.setOnClickListener(v2 -> addEmployeeDialog.dismiss());

        return builder.create();
    }
    public void clickEmployee(String employeeID){
        Intent intent = new Intent(this, Employee.class);
        intent.putExtra("employeeID", employeeID);
        intent.putExtra("restaurantID", restaurantId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove the listener when the activity is destroyed
        if (valueEventListener != null) {
            database.removeEventListener(valueEventListener);
        }
    }

}