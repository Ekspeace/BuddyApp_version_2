package com.ekspeace.buddyapp_v2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.ekspeace.buddyapp_v2.R;

public class Splash extends AppCompatActivity {
    private static int SPLASH_SCREEN = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed((Runnable) () -> {
            Intent intent = new Intent(this, StartUp.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);
    }
}