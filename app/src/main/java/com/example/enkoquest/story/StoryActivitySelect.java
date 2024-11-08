package com.example.enkoquest.story;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.LoginIndexActivity;
import com.example.enkoquest.R;
import com.example.enkoquest.user.FindIdActivity;

public class StoryActivitySelect extends AppCompatActivity implements View

        .OnClickListener {

    Button btn1, btn2;
    ImageButton imageButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_story_select);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btn1 = findViewById(R.id.buttonChalKor);
        btn2 = findViewById(R.id.buttonChalEng);
        imageButtonBack = findViewById(R.id.imageButtonBack);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        imageButtonBack.setOnClickListener(view -> navigateToMainActivity());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonChalKor) {
            Intent intent = new Intent(this, StoryActivityKor.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.buttonChalEng) {
            Intent intent = new Intent(this, StoryActivityEng.class);
            startActivity(intent);
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, LoginIndexActivity.class); // 메인 액티비티로 이동
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }
}