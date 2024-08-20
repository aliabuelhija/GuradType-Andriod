package com.example.guradtype.UI_Components;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guradtype.API.ApiService;
import com.example.guradtype.API.Requests.SignUpRequest;
import com.example.guradtype.API.Responses.SignUpResponse;
import com.example.guradtype.API.RetrofitClient;
import com.example.guradtype.databinding.ActivitySignupBinding;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySignup extends AppCompatActivity {
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginText.setOnClickListener(view -> {
            Intent intent = new Intent(ActivitySignup.this, ActivityLogin.class);
            Toast.makeText(ActivitySignup.this, "Navigating to Login Screen", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });
        binding.btnSignUp.setOnClickListener(view -> {
            String username = Objects.requireNonNull(binding.inputUsername.getText()).toString();
            String email = Objects.requireNonNull(binding.inputEmail.getText()).toString();
            String password = Objects.requireNonNull(binding.inputPassword.getText()).toString();
            signup(username, email, password);
        });
    }

    private void signup(String username, String email, String password) {
        ApiService apiService = RetrofitClient.getApiService();
        SignUpRequest signUpRequest = new SignUpRequest(username, email, password);

        Call<SignUpResponse> call = apiService.signup(signUpRequest);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ActivitySignup.this, "Sign up successful! You will back to login page", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ActivitySignup.this, ActivityLogin.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ActivitySignup.this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            System.err.println("Error body: " + errorBody);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ActivitySignup.this, "Sign up failed. Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
