package com.example.mitarbeiterverwaltung;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    MitarbeiterVerwalten context;
    ArrayList<User> list;
    String restaurantId;
    DatabaseReference dbRef;

    public MyAdapter(MitarbeiterVerwalten context, ArrayList<User> list, String restaurantId) {
        this.context = context;
        this.list = list;
        this.restaurantId = restaurantId;
        this.dbRef = FirebaseDatabase.getInstance().getReference("Schluessel").child(restaurantId);
    }

    public void deleteEmployee(String employeeId, final List<User> employees) {
        dbRef.child(employeeId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                renumberEmployees(employees);
            }
        });
    }

    private void renumberEmployees(final List<User> employees) {
        final DatabaseReference reference = dbRef;

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int index = 0; // Start from the first entry

                for (DataSnapshot employeeSnapshot : dataSnapshot.getChildren()) {
                    String oldEmployeeId = employeeSnapshot.getKey();
                    User oldEmployeeData = employeeSnapshot.getValue(User.class);

                    if (oldEmployeeData != null) {
                        dbRef.child(oldEmployeeId).removeValue(); // Delete the old numbered entry first

                        String newEmployeeId = "M" + String.format("%03d", index + 1);
                        User newEmployeeData = new User(
                                oldEmployeeData.getKey(),
                                oldEmployeeData.getName()
                        );

                        reference.child(newEmployeeId).setValue(newEmployeeData); // Then add the new numbered entry
                        index++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < list.size()) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (position >= 0 && position < list.size()) {
            User user = list.get(position);
            holder.mName.setText(user.getName());
            holder.mKey.setText(user.getKey());


            holder.fabDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.mName.getContext());
                    builder.setTitle("Sind sie sicher?");
                    builder.setMessage("Dieser Vorgang kann nicht rückgängig gemacht werden.");

                    builder.setPositiveButton("Löschen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int position = holder.getAdapterPosition();
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference("Schluessel").child(restaurantId);
                            database.child("M" + String.format("%03d", position + 1)).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    removeItem(position);
                                }
                                deleteEmployee("M" + String.format("%03d", position + 1), list);
                            });
                        }
                    });

                    builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.show();


                }
            });

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.clickEmployee("M" + String.format("%03d", holder.getAdapterPosition() + 1));
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mName, mKey;
        FloatingActionButton fabDelete;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.tvName);
            mKey = itemView.findViewById(R.id.tvKey);
            mKey.setTextIsSelectable(true);
            fabDelete = itemView.findViewById(R.id.fabDelete);
            cardView = itemView.findViewById(R.id.employees_rv_cardview);
        }
    }
}