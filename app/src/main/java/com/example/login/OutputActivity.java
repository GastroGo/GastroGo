package com.example.login;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class OutputActivity extends AppCompatActivity {

    TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);

        welcomeTextView = findViewById(R.id.welcome_text_view);
        Model model = Model.getInstance();
        String user = model.getUser();
        welcomeTextView.setText("Welcome " + user + "!");
    }
}