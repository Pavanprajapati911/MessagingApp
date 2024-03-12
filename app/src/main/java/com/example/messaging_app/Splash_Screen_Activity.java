package com.example.messaging_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Splash_Screen_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                boolean loggedIn =  sharedPreferences.getBoolean("LoggedInOrNot",false);

                if (loggedIn){
                    Intent intent = new Intent(Splash_Screen_Activity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }else {
                    Intent intent = new Intent(Splash_Screen_Activity.this,Enter_Mobile_No_Activity.class);
                    startActivity(intent);
                    finish();
                }

            }
        },3000);

    }
}