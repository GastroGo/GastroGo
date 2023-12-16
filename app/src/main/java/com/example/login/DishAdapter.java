package com.example.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {

    private final List<Speisekarte> dishes;
    private final String restaurantId;

    public DishAdapter(List<Speisekarte> dishes, String restaurantId) {
        this.dishes = dishes;
        this.restaurantId = restaurantId;
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
        holder.dishPrice.setText(String.format("%.2f€", dish.getPreis())); //Formatieren des Preises mit zwei Dezimalstellen

        StringBuilder zutatenText = new StringBuilder();
        if (dish.getZutaten() != null) {
            for (Map.Entry<String, Boolean> entry : dish.getZutaten().entrySet()) {
                if (entry.getValue()) {
                    zutatenText.append(entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1)).append(", ");
                }
            }
        }
        if (zutatenText.length() > 0) {
            zutatenText.deleteCharAt(zutatenText.length() - 2); //Entfernen des letzten Kommas und Leerzeichens
        }
        holder.textViewInfo.setText(zutatenText.toString());

        holder.buttonEditDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                View view = inflater.inflate(R.layout.dialog_edit_dish, null);
                builder.setView(view);

                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editDishName = view.findViewById(R.id.edit_dish_name);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editDishPrice = view.findViewById(R.id.edit_dish_price);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button saveButton = view.findViewById(R.id.save_button);

                editDishName.setText(dish.getGericht());
                editDishPrice.setText(String.valueOf(dish.getPreis()));

                AlertDialog dialog = builder.create();

                saveButton.setOnClickListener(v1 -> {
                    String name = editDishName.getText().toString();
                    String priceString = editDishPrice.getText().toString();

                    if (name.isEmpty() || priceString.isEmpty()) {
                        Toast.makeText(v1.getContext(), "Eingabe unvollständig", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price = Double.parseDouble(priceString);
                    dish.setGericht(name);
                    dish.setPreis(price);

                    int currentPosition = holder.getAdapterPosition();
                    DatabaseReference dbRefDishes = FirebaseDatabase.getInstance().getReference("Restaurants").child(restaurantId).child("speisekarte");
                    dbRefDishes.child("G" + String.format("%03d", currentPosition + 1)).setValue(dish);

                    dishes.set(currentPosition, dish);
                    notifyItemChanged(currentPosition);

                    dialog.dismiss();
                });

                dialog.show();
            }
        });

        holder.buttonDeleteDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                View view = inflater.inflate(R.layout.dialog_delete_dish, null);
                builder.setView(view);

                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button deleteButton = view.findViewById(R.id.delete_button);

                AlertDialog dialog = builder.create();

                deleteButton.setOnClickListener(v1 -> {
                    int currentPosition = holder.getAdapterPosition();
                    DatabaseReference dbRefDishes = FirebaseDatabase.getInstance().getReference("Restaurants").child(restaurantId).child("speisekarte");
                    dbRefDishes.child("G" + String.format("%03d", currentPosition + 1)).removeValue();

                    dishes.remove(currentPosition);
                    notifyItemRemoved(currentPosition);

                    // Löschen aller Gerichte aus der Datenbank
                    for (int i = 0; i < dishes.size(); i++) {
                        dbRefDishes.child("G" + String.format("%03d", i + 1)).removeValue();
                    }

                    // Hinzufügen der Gerichte zurück in die Datenbank mit der richtigen Nummerierung
                    for (int i = 0; i < dishes.size(); i++) {
                        dbRefDishes.child("G" + String.format("%03d", i + 1)).setValue(dishes.get(i));
                    }

                    dialog.dismiss();
                });

                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    static class DishViewHolder extends RecyclerView.ViewHolder {
        TextView dishName, dishPrice, textViewInfo;
        FloatingActionButton buttonEditDish, buttonDeleteDish;

        DishViewHolder(View view) {
            super(view);
            dishName = view.findViewById(R.id.dish_name);
            dishPrice = view.findViewById(R.id.dish_price);
            textViewInfo = view.findViewById(R.id.textViewAdditionalInfo);
            buttonEditDish = view.findViewById(R.id.buttonEditDish);
            buttonDeleteDish = view.findViewById(R.id.buttonDeleteDish);
        }
    }
}