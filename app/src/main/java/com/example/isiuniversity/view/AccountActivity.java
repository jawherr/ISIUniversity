package com.example.isiuniversity.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.isiuniversity.R;
import com.mikhaellopez.circularimageview.CircularImageView;

public class AccountActivity extends AppCompatActivity {

    private String username;
    private CircularImageView circularImageView;
    private TextView tv_username,tv_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        circularImageView=findViewById(R.id.image_profile);
        tv_username=findViewById(R.id.tv_username);
        tv_email=findViewById(R.id.tv_email);
        SharedPreferences sharedPref = getSharedPreferences("ISIUniversityPerf", Context.MODE_PRIVATE);
        username = sharedPref.getString("username", null);
        tv_email.setText(getIntent().getStringExtra("email"));
        if(username.equals("agent")){
            circularImageView.setImageResource(R.drawable.agent);
        } else if(username.equals("enseignant")){
            circularImageView.setImageResource(R.drawable.enseignant);
        } else if(username.equals("etudiant")){
            circularImageView.setImageResource(R.drawable.etudiant);
        }
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

    public void back(View view) {
        finish();
    }
}
