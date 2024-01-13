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
import com.example.DBKlassen.Tische;
import com.example.login.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RV_Adapter_Tische extends RecyclerView.Adapter<RV_Adapter_Tische.ViewHolder> {

    private final TablelistModel tableListO = TablelistModel.getInstance();
    private final Tische[] tischeArray = TablelistModel.getInstance().getTischeArray();
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
                .child("T" + String.format("%03d", tableNum))
                .child("bestellungen");


        Log.i("Gerichte", String.valueOf(numberOfGerichte));


        String gericht;
        for(int x = 1; x <= numberOfGerichte; x++){
            gericht = "G" + String.format("%03d", x);
            bestellungenRef.child(gericht).setValue(0);
        }

    }

    public void setNumberOfGerichte(int numberOfGerichte){
        this.numberOfGerichte = numberOfGerichte;
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
