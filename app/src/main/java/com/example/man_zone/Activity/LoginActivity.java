package com.example.man_zone.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.man_zone.ApiClient.ApiClient;
import com.example.man_zone.Interfaces.AuthorizeService;
import com.example.man_zone.Model.LoginRequest;
import com.example.man_zone.Model.LoginResponse;
import com.example.man_zone.Model.UserModel;
import com.example.man_zone.Utils.PrefsHelper;
import com.example.man_zone.databinding.ActivityLoginBinding; // Thêm dòng này

import org.json.JSONObject;

import java.io.Console;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        binding.textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get email and password from EditText fields
                String email = binding.editTextEmail.getText().toString().trim();
                String password = binding.editTextPassword.getText().toString().trim();

                // Validate inputs
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Please enter a valid email address.", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                // Create an instance of AuthorizeService
                AuthorizeService authorizeService = ApiClient.getClient().create(AuthorizeService.class);

                // Call the login API
                LoginRequest request = new LoginRequest(email, password);
                Call<LoginResponse> call = authorizeService.login(request);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse loginResponse = response.body();

                            if (loginResponse.isSuccess()) {
                                String token = loginResponse.getData().getToken();
                                String email = getEmailFromToken(token);
                                String userId = getUserIdFromToken(token);
                                Log.d("LOGIN_SUCCESS", "Token: " + (token != null ? "present" : "null"));
                                Log.d("LOGIN_SUCCESS", "Token length: " + (token != null ? token.length() : 0));
                                Log.d("LOGIN_SUCCESS",
                                        "Token starts with: "
                                                + (token != null && token.length() > 10 ? token.substring(0, 10) + "..."
                                                        : token));
                                Log.d("LOGIN_SUCCESS", "Email: " + email);
                                Log.d("LOGIN_SUCCESS", "User ID: " + userId);

                                // Save login info using utility
                                PrefsHelper.saveLoginInfo(LoginActivity.this, token, email, userId);

                                // Verify saved data
                                boolean isLoggedIn = PrefsHelper.isLoggedIn(LoginActivity.this);
                                Log.d("LOGIN_VERIFY", "Is logged in after save: " + isLoggedIn);
                                String savedToken = PrefsHelper.getToken(LoginActivity.this);
                                Log.d("LOGIN_VERIFY", "Saved token matches: " + token.equals(savedToken));

                                // Show a success message and navigate to the main activity
                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class); // Change to your
                                                                                                    // main activity
                                startActivity(intent);
                                finish();
                            } else {
                                // Login failed
                                Toast.makeText(LoginActivity.this, "Login failed: " + loginResponse.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public static String getEmailFromToken(String token) {
        if (token == null)
            return null; // Tránh null

        try {
            String[] parts = token.split("\\."); // Phải dùng \\.

            if (parts.length != 3)
                return null; // JWT có 3 phần

            String payload = parts[1];
            byte[] decodedBytes = android.util.Base64.decode(payload, android.util.Base64.URL_SAFE);
            String decodedPayload = new String(decodedBytes, "UTF-8");

            JSONObject jsonObject = new JSONObject(decodedPayload);
            return jsonObject.optString("email");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getUserIdFromToken(String token) {
        if (token == null)
            return null;

        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3)
                return null;

            String payload = parts[1];
            byte[] decodedBytes = android.util.Base64.decode(payload, android.util.Base64.URL_SAFE);
            String decodedPayload = new String(decodedBytes, "UTF-8");

            JSONObject jsonObject = new JSONObject(decodedPayload);

            // Try different possible field names for user ID
            String userId = jsonObject.optString("userId", null);
            if (userId == null || userId.isEmpty()) {
                userId = jsonObject.optString("customerId", null);
            }
            if (userId == null || userId.isEmpty()) {
                userId = jsonObject.optString("sub", null); // JWT standard subject field
            }
            if (userId == null || userId.isEmpty()) {
                userId = jsonObject.optString("id", null);
            }

            return userId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
