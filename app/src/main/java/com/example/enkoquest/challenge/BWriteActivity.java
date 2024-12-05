package com.example.enkoquest.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.EngGmActivity;
import com.example.enkoquest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BWriteActivity extends AppCompatActivity{

    private ImageButton imageButtonBack;

    private TextView showMeaning, showExample, showTranslation, showHint, level;
    private EditText inputAnswer;
    private Button btnSubmit;

    private List<Word> wordList = new ArrayList<>();
    private int currentLevel = 1;  // 현재 레벨 변수 추가
    private int currentQuestionIndex = 0; // 셔플된 리스트에서 순차적으로 문제를 출제하기 위한 인덱스
    private int highestLevel = 0;

    private ImageView[] hearts;
    private int life = 5;

    private FirebaseAuth auth; // Firebase 인증 인스턴스
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount");

    // Bundle에 데이터 추가
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bwrite);

        imageButtonBack = findViewById(R.id.imageButtonBack);

        showMeaning = findViewById(R.id.showMeaning);
        showExample = findViewById(R.id.showExample);
        showTranslation = findViewById(R.id.showTranslation);
        showHint = findViewById(R.id.showHint);
        level = findViewById(R.id.level); // Level TextView 초기화

        inputAnswer = findViewById(R.id.inputAnswer);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();

        //하트 이미지뷰 초기화
        hearts = new ImageView[]{
                findViewById(R.id.heart1),
                findViewById(R.id.heart2),
                findViewById(R.id.heart3),
                findViewById(R.id.heart4),
                findViewById(R.id.heart5)
        };

        // Firebase에서 데이터 가져오기
        fetchDataFromFirebase();

        // 뒤로 가기 버튼 클릭 시 SelectWordActivity로 이동
        imageButtonBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, EngGmActivity.class);
            startActivity(intent);
        });


    }

    private void fetchDataFromFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("word2000/word_2000");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 단어 목록을 가져와서 리스트에 저장
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Firebase에서 번호, 단어, 의미, 예제 정보 가져옴
                    Integer number = snapshot.child("번호").getValue(Integer.class);
                    String word = snapshot.child("단어").getValue(String.class);
                    String meaning = snapshot.child("의미").getValue(String.class);
                    String example = snapshot.child("예제").getValue(String.class);
                    String translation = snapshot.child(("해석")).getValue(String.class);
                    if (number != null && word != null && meaning != null && example != null && translation != null) {
                        wordList.add(new Word(number, word, meaning, example,translation));
                    }
                }
                Log.d("BWriteActivity", "Loaded words: " + wordList.size());
                if (!wordList.isEmpty()) {
                    // 단어 리스트 셔플하여 중복 없이 랜덤 순서로 출제
                    Collections.shuffle(wordList);
                    loadNewQuestion();
                } else {
                    Log.e("BWriteActivity", "No data loaded from firebase");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BWriteActivity.this, "데이터 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserChallengeLevel() {
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            DatabaseReference userDatabase = FirebaseDatabase.getInstance()
                    .getReference("UserAccount")
                    .child(firebaseUser.getUid())
                    .child("challengeLevel")
                    .child("bwriteLevel");

            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        highestLevel = dataSnapshot.getValue(Integer.class);
                        if (highestLevel == 0 ) {
                            highestLevel = 1;
                        }
                    } else {
                        highestLevel = 1;
                    }
                    loadNewQuestion();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("BWriteActivity", "loadUserChallengeLevel:onCancelled", databaseError.toException());
                }
            });
        }
    }

    private void loadNewQuestion() {
        if (currentQuestionIndex >= wordList.size()) {
            return;
        }

        // 현재 인덱스에 해당하는 문제를 가져옴
        Word question = wordList.get(currentQuestionIndex);
        currentQuestionIndex++; // 다음 문제를 위한 인덱스 증가

        // 문제 예제에서 단어를 빈칸으로 처리
        String exampleWithBlank = question.getExample().replace(question.getWord(), "_____");
        showMeaning.setText(question.getMeaning());
        showExample.setText(exampleWithBlank);  // 문제 예제 텍스트뷰에 빈칸 표시
        showTranslation.setText(question.getTranslation());

        String word = question.getWord(); // 단어 가져오기
        if (word != null && !word.isEmpty()) {
            char firstChar = word.charAt(0); // 첫 글자 가져오기
            showHint.setText("힌트: " + firstChar + "____"); // 힌트 텍스트뷰에 설정
        } else {
            showHint.setText("힌트를 제공할 수 없습니다."); // 단어가 없을 경우 처리
        }



        // 문제의 보기 예제와 해석을 번들에 추가
        bundle.putString("questionExample", question.getExample());
        bundle.putString("translationExample", question.getTranslation());
        bundle.putString("meaning", question.getMeaning());



        String myAnswer = inputAnswer.getText().toString();
        // 각 보기의 정답 여부 추가
        boolean isCorrect = myAnswer.equals(question.getMeaning());
        bundle.putBoolean("IS_CORRECT", isCorrect); // 정답 여부를 Bundle에 추가


        // 버튼 클릭 리스너에 Bundle 전달
        setButtonListeners(question.getWord(), bundle);
    }

    private void setButtonListeners(String correctAnswer, Bundle bundle){
        View.OnClickListener listener = v -> {
            String userAnswer = inputAnswer.getText().toString().trim();
            Word currentWord = wordList.get(currentQuestionIndex - 1); // 현재 문제

            // `myAnswer`로 사용자의 선택 추가
            bundle.putString("MY_ANSWER", userAnswer);
            bundle.putString("CORRECT_ANSWER", correctAnswer);


            if (userAnswer.equalsIgnoreCase(currentWord.getWord())) {
                Toast.makeText(this, "정답입니다!", Toast.LENGTH_SHORT).show();
                updateLevel(); // 레벨 업데이트
            } else {
                if (life > 0) {
                    life--; //체력 감소
                    updateHearts(); //하트 상태 업데이트
                }
                Word question = wordList.get(currentQuestionIndex);

                // 정답 여부와 함께 Bundle을 Intent에 추가
                Intent intent = new Intent(BWriteActivity.this, BWriteExplanation.class);

                bundle.putInt("LIFE_REMAINING", life); // 남은 하트 수 전달
                bundle.putBoolean("SHOW_RETRY", life == 0); // 재도전 여부 전달

                intent.putExtras(bundle);  // 데이터를 Intent에 추가
                startActivityForResult(intent, 100); // Activity 결과 대기
            }
        };
        btnSubmit.setOnClickListener(listener);
    };


    // Word 객체 클래스 정의
    private static class Word {
        private int number;
        private String word;
        private String meaning;
        private String example;
        private String translation;

        public Word(int number, String word, String meaning, String example,String translation) {
            this.number = number;
            this.word = word;
            this.meaning = meaning;
            this.example = example;
            this.translation = translation;
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

        public String getTranslation(){return translation;}
    }

    private void saveChallengeLevel(int challengLevel) {
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            // Firebase 데이터베이스 참조 - 사용자 ID를 기준으로 저장
            DatabaseReference database = FirebaseDatabase.getInstance()
                    .getReference("UserAccount")
                    .child(firebaseUser.getUid())
                    .child("challengeLevel")
                    .child("bwriteLevel");

            database.setValue(challengLevel);
        }
    }

    private void updateLevel() {
        //다음 문제로 넘어가므로 Level 증가
        currentLevel++;
        level.setText("Level" + currentLevel);
        if (currentLevel > highestLevel) {
            highestLevel = currentLevel;
            saveChallengeLevel(highestLevel - 1);
        }
        loadNewQuestion();
    }

    private void updateHearts() {
        for (int i = 0; i < hearts.length; i++) {
            if (i < life) {
                hearts[i].setImageResource(R.drawable.full_life);
            } else {
                hearts[i].setImageResource(R.drawable.no_life);
            }
        }
        if (life == 0) {
            Toast.makeText(this, "Game Over! You have no lives left.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.hasExtra("LIFE_REMAINING")) {
                    life = data.getIntExtra("LIFE_REMAINING", life);
                    updateHearts();
                }
            }
            //새로운 문제 로드
            loadNewQuestion();
        }
    }

}