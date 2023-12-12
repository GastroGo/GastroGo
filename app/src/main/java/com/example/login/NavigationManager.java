package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class NavigationManager {

    public static void setupBottomNavigationView(BottomNavigationView bottomNavigationView, Context context) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menuAccount) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(context, Login.class);
                    context.startActivity(intent);
                    return true;
                } else if (id == R.id.menuHome) {
                    Intent intent = new Intent(context, Startseite.class);
                    context.startActivity(intent);
                    return true;
                }
                //Weitere If Anweisungen für andere Icons
                return false;
            }
        });
    }
}