package com.hospital.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hospital.serviceapp.R;
import com.hospital.serviceapp.database.DatabaseHelper;
import com.hospital.serviceapp.models.User;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin, btnAdminLogin;
    private TextView tvRegister, tvForgotPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(false);
            }
        });

        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(true);
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Please contact admin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(boolean mustBeAdmin) {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return;
        }

        User user = dbHelper.loginUser(username, password);
        if (user != null) {
            if (mustBeAdmin && !user.getRole().equals("admin")) {
                Toast.makeText(this, "Access denied. Not an admin account.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

            if (user.getRole().equals("admin")) {
                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
            } else {
                Intent intent = new Intent(LoginActivity.this, ServiceRequestActivity.class);
                intent.putExtra("user_id", user.getId());
                startActivity(intent);
            }
            finish();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }
}