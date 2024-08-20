package com.example.guradtype.UI_Components;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guradtype.API.ApiService;
import com.example.guradtype.API.Requests.LoginRequest;
import com.example.guradtype.API.Responses.LoginResponse;
import com.example.guradtype.API.RetrofitClient;
import com.example.guradtype.databinding.ActivityLoginBinding;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLogin extends AppCompatActivity {

    private static final String PREFS_NAME = "GuardTypePrefs";
    private static final String PREF_IS_LOGGED_IN = "isLoggedIn";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PACKAGE_NAME = "com.example.guradtype";

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearPreferences();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textView6.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityLogin.this, ActivitySignup.class);
            startActivity(intent);
        });

        binding.btnSignUp.setOnClickListener(view -> {
            String username = Objects.requireNonNull(binding.inputUsername.getText()).toString();
            String password = Objects.requireNonNull(binding.inputPassword.getText()).toString();
            login(username, password);
        });
    }
//    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//    SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean(PREF_IS_LOGGED_IN, true);
//        editor.putString(PREF_USERNAME, username);
//        editor.putString(PREF_PASSWORD, password);
//        editor.apply();
    private void login(String username, String password) {
        ApiService apiService = RetrofitClient.getApiService();
        LoginRequest loginRequest = new LoginRequest(username, password);

        Call<LoginResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    clearPreferences();
                    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(PREF_IS_LOGGED_IN, true);
                    editor.putString(PREF_USERNAME, username);
                    editor.putString(PREF_PASSWORD, password);
                    editor.apply();

                    Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
                    intent.putExtra(PREF_USERNAME, username);
                    intent.putExtra(PREF_PASSWORD, password);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ActivityLogin.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Toast.makeText(ActivityLogin.this, "Login failed. Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOutKbEnabled() && !isUserLoggedIn()) {
            promptForSystemKeyboard();
        }
    }

    private void promptForSystemKeyboard() {
        Toast.makeText(this, "Please switch to the system keyboard for logging in", Toast.LENGTH_LONG).show();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showInputMethodPicker();
        }
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_IS_LOGGED_IN, false);
    }

    private boolean isOutKbEnabled() {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> inputMethods = imm.getEnabledInputMethodList(); // Get a list of all enabled input methods on the device

        for (InputMethodInfo inputMethod : inputMethods) {
            if (inputMethod.getPackageName().equals(PACKAGE_NAME)) {
                return true; // Return true if our custom keyboard's package name is found in the list of enabled input methods
            }
        }
        return false; // Return false if our custom keyboard is not enabled
    }

    private void clearPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
