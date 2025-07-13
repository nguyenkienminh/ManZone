package com.example.man_zone.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.man_zone.R;

public class ProfileActivity extends BaseActivity {
    private TextView tvUserName, tvUserPhone, tvUserEmail, tvUserPoint;
    private Button btnLogout;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvUserName = findViewById(R.id.tvUserName);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPoint = findViewById(R.id.tvUserPoint);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);

        // Retrieve data from SharedPreferences

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userName = sharedPreferences.getString("name", "N/A");
        String userPhone = sharedPreferences.getString("phone", "N/A");
        String userEmail = sharedPreferences.getString("email", "N/A");
        int userPoints = sharedPreferences.getInt("accumulatedPoint", 0);

// Set data to views
        tvUserName.setText(userName);
        tvUserPhone.setText("Phone: " + userPhone);
        tvUserEmail.setText("Email: " + userEmail);
        tvUserPoint.setText("Points: " + userPoints);




        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLogout.setOnClickListener(v -> {
            // Clear saved data
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Show a toast message and navigate back to login
            Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });


    }
}