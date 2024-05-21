package com.example.orders;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.DBKlassen.DishModel;
import com.example.DBKlassen.states;
import com.example.login.R;

import java.util.ArrayList;

public class RV_Adapter_Orders extends RecyclerView.Adapter<RV_Adapter_Orders.ViewHolder>{

    DishModel dishModel = DishModel.getInstance();
    OrdersActivity ordersActivity;

    public RV_Adapter_Orders(OrdersActivity ordersActivity) {
        this.ordersActivity = ordersActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_orders, parent, false);
        return new RV_Adapter_Orders.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int pos = position;
        String key = new ArrayList<>(dishModel.getOrders().keySet()).get(pos);

        holder.dishName.setText(dishModel.getOrders().get(key) + "x " + dishModel.getDishNames().get(key));

        holder.checkBox.setChecked(dishModel.curState == states.OPEN ? false : true);

        if (dishModel.curState == states.OPEN && dishModel.getClosingDishes().contains(key)){
            holder.cardView.setCardBackgroundColor(Color.GRAY);
            holder.checkBox.setChecked(true);

        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ordersActivity.closeOpenOrders(key);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dishModel.getOrders().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView dishName;
        private final CheckBox checkBox;
        private final CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.dishName = itemView.findViewById(R.id.RV_TV_DishName);
            this.checkBox = itemView.findViewById(R.id.RV_CB_CheckBox);
            this.cardView = itemView.findViewById(R.id.RV_CardView);
        }
    }
}
