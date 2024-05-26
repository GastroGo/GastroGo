package com.example.employeemanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.DropdownManager;
import com.example.login.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Employee extends AppCompatActivity {

    String restaurantID;
    String employeeID;
    FloatingActionButton back, addWorkdayButton;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();
    RecyclerView recyclerView;
    EmployeeSaveModel employeeModel = EmployeeSaveModel.getInstance();
    RV_Adapter_EmployeeWorkDay adapter;
    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mitarbeiter_verwalten);

        restaurantID = getIntent().getStringExtra("restaurantID");
        employeeID = getIntent().getStringExtra("employeeID");

        TextView headerText = findViewById(R.id.text);
        addWorkdayButton = findViewById(R.id.mAnlegen);

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        back = findViewById(R.id.btn_back);

        back.setOnClickListener(view -> finish());

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                EmployeeItem employee = snapshot.child("/Schluessel/" + restaurantID + "/" + employeeID).getValue(EmployeeItem.class);
                headerText.setText(employee.name);
                employeeModel.setEmployee(employee);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setupAdapter();

        addWorkdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });

    }

    private void setupAdapter(){
        recyclerView = findViewById(R.id.mListe);
        adapter = new RV_Adapter_EmployeeWorkDay(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void deleteWorkday(String day){
        dbRef.child("/Schluessel/" + restaurantID + "/" + employeeID + "/arbeitsZeiten/" + day).removeValue();
    }

    public void showEditDialog(String dateToEdit){
        Dialog dialog = new Dialog(this, R.style.RoundedDialog);
        dialog.setContentView(R.layout.dialog_add_edit_workday);

        Button saveButton = dialog.findViewById(R.id.dialog_add_edit_workday_save_button);
        Button cacelButton = dialog.findViewById(R.id.dialog_add_edit_workday_cancel_button);
        Button selectDateButton = dialog.findViewById(R.id.dialog_add_edit_workday_date_button);
        Button selectStartTimeButton = dialog.findViewById(R.id.dialog_add_edit_workday_starttime_button);
        Button selectEndTimeButton = dialog.findViewById(R.id.dialog_add_edit_workday_endtime_button);

        //set Button text to current workday in database
        selectDateButton.setText(dateToEdit.substring(0, 2) + "." + dateToEdit.substring(2, 4) + "." + dateToEdit.substring(4));
        selectStartTimeButton.setText(employeeModel.employee.arbeitsZeiten.get(dateToEdit).substring(0, 5));
        selectEndTimeButton.setText(employeeModel.employee.arbeitsZeiten.get(dateToEdit).substring(6, 11));

        dialog.show();

        cacelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = (String) selectDateButton.getText();
                String startTime = (String) selectStartTimeButton.getText();
                String endTime = (String) selectEndTimeButton.getText();
                if (date.equalsIgnoreCase("datum") | startTime.equalsIgnoreCase("beginn") | endTime.equalsIgnoreCase("ende")){
                    return;
                }
                Log.i("time", date + " - " + startTime + " - " + endTime);
                employeeModel.employee.arbeitsZeiten.remove(dateToEdit);
                deleteWorkday(dateToEdit);
                employeeModel.employee.arbeitsZeiten.put(date.replace(".", ""), startTime + "-" + endTime);
                uploadData();
                dialog.cancel();
            }
        });

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(Integer.valueOf(dateToEdit.substring(4)), Integer.valueOf(dateToEdit.substring(2, 4)), Integer.valueOf(dateToEdit.substring(0, 2)), dialog);
            }
        });

        selectStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startTime = (String) selectStartTimeButton.getText();
                showTimePickerDialog(Integer.parseInt(startTime.substring(0, 2)), Integer.parseInt(startTime.substring(3)), true, dialog);
            }
        });

        selectEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String endTime = (String) selectEndTimeButton.getText();
                endTime = endTime.contains("x") ? "00:00" : endTime;
                showTimePickerDialog(Integer.parseInt(endTime.substring(0, 2)), Integer.parseInt(endTime.substring(3)), false, dialog);
            }
        });
    }

    public void showAddDialog(){
        Dialog dialog = new Dialog(this, R.style.RoundedDialog);
        dialog.setContentView(R.layout.dialog_add_edit_workday);

        TextView addText = dialog.findViewById(R.id.dialog_add_edit_workday_text);
        addText.setText("Hinzufügen");
        Button saveButton = dialog.findViewById(R.id.dialog_add_edit_workday_save_button);
        saveButton.setText("Hinzufügen");
        Button cacelButton = dialog.findViewById(R.id.dialog_add_edit_workday_cancel_button);

        Button selectDateButton = dialog.findViewById(R.id.dialog_add_edit_workday_date_button);
        Button selectStartTimeButton = dialog.findViewById(R.id.dialog_add_edit_workday_starttime_button);
        Button selectEndTimeButton = dialog.findViewById(R.id.dialog_add_edit_workday_endtime_button);

        dialog.show();

        cacelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = (String) selectDateButton.getText();
                String startTime = (String) selectStartTimeButton.getText();
                String endTime = (String) selectEndTimeButton.getText();
                if (date.equalsIgnoreCase("datum") | startTime.equalsIgnoreCase("beginn") | endTime.equalsIgnoreCase("ende")){
                    return;
                }
                Log.i("time", date + " - " + startTime + " - " + endTime);
                employeeModel.employee.arbeitsZeiten.put(date.replace(".", ""), startTime + "-" + endTime);
                uploadData();
                dialog.cancel();
            }
        });

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = (String) selectDateButton.getText();
                if (date.equalsIgnoreCase("datum")){
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    showDatePickerDialog(year, month, day, dialog);
                } else {
                    date = date.replace(".", "");
                    showDatePickerDialog(Integer.valueOf(date.substring(4)), Integer.valueOf(date.substring(2, 4)), Integer.valueOf(date.substring(0, 2)), dialog);
                }
            }
        });

        selectStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startTime = (String) selectStartTimeButton.getText();
                if (startTime.equalsIgnoreCase("beginn")){
                    int hour = calendar.get(Calendar.HOUR_OF_DAY) + 2;
                    int minute = calendar.get(Calendar.MINUTE);

                    showTimePickerDialog(hour, minute, true, dialog);
                } else {
                    showTimePickerDialog(Integer.parseInt(startTime.substring(0, 2)), Integer.parseInt(startTime.substring(3)), true, dialog);
                }
            }
        });

        selectEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String endTime = (String) selectEndTimeButton.getText();
                if (endTime.equalsIgnoreCase("ende")){
                    int hour = calendar.get(Calendar.HOUR_OF_DAY) + 3;
                    int minute = calendar.get(Calendar.MINUTE);

                    showTimePickerDialog(hour, minute, false, dialog);
                } else {
                    showTimePickerDialog(Integer.parseInt(endTime.substring(0, 2)), Integer.parseInt(endTime.substring(3)), false, dialog);
                }
            }
        });

    }

    public void showDatePickerDialog(int curYear, int curMonth, int curDay, Dialog dialog){

        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Button dateButton = dialog.findViewById(R.id.dialog_add_edit_workday_date_button);
                dateButton.setText(String.format("%02d.%02d.%04d", day, month + 1, year));
            }
        }, curYear, curMonth - 1, curDay);

        datePicker.show();

    }

    public void showTimePickerDialog(int curHour, int curMin, boolean starttime, Dialog dialog){
        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                Button timeButton = dialog.findViewById(starttime == true ? R.id.dialog_add_edit_workday_starttime_button : R.id.dialog_add_edit_workday_endtime_button);
                timeButton.setText(String.format("%02d:%02d", hour, min));
            }
        }, curHour, curMin, true);

        timePicker.show();
    }

    public void showDeleteDialog(String date){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.RoundedDialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_delete_dish, null);
        builder.setView(view);

        Button deleteButton = view.findViewById(R.id.delete_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        TextView text = view.findViewById(R.id.confirm);

        text.setText(date.substring(0, 2) + "." + date.substring(2, 4) + "." + date.substring(4) + " wirklich Löschen?");
        text.setGravity(Gravity.CENTER);

        AlertDialog dialog = builder.create();

        deleteButton.setOnClickListener(view1 -> {
            deleteWorkday(date);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(view1 -> dialog.cancel());

        dialog.show();

    }

    public void uploadData(){
        EmployeeItem employee = employeeModel.getEmployee();

        DatabaseReference dbRef = this.dbRef.child("/Schluessel/" + restaurantID + "/" + employeeID + "/arbeitsZeiten");

        dbRef.setValue(employee.getArbeitsZeiten());
    }
}