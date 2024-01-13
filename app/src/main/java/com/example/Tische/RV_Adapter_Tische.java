package com.example.Tische;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DBKlassen.TablelistModel;
import com.example.DBKlassen.Tische;
import com.example.login.R;

public class RV_Adapter_Tische extends RecyclerView.Adapter<RV_Adapter_Tische.ViewHolder> {

    private final TablelistModel tableListO = TablelistModel.getInstance();
    private final Tische[] tischeArray = TablelistModel.getInstance().getTischeArray();
    private final OnItemClickListener onItemClickListener;

    public RV_Adapter_Tische(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(pos + 1);
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
        private final LinearLayout cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tableNr = itemView.findViewById(R.id.RV_TV_TableNr);
            this.timer = itemView.findViewById(R.id.RV_TV_Timer);
            this.checkBox = itemView.findViewById(R.id.RV_CB_CheckBox);
            cardView = itemView.findViewById(R.id.RV_CardView);
        }
    }
}
