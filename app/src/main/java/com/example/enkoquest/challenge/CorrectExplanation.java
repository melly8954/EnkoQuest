package com.example.enkoquest.challenge;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.enkoquest.EngGmActivity;
import com.example.enkoquest.R;

public class CorrectExplanation extends AppCompatActivity {
    TextView retryButton, moveMainButton;
    TextView myAnswer, correctAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_explanation);

        retryButton = findViewById(R.id.retryTextView);
        moveMainButton = findViewById(R.id.moveMainTextView);
        TextView nextButton = findViewById(R.id.nextTextView); //다음으로 버튼 추가
//        myAnswer = findViewById(R.id.myAnswer);
//        correctAnswer = findViewById(R.id.correctAnswer);

        retryButton.setVisibility(View.GONE);
        moveMainButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);

        // Bundle을 Intent에서 가져오기
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            boolean showRetry = bundle.getBoolean("SHOW_RETRY", false);

            if (showRetry) {
                //하트가 모두 소진되는 경우
                retryButton.setVisibility(View.VISIBLE);
                moveMainButton.setVisibility(View.VISIBLE);
            } else {
                //하트가 남아있는 경우
                nextButton.setVisibility(View.VISIBLE);
            }

            String selectedAnswer = bundle.getString("MY_ANSWER"); // 선택한 답변
            String correctAnswer = bundle.getString("CORRECT_ANSWER"); // 정답

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
//            boolean isCorrect1 = bundle.getBoolean("IS_CORRECT_1");
//            boolean isCorrect2 = bundle.getBoolean("IS_CORRECT_2");
//            boolean isCorrect3 = bundle.getBoolean("IS_CORRECT_3");
//            boolean isCorrect4 = bundle.getBoolean("IS_CORRECT_4");

            // 각 TextView에 값 설정
            setUpExplanationView(
                    R.id.explanationWord1, R.id.explanationMeaning1, R.id.explanationExample1, R.id.explanationAnswerStatus1,
                    word1, meaning1, example1, selectedAnswer, "1", correctAnswer);

            setUpExplanationView(
                    R.id.explanationWord2, R.id.explanationMeaning2, R.id.explanationExample2, R.id.explanationAnswerStatus2,
                    word2, meaning2, example2, selectedAnswer, "2", correctAnswer);

            setUpExplanationView(
                    R.id.explanationWord3, R.id.explanationMeaning3, R.id.explanationExample3, R.id.explanationAnswerStatus3,
                    word3, meaning3, example3, selectedAnswer, "3", correctAnswer);

            setUpExplanationView(
                    R.id.explanationWord4, R.id.explanationMeaning4, R.id.explanationExample4, R.id.explanationAnswerStatus4,
                    word4, meaning4, example4, selectedAnswer, "4", correctAnswer);
        }

        // 선택한 답변과 정답 가져오기
//        String chosen = bundle.getString("MY_ANSWER"); // 사용자가 선택한 답변
//        String correct = bundle.getString("CORRECT_ANSWER"); // 정답

        // 선택한 답변과 정답을 표시
//        myAnswer.setText("내가 선택한 답변: " + chosen);
//        myAnswer.setTextColor(getResources().getColor(android.R.color.holo_red_light));
//
//        correctAnswer.setText("정답: " + correct);
//        correctAnswer.setTextColor(getResources().getColor(R.color.correct));

        retryButton.setOnClickListener(v -> {
            Intent intent = new Intent(CorrectExplanation.this, CorrectWordActivity.class);
            finish();
            startActivity(intent);
        });

        moveMainButton.setOnClickListener(v -> {
            Intent intent = new Intent(CorrectExplanation.this, EngGmActivity.class);
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

    @SuppressLint("ResourceAsColor")
    private void setUpExplanationView(int wordResId, int meaningResId, int exampleResId, int statusResId,
                                      String word, String meaning, String example,
                                      String selectedAnswer, String currentOptionNumber, String correctAnswer) {

        TextView wordTextView = findViewById(wordResId);
        TextView meaningTextView = findViewById(meaningResId);
        TextView exampleTextView = findViewById(exampleResId);
        TextView answerStatusTextView = findViewById(statusResId);

        wordTextView.setText(word);
        meaningTextView.setText(meaning);
        exampleTextView.setText(example);

        if (currentOptionNumber.equals(correctAnswer)) {
            // 정답인 선택지에는 O 표시 (초록색)
            answerStatusTextView.setText("O");
            answerStatusTextView.setTextColor(getResources().getColor(R.color.correct));
        } else if (selectedAnswer.equals(currentOptionNumber)) {
            // 사용자가 선택했으나 틀렸을 때 X 표시 (빨간색)
            answerStatusTextView.setText("X");
            answerStatusTextView.setTextColor(getResources().getColor(R.color.wrong));
        } else {
            // 선택하지 않은 다른 옵션은 빈 상태로 유지
            answerStatusTextView.setText("");
        }
    }


}
