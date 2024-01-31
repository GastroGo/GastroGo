package com.example.Tische;

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
import com.example.login.Tisch;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.Duration;
import java.time.LocalTime;


public class RV_Adapter_Tische extends RecyclerView.Adapter<RV_Adapter_Tische.ViewHolder> {

    private final TablelistModel tableListO = TablelistModel.getInstance();
    private final Tisch[] tischeArray = TablelistModel.getInstance().getTischeArray();
    private final OnItemClickListener onItemClickListener;
    private String restaurantID;
    private int numberOfGerichte;

    public RV_Adapter_Tische(OnItemClickListener onItemClickListener, String restaurantID) {
        this.onItemClickListener = onItemClickListener;
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
        int pos = position;
        holder.tableNr.setText("Tisch " + (pos + 1));
        if (tableListO.getNumberOrders(pos+1) == 0){
            holder.timer.setText("-");
        }
        else {
            holder.timer.setText(getElapsedTime(tableListO.getTischeArray()[pos].getLetzteBestellung()));
        }


        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAllOrders(pos + 1);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(pos + 1);
            }
        });
    }

    private void resetAllOrders(int tableNum){
        DatabaseReference bestellungenRef = FirebaseDatabase.getInstance()
                .getReference("Restaurants")
                .child(restaurantID)
                .child("tische")
                .child("T" + String.format("%03d", tableNum));


        Log.i("Gerichte", String.valueOf(numberOfGerichte));


        for(int x = 1; x <= numberOfGerichte; x++){
            String gericht = "G" + String.format("%03d", x);
            bestellungenRef.child("bestellungen").child(gericht).setValue(0);
        }

        for(int x = 1; x <= numberOfGerichte; x++){
            String gericht = "G" + String.format("%03d", x);
            bestellungenRef.child("geschlosseneBestellungen").child(gericht).setValue(0);
        }

    }

    public void setNumberOfGerichte(int numberOfGerichte){
        this.numberOfGerichte = numberOfGerichte;
    }


    public String getCurrentTime(){
        ZonedDateTime now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = ZonedDateTime.now(ZoneId.of("Europe/Berlin"));
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("HH:mm");
        }
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentTime = now.format(formatter);
        }
        return currentTime;
    }

    public String getElapsedTime(String startingTime){
        LocalTime startTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startTime = LocalTime.parse(startingTime, DateTimeFormatter.ofPattern("HH:mm"));
        }
        LocalTime now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalTime.parse(getCurrentTime(), DateTimeFormatter.ofPattern("HH:mm"));
        }
        Duration duration = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            duration = Duration.between(startTime, now);
        }
        long hours = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            hours = duration.toHours();
        }
        long minutes = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            minutes = duration.toMinutes() % 60;
        }
        return String.format("%02d:%02d", hours, minutes);
    }

    @Override
    public int getItemCount() {
        return tableListO.getNumberOfTables();
    }

    public interface OnItemClickListener {
        void onItemClick(int tableNumber);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tableNr;
        private final TextView timer;
        private final CheckBox checkBox;
        private final CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tableNr = itemView.findViewById(R.id.RV_TV_TableNr);
            this.timer = itemView.findViewById(R.id.RV_TV_Timer);
            this.checkBox = itemView.findViewById(R.id.RV_CB_CheckBoxTische);
            cardView = itemView.findViewById(R.id.RV_CardView);
        }
    }
}
