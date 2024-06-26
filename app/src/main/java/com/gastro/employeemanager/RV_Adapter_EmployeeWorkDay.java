package com.gastro.employeemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gastro.login.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class RV_Adapter_EmployeeWorkDay extends RecyclerView.Adapter<RV_Adapter_EmployeeWorkDay.ViewHolder>{
    EmployeeSaveModel employeeModel = EmployeeSaveModel.getInstance();
    Employee context;

    public RV_Adapter_EmployeeWorkDay(Employee context){
        this.context = context;
    }

    @NonNull
    @Override
    public RV_Adapter_EmployeeWorkDay.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_employeeworkday, parent, false);
        return new RV_Adapter_EmployeeWorkDay.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;

        List<String> keys = new ArrayList<>(employeeModel.employee.arbeitsZeiten.keySet());
        String key = keys.get(pos);

        holder.hours.setText(employeeModel.employee.arbeitsZeiten.get(key));
        holder.date.setText(key.substring(0, 2) + "." + key.substring(2, 4) + "." + key.substring(4));

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.showEditDialog(key);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.showDeleteDialog(key);
            }
        });
    }

    @Override
    public int getItemCount() {
        try{
            return employeeModel.employee.arbeitsZeiten.size();
        } catch (Exception e){
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView hours, date;
        FloatingActionButton delete, edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            hours = itemView.findViewById(R.id.rv_employeeworkday_hours);
            date = itemView.findViewById(R.id.rv_employeeworkday_date);
            delete = itemView.findViewById(R.id.buttonDeleteWorkday);
            edit = itemView.findViewById(R.id.buttonEditWorkday);
        }
    }

}
