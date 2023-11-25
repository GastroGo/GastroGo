package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InputActivity extends AppCompatActivity {

    EditText userEditText, passwordEditText;
    Button loginButton;
    //test

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        userEditText = findViewById(R.id.user_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String user = userEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                Model model = Model.getInstance();
                model.setUser(user);
                model.setPassword(password);
                model.save(v.getContext());
                Intent intent = new Intent(v.getContext(), OutputActivity.class);
                startActivity(intent);
            }
        });

    }
}