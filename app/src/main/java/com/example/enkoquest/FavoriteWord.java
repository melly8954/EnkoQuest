package com.example.enkoquest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteWord extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private List<Word> savedWordList = new ArrayList<>();
    private int currentIndex = 0;

    private TextView showNumber, showWord, showMeaning, showExample, showTranslation;
    private Button leftButton, rightButton;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_word);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        // Firebase 참조 설정
        if (user != null) {
            userRef = FirebaseDatabase.getInstance()
                    .getReference("UserAccount")
                    .child(user.getUid())
                    .child("savedWords");
        } else {
            Toast.makeText(this, "로그인 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // UI 요소 연결
        showNumber = findViewById(R.id.showNumber);
        showWord = findViewById(R.id.showWord);
        showMeaning = findViewById(R.id.showMeaning);
        showExample = findViewById(R.id.showExample);
        showTranslation = findViewById(R.id.showTranslation);
        leftButton = findViewById(R.id.left_button);
        rightButton = findViewById(R.id.right_button);
        backBtn = findViewById(R.id.imageButtonBack);

        // 클릭 리스너 설정
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        // 데이터 로드
        fetchSavedWords();
    }

    private void fetchSavedWords() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                savedWordList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Word word = snapshot.getValue(Word.class);
                    if (word != null) {
                        savedWordList.add(word);
                    }
                }

                if (!savedWordList.isEmpty()) {
                    currentIndex = 0;
                    updateUI(currentIndex);
                } else {
                    Toast.makeText(FavoriteWord.this, "저장된 단어가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavoriteWord.this, "데이터 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(int index) {
        if (savedWordList.isEmpty()) return;

        Word currentWord = savedWordList.get(index);

        showNumber.setText("Page_" + currentWord.getNumber());
        showWord.setText(currentWord.getWord());
        showMeaning.setText(currentWord.getMeaning());
        showExample.setText(currentWord.getExample());
        showTranslation.setText(currentWord.getTranslation());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.left_button) {
            if (!savedWordList.isEmpty()) {
                currentIndex = (currentIndex - 1 + savedWordList.size()) % savedWordList.size();
                updateUI(currentIndex);
            }
        } else if (view.getId() == R.id.right_button) {
            if (!savedWordList.isEmpty()) {
                currentIndex = (currentIndex + 1) % savedWordList.size();
                updateUI(currentIndex);
            }
        } else if (view.getId() == R.id.imageButtonBack) {
            finish();
        }
    }

    // Word 데이터 클래스 정의
    public static class Word {
        private Integer number;
        private String word;
        private String meaning;
        private String example;
        private String translation;

        public Word() {
        }

        public Word(Integer number, String word, String meaning, String example, String translation) {
            this.number = number;
            this.word = word;
            this.meaning = meaning;
            this.example = example;
            this.translation = translation;
        }

        public Integer getNumber() {
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

        public String getTranslation() {
            return translation;
        }
    }
}