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

import com.example.man_zone.ApiClient.ApiClient;
import com.example.man_zone.Interfaces.CustomerService;
import com.example.man_zone.Model.ProfileResponse;
import com.example.man_zone.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        // Init views
        tvUserName = findViewById(R.id.tvUserName);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPoint = findViewById(R.id.tvUserPoint);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);

        // Retrieve token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        // Call API to get profile
        CustomerService userService = ApiClient.getClient().create(CustomerService.class);
        Call<ProfileResponse> call = userService.getProfile("Bearer " + token);

        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse.UserData userData = response.body().getData();

                    tvUserName.setText(userData.getFirstName() + " " + userData.getLastName());
                    tvUserPhone.setText("Phone: " + userData.getPhoneNumber());
                    tvUserEmail.setText("Email: " + userData.getEmail());
                    tvUserPoint.setText("Address: " + userData.getAddress());
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to get profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Back button event
        btnBack.setOnClickListener(v -> finish());

        // Logout button event
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
