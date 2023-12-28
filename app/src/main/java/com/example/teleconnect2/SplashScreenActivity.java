package com.example.teleconnect2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Define a delay for the splash screen
        int SPLASH_SCREEN_TIMEOUT = 3000; // 3 seconds

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity after the delay
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                finish(); // Close the splash screen activity
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }
}

