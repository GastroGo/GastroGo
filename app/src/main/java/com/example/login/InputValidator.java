package com.example.login;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class InputValidator {

    private Context context;
    private boolean requireCaseSensitivity = false;     //schaltet Notwendigkeit für Groß-/Kleinbuchstaben aus/an

    public InputValidator(Context context) {
        this.context = context;
    }

    public boolean validateInput(TextInputEditText editText, String errorMessage) {
        String input = editText.getText().toString();
        if (TextUtils.isEmpty(input)) {
            editText.setError(errorMessage);
            return false;
        }
        return true;
    }

    public boolean isNumeric(String str, String errorMessage) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean isPasswordValid(TextInputEditText editTextPassword) {
        String password = editTextPassword.getText().toString();
        if (password.length() < 6) {
            editTextPassword.setError("Passwort muss mindestens 6 Zeichen lang sein");
            return false;
        }

        boolean hasDigit = false;
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            }

            if (hasDigit && (!requireCaseSensitivity || (hasUpperCase && hasLowerCase))) {
                return true;
            }
        }

        editTextPassword.setError("Passwort muss mindestens eine Ziffer, einen Großbuchstaben und einen Kleinbuchstaben enthalten");
        return false;
    }
}