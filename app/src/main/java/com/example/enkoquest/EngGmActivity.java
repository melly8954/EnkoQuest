package com.example.enkoquest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.enkoquest.challenge.BWriteActivity;
import com.example.enkoquest.challenge.BlankActivity;
import com.example.enkoquest.challenge.CorrectWordActivity;

public class EngGmActivity extends AppCompatActivity {

    TextView wordMatching;
    TextView fillInTheBlank;
    ImageButton imageButtonBack;
    TextView bwrite;

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

        bwrite = findViewById(R.id.bwrite);
        bwrite.setOnClickListener(v -> {
            Intent intent = new Intent(this, BWriteActivity.class);
            startActivity(intent);
        });

        imageButtonBack = findViewById(R.id.imageButtonBack);
        imageButtonBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });



    }
}