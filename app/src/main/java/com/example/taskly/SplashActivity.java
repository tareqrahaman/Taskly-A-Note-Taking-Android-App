package com.example.taskly;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Make Full Screen (Hide Status Bar)
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_splash);

        // 2. Setup Simple Animations (Optional but recommended)
        // You can reuse your existing 'fab_open' or create a 'fade_in'
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.label_open);

        ImageView logo = findViewById(R.id.imgLogo);
        //TextView text = findViewById(R.id.tvAppName);

        logo.startAnimation(fadeIn);
        //text.startAnimation(fadeIn);

        // 3. Delayed Navigation
        // Runs the code inside run() after 2500 milliseconds (2.5 seconds)
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start Main Activity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                // Close Splash Activity so user can't go back to it
                finish();
            }
        }, 2500);
    }
}