package com.example.enkoquest.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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

public class BlankActivity extends AppCompatActivity {

    private TextView exampleView, levelTextView;
    private Button optionButton1, optionButton2, optionButton3, optionButton4;
    private ImageButton imageButtonBack;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount");

    private List<Word> wordList = new ArrayList<>();
    private int currentQuestionIndex = 0; // 셔플된 리스트에서 순차적으로 문제를 출제하기 위한 인덱스
    private String blankExample;
    private String saveKey;

    // pairedOptions를 전역변수로 선언
    private List<Pair<String,Word>> pairedOptions = new ArrayList<>();

    private String correctWord;
    private int currentLevel = 1;
    private int highestLevel = 0;


    // 챌린지 하트
    private ImageView[] hearts;
    private int life = 5;

    // Bundle에 데이터 추가
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blank);

        // 뷰 초기화
        exampleView = findViewById(R.id.exampleView);
        levelTextView = findViewById(R.id.levelTextView);
        optionButton1 = findViewById(R.id.optionButton1);
        optionButton2 = findViewById(R.id.optionButton2);
        optionButton3 = findViewById(R.id.optionButton3);
        optionButton4 = findViewById(R.id.optionButton4);
        imageButtonBack = findViewById(R.id.imageButtonBack);

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

        // Firebase에서 문제와 선택지 가져오기
        fetchExampleFromFirebase();
        loadUserChallengeLevel();


        // 뒤로 가기 버튼 클릭 시 SelectWordActivity로 이동
        imageButtonBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, EngGmActivity.class);
            startActivity(intent);
        });
    }

    // Firebase에서 예제와 선택지 가져오기
    private void fetchExampleFromFirebase() {
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
                Log.d("BlankActivity", "Loaded words: " + wordList.size());
                if (!wordList.isEmpty()) {
                    // 단어 리스트 셔플하여 중복 없이 랜덤 순서로 출제
                    Collections.shuffle(wordList);
                    loadNewQuestion();
                } else {
                    Log.e("BlankActivity", "No data loaded from firebase");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BlankActivity.this, "데이터 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserChallengeLevel() {
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            DatabaseReference userDatabase = FirebaseDatabase.getInstance()
                    .getReference("UserAccount")
                    .child(firebaseUser.getUid())
                    .child("challengeLevel");

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
                    Log.w("CorrectWordActivity", "loadUserChallengeLevel:onCancelled", databaseError.toException());
                }
            });
        }
    }

    private void loadNewQuestion() {
        if (currentQuestionIndex >= wordList.size()) {
            // 모든 문제를 출제한 경우 알림 메시지 표시
            Toast.makeText(this, "모든문제 출제 완료", Toast.LENGTH_SHORT).show(); //모든 물제 출제시 알림 메세지 표시
            return;
        }
        resetButtons(); // 보기버튼 상태 초기화

        // 현재 인덱스에 해당하는 문제를 가져옴
        Word question = wordList.get(currentQuestionIndex);
        currentQuestionIndex++; // 다음 문제를 위한 인덱스 증가

        // 문제 예제에서 단어를 빈칸으로 처리
        String exampleWithBlank = question.getExample().replace(question.getWord(), "_____");
        exampleView.setText(exampleWithBlank);  // 문제 예제 텍스트뷰에 빈칸 표시

        // 문제의 보기 예제와 해석을 번들에 추가
        bundle.putString("questionExample", question.getExample());
        bundle.putString("translationExample", question.getTranslation());
        
        // blankExample에 값을 저장하고 saveKey에 할당
        blankExample = exampleWithBlank;
        saveKey = blankExample;  // saveKey에 blankExample 저장

        pairedOptions.clear(); // 이전 질문에 대한 옵션 리스트 초기화
        pairedOptions.add(new Pair<>(question.getWord(), question));

        // 다른 의미들 추가
        Random random = new Random();

        while (pairedOptions.size() < 4) {
            Word randomWord = wordList.get(random.nextInt(wordList.size()));
            if (!pairedOptions.contains(new Pair<>(randomWord.getWord(), randomWord))) {
                pairedOptions.add(new Pair<>(randomWord.getWord(), randomWord));
            }
        }

        // 보기 옵션 섞기
        Collections.shuffle(pairedOptions);

        for (int i = 0; i < pairedOptions.size(); i++) {
            String word = pairedOptions.get(i).second.getWord();
            String meaning = pairedOptions.get(i).second.getMeaning();
            String example = pairedOptions.get(i).second.getExample();
            String translation = pairedOptions.get(i).second.getTranslation();

            bundle.putString("WORD_" + (i + 1), word);
            bundle.putString("MEANING_" + (i + 1), meaning);
            bundle.putString("EXAMPLE_" + (i + 1), example);
            bundle.putString("TRANSLATION_" + (i + 1), translation);

            // 각 보기의 정답 여부 추가
            boolean isCorrect = meaning.equals(question.getMeaning());
            bundle.putBoolean("IS_CORRECT_" + (i + 1), isCorrect); // 정답 여부를 Bundle에 추가
        }

        // 보기 버튼 설정
        optionButton1.setText(pairedOptions.get(0).first);
        optionButton2.setText(pairedOptions.get(1).first);
        optionButton3.setText(pairedOptions.get(2).first);
        optionButton4.setText(pairedOptions.get(3).first);

        // 버튼 클릭 리스너에 Bundle 전달
        setOptionButtonListeners(question.getWord(), bundle);
    }

    private void setOptionButtonListeners(String correctAnswer, Bundle bundle) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        View.OnClickListener listener = v -> {
            Button clickedButton = (Button) v;
            String chosenAnswer = clickedButton.getText().toString();

            // 정답 여부 확인
            boolean isCorrect = chosenAnswer.equals(correctAnswer);

            // 선택한 버튼 번호 가져오기
            String chosenOptionNumber;
            String correctOptionNumber;
            if (v == optionButton1) {
                chosenOptionNumber = "1";
            } else if (v == optionButton2) {
                chosenOptionNumber = "2";
            } else if (v == optionButton3) {
                chosenOptionNumber = "3";
            } else {
                chosenOptionNumber = "4";
            }

            // 정답 번호 설정
            if (correctAnswer.equals(pairedOptions.get(0).first)) {
                correctOptionNumber = "1";
            } else if (correctAnswer.equals(pairedOptions.get(1).first)) {
                correctOptionNumber = "2";
            } else if (correctAnswer.equals(pairedOptions.get(2).first)) {
                correctOptionNumber = "3";
            } else {
                correctOptionNumber = "4";
            }

            // `myAnswer`로 사용자의 선택 추가
            bundle.putString("MY_ANSWER", chosenOptionNumber);

            bundle.putString("CORRECT_ANSWER", correctOptionNumber);

            // 선택한 버튼에 대한 처리
            if (isCorrect) {
                updateLevel();
            } else {
                // 오답일 경우 버튼 배경색 변경 및 'X' 표시
                if (!chosenAnswer.startsWith("X")) {
                    clickedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                    clickedButton.setText("X " + chosenAnswer);

                    if (life > 0) {
                        life--; //체력 감소
                        updateHearts(); //하트 상태 업데이트
                    }

                    Word question = wordList.get(currentQuestionIndex);
                    String userId = firebaseUser.getUid();
                    String example = saveKey;  // 문제에서 보기로 나온 예제
                    String answer = correctAnswer;

                    // 정답 여부와 함께 Bundle을 Intent에 추가
                    Intent intent = new Intent(BlankActivity.this, BlankExplanation.class);

                    bundle.putBoolean("IS_CORRECT", isCorrect);  // 정답 여부 추가
                    bundle.putInt("LIFE_REMAINING", life); // 남은 하트 수 전달
                    bundle.putBoolean("SHOW_RETRY", life == 0); // 재도전 여부 전달

                    intent.putExtras(bundle);  // 데이터를 Intent에 추가
                    startActivityForResult(intent, 100); // Activity 결과 대기
                }
            }
        };

        optionButton1.setOnClickListener(listener);
        optionButton2.setOnClickListener(listener);
        optionButton3.setOnClickListener(listener);
        optionButton4.setOnClickListener(listener);
    }

    private void saveChallengeLevel(int challengLevel) {
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            // Firebase 데이터베이스 참조 - 사용자 ID를 기준으로 저장
            DatabaseReference database = FirebaseDatabase.getInstance()
                    .getReference("UserAccount")
                    .child(firebaseUser.getUid())
                    .child("challengeLevel");

            database.setValue(challengLevel);
        }
    }

    private void updateLevel() {
        //다음 문제로 넘어가므로 Level 증가
        currentLevel++;
        levelTextView.setText("Level" + currentLevel);
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

    // 예제의 해석을 가져오는 메서드
    // 해석은 Firebase에서 가져오는 방식이나 임시 데이터를 반환할 수 있습니다.
    private String getInterpretation(String word) {
        // 예: Firebase 데이터베이스에서 가져오는 로직 또는 하드코딩된 샘플
        switch (word.toLowerCase()) {
            case "example1":
                return "이것은 첫 번째 예제의 해석입니다.";
            case "example2":
                return "이것은 두 번째 예제의 해석입니다.";
            default:
                return "해당 단어에 대한 해석이 없습니다.";
        }
    }

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

        public int getNumber() {
            return number;
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


    private void resetButtons() {
        // 버튼 초기화: 배경색을 원래대로 되돌리고 텍스트에서 "X"를 제거
        optionButton1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // 기본 색상으로 설정
        optionButton1.setText(optionButton1.getText().toString().replace("X ", ""));

        optionButton2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // 기본 색상으로 설정
        optionButton2.setText(optionButton2.getText().toString().replace("X ", ""));

        optionButton3.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // 기본 색상으로 설정
        optionButton3.setText(optionButton3.getText().toString().replace("X ", ""));

        optionButton4.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // 기본 색상으로 설정
        optionButton4.setText(optionButton4.getText().toString().replace("X ", ""));
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
