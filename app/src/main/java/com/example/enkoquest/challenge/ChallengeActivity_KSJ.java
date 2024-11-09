package com.example.enkoquest.challenge;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChallengeActivity_KSJ extends AppCompatActivity {

    private TextView textView;
    private Button btn1, btn2, btn3, btn4;
    private List<Word> wordList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_challenge);

        // View 요소 초기화
        textView = findViewById(R.id.wordTextView);
        btn1 = findViewById(R.id.optionButton1);
        btn2 = findViewById(R.id.optionButton2);
        btn3 = findViewById(R.id.optionButton3);
        btn4 = findViewById(R.id.optionButton4);

        // Firebase에서 데이터 가져오기
        fetchDataFromFirebase();

        // 시스템 인셋 설정 (화면 여백 처리)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchDataFromFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("word2000/word_2000");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String word = snapshot.child("단어").getValue(String.class);
                    String meaning = snapshot.child("의미").getValue(String.class);
                    wordList.add(new Word(word, meaning));
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

        // 버튼에 보기 옵션 설정
        btn1.setText(options.get(0));
        btn2.setText(options.get(1));
        btn3.setText(options.get(2));
        btn4.setText(options.get(3));

        // 각 버튼에 클릭 리스너 추가
        setOptionButtonListeners(questionWord.getMeaning());
    }

    private void setOptionButtonListeners(String correctAnswer) {
        View.OnClickListener listener = v -> {
            Button clickedButton = (Button) v;
            String chosenAnswer = clickedButton.getText().toString();
            if (chosenAnswer.equals(correctAnswer)) {
                Toast.makeText(ChallengeActivity_KSJ.this, "Correct!", Toast.LENGTH_SHORT).show();
                loadNewQuestion();
            } else {
                Toast.makeText(ChallengeActivity_KSJ.this, "Incorrect. Try again!", Toast.LENGTH_SHORT).show();
            }
        };

        btn1.setOnClickListener(listener);
        btn2.setOnClickListener(listener);
        btn3.setOnClickListener(listener);
        btn4.setOnClickListener(listener);
    }

    // Word 객체 정의
    private static class Word {
        private String word;
        private String meaning;

        public Word(String word, String meaning) {
            this.word = word;
            this.meaning = meaning;
        }

        public String getWord() {
            return word;
        }

        public String getMeaning() {
            return meaning;
        }
    }
}
