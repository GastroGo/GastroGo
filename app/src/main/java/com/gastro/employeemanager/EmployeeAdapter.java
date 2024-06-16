package com.gastro.employeemanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gastro.login.R;
import com.gastro.manage.EmployeeManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.MyViewHolder> {

    EmployeeManager context;
    ArrayList<EmployeeItem> list;
    String restaurantId;
    DatabaseReference dbRef;
    private AlertDialog deleteEmployeeDialog;

    public EmployeeAdapter(EmployeeManager context, ArrayList<EmployeeItem> list, String restaurantId) {
        this.context = context;
        this.list = list;
        this.restaurantId = restaurantId;
        this.dbRef = FirebaseDatabase.getInstance().getReference("Schluessel").child(restaurantId);
    }

    public void deleteEmployee(String employeeId, final List<EmployeeItem> employees) {
        dbRef.child(employeeId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                renumberEmployees(employees);
            }
        });
    }

    private void renumberEmployees(final List<EmployeeItem> employees) {
        final DatabaseReference reference = dbRef;

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int index = 0; // Start from the first entry

                for (DataSnapshot employeeSnapshot : dataSnapshot.getChildren()) {
                    String oldEmployeeId = employeeSnapshot.getKey();
                    EmployeeItem oldEmployeeData = employeeSnapshot.getValue(EmployeeItem.class);

                    if (oldEmployeeData != null) {
                        assert oldEmployeeId != null;
                        dbRef.child(oldEmployeeId).removeValue(); // Delete the old numbered entry first

                        @SuppressLint("DefaultLocale") String newEmployeeId = "M" + String.format("%03d", index + 1);
                        EmployeeItem newEmployeeData = new EmployeeItem(
                                oldEmployeeData.getKey(),
                                oldEmployeeData.getName(),
                                oldEmployeeData.getArbeitsZeiten()
                        );

                        reference.child(newEmployeeId).setValue(newEmployeeData);
                        reference.child(newEmployeeId + "/UID").setValue(oldEmployeeData.getUID());// Then add the new numbered entry
                        index++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false);
        return new MyViewHolder(v);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < list.size()) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (position >= 0 && position < list.size()) {
            EmployeeItem user = list.get(position);
            holder.mName.setText(user.getName());
            holder.mKey.setText(user.getKey());


            holder.fabDelete.setOnClickListener(v -> {
                deleteEmployeeDialog = createDeleteEmployeeDialog(holder); // Initialize the dialog here
                deleteEmployeeDialog.show();
            });


            //noinspection deprecation
            holder.cardView.setOnClickListener(view -> context.clickEmployee("M" + String.format("%03d", holder.getAdapterPosition() + 1)));

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
    @SuppressLint("DefaultLocale")
    private AlertDialog createDeleteEmployeeDialog(MyViewHolder holder) {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.RoundedDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_delete_item, null);
        builder.setView(view);

        TextView title = view.findViewById(R.id.confirm);
        Button delete_button = view.findViewById(R.id.delete_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        title.setText(context.getString(R.string.delete_employee));
        cancelButton.setOnClickListener(v -> deleteEmployeeDialog.dismiss());
        delete_button.setOnClickListener(v -> {
            //noinspection deprecation
            int position = holder.getAdapterPosition();
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("Schluessel").child(restaurantId);
            database.child("M" + String.format("%03d", position + 1)).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    removeItem(position);
                }
                deleteEmployee("M" + String.format("%03d", position + 1), list);
            });
            deleteEmployeeDialog.dismiss();
        });
        return builder.create(); // Return the dialog
    }
}