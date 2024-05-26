package com.example.login;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    InputValidator inputValidator;
    FloatingActionButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.registerButton);
        inputValidator = new InputValidator(this);
        back = findViewById(R.id.backButton);

        back.setOnClickListener(v -> {
            finish();
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());

                if (!inputValidator.validateInput(editTextEmail, "Email eingeben") ||
                        !inputValidator.validateInput(editTextPassword, "Passwort eingeben")) {
                    return;
                }

                if (!inputValidator.isPasswordValid(editTextPassword)) {
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Account erstellt",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplication(), Login.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        editTextEmail.setError("Diese E-Mail ist bereits registriert");
                                    } else {
                                        Log.w("AuthError", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(Register.this, "Authentifizierungsfehler: " + task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
            }
        });
    }
}