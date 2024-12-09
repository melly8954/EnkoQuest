package com.example.enkoquest.challenge;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.enkoquest.EngGmActivity;
import com.example.enkoquest.R;

public class BWriteExplanation extends AppCompatActivity {

    TextView correctAnswer, userAnswer, showExample, transExample,showMeaning,answerChecking;
    TextView retryBlank, moveMainButton, nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bwrite_explanation);

        correctAnswer = findViewById(R.id.correctAnswerWord);
        userAnswer = findViewById(R.id.userAnswerWord);
        showExample = findViewById(R.id.showExample);
        showMeaning = findViewById(R.id.showMeaning);
        transExample = findViewById(R.id.transExample);
        answerChecking = findViewById(R.id.answerChecking);

        retryBlank = findViewById(R.id.retryBlank);
        nextButton = findViewById(R.id.nextBlank);
        moveMainButton = findViewById(R.id.moveMainButton);

        retryBlank.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        moveMainButton.setVisibility(View.GONE);

        // Bundle에서 데이터 가져오기
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            boolean showRetry = bundle.getBoolean("SHOW_RETRY", false);

            if (showRetry) {
                retryBlank.setVisibility(View.VISIBLE);
                moveMainButton.setVisibility(View.VISIBLE);
            } else {
                nextButton.setVisibility(View.VISIBLE);
            }

            // 예제 데이터 가져오기 meaning
            String questionExample = bundle.getString("questionExample");
            String correct = bundle.getString("CORRECT_ANSWER");
            String userChoice = bundle.getString("MY_ANSWER");
            String translationExample = bundle.getString("translationExample");
            String meaning = bundle.getString("meaning");
            Boolean isCorrect = bundle.getBoolean("IS_CORRECT");

            if (questionExample != null && correct != null) {
                // 빈칸을 정답으로 대체한 텍스트 생성
                String replacedText = questionExample.replace("______", correct);
                SpannableString spannableString = new SpannableString(replacedText);

                // 정답 위치 계산
                int startIndex = replacedText.indexOf(correct);
                if (startIndex >= 0) {
                    spannableString.setSpan(
                            new ForegroundColorSpan(Color.RED),
                            startIndex,
                            startIndex + correct.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }

                // TextView에 설정
                showExample.setText(spannableString);
            }


            // 데이터 설정
            answerChecking.setText("오답입니다.");
            answerChecking.setTextColor(Color.RED);

            transExample.setText(translationExample);

            correctAnswer.setText("정답: " + correct);
            correctAnswer.setTextColor(getResources().getColor(R.color.correct));
            userAnswer.setText("내가 선택한 답변: " + userChoice);
            showMeaning.setText(meaning);
        }

        // 버튼 클릭 이벤트 설정
        retryBlank.setOnClickListener(v -> {
            Intent intent = new Intent(BWriteExplanation.this, BWriteActivity.class);
            finish();
            startActivity(intent);
        });

        moveMainButton.setOnClickListener(v -> {
            Intent intent = new Intent(BWriteExplanation.this, EngGmActivity.class);
            finish();
            startActivity(intent);
        });

        nextButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("LIFE_REMAINING", getIntent().getIntExtra("LIFE_REMAINING", 5));
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
