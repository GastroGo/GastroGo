package com.gastro.orders;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gastro.database.States;
import com.gastro.login.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class RV_Adapter_Orders extends RecyclerView.Adapter<RV_Adapter_Orders.ViewHolder>{

    DishModel dishModel = DishModel.getInstance();
    OrdersActivity bestellungenActivity;

    public RV_Adapter_Orders(OrdersActivity bestellungenActivity) {
        this.bestellungenActivity = bestellungenActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_bestellung, parent, false);
        return new RV_Adapter_Orders.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int pos = position;
        String key = new ArrayList<>(dishModel.getOrders().keySet()).get(pos);

        holder.dishName.setText(dishModel.getOrders().get(key) + "x " + dishModel.getDishNames().get(key));

        holder.checkBox.setChecked(dishModel.curState == States.OPEN ? false : true);

        if (dishModel.curState == States.OPEN && dishModel.getClosingDishes().contains(key)){
            holder.cardView.setCardBackgroundColor(Color.GRAY);
            holder.checkBox.setChecked(true);

        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bestellungenActivity.closeOpenOrders(key);
            }
        });

        holder.deleteOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bestellungenActivity.deleteOrder(key);
            }
        });

    }

    @Override
    public int getItemCount() {
        try {
            return dishModel.getOrders().size();
        } catch (Exception e){
            return 0;
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView dishName;
        private final CheckBox checkBox;
        private final CardView cardView;
        private final FloatingActionButton deleteOrderButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.dishName = itemView.findViewById(R.id.RV_TV_DishName);
            this.checkBox = itemView.findViewById(R.id.RV_CB_CheckBox);
            this.cardView = itemView.findViewById(R.id.RV_CardView);
            this.deleteOrderButton = itemView.findViewById(R.id.buttonDeleteOrder);
        }
    }
}
