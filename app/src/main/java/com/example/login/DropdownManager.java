package com.example.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;

public class DropdownManager {

    private Context context;
    private int menuId;
    private int imageViewId;

    public DropdownManager(Context context, int menuId, int imageViewId) {
        this.context = context;
        this.menuId = menuId;
        this.imageViewId = imageViewId;
    }

    public void setupDropdown() {
        ImageView imageMenu = ((Startseite) context).findViewById(imageViewId);
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(menuId, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.logout) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(context, Login.class);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }
}