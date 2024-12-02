package com.example.enkoquest.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.EngGmActivity;
import com.example.enkoquest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class BWriteActivity extends AppCompatActivity {

    private ImageButton imageButtonBack;

    private List<CorrectWordActivity.Word> wordList = new ArrayList<>();
    private int currentLevel = 1;  // 현재 레벨 변수 추가
    private int currentQuestionIndex = 0; // 셔플된 리스트에서 순차적으로 문제를 출제하기 위한 인덱스
    private int highestLevel = 0;

    private ImageView[] hearts;
    private int life = 5;

    private FirebaseAuth auth; // Firebase 인증 인스턴스
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bwrite);

        imageButtonBack = findViewById(R.id.imageButtonBack);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();


        // 뒤로 가기 버튼 클릭 시 SelectWordActivity로 이동
        imageButtonBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, EngGmActivity.class);
            startActivity(intent);
        });
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
}