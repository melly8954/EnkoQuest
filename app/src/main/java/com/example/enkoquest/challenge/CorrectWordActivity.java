package com.example.enkoquest.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.enkoquest.ExplanationActivity;
import com.example.enkoquest.R;
import com.example.enkoquest.SelectWordActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CorrectWordActivity extends AppCompatActivity {

    private TextView textView, levelTextView;
    private Button btn1, btn2, btn3, btn4;
    private List<Word> wordList = new ArrayList<>();
    private int currentLevel = 1;  // 현재 레벨 변수 추가
    private ImageButton imageButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_correct_word);

        // View 요소 초기화
        textView = findViewById(R.id.wordTextView);
        btn1 = findViewById(R.id.optionButton1);
        btn2 = findViewById(R.id.optionButton2);
        btn3 = findViewById(R.id.optionButton3);
        btn4 = findViewById(R.id.optionButton4);
        imageButtonBack = findViewById(R.id.imageButtonBack);

        levelTextView = findViewById(R.id.levelTextView); // Level TextView 초기화

        imageButtonBack.setOnClickListener(view -> {
           Intent intent = new Intent(this, SelectWordActivity.class);
           startActivity(intent);
        });

        // Firebase에서 데이터 가져오기
        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("word2000/word_2000");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String word = snapshot.child("단어").getValue(String.class);
                    String meaning = snapshot.child("의미").getValue(String.class);
                    String example = snapshot.child("예제").getValue(String.class);
                    wordList.add(new Word(word, meaning, example));
                }
                Log.d("ChallengeActivity", "Loaded words: " + wordList.size());
                loadNewQuestion();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ChallengeActivity", "loadWord:onCancelled", databaseError.toException());
            }
        });
    }


    private void loadNewQuestion() {
        if (wordList.isEmpty()) {
            return;
        }
        // 이전에 잘못된 답을 선택한 경우 버튼 초기화
        resetButtons();

        // 무작위로 문제 단어 선택
        Random random = new Random();
        Word questionWord = wordList.get(random.nextInt(wordList.size()));
        textView.setText(questionWord.getWord());

        // 보기 옵션 설정 (정답 포함하여 무작위로 의미 선택)
        List<String> options = new ArrayList<>();
        options.add(questionWord.getMeaning()); // 정답 의미 추가

        // 다른 의미들 추가
        while (options.size() < 4) {
            String randomMeaning = wordList.get(random.nextInt(wordList.size())).getMeaning();
            if (!options.contains(randomMeaning)) { // 중복된 의미가 없도록 확인
                options.add(randomMeaning);
            }
        }

        // 보기 옵션 섞기
        java.util.Collections.shuffle(options);

        Bundle bundle = new Bundle();

        // 각 버튼에 클릭 리스너 추가
        String[] allWords = new String[wordList.size()];
        String[] allMeanings = new String[wordList.size()];
        String[] allExamples = new String[wordList.size()];

        for (int i = 0; i < wordList.size(); i++) {
            allWords[i] = wordList.get(i).getWord();
            allMeanings[i] = wordList.get(i).getMeaning();
            allExamples[i] = wordList.get(i).getExample();

            // 버튼에 보기 옵션 설정
            btn1.setText(options.get(0));
            btn2.setText(options.get(1));
            btn3.setText(options.get(2));
            btn4.setText(options.get(3));

        }
        // Bundle에 배열 값들을 추가
        for (int i = 0; i < wordList.size(); i++) {
            bundle.putString("WORD_" + (i + 1), allWords[i]);
            bundle.putString("MEANING_" + (i + 1), allMeanings[i]);
            bundle.putString("EXAMPLE_" + (i + 1), allExamples[i]);
        }


        setOptionButtonListeners(questionWord.getMeaning(), allWords, allMeanings, allExamples,bundle);
    }

    private void setOptionButtonListeners(String correctAnswer, String[] allWords, String[] allMeanings, String[] allExamples,Bundle bundle) {
        View.OnClickListener listener = v -> {
            Button clickedButton = (Button) v;
            String chosenAnswer = clickedButton.getText().toString();
            if (chosenAnswer.equals(correctAnswer)) {
                // 정답을 맞추면 Level 증가
                currentLevel++;
                levelTextView.setText("Level: " + currentLevel); // Level TextView 업데이트
                loadNewQuestion();
            } else {
                // 오답일 경우
                if (!chosenAnswer.startsWith("X")){
                    clickedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));  // 버튼 배경을 빨간색으로 변경
                    clickedButton.setText("X " + chosenAnswer);  // 버튼 텍스트 앞에 X 표시 추가

                    // 해설을 비동기적으로 가져오기
                    getExplanationForAnswer(chosenAnswer, new ExplanationCallback() {
                        @Override
                        public void onExplanationFound(String word, String meaning, String example) {
                            // 해설 페이지로 이동
                            Intent intent = new Intent(CorrectWordActivity.this, ExplanationActivity.class);

                            // Bundle을 Intent에 담기
                            intent.putExtras(bundle);
                            startActivity(intent);  // 해설 페이지로 이동
                        }
                    });
                }

            }
        };

        btn1.setOnClickListener(listener);
        btn2.setOnClickListener(listener);
        btn3.setOnClickListener(listener);
        btn4.setOnClickListener(listener);
    }

    private void resetButtons() {
        // 버튼 초기화: 배경색을 원래대로 되돌리고 텍스트에서 "X"를 제거
        btn1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // 기본 색상으로 설정
        btn1.setText(btn1.getText().toString().replace("X ", ""));

        btn2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // 기본 색상으로 설정
        btn2.setText(btn2.getText().toString().replace("X ", ""));

        btn3.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // 기본 색상으로 설정
        btn3.setText(btn3.getText().toString().replace("X ", ""));

        btn4.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // 기본 색상으로 설정
        btn4.setText(btn4.getText().toString().replace("X ", ""));
    }

    private void getExplanationForAnswer(String chosenAnswer, final ExplanationCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("word2000/word_2000");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String word = snapshot.child("단어").getValue(String.class);
                    String meaning = snapshot.child("의미").getValue(String.class);
                    String example = snapshot.child("예제").getValue(String.class);

                    if (meaning != null && meaning.equals(chosenAnswer)) {
                        callback.onExplanationFound(word,meaning,example);
                        return;
                    }
                }
                callback.onExplanationFound("해설을 찾을 수 없습니다.", "", "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onExplanationFound("데이터 로딩 실패", "", "");
            }
        });
    }

    // 콜백 인터페이스 정의
    public interface ExplanationCallback {
        void onExplanationFound(String word, String meaning, String example);
    }


    // Word 객체 정의
    private static class Word {
        private String word;
        private String meaning;
        private String example;

        public Word(String word, String meaning, String example) {
            this.word = word;
            this.meaning = meaning;
            this.example = example;
        }

        public String getWord() {
            return word;
        }

        public String getMeaning() {
            return meaning;
        }
        public String getExample() {
            return example;
        }
    }


}
