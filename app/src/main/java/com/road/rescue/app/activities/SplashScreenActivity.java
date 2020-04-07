package com.road.rescue.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;
import com.road.rescue.app.R;

public class SplashScreenActivity extends AppCompatActivity {
    ProgressBar progressBar2;
    ImageView splashImage;
    MaterialTextView splashText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_splash_screen);
            splashImage = findViewById(R.id.splashImage);
            splashText = findViewById(R.id.splashText);
            progressBar2 = findViewById(R.id.progressBar2);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.transition);
            Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.transition2);
            // assigning animations to the widgets
            splashImage.startAnimation(animation);
            splashText.startAnimation(animation);
            progressBar2.startAnimation(animation2);

            // to manage the handler
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 3000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
