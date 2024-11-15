package com.example.enkoquest.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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

    private TextView textView, levelTextView;
    private Button btn1, btn2, btn3, btn4;
    private List<Word> wordList = new ArrayList<>();
    private int currentLevel = 1;  // 현재 레벨 변수 추가
    private int currentQuestionIndex = 0; // 셔플된 리스트에서 순차적으로 문제를 출제하기 위한 인덱스
    private ImageButton imageButtonBack;
    private FirebaseAuth auth; // Firebase 인증 인스턴스
    private int highestLevel = 0;

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

        levelTextView = findViewById(R.id.levelTextView);

        // 뒤로 가기 버튼 클릭 시 SelectWordActivity로 이동
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
                    // Firebase에서 단어, 의미, 예제 정보 가져옴
                    String word = snapshot.child("단어").getValue(String.class);
                    String meaning = snapshot.child("의미").getValue(String.class);
                    String example = snapshot.child("예제").getValue(String.class);
                    wordList.add(new Word(word, meaning, example));
                }
                Log.d("ChallengeActivity", "Loaded words: " + wordList.size());

                // 단어 리스트 셔플하여 중복 없이 랜덤 순서로 출제
                Collections.shuffle(wordList);
                loadNewQuestion();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ChallengeActivity", "loadWord:onCancelled", databaseError.toException());
            }
        });
    }


    private void loadNewQuestion() {
        if (currentQuestionIndex >= wordList.size()) {
            // 모든 문제를 출제한 경우 알림 메시지 표시
            Toast.makeText(this, "모든문제 출제 완료", Toast.LENGTH_SHORT).show(); //모든 물제 출제시 알림 메세지 표시
            return;
        }

        resetButtons(); // 보기버튼 상태 초기화

        // 현재 인덱스에 해당하는 문제를 가져옴
        Word questionWord = wordList.get(currentQuestionIndex);
        currentQuestionIndex++; // 다음 문제를 위한 인덱스 증가
        textView.setText(questionWord.getWord());  // 문제 단어를 텍스트뷰에 표시

        List<Pair<String, Word>> pairedOptions = new ArrayList<>();
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
                updateLevel();
            } else {
                // 오답일 경우 버튼 배경색 변경 및 'X' 표시
                if (!chosenAnswer.startsWith("X")) {
                    clickedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                    clickedButton.setText("X " + chosenAnswer);

                    // 정답 여부 포함하여 설명 화면으로 이동
                    getExplanationForAnswer(chosenAnswer, isCorrect, new ExplanationCallback() {
                        @Override
                        public void onExplanationFound(String word, String meaning, String example) {
                            // 정답 여부와 함께 Bundle을 Intent에 추가
                            Intent intent = new Intent(CorrectWordActivity.this, ExplanationActivity.class);
                            bundle.putBoolean("IS_CORRECT", isCorrect);  // 정답 여부 추가
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

    private void saveChallengeLevel(int challengLevel) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        // Firebase 데이터베이스 참조 - 사용자 ID를 기준으로 저장
        DatabaseReference database = FirebaseDatabase.getInstance()
                .getReference("UseAccount")
                .child(firebaseUser.getUid())
                .child("challengeLevel");

        database.setValue(challengLevel)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CorrectWordActivity.this, "저장 완료", Toast.LENGTH_SHORT).show();
                    Log.d("ChallengeActivity", "저장 완료" + challengLevel); // 저장 완료시 표시 (나중에 삭제 하면 됨)
                })
                .addOnFailureListener(e -> Log.e("ChallengeActivity", "저장 실패", e));
    }

    private void updateLevel() {
        currentLevel++;
        levelTextView.setText("Level: " + currentLevel);

        if (currentLevel > highestLevel){
            highestLevel = currentLevel;
            saveChallengeLevel(highestLevel);
        }

        loadNewQuestion();
    }
}
