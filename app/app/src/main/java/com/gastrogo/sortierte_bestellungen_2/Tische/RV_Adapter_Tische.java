package com.gastrogo.sortierte_bestellungen_2.Tische;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gastrogo.sortierte_bestellungen_2.DBKlassen.Tische;

import com.gastrogo.sortierte_bestellungen_2.DBKlassen.TablelistModel;
import com.gastrogo.sortierte_bestellungen_2.R;

public class RV_Adapter_Tische extends RecyclerView.Adapter<RV_Adapter_Tische.ViewHolder> {

    private final TablelistModel tableListO = TablelistModel.getInstance();
    private final Tische[] tischeArray = TablelistModel.getInstance().getTischeArray();
    private final OnItemClickListener onItemClickListener;

    public RV_Adapter_Tische(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RV_Adapter_Tische.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_tische, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RV_Adapter_Tische.ViewHolder holder, int position) {
        holder.tableNr.setText("Tisch " + (position + 1));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(holder.getAdapterPosition() + 1);
            }
        });
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
            this.checkBox = itemView.findViewById(R.id.RV_CB_CheckBox);
            cardView = itemView.findViewById(R.id.RV_CardView);
        }
    }
}
