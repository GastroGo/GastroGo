package com.gastro.qrcodereader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gastro.login.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.GerichtViewHolder> {

    private List<Dish> dishList;

    public DishAdapter(List<Dish> dishList) {
        this.dishList = dishList;
    }

    @NonNull
    @Override
    public GerichtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gericht, parent, false);
        return new GerichtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GerichtViewHolder holder, int position) {
        Dish dish = dishList.get(position);
        holder.textViewGerichtName.setText(dish.getGerichtName());

        double preis = dish.getPreis();
        String preisString = String.format("%.2f", preis);
        String formattedPreis = preisString.replace('.', ',') + "€";
        holder.textViewGerichtPreis.setText(formattedPreis);



        StringBuilder zutatenText = new StringBuilder();
        for (String zutat : dish.getZutaten()) {
            zutatenText.append(zutat.substring(0, 1).toUpperCase() + zutat.substring(1)).append(", ");
        }
        zutatenText.deleteCharAt(zutatenText.length()-2);
        holder.textViewInfo.setText(zutatenText.toString());

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Dish selectedDish = dishList.get(position);
                if (position != RecyclerView.NO_POSITION) {
                    selectedDish.setSelected(true);
                    selectedDish.setAmount(selectedDish.getAmount() + 1);

                    holder.gerichtLayout.setSelected(true);
                    holder.amount.setText(String.valueOf(selectedDish.getAmount()));
                }

            }
        });

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Dish selectedDish = dishList.get(position);

                    if (selectedDish.getAmount() > 0) {
                        selectedDish.setAmount(selectedDish.getAmount() - 1);
                    }

                    if (selectedDish.getAmount() == 0) {
                        selectedDish.setSelected(false);
                        holder.gerichtLayout.setSelected(false);
                    }
                    holder.amount.setText(String.valueOf(selectedDish.getAmount()));
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return dishList.size();
    }

    static class GerichtViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGerichtName;
        TextView textViewGerichtPreis;
        TextView textViewInfo;
        CardView gerichtLayout;
        TextView amount;
        FloatingActionButton btnMinus;
        FloatingActionButton btnPlus;


        GerichtViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGerichtName = itemView.findViewById(R.id.textViewGerichtName);
            textViewGerichtPreis = itemView.findViewById(R.id.textViewGerichtPreis);
            textViewInfo = itemView.findViewById(R.id.textViewAdditionalInfo);
            gerichtLayout = itemView.findViewById(R.id.gerichtLayout);
            amount = itemView.findViewById(R.id.amount);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
}
