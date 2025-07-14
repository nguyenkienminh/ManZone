package com.example.man_zone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.man_zone.ApiClient.ApiClient;
import com.example.man_zone.Interfaces.CustomerService;
import com.example.man_zone.Model.CustomerModel;
import com.example.man_zone.Model.RegisterResponse;
import com.example.man_zone.databinding.ActivityRegisterBinding; // Thêm dòng này
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity {
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
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

        binding.textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = binding.editTextFirstName.getText().toString().trim();
                String lastName = binding.editTextLastName.getText().toString().trim();
                String email = binding.editTextEmail.getText().toString().trim();
                String phone = binding.editTextPhone.getText().toString().trim();
                String password = binding.editTextPassword.getText().toString().trim();
                String address = binding.editTextAddress.getText().toString().trim();

                // Validate non-empty fields
                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                        phone.isEmpty() || password.isEmpty() || address.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate email format
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate phone number is exactly 10 digits
                if (phone.length() != 10 || !phone.matches("\\d+")) {
                    Toast.makeText(RegisterActivity.this, "Please enter a valid 10-digit phone number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a CustomerModel with input values
                CustomerModel customer = new CustomerModel(
                        firstName,
                        lastName,
                        password,
                        phone,
                        email,
                        address
                );

                // Create a CustomerService instance and make the request
                CustomerService customerService = ApiClient.getClient().create(CustomerService.class);
                Call<RegisterResponse> call = customerService.addCustomer(customer);

                call.enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            RegisterResponse registerResponse = response.body();

                            if (registerResponse.isSuccess()) {
                                // Registration succeeded
                                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Registration failed, show the error message from server
                                String errorMessage = registerResponse.getMessage();
                                Toast.makeText(RegisterActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }
}
