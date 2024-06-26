package com.gastro.homepage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.gastro.login.R;
import com.gastro.orderview.OrderView;
import com.gastro.settings.SettingsModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationManager {

    public static void setupBottomNavigationView(BottomNavigationView bottomNavigationView, Context context) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menuHome) {
                    Intent intent = new Intent(context, Homepage.class);
                    if (context instanceof Activity) {
                        intent.putExtra("employee", ((Activity) context).getIntent().getBooleanExtra("employee", false));
                    }
                    context.startActivity(intent);
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                    return true;
                } else if (id == R.id.menuOrderView) {
                    Intent intent = new Intent(context, OrderView.class);
                    intent.putExtra("restaurantId", SettingsModel.getInstance().getRestaurantId());
                    intent.putExtra("tableId", SettingsModel.getInstance().getTableNr());
                    context.startActivity(intent);
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                    return true;
                }
                return false;
            }
        });
    }

}