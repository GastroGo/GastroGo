package com.gastro.bill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gastro.login.R;

import java.util.ArrayList;
import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder>{

    BillModel billModel = BillModel.getInstance();

    @NonNull
    @Override
    public BillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_bill, parent, false);
        return new BillAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<String> keys = new ArrayList<>(billModel.orders.keySet());
        String key = keys.get(position);
        holder.text.setText(billModel.orders.get(key) + "x " + billModel.dishNames.get(key) + " (" + billModel.dishCosts.get(key) + "€)");
        holder.costText.setText(String.valueOf(billModel.orders.get(key) * billModel.dishCosts.get(key)) + "€");
    }

    @Override
    public int getItemCount() {
        return billModel.orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        TextView costText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.text = itemView.findViewById(R.id.billDishTV);
            this.costText = itemView.findViewById(R.id.billCostTV);
        }
    }
}
