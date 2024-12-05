package com.example.enkoquest;

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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.challenge.BWriteActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordBook extends AppCompatActivity implements View.OnClickListener {

    private List<Word> wordList = new ArrayList<>();
    private int currentQuestionIndex = 1; // 셔플된 리스트에서 순차적으로 문제를 출제하기 위한 인덱스

    private FirebaseAuth auth; // Firebase 인증 인스턴스
    DatabaseReference database = FirebaseDatabase.getInstance().getReference("word2000/word_2000");

    private TextView showNumber, showWord, showMeaning, showExample, showTranslation;
    private Button leftButton, rightButton;
    private ImageButton imageButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_word_book);

        showNumber = findViewById(R.id.showNumber);
        showWord = findViewById(R.id.showWord);
        showMeaning = findViewById(R.id.showMeaning);
        showExample = findViewById(R.id.showExample);
        showTranslation = findViewById(R.id.showTranslation);

        leftButton = findViewById(R.id.left_button);
        rightButton = findViewById(R.id.right_button);

        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        imageButtonBack = findViewById(R.id.imageButtonBack);

        fetchDataFromFirebase();

        imageButtonBack.setOnClickListener(this);
    }

    private void fetchDataFromFirebase() {
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
                        wordList.add(new Word(number, word, meaning, example, translation));
                    }
                }
                Log.d("BWriteActivity", "Loaded words: " + wordList.size());
                if (!wordList.isEmpty()) {
                    // 현재 인덱스에 해당하는 문제를 가져옴
                    Word question = wordList.get(currentQuestionIndex -1);
                    Integer number = question.getNumber();
                    String word = question.getWord();
                    String meaning = question.getMeaning();
                    String example = question.getExample();
                    String translation = question.getTranslation();

                    showNumber.setText("Page_" + number);
                    showWord.setText(word);
                    showMeaning.setText(meaning);
                    showExample.setText(example);
                    showTranslation.setText(translation);
                } else {
                    Log.e("BWriteActivity", "No data loaded from firebase");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(WordBook.this, "데이터 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.left_button){
            if (currentQuestionIndex > 1) {
                currentQuestionIndex --;
                fetchDataFromFirebase();
            }else if(currentQuestionIndex == 1){
                currentQuestionIndex = 200;
                fetchDataFromFirebase();
            }
        }
        if(view.getId() == R.id.right_button){
            if (currentQuestionIndex < wordList.size()){
                currentQuestionIndex ++;
                fetchDataFromFirebase();
            }else if(currentQuestionIndex == wordList.size()){
                currentQuestionIndex = 1;
                fetchDataFromFirebase();
            }
        }
        if (view.getId() == R.id.imageButtonBack) {
            Intent intent = new Intent(WordBook.this, MainActivity.class);
            startActivity(intent);
        }
    }

    // Word 객체 클래스 정의
    private static class Word {
        private Integer number;
        private String word;
        private String meaning;
        private String example;
        private String translation;

        public Word(Integer number, String word, String meaning, String example, String translation) {
            this.number = number;
            this.word = word;
            this.meaning = meaning;
            this.example = example;
            this.translation = translation;
        }

        public Integer getNumber() { return number;}

        public String getWord() {
            return word;
        }

        public String getMeaning() {
            return meaning;
        }

        public String getExample() {
            return example;
        }

        public String getTranslation() {
            return translation;
        }
    }
}