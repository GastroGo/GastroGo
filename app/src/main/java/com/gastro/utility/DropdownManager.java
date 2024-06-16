package com.gastro.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.gastro.login.R;
import com.gastro.login.Welcome;
import com.gastro.settings.Settings;
import com.google.firebase.auth.FirebaseAuth;

public class DropdownManager {

    private final Context context;
    private final int menuId;
    private final int imageViewId;

    public DropdownManager(Context context, int menuId, int imageViewId) {
        this.context = context;
        this.menuId = menuId;
        this.imageViewId = imageViewId;
    }

    public void setupDropdown() {
        if (context instanceof Activity) {
            ImageView imageMenu = ((Activity) context).findViewById(imageViewId);
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
                                Intent intent = new Intent(context, Welcome.class);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                                return true;
                            } else if (id == R.id.settings) {
                                Intent intent = new Intent(context, Settings.class);
                                context.startActivity(intent);
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
}