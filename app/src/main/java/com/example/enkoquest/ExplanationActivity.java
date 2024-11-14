package com.example.enkoquest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.enkoquest.challenge.CorrectWordActivity;

public class ExplanationActivity extends AppCompatActivity {
    Button retryButton, moveMainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explanation);

        retryButton = findViewById(R.id.retryButton);
        moveMainButton = findViewById(R.id.moveMainButton);

        retryButton.setOnClickListener(v -> {
                    Intent intent = new Intent(ExplanationActivity.this, CorrectWordActivity.class);
                    finish();
                    startActivity(intent);
                });
        moveMainButton.setOnClickListener(v -> {
                    Intent intent = new Intent(ExplanationActivity.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                });

        // Bundle을 Intent에서 가져오기
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            // 각 선택지에 대한 단어, 의미, 예시 가져오기
            String word1 = bundle.getString("WORD_1");
            String meaning1 = bundle.getString("MEANING_1");
            String example1 = bundle.getString("EXAMPLE_1");

            String word2 = bundle.getString("WORD_2");
            String meaning2 = bundle.getString("MEANING_2");
            String example2 = bundle.getString("EXAMPLE_2");

            String word3 = bundle.getString("WORD_3");
            String meaning3 = bundle.getString("MEANING_3");
            String example3 = bundle.getString("EXAMPLE_3");

            String word4 = bundle.getString("WORD_4");
            String meaning4 = bundle.getString("MEANING_4");
            String example4 = bundle.getString("EXAMPLE_4");

            // 각 TextView에 값 설정
            TextView wordTextView1 = findViewById(R.id.explanationWord1);
            TextView meaningTextView1 = findViewById(R.id.explanationMeaning1);
            TextView exampleTextView1 = findViewById(R.id.explanationExample1);

            wordTextView1.setText(word1);
            meaningTextView1.setText(meaning1);
            exampleTextView1.setText(example1);

            TextView wordTextView2 = findViewById(R.id.explanationWord2);
            TextView meaningTextView2 = findViewById(R.id.explanationMeaning2);
            TextView exampleTextView2 = findViewById(R.id.explanationExample2);

            wordTextView2.setText(word2);
            meaningTextView2.setText(meaning2);
            exampleTextView2.setText(example2);

            TextView wordTextView3 = findViewById(R.id.explanationWord3);
            TextView meaningTextView3 = findViewById(R.id.explanationMeaning3);
            TextView exampleTextView3 = findViewById(R.id.explanationExample3);

            wordTextView3.setText(word3);
            meaningTextView3.setText(meaning3);
            exampleTextView3.setText(example3);

            TextView wordTextView4 = findViewById(R.id.explanationWord4);
            TextView meaningTextView4 = findViewById(R.id.explanationMeaning4);
            TextView exampleTextView4 = findViewById(R.id.explanationExample4);

            wordTextView4.setText(word4);
            meaningTextView4.setText(meaning4);
            exampleTextView4.setText(example4);
        }
    }
}
