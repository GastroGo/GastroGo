package com.gastro.qrcodereader;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gastro.login.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Dish> selectedGerichte;
    private OnListEmptyListener onListEmptyListener;

    public OrderAdapter(List<Dish> selectedGerichte) {
        this.selectedGerichte = selectedGerichte;
    }

    public void setOnListEmptyListener(OnListEmptyListener listener) {
        this.onListEmptyListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_item_gericht, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Dish dish = selectedGerichte.get(position);

        holder.textViewGerichtName.setText(dish.getGerichtName());
        holder.textViewGerichtPreis.setText(String.format("%.2f€", dish.getPreis() * dish.getAmount()));
        holder.editTextAmount.setText(String.valueOf(dish.getAmount()));

        holder.editTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (editable.length() == 0) {
                        dish.setFinalAmount(0);
                    }
                    int amount = Integer.parseInt(editable.toString());

                    if (amount > 99) {
                        holder.editTextAmount.setText(String.valueOf(amount / 10));
                        amount = amount / 10;
                        Toast.makeText(holder.itemView.getContext(), R.string.maximum_exceeded, Toast.LENGTH_SHORT).show();
                    }
                    dish.setFinalAmount(amount);
                    holder.textViewGerichtPreis.setText(String.format("%.2f€", dish.getPreis() * amount));
                } catch (NumberFormatException e) {
                }
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Dish removeDish = selectedGerichte.get(position);
                removeDish.setFinalAmount(0);
                selectedGerichte.remove(removeDish);
                notifyItemRemoved(position);

                if (selectedGerichte.isEmpty() && onListEmptyListener != null) {
                    onListEmptyListener.onListEmpty();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedGerichte.size();
    }

    public interface OnListEmptyListener {
        void onListEmpty();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGerichtName;
        TextView textViewGerichtPreis;
        EditText editTextAmount;
        FloatingActionButton deleteBtn;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGerichtName = itemView.findViewById(R.id.textViewOrderGerichtName);
            textViewGerichtPreis = itemView.findViewById(R.id.textViewOrderGerichtPreis);
            editTextAmount = itemView.findViewById(R.id.editTextOrderAmount);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
