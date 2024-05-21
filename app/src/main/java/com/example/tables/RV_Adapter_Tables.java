package com.example.tables;

import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DBKlassen.TablelistModel;
import com.example.login.R;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class RV_Adapter_Tables extends RecyclerView.Adapter<RV_Adapter_Tables.ViewHolder> {

    private static final TablelistModel tableModel = TablelistModel.getInstance();
    private String restaurantID;

    private Handler handler = new Handler();
    private Runnable runnable;
    private final OnItemClickListener onItemClickListener;



    public RV_Adapter_Tables(String restaurantID, OnItemClickListener onItemClickListener) {
        this.restaurantID = restaurantID;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_tables, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;
        List<String> keys = new ArrayList<>(tableModel.getTableNumAndTimerMap().keySet());

        holder.tableNr.setText(keys.get(pos));
        holder.timer.setText(getElapsedTime(tableModel.getTableNumAndTimerMap().get(keys.get(pos))));

        // Start the timer for this item
        final int currentPosition = pos;
        runnable = new Runnable() {
            @Override
            public void run() {
                holder.timer.setText(getElapsedTime(tableModel.getTableNumAndTimerMap().get(keys.get(currentPosition))));
                handler.postDelayed(this, 1000); // Update the timer every second
            }
        };
        handler.post(runnable);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(Integer.parseInt(keys.get(pos).substring(1)));
            }
        });

    }


    @Override
    public int getItemCount() {
        return tableModel.getTableNumAndTimerMap() != null ? tableModel.getTableNumAndTimerMap().size() : 0;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        handler.removeCallbacks(runnable);
    }


    private String getElapsedTime(String lastOrderTime) {
        if (lastOrderTime.equals("-")) {
            return "-";
        }

        long minutes = 0;
        long seconds = 0;

        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime lastOrder = LocalTime.parse(lastOrderTime, formatter);
            LocalTime now = LocalTime.now();
            now = now.plusHours(2);

            minutes = ChronoUnit.MINUTES.between(lastOrder, now);
            seconds = ChronoUnit.SECONDS.between(lastOrder, now) % 60;
        }


        return String.format("%02d:%02d", minutes, seconds);
    }

    public interface OnItemClickListener {
        void onItemClick(int tableNumber);
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
