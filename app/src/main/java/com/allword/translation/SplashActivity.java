package com.allword.translation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final DatabaseHelper2 dataBaseHelper = new DatabaseHelper2(getApplicationContext());
        try {
            dataBaseHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("123321", ""+e.getMessage());
        }

        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            //Do something after 100ms

            startActivity(new Intent(this, MainActivity2.class));
            finish();
            //  autoUpdate();

        }, 1500);

    }

}