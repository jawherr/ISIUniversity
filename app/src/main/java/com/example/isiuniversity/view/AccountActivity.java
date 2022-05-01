package com.example.isiuniversity.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.isiuniversity.R;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        SharedPreferences sharedPref = getSharedPreferences("ISIUniversityPerf", Context.MODE_PRIVATE);
        int auth = sharedPref.getInt("auth",0);
        if(auth==0)
        {
            Intent start = new Intent(this, LoginActivity.class);
            startActivity(start);
            finish();
        }
    }

    public void logout(View view) {
        SharedPreferences sharedPref = getSharedPreferences("ISIUniversityPerf", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("auth", 0);
        editor.putString("username", "");
        editor.putString("password", "");
        editor.apply();
        Intent start = new Intent(AccountActivity.this, LoginActivity.class);
        startActivity(start);
        finish();
    }
}
