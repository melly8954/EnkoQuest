package com.example.enkoquest.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.enkoquest.EngGmActivity;
import com.example.enkoquest.R;

public class CorrectExplanation extends AppCompatActivity {
    Button retryButton, moveMainButton;
    TextView myAnswer,correctAnswer;

    private boolean isFinishing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_explanation);

        retryButton = findViewById(R.id.retryButton);
        moveMainButton = findViewById(R.id.moveMainButton);
        Button nextButton = findViewById(R.id.nextButton); //다음으로 버튼 추가
        myAnswer = findViewById(R.id.myAnswer);
        correctAnswer = findViewById(R.id.correctAnswer);

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

            // 선택한 답변과 정답 가져오기
            String chosenAnswer = bundle.getString("MY_ANSWER", "N/A"); // 사용자가 선택한 답변
            String correctAnswerText = bundle.getString("CORRECT_ANSWER", "N/A"); // 정답

            // 선택한 답변과 정답을 표시
            myAnswer.setText("내가 선택한 답변: " + chosenAnswer);
            myAnswer.setTextColor(getResources().getColor(android.R.color.holo_red_light));

            correctAnswer.setText("정답: " + correctAnswerText);
            correctAnswer.setTextColor(getResources().getColor(R.color.correct));

            // 선택지가 비어있는 경우 대비
            setUpExplanationView(R.id.explanationWord1, R.id.explanationMeaning1, R.id.explanationExample1, R.id.explanationAnswerStatus1,
                    bundle.getString("WORD_1", ""), bundle.getString("MEANING_1", ""), bundle.getString("EXAMPLE_1", ""),
                    bundle.getBoolean("IS_CORRECT_1", false));
            setUpExplanationView(R.id.explanationWord2, R.id.explanationMeaning2, R.id.explanationExample2, R.id.explanationAnswerStatus2,
                    bundle.getString("WORD_2", ""), bundle.getString("MEANING_2", ""), bundle.getString("EXAMPLE_2", ""),
                    bundle.getBoolean("IS_CORRECT_2", false)
            );
            setUpExplanationView(R.id.explanationWord3, R.id.explanationMeaning3, R.id.explanationExample3, R.id.explanationAnswerStatus3,
                    bundle.getString("WORD_3", ""), bundle.getString("MEANING_3", ""), bundle.getString("EXAMPLE_3", ""),
                    bundle.getBoolean("IS_CORRECT_3", false)
            );
            setUpExplanationView(R.id.explanationWord4, R.id.explanationMeaning4, R.id.explanationExample4, R.id.explanationAnswerStatus4,
                    bundle.getString("WORD_4", ""), bundle.getString("MEANING_4", ""), bundle.getString("EXAMPLE_4", ""),
                    bundle.getBoolean("IS_CORRECT_4", false)
            );
        }

        retryButton.setOnClickListener(v -> handleButtonClick(() -> {
            Intent intent = new Intent(CorrectExplanation.this, CorrectWordActivity.class);
            finish();
            startActivity(intent);
        }));

        moveMainButton.setOnClickListener(v -> handleButtonClick(() -> {
            Intent intent = new Intent(CorrectExplanation.this, EngGmActivity.class);
            finish();
            startActivity(intent);
        }));

        nextButton.setOnClickListener(v -> handleButtonClick(() -> {
            //다음 문제로 넘어가기 위해 RESULT_OK와 추가 데이터 설정
            Intent resultIntent = new Intent();
            resultIntent.putExtra("LIFE_REMAINING", getIntent().getIntExtra("LIFE_REMAINING", 5));
            setResult(RESULT_OK, resultIntent); //다음 문제로 돌아가기 위한 결과 코드 설정
            finish(); //현재 화면 종료
        }));
    }
    private void handleButtonClick(Runnable action) {
        if(isFinishing) return;
        isFinishing = true;
        action.run();
    }

    private void setUpExplanationView(int wordResId, int meaningResId, int exampleResId, int statusResId,
                                      String word, String meaning, String example, boolean isCorrect) {
        TextView wordTextView = findViewById(wordResId);
        TextView meaningTextView = findViewById(meaningResId);
        TextView exampleTextView = findViewById(exampleResId);
        TextView answerStatusTextView = findViewById(statusResId);

        wordTextView.setText(word != null ? word : "N/A");
        meaningTextView.setText(meaning != null ? meaning : "N/A");
        exampleTextView.setText(example != null ? example : "N/A");

        if (isCorrect) {
            answerStatusTextView.setText("정답입니다!");
            answerStatusTextView.setTextColor(getResources().getColor(R.color.correct)); // 정답 색상
        } else {
            answerStatusTextView.setText("오답입니다.");
            answerStatusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light)); // 오답 색상
        }
    }
}
