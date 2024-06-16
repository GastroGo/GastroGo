package com.gastro.manage;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gastro.database.Menu;
import com.gastro.login.BaseActivity;
import com.gastro.login.R;
import com.gastro.settings.SettingsModel;
import com.gastro.utility.AnimationUtil;
import com.gastro.utility.DropdownManager;
import com.gastro.utility.FirebaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ManageMenu extends BaseActivity {

    FloatingActionButton addButton, backButton;
    String restaurantId;
    private List<Menu> dishes;
    private DishAdapter dishAdapter;
    private FirebaseManager firebaseManager;
    private AlertDialog addDishDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_menu);

        initializeComponents();
        setupEventListeners();
        loadDishes();
    }

    private void initializeComponents() {
        addButton = findViewById(R.id.buttonAdd);
        backButton = findViewById(R.id.btn_back);
        restaurantId = getIntent().getStringExtra("restaurantId");   //Übergabe der Restaurant ID
        dishes = new ArrayList<>();
        dishAdapter = new DishAdapter(dishes, restaurantId);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dishAdapter);

        firebaseManager = new FirebaseManager(restaurantId);
    }

    private void setupEventListeners() {
        AnimationUtil.applyButtonAnimation(addButton, this, this::addDishes);

        DropdownManager dropdownManager = new DropdownManager(this, R.menu.dropdown_menu, R.id.imageMenu);
        dropdownManager.setupDropdown();

        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void addDishes() {
        addDishDialog = createAddDishDialog();
        addDishDialog.show();
    }

    private AlertDialog createAddDishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ManageMenu.this, R.style.RoundedDialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_dish, null);
        builder.setView(view);

        EditText dishName = view.findViewById(R.id.dish_name);
        EditText dishPrice = view.findViewById(R.id.dish_price);
        Button addDishButton = view.findViewById(R.id.add_dish_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        addDishButton.setOnClickListener(v1 -> {
            if (validateInputs(dishName, dishPrice)) {
                Menu gericht = createDish(dishName, dishPrice);
                firebaseManager.addDish(gericht, dishes, dishAdapter);
                addDishDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(v2 -> addDishDialog.dismiss());

        return builder.create();
    }

    private boolean validateInputs(EditText dishName, EditText dishPrice) {
        String name = dishName.getText().toString();
        String priceString = dishPrice.getText().toString();

        if (name.isEmpty() || priceString.isEmpty()) {
            Toast.makeText(ManageMenu.this, R.string.input_incomplete, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private Menu createDish(EditText dishName, EditText dishPrice) {
        String name = dishName.getText().toString();
        String priceString = dishPrice.getText().toString();

        String languageCode = SettingsModel.getInstance().getLanguageCode();

        NumberFormat format;
        if ("de".equals(languageCode)) {
            format = NumberFormat.getInstance(Locale.GERMANY);
        } else {
            format = NumberFormat.getInstance(Locale.ENGLISH);
        }
        double price;
        try {
            Number number = format.parse(priceString);
            price = (number != null) ? number.doubleValue() : 0.0;
        } catch (ParseException e) {
            e.printStackTrace();
            price = 0.0;
        }

        Map<String, Boolean> zutaten = new HashMap<>();
        zutaten.put("eier", false);
        zutaten.put("fleisch", false);
        zutaten.put("milch", false);

        return new Menu(name, price, null, zutaten);
    }

    private void loadDishes() {
        firebaseManager.loadDishes(dishes, dishAdapter);
    }
}