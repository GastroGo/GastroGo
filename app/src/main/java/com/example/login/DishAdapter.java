package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {

    private final List<Speisekarte> dishes;

    public DishAdapter(List<Speisekarte> dishes) {
        this.dishes = dishes;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dish, parent, false);
        return new DishViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        Speisekarte dish = dishes.get(position);
        holder.dishName.setText(dish.getGericht());
        holder.dishPrice.setText(String.valueOf(dish.getPreis()));
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    static class DishViewHolder extends RecyclerView.ViewHolder {
        TextView dishName, dishPrice;

        DishViewHolder(View view) {
            super(view);
            dishName = view.findViewById(R.id.dish_name);
            dishPrice = view.findViewById(R.id.dish_price);
        }
    }
}