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

        resetButtons();

        Random random = new Random();
        Word questionWord = wordList.get(random.nextInt(wordList.size()));
        textView.setText(questionWord.getWord());

        List<String> options = new ArrayList<>();
        List<Word> optionWords = new ArrayList<>(); // 보기 단어 리스트 추가
        options.add(questionWord.getMeaning());
        optionWords.add(questionWord);

        // 다른 의미들 추가
        while (options.size() < 4) {
            Word randomWord = wordList.get(random.nextInt(wordList.size()));
            String randomMeaning = randomWord.getMeaning();
            if (!options.contains(randomMeaning)) { // 중복된 의미가 없도록 확인
                options.add(randomMeaning);
                optionWords.add(randomWord); // 해당 단어를 옵션 단어 리스트에 추가
            }
        }

        // 보기 옵션 섞기
        java.util.Collections.shuffle(options);

        // Bundle에 데이터를 정확하게 추가
        Bundle bundle = new Bundle();
        for (int i = 0; i < options.size(); i++) {
            // 옵션의 의미에 맞는 단어와 예제를 추가
            String word = optionWords.get(i).getWord();
            String meaning = options.get(i);
            String example = optionWords.get(i).getExample();

            bundle.putString("WORD_" + (i + 1), word);
            bundle.putString("MEANING_" + (i + 1), meaning);
            bundle.putString("EXAMPLE_" + (i + 1), example);
        }

        // 보기 버튼 설정
        btn1.setText(options.get(0));
        btn2.setText(options.get(1));
        btn3.setText(options.get(2));
        btn4.setText(options.get(3));

        // 버튼 클릭 리스너에 Bundle 전달
        setOptionButtonListeners(questionWord.getMeaning(), bundle);
    }


    private void setOptionButtonListeners(String correctAnswer, Bundle bundle) {
        View.OnClickListener listener = v -> {
            Button clickedButton = (Button) v;
            String chosenAnswer = clickedButton.getText().toString();

            if (chosenAnswer.equals(correctAnswer)) {
                currentLevel++;
                levelTextView.setText("Level: " + currentLevel);
                loadNewQuestion();
            } else {
                if (!chosenAnswer.startsWith("X")) {
                    clickedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                    clickedButton.setText("X " + chosenAnswer);

                    getExplanationForAnswer(chosenAnswer, new ExplanationCallback() {
                        @Override
                        public void onExplanationFound(String word, String meaning, String example) {
                            Intent intent = new Intent(CorrectWordActivity.this, ExplanationActivity.class);
                            intent.putExtras(bundle);  // Bundle을 Intent에 추가하여 ExplanationActivity로 전달
                            startActivity(intent);
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
