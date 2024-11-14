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

            // 각 선택지에 대한 정답 여부 가져오기
            boolean isCorrect1 = bundle.getBoolean("IS_CORRECT_1");
            boolean isCorrect2 = bundle.getBoolean("IS_CORRECT_2");
            boolean isCorrect3 = bundle.getBoolean("IS_CORRECT_3");
            boolean isCorrect4 = bundle.getBoolean("IS_CORRECT_4");

            // 각 TextView에 값 설정
            TextView wordTextView1 = findViewById(R.id.explanationWord1);
            TextView meaningTextView1 = findViewById(R.id.explanationMeaning1);
            TextView exampleTextView1 = findViewById(R.id.explanationExample1);
            TextView explanationAnswerStatus1 = findViewById(R.id.explanationAnswerStatus1);  // 추가: 정답 여부 표시

            wordTextView1.setText(word1);
            meaningTextView1.setText(meaning1);
            exampleTextView1.setText(example1);
            explanationAnswerStatus1.setText(isCorrect1 ? "정답입니다!" : "오답입니다.");  // 정답 여부 표시

            TextView wordTextView2 = findViewById(R.id.explanationWord2);
            TextView meaningTextView2 = findViewById(R.id.explanationMeaning2);
            TextView exampleTextView2 = findViewById(R.id.explanationExample2);
            TextView explanationAnswerStatus2 = findViewById(R.id.explanationAnswerStatus2);  // 추가: 정답 여부 표시

            wordTextView2.setText(word2);
            meaningTextView2.setText(meaning2);
            exampleTextView2.setText(example2);
            explanationAnswerStatus2.setText(isCorrect2 ? "정답입니다!" : "오답입니다.");  // 정답 여부 표시

            TextView wordTextView3 = findViewById(R.id.explanationWord3);
            TextView meaningTextView3 = findViewById(R.id.explanationMeaning3);
            TextView exampleTextView3 = findViewById(R.id.explanationExample3);
            TextView explanationAnswerStatus3 = findViewById(R.id.explanationAnswerStatus3);  // 추가: 정답 여부 표시

            wordTextView3.setText(word3);
            meaningTextView3.setText(meaning3);
            exampleTextView3.setText(example3);
            explanationAnswerStatus3.setText(isCorrect3 ? "정답입니다!" : "오답입니다.");  // 정답 여부 표시

            TextView wordTextView4 = findViewById(R.id.explanationWord4);
            TextView meaningTextView4 = findViewById(R.id.explanationMeaning4);
            TextView exampleTextView4 = findViewById(R.id.explanationExample4);
            TextView explanationAnswerStatus4 = findViewById(R.id.explanationAnswerStatus4);  // 추가: 정답 여부 표시

            wordTextView4.setText(word4);
            meaningTextView4.setText(meaning4);
            exampleTextView4.setText(example4);
            explanationAnswerStatus4.setText(isCorrect4 ? "정답입니다!" : "오답입니다.");  // 정답 여부 표시
        }
    }
}
