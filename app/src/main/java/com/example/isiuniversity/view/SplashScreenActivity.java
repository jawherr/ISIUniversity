package com.example.isiuniversity.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.isiuniversity.R;
import com.example.isiuniversity.storage.SharedPrefManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(SharedPrefManager.getInstance(SplashScreenActivity.this).isLoggedIn()) {
                    startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                    finish();
                } else if(SharedPrefManager.getInstance(SplashScreenActivity.this).isLoggedOut()){
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, WelcomeScreenActivity.class));
                    finish();
                }
            }
        },2000);
    }
}