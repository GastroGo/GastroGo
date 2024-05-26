package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tische.TischeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EmployeesView extends AppCompatActivity {

    ConstraintLayout settings, user, work;
    FloatingActionButton back;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();
    Map<String, String> workData = new HashMap<>();
    Calendar calendar = Calendar.getInstance();
    String restaurantId;
    String employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees_menu);

        settings = findViewById(R.id.settingButtonEmployees);
        user = findViewById(R.id.toUserPage);
        work = findViewById(R.id.toWorkersPage);
        back = findViewById(R.id.btn_back);

        restaurantId = getIntent().getStringExtra("restaurantId");
        employeeId = getIntent().getStringExtra("employeeId");

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        TextView headerText = findViewById(R.id.text);
        headerText.setText("Mitarbeiter");

        getWorkdays();


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Homepage.class);
                intent.putExtra("employee", getIntent().getBooleanExtra("employee", false));
                startActivity(intent);
            }
        });

        work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStartWorkDialog();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Homepage.class);
                intent.putExtra("employee", getIntent().getBooleanExtra("employee", false));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });
    }

    private void showStartWorkDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.RoundedDialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_delete_dish, null);
        builder.setView(view);

        Button confirmButton = view.findViewById(R.id.delete_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        TextView text = view.findViewById(R.id.confirm);

        String date = String.format("%02d%02d%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));


        text.setText(workData != null && workData.containsKey(date) && workData.get(date).contains("x") ? "Arbeitstag forsetzen?":"Arbeitstag beginnen?");
        text.setGravity(Gravity.CENTER);
        confirmButton.setText("bestätigen");

        AlertDialog dialog = builder.create();

        confirmButton.setOnClickListener(view1 -> {
            if (workData != null && workData.containsKey(date) && workData.get(date).contains("x")){
                goToTables();
            }else {
                if (workData == null){
                    workData = new HashMap<>();
                }
                startWorkday();
            }
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(view1 -> dialog.cancel());

        dialog.show();
    }

    private void getWorkdays(){
        dbRef.child("/Schluessel/" + restaurantId + "/" + employeeId + "/arbeitsZeiten").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                workData = (Map<String, String>) dataSnapshot.getValue();
                Log.i("id", workData + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void startWorkday(){
        String date = String.format("%02d%02d%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        String time = String.format("%02d:%02d-xx:xx", calendar.get(Calendar.HOUR_OF_DAY) + 2, calendar.get(Calendar.MINUTE));
        workData.put(date, time);

        DatabaseReference dbRef = this.dbRef.child("/Schluessel/" + restaurantId + "/" + employeeId + "/arbeitsZeiten");
        dbRef.setValue(workData);

        Log.i("id", workData + "");
        goToTables();
    }

    private void goToTables(){
        Intent intent = new Intent(getApplicationContext(), TischeActivity.class);
        intent.putExtra("restaurantId", restaurantId);
        intent.putExtra("employeeId", employeeId);
        startActivity(intent);
    }
}