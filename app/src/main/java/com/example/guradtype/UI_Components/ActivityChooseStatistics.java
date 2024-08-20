package com.example.guradtype.UI_Components;


import com.example.guradtype.databinding.ChooseStaticActivityBinding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class ActivityChooseStatistics extends AppCompatActivity {
    private ChooseStaticActivityBinding binding;

    private String username;
    private static final String PREF_USERNAME = "username";
    private static final String PREFS_NAME = "GuardTypePrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChooseStaticActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(PREF_USERNAME, "default_username");

        TextView back = binding.back;
        back.setOnClickListener(view -> back());

        TextView frequentWords = binding.frequentWords;
        frequentWords.setOnClickListener(view -> openFrequentWords());

        TextView textViewKeyboardChanges  = binding.textViewKeyboardChanges;
        textViewKeyboardChanges.setOnClickListener(view -> opentKeyboardChanges());

        TextView textViewOffensiveHour  = binding.textViewOffensiveHour;
        textViewOffensiveHour.setOnClickListener(view -> openOffensiveHour());
    }

    private void openOffensiveHour() {
        Intent intent = new Intent(ActivityChooseStatistics.this, ActivityOffensiveHours.class);
        intent.putExtra(username, username);
        startActivity(intent);
        finish();
    }

    private void opentKeyboardChanges() {
        Intent intent = new Intent(ActivityChooseStatistics.this, KeyboardChangeStatistics.class);
        intent.putExtra(username, username);
        startActivity(intent);
        finish();

    }

    private void back() {
        Intent intent = new Intent(ActivityChooseStatistics.this, MainActivity.class);
        intent.putExtra(username, username);
        startActivity(intent);
        finish();


    }

    private void openFrequentWords() {
        Intent intent = new Intent(ActivityChooseStatistics.this, ActivityFrequentWordsStatistics.class);
        intent.putExtra(username, username);
        startActivity(intent);
        finish();
    }

}
