package com.example.Bestellungen;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DBKlassen.GerichteModel;
import com.example.DBKlassen.TablelistModel;
import com.example.login.R;
import com.example.qrcodegenerator.Gericht;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.widget.Toast;

public class RV_Adapter_Bestellungen extends RecyclerView.Adapter<RV_Adapter_Bestellungen.ViewHolder>{

    int tableNumber;
    String restaurantID;
    TablelistModel tableListO = TablelistModel.getInstance();
    GerichteModel gerichteListeO = GerichteModel.getInstance();
    ArrayList<String[]> tableOrders = new ArrayList<String[]>();

    public RV_Adapter_Bestellungen(int tableNumber, String restaurantID){
        this.tableNumber = tableNumber;
        this.restaurantID = restaurantID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_bestellung, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;
        holder.dishName.setText(tableOrders.get(pos)[1] + "x " + gerichteListeO.getGerichtName(tableOrders.get(pos)[0]));

        if (tableListO.getBestellungsFilter() == 1){
            holder.checkBox.setChecked(false);
        } else if (tableListO.getBestellungsFilter() == 2) {
            holder.checkBox.setChecked(true);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetOrder(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        int counter = 0;
        tableOrders.clear();

        if (tableListO.getBestellungsFilter() == 1){
            for (Map.Entry<String, Integer> entry : tableListO.getTischeArray()[tableNumber-1].getBestellungen().entrySet()) {
                //restaurantTische.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                if(entry.getValue() != 0){
                    tableOrders.add(new String[]{entry.getKey(), String.valueOf(entry.getValue())});
                    counter ++;
                }
            }
            Log.i("order", "Seite 1");
        }else if (tableListO.getBestellungsFilter() == 2){
            for (Map.Entry<String, Integer> entry : tableListO.getTischeArray()[tableNumber-1].getGeschlosseneBestellungen().entrySet()) {
                //restaurantTische.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                if(entry.getValue() != 0){
                    tableOrders.add(new String[]{entry.getKey(), String.valueOf(entry.getValue())});
                    counter ++;
                }
            }
            Log.i("order", "Seite 2");
        }

        return counter;
    }

    private void resetOrder(int indexOfOrder) {
        Log.i("order", String.format("%03d", tableNumber));
        DatabaseReference bestellungenRef = FirebaseDatabase.getInstance()
                .getReference("Restaurants")
                .child(restaurantID)
                .child("tische")
                .child("T" + String.format("%03d", tableNumber));

        String gericht = tableOrders.get(indexOfOrder)[0];

        if (tableListO.getBestellungsFilter() == 1){
            int numberClosed = tableListO.getTischeArray()[tableNumber-1].getGeschlosseneBestellungen() .get(gericht);
            bestellungenRef.child("geschlosseneBestellungen").child(gericht).setValue(numberClosed + Integer.valueOf(tableOrders.get(indexOfOrder)[1]));
            bestellungenRef.child("bestellungen").child(gericht).setValue(0);
        } else if (tableListO.getBestellungsFilter() == 2) {
            int numberClosed = tableListO.getTischeArray()[tableNumber-1].getBestellungen() .get(gericht);
            bestellungenRef.child("bestellungen").child(gericht).setValue(numberClosed + Integer.valueOf(tableOrders.get(indexOfOrder)[1]));
            bestellungenRef.child("geschlosseneBestellungen").child(gericht).setValue(0);
        }

        Log.i("order", String.valueOf(tableListO.getBestellungsFilter()));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView dishName;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.dishName = itemView.findViewById(R.id.RV_TV_DishName);
            this.checkBox = itemView.findViewById(R.id.RV_CB_CheckBox);
        }


    }
}
