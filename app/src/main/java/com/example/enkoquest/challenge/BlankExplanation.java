package com.example.enkoquest.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.EngGmActivity;
import com.example.enkoquest.R;

public class BlankExplanation extends AppCompatActivity {
    TextView correctAnswer,userAnswer,showExample,transExample;
    Button retryBlank,moveMainButton,nextButton;
    ImageButton imageButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blank_explanation);

        correctAnswer = findViewById(R.id.correctAnswerWord);
        userAnswer = findViewById(R.id.userAnswerWord);
        showExample = findViewById(R.id.showExample);
        transExample = findViewById(R.id.transExample);

        retryBlank = findViewById(R.id.retryBlank);
        nextButton = findViewById(R.id.nextBlank);
        moveMainButton = findViewById(R.id.moveMainButton);
        imageButtonBack = findViewById(R.id.imageButtonBackExplanation);

        retryBlank.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        moveMainButton.setVisibility(View.GONE);

        // 뒤로 가기 버튼 클릭 시 SelectWordActivity로 이동
        imageButtonBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, EngGmActivity.class);
            startActivity(intent);
        });

        // Bundle을 Intent에서 가져오기
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            boolean showRetry = bundle.getBoolean("SHOW_RETRY", false);

            if(showRetry) {
                //하트가 모두 소진되는 경우
                retryBlank.setVisibility(View.VISIBLE);
                moveMainButton.setVisibility(View.VISIBLE);
            } else {
                //하트가 남아있는 경우
                nextButton.setVisibility(View.VISIBLE);
            }

            // 각 선택지에 대한 단어, 의미, 예시 가져오기
            String word1 = bundle.getString("WORD_1");
            String meaning1 = bundle.getString("MEANING_1");
            String example1 = bundle.getString("EXAMPLE_1");
            String translation1 = bundle.getString("TRANSLATION_1");

            String word2 = bundle.getString("WORD_2");
            String meaning2 = bundle.getString("MEANING_2");
            String example2 = bundle.getString("EXAMPLE_2");
            String translation2 = bundle.getString("TRANSLATION_2");

            String word3 = bundle.getString("WORD_3");
            String meaning3 = bundle.getString("MEANING_3");
            String example3 = bundle.getString("EXAMPLE_3");
            String translation3 = bundle.getString("TRANSLATION_3");

            String word4 = bundle.getString("WORD_4");
            String meaning4 = bundle.getString("MEANING_4");
            String example4 = bundle.getString("EXAMPLE_4");
            String translation4 = bundle.getString("TRANSLATION_4");

            // 각 선택지에 대한 정답 여부 가져오기
            boolean isCorrect1 = bundle.getBoolean("IS_CORRECT_1");
            boolean isCorrect2 = bundle.getBoolean("IS_CORRECT_2");
            boolean isCorrect3 = bundle.getBoolean("IS_CORRECT_3");
            boolean isCorrect4 = bundle.getBoolean("IS_CORRECT_4");

        }
        // 선택한 답변과 정답 가져오기
        String chosen = bundle.getString("MY_ANSWER"); // 사용자가 선택한 답변
        String correct = bundle.getString("CORRECT_ANSWER"); // 정답
        String questionExample = bundle.getString("questionExample");
        String translationExample = bundle.getString("translationExample");

        // 선택한 답변과 정답을 표시
        userAnswer.setText("내가 선택한 답변: " + chosen);
        correctAnswer.setText("정답: " + correct);
        showExample.setText(questionExample);
        transExample.setText(translationExample);

        retryBlank.setOnClickListener(v -> {
            Intent intent = new Intent(BlankExplanation.this, BlankActivity.class);
            finish();
            startActivity(intent);
        });

        moveMainButton.setOnClickListener(v -> {
            Intent intent = new Intent(BlankExplanation.this, EngGmActivity.class);
            finish();
            startActivity(intent);
        });

        nextButton.setOnClickListener(v -> {
            //다음 문제로 넘어가기 위해 RESULT_OK와 추가 데이터 설정
            Intent resultIntent = new Intent();
            resultIntent.putExtra("LIFE_REMAINING", getIntent().getIntExtra("LIFE_REMAINING", 5));
            setResult(RESULT_OK, resultIntent); //다음 문제로 돌아가기 위한 결과 코드 설정
            finish(); //현재 화면 종료
        });
    }

}