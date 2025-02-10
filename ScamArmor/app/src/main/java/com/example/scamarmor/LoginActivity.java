package com.example.scamarmor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String ADMIN_CODE = "adminpunya";

    private EditText adminCodeEditText;
    private Button adminLoginButton;
    private Button userLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        adminCodeEditText = findViewById(R.id.adminCodeEditText);
        adminLoginButton = findViewById(R.id.adminLoginButton);
        userLoginButton = findViewById(R.id.userLoginButton);

        adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode = adminCodeEditText.getText().toString().trim();
                if (TextUtils.isEmpty(enteredCode)) {
                    Toast.makeText(LoginActivity.this, "Please enter the admin code", Toast.LENGTH_SHORT).show();
                } else if (enteredCode.equals(ADMIN_CODE)) {
                    // Admin login successful
                    loginSuccessful(true);
                } else {
                    // Incorrect admin code
                    Toast.makeText(LoginActivity.this, "Incorrect admin code. Please try again.", Toast.LENGTH_SHORT).show();
                    adminCodeEditText.setText("");
                }
            }
        });

        userLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // User login (without password check)
                loginSuccessful(false);
            }
        });
    }

    private void loginSuccessful(boolean isAdmin) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("isLoggedIn", true);
        intent.putExtra("isAdmin", isAdmin);
        startActivity(intent);
        finish();
    }
}
