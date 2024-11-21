package com.example.enkoquest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.challenge.BlankActivity;
import com.example.enkoquest.challenge.CorrectWordActivity;

public class EngGmActivity extends AppCompatActivity {

    TextView wordMatching;
    TextView fillInTheBlank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eng_gm);

        wordMatching = findViewById(R.id.wordMatching);
        wordMatching.setOnClickListener(v -> {
            Intent intent = new Intent(this, CorrectWordActivity.class);
            startActivity(intent);
        });

        fillInTheBlank = findViewById(R.id.fillInTheBlank);
        fillInTheBlank.setOnClickListener(v -> {
            Intent intent = new Intent(this, BlankActivity.class);
            startActivity(intent);
        });



    }
}