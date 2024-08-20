package com.example.guradtype.UI_Components;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guradtype.databinding.ActivityHelpBinding;

public class HelpActivity extends AppCompatActivity {
    private ActivityHelpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TextView back= binding.back;
        back.setOnClickListener(view -> back());
    }

    private void back() {
        Intent intent = new Intent(HelpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}