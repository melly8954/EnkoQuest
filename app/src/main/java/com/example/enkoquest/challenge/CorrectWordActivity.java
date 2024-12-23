package com.example.enkoquest.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
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

public class CorrectWordActivity extends AppCompatActivity {

    private ImageButton imageButtonBack;
    private TextView textView, levelTextView;
    private TextView option1, option2, option3, option4;
    private List<Word> wordList = new ArrayList<>();
    private int currentLevel = 1;  // 현재 레벨 변수 추가
    private int currentQuestionIndex = 0; // 셔플된 리스트에서 순차적으로 문제를 출제하기 위한 인덱스
    private ImageView[] hearts;
    private int life = 5;
    private int highestLevel = 0;
    private FirebaseAuth auth; // Firebase 인증 인스턴스
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount");
    private String saveKey;

    // pairedOptions를 전역변수로 선언
    private List<Pair<String, Word>> pairedOptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_correct_word);

        // View 요소 초기화
        textView = findViewById(R.id.exampleView);
        option1 = findViewById(R.id.optionTextView1);
        option2 = findViewById(R.id.optionTextView2);
        option3 = findViewById(R.id.optionTextView3);
        option4 = findViewById(R.id.optionTextView4);
        imageButtonBack = findViewById(R.id.imageButtonBack);
        levelTextView = findViewById(R.id.levelTextView); // Level TextView 초기화

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
        loadUserChallengeLevel();

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
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Firebase에서 번호, 단어, 의미, 예제 정보 가져옴
                    Integer number = snapshot.child("번호").getValue(Integer.class);
                    String word = snapshot.child("단어").getValue(String.class);
                    String meaning = snapshot.child("의미").getValue(String.class);
                    String example = snapshot.child("예제").getValue(String.class);
                    if (number != null && word != null && meaning != null && example != null) {
                        wordList.add(new Word(number, word, meaning, example));
                    }
                }
                Log.d("ChallengeActivity", "Loaded words: " + wordList.size());
                if (!wordList.isEmpty()) {
                    // 단어 리스트 셔플하여 중복 없이 랜덤 순서로 출제
                    Collections.shuffle(wordList);
                    loadNewQuestion();
                } else {
                    Log.e("ChallengeActivity", "No data loaded from firebase");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ChallengeActivity", "loadWord:onCancelled", databaseError.toException());
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
                    .child("correctLevel");

            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        highestLevel = dataSnapshot.getValue(Integer.class);
                        if (highestLevel == 0) {
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
            return;
        }

        // 현재 인덱스에 해당하는 문제를 가져옴
        Word questionWord = wordList.get(currentQuestionIndex);
        currentQuestionIndex++; // 다음 문제를 위한 인덱스 증가
        textView.setText(questionWord.getWord());  // 문제 단어를 텍스트뷰에 표시
        saveKey = textView.getText().toString();

        pairedOptions.clear(); // 이전 질문에 대한 옵션 리스트 초기화
        pairedOptions.add(new Pair<>(questionWord.getMeaning(), questionWord));

        // 다른 의미들 추가
        Random random = new Random();
        while (pairedOptions.size() < 4) {
            Word randomWord = wordList.get(random.nextInt(wordList.size()));
            if (!pairedOptions.contains(new Pair<>(randomWord.getMeaning(), randomWord))) {
                pairedOptions.add(new Pair<>(randomWord.getMeaning(), randomWord));
            }
        }

        // 보기 옵션 섞기
        Collections.shuffle(pairedOptions);

        // Bundle에 데이터 추가
        Bundle bundle = new Bundle();
        for (int i = 0; i < pairedOptions.size(); i++) {
            String word = pairedOptions.get(i).second.getWord();
            String meaning = pairedOptions.get(i).first;
            String example = pairedOptions.get(i).second.getExample();

            bundle.putString("WORD_" + (i + 1), word);
            bundle.putString("MEANING_" + (i + 1), meaning);
            bundle.putString("EXAMPLE_" + (i + 1), example);

            // 각 보기의 정답 여부 추가
            boolean isCorrect = meaning.equals(questionWord.getMeaning());
            bundle.putBoolean("IS_CORRECT_" + (i + 1), isCorrect); // 정답 여부를 Bundle에 추가
        }

        // 보기 버튼 설정
        option1.setText(pairedOptions.get(0).first);
        setTextViewBackGround(option1);
        option2.setText(pairedOptions.get(1).first);
        setTextViewBackGround(option2);
        option3.setText(pairedOptions.get(2).first);
        setTextViewBackGround(option3);
        option4.setText(pairedOptions.get(3).first);
        setTextViewBackGround(option4);

        // 버튼 클릭 리스너에 Bundle 전달
        setOptionButtonListeners(questionWord.getMeaning(), bundle);
    }

    private void setTextViewBackGround(TextView textView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setBackgroundResource(R.drawable.optiontext_background);
    }

    private void setOptionButtonListeners(String correctAnswer, Bundle bundle) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        View.OnClickListener listener = v -> {
            TextView clickedTextView = (TextView) v;
            String chosenAnswer = clickedTextView.getText().toString();

            // 정답 여부 확인
            boolean isCorrect = chosenAnswer.equals(correctAnswer);

            // 선택한 버튼 번호 가져오기
            String chosenOptionNumber;
            String correctOptionNumber;
            if (v == option1) {
                chosenOptionNumber = "1";
            } else if (v == option2) {
                chosenOptionNumber = "2";
            } else if (v == option3) {
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
                    clickedTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    clickedTextView.setBackgroundResource(R.drawable.option_background_wrong);
                    clickedTextView.setText("X");

                    if (life > 0) {
                        life--; //체력 감소
                        updateHearts(); //하트 상태 업데이트
                    }

                    Word questionWord = wordList.get(currentQuestionIndex);
                    String userId = firebaseUser.getUid();
                    String word = saveKey;
                    String answer = correctAnswer;

                    // ExplanationActivity로 이동
                    getExplanationForAnswer(chosenAnswer, isCorrect, new ExplanationCallback() {
                        @Override
                        public void onExplanationFound(String word, String meaning, String example) {
                            // 정답 여부와 함께 Bundle을 Intent에 추가
                            Intent intent = new Intent(CorrectWordActivity.this, CorrectExplanation.class);

                            bundle.putBoolean("IS_CORRECT", isCorrect);  // 정답 여부 추가
                            bundle.putInt("LIFE_REMAINING", life); // 남은 하트 수 전달
                            bundle.putBoolean("SHOW_RETRY", life == 0); // 재도전 여부 전달

                            intent.putExtras(bundle);  // 데이터를 Intent에 추가
                            startActivityForResult(intent, 100); // Activity 결과 대기
                        }
                    });
                }
            }
        };

        option1.setOnClickListener(listener);
        option2.setOnClickListener(listener);
        option3.setOnClickListener(listener);
        option4.setOnClickListener(listener);
    }

    private void getExplanationForAnswer(String chosenAnswer, Boolean isCorrect, final ExplanationCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("word2000/word_2000");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String word = snapshot.child("단어").getValue(String.class);
                    String meaning = snapshot.child("의미").getValue(String.class);
                    String example = snapshot.child("예제").getValue(String.class);

                    if (meaning != null && meaning.equals(chosenAnswer)) {
                        callback.onExplanationFound(word, meaning, example);
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

    // Word 객체 클래스 정의
    private static class Word {
        private int number;
        private String word;
        private String meaning;
        private String example;

        public Word(int number, String word, String meaning, String example) {
            this.number = number;
            this.word = word;
            this.meaning = meaning;
            this.example = example;
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
    }

    private void saveChallengeLevel(int challengLevel) {
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            // Firebase 데이터베이스 참조 - 사용자 ID를 기준으로 저장
            DatabaseReference database = FirebaseDatabase.getInstance()
                    .getReference("UserAccount")
                    .child(firebaseUser.getUid())
                    .child("challengeLevel")
                    .child("correctLevel");

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("LIFE_REMAINING", life);
        setResult(RESULT_OK, intent);
        finish();
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

}
