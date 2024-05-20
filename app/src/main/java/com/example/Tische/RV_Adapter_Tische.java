package com.example.Tische;

import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DBKlassen.TablelistModel;
import com.example.login.R;

import java.util.ArrayList;
import java.util.List;


public class RV_Adapter_Tische extends RecyclerView.Adapter<RV_Adapter_Tische.ViewHolder> {

    private static final TablelistModel tableModel = TablelistModel.getInstance();
    private String restaurantID;



    public RV_Adapter_Tische(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_tische, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        List<String> keys = new ArrayList<>(tableModel.getTableNumAndTimerMap().keySet());

        holder.tableNr.setText(keys.get(position));
        holder.timer.setText(tableModel.getTableNumAndTimerMap().get(keys.get(position)));
    }

    @Override
    public int getItemCount() {
        return tableModel.getTableNumAndTimerMap() != null ? tableModel.getTableNumAndTimerMap().size() : 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tableNr;
        private final TextView timer;
        private final CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tableNr = itemView.findViewById(R.id.RV_TV_TableNr);
            this.timer = itemView.findViewById(R.id.RV_TV_Timer);
            cardView = itemView.findViewById(R.id.RV_CardView);
        }
    }
}
