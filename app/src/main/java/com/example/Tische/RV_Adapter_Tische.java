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
import com.example.login.Tisch;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;


public class RV_Adapter_Tische extends RecyclerView.Adapter<RV_Adapter_Tische.ViewHolder> {

    private static final TablelistModel tableListO = TablelistModel.getInstance();
    private final Tisch[] tischeArray = TablelistModel.getInstance().getTischeArray();
    private final OnItemClickListener onItemClickListener;
    private String restaurantID;
    private int numberOfGerichte;
    private static Handler handler = new Handler();


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
        // Cancel any existing updates to the timer TextView
        handler.removeCallbacksAndMessages(holder);

        // Schedule the first update
        holder.updateTimer.run();
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

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);

        // Cancel any updates to the timer TextView when the view is recycled
        handler.removeCallbacksAndMessages(holder);
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


    public static String getCurrentTime(){
        ZonedDateTime now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = ZonedDateTime.now(ZoneId.of("Europe/Berlin"));
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        }
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentTime = now.format(formatter);
        }
        return currentTime;
    }

public static String getElapsedTime(String startingTime){
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    Date date1 = null;
    Date date2 = null;
    try {
        date1 = format.parse(startingTime);
        date2 = format.parse(getCurrentTime());
    } catch (ParseException e) {
        e.printStackTrace();
    }

    long difference = date2.getTime() - date1.getTime();
    if (difference < 0) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date2);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        date2 = calendar.getTime();
        difference = date2.getTime() - date1.getTime();
    }

    long seconds = (difference / 1000) % 60;
    long minutes = (difference / (1000 * 60)) % 60;
    long hours = (difference / (1000 * 60 * 60)) % 24;

    return String.format("%02d:%02d", minutes, seconds);
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


        private Runnable updateTimer = new Runnable() {
            @Override
            public void run() {

                int pos = getAdapterPosition();

                if (tableListO.getNumberOrders(pos+1) == 0){
                    timer.setText("-");
                }
                else {
                    timer.setText(getElapsedTime(tableListO.getTischeArray()[pos].getLetzteBestellung()));
                }

                handler.postDelayed(this, 1000);
            }
        };
    }
}
