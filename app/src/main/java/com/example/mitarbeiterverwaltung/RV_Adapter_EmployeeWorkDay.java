package com.example.mitarbeiterverwaltung;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.example.tables.RV_Adapter_Tables;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RV_Adapter_EmployeeWorkDay extends RecyclerView.Adapter<RV_Adapter_EmployeeWorkDay.ViewHolder>{
    User employee;

    public RV_Adapter_EmployeeWorkDay(User employee){
        this.employee = employee;
    }

    @NonNull
    @Override
    public RV_Adapter_EmployeeWorkDay.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_employeeworkday, parent, false);
        return new RV_Adapter_EmployeeWorkDay.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return employee.getArbeitsZeiten().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView hours, date;
        FloatingActionButton delete, edit;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.employees_rv_cardview);
            hours = itemView.findViewById(R.id.rv_employeeworkday_hours);
            date = itemView.findViewById(R.id.rv_employeeworkday_date);
            delete = itemView.findViewById(R.id.buttonDeleteWorkday);
            edit = itemView.findViewById(R.id.buttonEditWorkday);
        }
    }

}
