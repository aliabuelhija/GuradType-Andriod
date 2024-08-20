package com.example.guradtype.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.example.guradtype.API.ApiService;
import com.example.guradtype.API.Requests.FirstActivationRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.guradtype.API.Requests.KeyboardChangeRequest;
import com.example.guradtype.API.Responses.FirstActivationResponse;
import com.example.guradtype.API.Responses.KeyboardChangeResponse;
import com.example.guradtype.API.RetrofitClient;

public class KeyboardObserver extends ContentObserver {
    private static final String TAG = "KeyboardObserver";
    private static final long CHECK_INTERVAL = 2000; // 2 seconds

    private final Context context;
    private final String guardTypeKeyboardId;
    private final Handler handler;
    private final Runnable checkKeyboardRunnable;
    private String lastKeyboardId = "";

    public KeyboardObserver(Handler handler, Context context, String guardTypeKeyboardId) {
        super(handler);
        this.context = context;
        this.guardTypeKeyboardId = guardTypeKeyboardId;
        this.handler = handler;

        this.checkKeyboardRunnable = new Runnable() {
            @Override
            public void run() {
                checkCurrentKeyboard();
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        };

        initialCheck();

        handler.post(checkKeyboardRunnable);
    }

    private void initialCheck() {
        String currentKeyboard = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        Log.d(TAG, "Initial switch to " + currentKeyboard);
        SharedPreferences sharedPreferences = context.getSharedPreferences("GuardTypePrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "default_username");
        sendFirstActivationRequest(username);
        lastKeyboardId = currentKeyboard;

    }

    private void sendFirstActivationRequest(String username) {
        FirstActivationRequest request = new FirstActivationRequest(username);

        ApiService apiService = RetrofitClient.getApiService();
        Call<FirstActivationResponse> call = apiService.firstActivation(request);
        call.enqueue(new Callback<FirstActivationResponse>() {
            @Override
            public void onResponse(Call<FirstActivationResponse> call, Response<FirstActivationResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "First activation request successful: " + response.body().toString());
                } else {
                    Log.d(TAG, "First activation request failed: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<FirstActivationResponse> call, Throwable t) {
                Log.d(TAG, "First activation request failed: " + t.getMessage());
            }
        });
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        checkCurrentKeyboard();
    }

    private void checkCurrentKeyboard() {
        String currentKeyboard = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        if (!currentKeyboard.equals(lastKeyboardId)) {
            if (!lastKeyboardId.isEmpty()) {
                keyboardChange(lastKeyboardId, currentKeyboard);
                if (currentKeyboard.equals(guardTypeKeyboardId)) {
                    Log.d(TAG, "Changed to GuardType from: " + lastKeyboardId);
                }
                sendKeyboardChangeRequest(lastKeyboardId, currentKeyboard);
            }
            lastKeyboardId = currentKeyboard;
        }
    }

    private void sendKeyboardChangeRequest(String oldKeyboard, String newKeyboard) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("GuardTypePrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "default_username");

        KeyboardChangeRequest request = new KeyboardChangeRequest(username, oldKeyboard, newKeyboard);

        ApiService apiService = RetrofitClient.getApiService();
        Call<KeyboardChangeResponse> call = apiService.keyboardChange(request);
        call.enqueue(new Callback<KeyboardChangeResponse>() {
            @Override
            public void onResponse(Call<KeyboardChangeResponse> call, Response<KeyboardChangeResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Keyboard change request successful: " + response.body().toString());
                } else {
                    Log.d(TAG, "Keyboard change request failed: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<KeyboardChangeResponse> call, Throwable t) {
                Log.d(TAG, "Keyboard change request failed: " + t.getMessage());
            }
        });
    }
    private void keyboardChange(String fromKeyboard, String toKeyboard) {
        Log.d(TAG, "The keyboard has been changed from " + fromKeyboard + " to " + toKeyboard);
    }

    public void stopObserving() {
        handler.removeCallbacks(checkKeyboardRunnable);
    }
}
