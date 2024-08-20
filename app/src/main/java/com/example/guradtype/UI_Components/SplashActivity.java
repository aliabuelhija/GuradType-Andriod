package com.example.guradtype.UI_Components;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guradtype.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 2000; // 2 seconds

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView welcomeText = findViewById(R.id.welcome_text); // זהו ה-ID של ה-TextView שלך

        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.5f, 1.5f,
                0.5f, 1.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setRepeatCount(ScaleAnimation.INFINITE);
        scaleAnimation.setRepeatMode(ScaleAnimation.REVERSE);

        welcomeText.startAnimation(scaleAnimation);

        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(SplashActivity.this, ActivityLogin.class);
            startActivity(mainIntent);
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}
