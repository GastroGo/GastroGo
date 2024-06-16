package com.gastro.manage;

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

import com.gastro.database.Menu;
import com.gastro.login.R;
import com.gastro.settings.SettingsModel;
import com.gastro.utility.FirebaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {

    private final List<Menu> dishes;
    private final String restaurantId;

    public DishAdapter(List<Menu> dishes, String restaurantId) {
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
        Menu dish = dishes.get(position);
        holder.dishName.setText(dish.getGericht());

        String languageCode = SettingsModel.getInstance().getLanguageCode();

        NumberFormat format;
        if ("de".equals(languageCode)) {
            format = NumberFormat.getNumberInstance(Locale.GERMANY);
        } else {
            format = NumberFormat.getNumberInstance(Locale.ENGLISH);
        }
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        String priceString = format.format(dish.getPreis());

        holder.dishPrice.setText(String.format("%s€", priceString));

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

        holder.buttonEditDish.setOnClickListener(v -> {
            AlertDialog dialog = createEditDishDialog(v, dish, holder);
            dialog.show();
        });

        holder.buttonDeleteDish.setOnClickListener(v -> {
            AlertDialog dialog = createDeleteDishDialog(v, dish, holder);
            dialog.show();
        });
    }

    private AlertDialog createEditDishDialog(View v, Menu dish, DishViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.RoundedDialog);
        LayoutInflater inflater = LayoutInflater.from(v.getContext());
        View view = inflater.inflate(R.layout.dialog_edit_dish, null);
        builder.setView(view);

        EditText editDishName = view.findViewById(R.id.edit_dish_name);
        EditText editDishPrice = view.findViewById(R.id.edit_dish_price);
        Button saveButton = view.findViewById(R.id.save_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        editDishName.setText(dish.getGericht());

        String languageCode = SettingsModel.getInstance().getLanguageCode();

        NumberFormat format;
        if ("de".equals(languageCode)) {
            format = NumberFormat.getInstance(Locale.GERMANY);
        } else {
            format = NumberFormat.getInstance(Locale.ENGLISH);
        }
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        String priceString = format.format(dish.getPreis());

        editDishPrice.setText(priceString);

        AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v1 -> {
            String name = editDishName.getText().toString();
            String priceInput = editDishPrice.getText().toString();

            if (name.isEmpty() || priceString.isEmpty()) {
                Toast.makeText(v1.getContext(), R.string.input_incomplete, Toast.LENGTH_SHORT).show();
                return;
            }
            double price;
            try {
                Number number = format.parse(priceInput);
                price = (number != null) ? number.doubleValue() : 0.0;
            } catch (ParseException e) {
                e.printStackTrace();
                price = 0.0;
            }

            dish.setGericht(name);
            dish.setPreis(price);

            int currentPosition = holder.getAdapterPosition();
            new FirebaseManager(restaurantId).updateDishInFirebase(dish, currentPosition, dishes);

            notifyItemChanged(currentPosition);

            dialog.dismiss();
        });
        cancelButton.setOnClickListener(v2 -> dialog.dismiss());

        return dialog;
    }

    private AlertDialog createDeleteDishDialog(View v, Menu dish, DishViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.RoundedDialog);
        LayoutInflater inflater = LayoutInflater.from(v.getContext());
        View view = inflater.inflate(R.layout.dialog_delete_item, null);
        builder.setView(view);

        Button deleteButton = view.findViewById(R.id.delete_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        AlertDialog dialog = builder.create();

        deleteButton.setOnClickListener(v1 -> {
            int currentPosition = holder.getAdapterPosition();
            new FirebaseManager(restaurantId).deleteDishFromFirebase(currentPosition, dishes);

            notifyItemRemoved(currentPosition);

            dialog.dismiss();
        });
        cancelButton.setOnClickListener(v2 -> dialog.dismiss());

        return dialog;
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    public static class DishViewHolder extends RecyclerView.ViewHolder {
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