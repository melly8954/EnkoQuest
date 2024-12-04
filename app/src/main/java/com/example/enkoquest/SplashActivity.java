package com.example.enkoquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        View splashScreen = findViewById(R.id.splash_window);
        splashScreen.setOnClickListener(view -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        textView = findViewById(R.id.touch_to_continue);
        Animation blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink); // 애니메이션 불러옴
        textView.startAnimation(blinkAnimation); // 화면을 터치해주세요 ! 텍스트를 무한으로 반짝이게 만듦
    }
}