package com.example.qrcodegenerator;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class GerichtAdapter extends RecyclerView.Adapter<GerichtAdapter.GerichtViewHolder> {

    private List<Gericht> gerichtList;

    public GerichtAdapter(List<Gericht> gerichtList) {
        this.gerichtList = gerichtList;
    }

    @NonNull
    @Override
    public GerichtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gericht, parent, false);
        return new GerichtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GerichtViewHolder holder, int position) {
        Gericht gericht = gerichtList.get(position);
        holder.textViewGerichtName.setText(gericht.getGerichtName());

        double preis = gericht.getPreis();
        String preisString = String.format("%.2f", preis);
        String formattedPreis = preisString.replace('.', ',') + "€";
        holder.textViewGerichtPreis.setText(formattedPreis);



        StringBuilder zutatenText = new StringBuilder();
        for (String zutat : gericht.getZutaten()) {
            zutatenText.append(zutat.substring(0, 1).toUpperCase() + zutat.substring(1)).append(", ");
        }
        zutatenText.deleteCharAt(zutatenText.length()-2);
        holder.textViewInfo.setText(zutatenText.toString());

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Gericht selectedGericht = gerichtList.get(position);
                if (position != RecyclerView.NO_POSITION) {
                    selectedGericht.setSelected(true);
                    selectedGericht.setAmount(selectedGericht.getAmount() + 1);

                    holder.gerichtLayout.setSelected(true);
                    holder.amount.setText(String.valueOf(selectedGericht.getAmount()));
                }

            }
        });

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Gericht selectedGericht = gerichtList.get(position);

                    if (selectedGericht.getAmount() > 0) {
                        selectedGericht.setAmount(selectedGericht.getAmount() - 1);
                    }

                    if (selectedGericht.getAmount() == 0) {
                        selectedGericht.setSelected(false);
                        holder.gerichtLayout.setSelected(false);
                    }
                    holder.amount.setText(String.valueOf(selectedGericht.getAmount()));
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return gerichtList.size();
    }

    static class GerichtViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGerichtName;
        TextView textViewGerichtPreis;
        TextView textViewInfo;
        LinearLayout gerichtLayout;
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
