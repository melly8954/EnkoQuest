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
            setUpExplanationView(R.id.explanationWord1, R.id.explanationMeaning1, R.id.explanationExample1, R.id.explanationAnswerStatus1, word1, meaning1, example1, isCorrect1);
            setUpExplanationView(R.id.explanationWord2, R.id.explanationMeaning2, R.id.explanationExample2, R.id.explanationAnswerStatus2, word2, meaning2, example2, isCorrect2);
            setUpExplanationView(R.id.explanationWord3, R.id.explanationMeaning3, R.id.explanationExample3, R.id.explanationAnswerStatus3, word3, meaning3, example3, isCorrect3);
            setUpExplanationView(R.id.explanationWord4, R.id.explanationMeaning4, R.id.explanationExample4, R.id.explanationAnswerStatus4, word4, meaning4, example4, isCorrect4);
        }
    }

    private void setUpExplanationView(int wordResId, int meaningResId, int exampleResId, int statusResId,
                                      String word, String meaning, String example, boolean isCorrect) {
        TextView wordTextView = findViewById(wordResId);
        TextView meaningTextView = findViewById(meaningResId);
        TextView exampleTextView = findViewById(exampleResId);
        TextView answerStatusTextView = findViewById(statusResId);

        wordTextView.setText(word);
        meaningTextView.setText(meaning);
        exampleTextView.setText(example);
        answerStatusTextView.setText(isCorrect ? "정답입니다!" : "오답입니다.");
    }
}
