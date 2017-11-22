package com.project1.softwaresoluitons.xyz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    ImageView splashImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences user = getSharedPreferences("user", Context.MODE_PRIVATE);

        splashImageView = new ImageView(this);

        splashImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        splashImageView.setImageResource(R.drawable.logo);
        setContentView(splashImageView);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                change();

            }

        }, 4000);
    }

    public void change() {
        finish();
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }
}


