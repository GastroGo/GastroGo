package com.example.mitarbeiterverwaltung;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;

public class MitarbeiterVerwalten extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    MyAdapter myadapter;
    ArrayList<User> list;
    HashSet<String> keysSet; // HashSet to store keys
    Button mAnlegen;
    Dialog dialog;
    Button mErstellen;
    EditText etName;
    public long count;
    TextView tvName, tvKey;
    FloatingActionButton back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mitarbeiter_verwalten);
        String restaurantId = getIntent().getStringExtra("restaurantId");

        dialog = new Dialog(MitarbeiterVerwalten.this);
        dialog.setContentView(R.layout.m_anlegen);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.m_anlegen_bg));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setDimAmount(0.5f);

        back = findViewById(R.id.fabBack);
        etName = dialog.findViewById(R.id.etName);
        mAnlegen = findViewById(R.id.mAnlegen);
        recyclerView = findViewById(R.id.mListe);
        database = FirebaseDatabase.getInstance().getReference("Schluessel").child(restaurantId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        mErstellen = dialog.findViewById(R.id.mErstellen);

        mErstellen.setOnClickListener(v -> {
            String nM = "M" + String.format("%03d", count+1);
            database.child(nM).child("name").setValue(etName.getText().toString());
            database.child(nM).child("key").setValue(generateKey());
            dialog.dismiss();
            etName.setText("");
        });

        back.setOnClickListener(v -> {
            finish();
        });

        mAnlegen.setOnClickListener(v -> {
            dialog.show();
        });

        list = new ArrayList<>();
        keysSet = new HashSet<>(); // Initialize the HashSet
        myadapter = new MyAdapter(this, list, restaurantId);
        recyclerView.setAdapter(myadapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = snapshot.getChildrenCount();
                list.clear(); // Clear the list
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    list.add(user); // Add the updated data to the list
                }
                myadapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public static String generateKey() {
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder();
        Model model = new Model();

        for (int i = 0; i < model.getKeyLength(); i++) {
            int randomIndex = random.nextInt(model.getKeyCharacters().length());
            char randomChar = model.getKeyCharacters().charAt(randomIndex);
            key.append(randomChar);
        }

        return key.toString();
    }
}