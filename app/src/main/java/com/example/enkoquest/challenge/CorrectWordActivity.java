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

import com.example.enkoquest.ExplanationActivity;
import com.example.enkoquest.R;
import com.example.enkoquest.SelectWordActivity;
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

    private TextView textView, levelTextView;
    private Button btn1, btn2, btn3, btn4;
    private List<Word> wordList = new ArrayList<>();
    private int currentLevel = 1;  // 현재 레벨 변수 추가
    private ImageButton imageButtonBack;
    private ImageView[] hearts;
    private int life = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_correct_word);

        //하트 이미지뷰 초기화
        hearts = new ImageView[] {
                findViewById(R.id.heart1),
                findViewById(R.id.heart2),
                findViewById(R.id.heart3),
                findViewById(R.id.heart4),
                findViewById(R.id.heart5)
        };

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
                    Integer number = snapshot.child("번호").getValue(Integer.class);
                    String word = snapshot.child("단어").getValue(String.class);
                    String meaning = snapshot.child("의미").getValue(String.class);
                    String example = snapshot.child("예제").getValue(String.class);
                    if(number != null && word != null && meaning != null && example != null) {
                        wordList.add(new Word(number, word, meaning, example));
                    }
                }

                Log.d("ChallengeActivity", "Loaded words: " + wordList.size());
                if(!wordList.isEmpty()) {
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


    private void loadNewQuestion() {
        if (wordList.isEmpty()) {
            return;
        }

        resetButtons();

        Random random = new Random();
        Word questionWord = wordList.get(random.nextInt(wordList.size()));
        textView.setText(questionWord.getWord());

        List<Pair<String, Word>> pairedOptions = new ArrayList<>();
        pairedOptions.add(new Pair<>(questionWord.getMeaning(), questionWord));

        // 다른 의미들 추가
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
        btn1.setText(pairedOptions.get(0).first);
        btn2.setText(pairedOptions.get(1).first);
        btn3.setText(pairedOptions.get(2).first);
        btn4.setText(pairedOptions.get(3).first);

        // 버튼 클릭 리스너에 Bundle 전달
        setOptionButtonListeners(questionWord.getMeaning(), bundle);
    }



    private void setOptionButtonListeners(String correctAnswer, Bundle bundle) {
        View.OnClickListener listener = v -> {
            Button clickedButton = (Button) v;
            String chosenAnswer = clickedButton.getText().toString();

            // 정답 여부 확인
            boolean isCorrect = chosenAnswer.equals(correctAnswer);

            // 선택한 버튼에 대한 처리
            if (isCorrect) {
                currentLevel++;
                levelTextView.setText("Level: " + currentLevel);
                loadNewQuestion();
            } else {
                if (!chosenAnswer.startsWith("X")) {
                    clickedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                    clickedButton.setText("X " + chosenAnswer);

                    if(life > 0) {
                        life --; //체력 감소
                        updateHearts(); //하트 상태 업데이트
                    }

                    // ExplanationActivity로 이동
                    getExplanationForAnswer(chosenAnswer, isCorrect, new ExplanationCallback() {
                        @Override
                        public void onExplanationFound(String word, String meaning, String example) {
                            Intent intent = new Intent(CorrectWordActivity.this, ExplanationActivity.class);

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
        public int getNumber(){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.hasExtra("LIFE_REMAINING")) {
                    life = data.getIntExtra("LIFE_REMAINING", life);
                    updateHearts();
                }
                //다음 문제로 넘어가므로 Level 증가
                currentLevel++;
                levelTextView.setText("Level" + currentLevel);
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
        for (int i=0; i<hearts.length; i++) {
            if(i<life) {
                hearts[i].setImageResource(R.drawable.full_life);
            } else {
                hearts[i].setImageResource(R.drawable.no_life);
            }
        }

        if(life==0) {
            Toast.makeText(this, "Game Over! You have no lives left.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

}
