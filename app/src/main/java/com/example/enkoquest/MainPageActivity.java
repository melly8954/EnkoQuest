package com.example.enkoquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.challenge.ChallengeActivity;
import com.example.enkoquest.story.StoryActivity;

public class MainPageActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn01, btn02, btn03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn01 = findViewById(R.id.buttonMyPage);
        btn02 = findViewById(R.id.buttonChallengeMode);
        btn03 = findViewById(R.id.buttonStoryMode);

        btn01.setOnClickListener(this);
        btn02.setOnClickListener(this);
        btn03.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonMyPage) {
            Intent intent = new Intent(MainPageActivity.this, MyPageActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.buttonChallengeMode) {
            Intent intent = new Intent(MainPageActivity.this, ChallengeActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.buttonStoryMode) {
            Intent intent = new Intent(MainPageActivity.this, StoryActivity.class);
            startActivity(intent);
        }
    }
}