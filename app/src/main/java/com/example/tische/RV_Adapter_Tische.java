package com.example.tische;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class RV_Adapter_Tische extends RecyclerView.Adapter<RV_Adapter_Tische.ViewHolder> {

    private static final TablelistModel tableModel = TablelistModel.getInstance();
    private String restaurantID;

    private Handler handler = new Handler();
    private Runnable runnable;
    private final TischeActivity mainActivity;



    public RV_Adapter_Tische(String restaurantID, TischeActivity mainActivity) {
        this.restaurantID = restaurantID;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_tische, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;
        List<String> keys = new ArrayList<>(tableModel.getTableNumAndTimerMap().keySet());
        String key = keys.get(pos);

        holder.tableNr.setText(key);
        holder.timer.setText(getElapsedTime(tableModel.getTableNumAndTimerMap().get(key)));

        Long l = tableModel.getTableNumAndStatus().get(key);
        if (l == 0) {
            holder.cardView.setCardBackgroundColor(Color.LTGRAY);
        } else if (l == 1) {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        } else if (l == 2) {
            holder.cardView.setCardBackgroundColor(Color.RED);
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                holder.timer.setText(getElapsedTime(tableModel.getTableNumAndTimerMap().get(key)));
                handler.postDelayed(this, 1000); // Update the timer every second
            }
        };
        handler.post(runnable);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onItemClick(Integer.parseInt(key.substring(1)));
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
