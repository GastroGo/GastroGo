package com.example.Bestellungen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.DBKlassen.DishModel;
import com.example.login.R;

import java.util.ArrayList;
import java.util.List;

public class RV_Adapter_Orders extends RecyclerView.Adapter<RV_Adapter_Orders.ViewHolder>{

    DishModel dishModel = DishModel.getInstance();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_bestellung, parent, false);
        return new RV_Adapter_Orders.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int pos = position;
        List<String> keys = new ArrayList<>(dishModel.getOrders().keySet());
        String key = keys.get(pos);

        holder.dishName.setText(dishModel.getOrders().get(key) + "x " + dishModel.getDishNames().get(key));

    }

    @Override
    public int getItemCount() {
        return dishModel.getOrders().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView dishName;
        private final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.dishName = itemView.findViewById(R.id.RV_TV_DishName);
            this.checkBox = itemView.findViewById(R.id.RV_CB_CheckBox);
        }
    }
}
