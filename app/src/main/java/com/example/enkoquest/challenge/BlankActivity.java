package com.example.enkoquest.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

    private TextView wordTextView, levelTextView;
    private Button optionButton1, optionButton2, optionButton3, optionButton4;
    private ImageButton imageButtonBack;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount");
    private String correctWord;
    private int currentLevel = 1;
    private int highestLevel = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blank);

        // 뷰 초기화
        wordTextView = findViewById(R.id.wordTextView);
        levelTextView = findViewById(R.id.levelTextView);
        optionButton1 = findViewById(R.id.optionButton1);
        optionButton2 = findViewById(R.id.optionButton2);
        optionButton3 = findViewById(R.id.optionButton3);
        optionButton4 = findViewById(R.id.optionButton4);
        imageButtonBack = findViewById(R.id.imageButtonBack);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();

        // Firebase에서 문제와 선택지 가져오기
        fetchExampleFromFirebase();

        // 선택지 클릭 리스너
        optionButton1.setOnClickListener(view -> checkAnswer(optionButton1));
        optionButton2.setOnClickListener(view -> checkAnswer(optionButton2));
        optionButton3.setOnClickListener(view -> checkAnswer(optionButton3));
        optionButton4.setOnClickListener(view -> checkAnswer(optionButton4));
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
                // 보기와 선지 초기화
                resetButtonState();
                
                // 단어 목록을 저장할 리스트
                List<String> wordList = new ArrayList<>();

                // 단어 목록을 가져와서 리스트에 저장
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String word = snapshot.child("단어").getValue(String.class);
                    if (word != null) {
                        wordList.add(word);
                    }
                }

                // 랜덤으로 정답을 선택
                String randomWord = wordList.get(new Random().nextInt(wordList.size()));

                // 예제 가져오기
                String example = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String word = snapshot.child("단어").getValue(String.class);
                    if (word != null && word.equals(randomWord)) {
                        example = snapshot.child("예제").getValue(String.class); // 예제 가져오기
                        break;
                    }
                }

                if (example != null && !example.isEmpty()) {
                    // 예제에서 단어를 빈칸으로 변경
                    String modifiedExample = example.replace(randomWord, "_____"); // 빈칸 처리
                    wordTextView.setText(modifiedExample);
                    correctWord = randomWord; // 정답 단어 저장

                    // 선택지 설정 (정답을 제외한 나머지 단어들로 선택지 생성)
                    setOptions(randomWord, wordList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BlankActivity.this, "데이터 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 선택지 설정 (정답을 제외한 단어들을 랜덤으로 선택)
    private void setOptions(String correctWord, List<String> wordList) {
        // 정답을 제외한 단어들만 선택
        wordList.remove(correctWord);

        // 나머지 3개 단어를 랜덤으로 선택
        List<String> randomOptions = new ArrayList<>();
        randomOptions.add(correctWord);  // 정답 추가

        while (randomOptions.size() < 4) {
            String randomOption = wordList.get(new Random().nextInt(wordList.size()));
            if (!randomOptions.contains(randomOption)) {
                randomOptions.add(randomOption);
            }
        }

        // 선택지 설정
        Collections.shuffle(randomOptions); // 선택지를 섞기
        optionButton1.setText(randomOptions.get(0));
        optionButton2.setText(randomOptions.get(1));
        optionButton3.setText(randomOptions.get(2));
        optionButton4.setText(randomOptions.get(3));
    }

    // 정답 체크
    private void checkAnswer(Button selectedOption) {
        String selectedAnswer = selectedOption.getText().toString().trim();

        if (TextUtils.isEmpty(selectedAnswer)) {
            Toast.makeText(this, "답을 선택하세요.", Toast.LENGTH_SHORT).show();
        } else if (selectedAnswer.equalsIgnoreCase(correctWord)) {
            selectedOption.setText("O " + selectedAnswer); // "X " 추가
            Toast.makeText(this, "정답입니다!", Toast.LENGTH_SHORT).show();
            updateLevel();
        } else {
            // 틀린 답일 때 버튼 배경 색 변경 및 "X " 추가
            selectedOption.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            selectedOption.setText("X " + selectedAnswer); // "X " 추가

            Toast.makeText(this, "틀렸습니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
        }
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
        fetchExampleFromFirebase();
    }

    // 버튼 상태 초기화
    private void resetButtonState() {
        // 각 버튼의 배경색을 원래대로 돌려놓고, 텍스트에서 "X"를 제거
        Button[] optionButtons = {optionButton1, optionButton2, optionButton3, optionButton4};
        for (Button button : optionButtons) {
            button.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // 원래 배경 색으로 설정
            String text = button.getText().toString();
            if (text.startsWith("X ")) {
                button.setText(text.substring(2)); // "X " 제거
            }
        }
    }
}
