package com.example.enkoquest.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class CorrectWordActivity extends AppCompatActivity {

    private ImageButton imageButtonBack;
    private TextView textView, levelTextView;
    private TextView textView, levelTextView, timer;
    private Button btn1, btn2, btn3, btn4;
    private List<Word> wordList = new ArrayList<>();
    private int currentLevel = 1;  // 현재 레벨 변수 추가
    private int currentQuestionIndex = 0; // 셔플된 리스트에서 순차적으로 문제를 출제하기 위한 인덱스
    private ImageView[] hearts;
    private int life = 5;
    private int highestLevel = 0;
    private FirebaseAuth auth; // Firebase 인증 인스턴스
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount");
    private CountDownTimer countDownTimer;
    private static final long TIMER_DURATION = 30000; //타이머 총 시간 (30초)
    private static final long TIMER_INTERVAL = 1000; // 타이머 간격 (1초)
    private boolean isExplanationShown = false;
    private String saveKey;

    // pairedOptions를 전역변수로 선언
    private List<Pair<String, Word>> pairedOptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_correct_word);

        timer = findViewById(R.id.timer); //UI 초기화

        //하트 이미지뷰 초기화
        hearts = new ImageView[]{
                findViewById(R.id.heart1),
                findViewById(R.id.heart2),
                findViewById(R.id.heart3),
                findViewById(R.id.heart4),
                findViewById(R.id.heart5)
        };

        // View 요소 초기화
        textView = findViewById(R.id.exampleView);
        btn1 = findViewById(R.id.optionButton1);
        btn2 = findViewById(R.id.optionButton2);
        btn3 = findViewById(R.id.optionButton3);
        btn4 = findViewById(R.id.optionButton4);
        imageButtonBack = findViewById(R.id.imageButtonBack);
        levelTextView = findViewById(R.id.levelTextView); // Level TextView 초기화

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();

        // 뒤로 가기 버튼 클릭 시 SelectWordActivity로 이동
        imageButtonBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, EngGmActivity.class);
            startActivity(intent);
        });

        // Firebase에서 데이터 가져오기
        fetchDataFromFirebase();
        loadUserChallengeLevel();
    }

    private void fetchDataFromFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("word2000/word_2000");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                wordList.clear(); //기존 리스트 초기화
                if (dataSnapshot.exists()) {
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
                }

                Log.d("ChallengeActivity", "Loaded words: " + wordList.size());
                if (!wordList.isEmpty()) {
                    // 단어 리스트 셔플하여 중복 없이 랜덤 순서로 출제
                    Collections.shuffle(wordList);
                    currentQuestionIndex = 0; //시작 문제 인덱스 초기화
                    loadNewQuestion(); //데이터 로드 후 문제 시작
                } else {
                    Log.e("ChallengeActivity", "No data loaded from firebase");
                    Toast.makeText(CorrectWordActivity.this, "문제를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CorrectWordActivity.this, "문제를 불러오지 못했습니다. 인터넷 연결을 확인하세요.", Toast.LENGTH_LONG).show();
                Log.w("ChallengeActivity", "loadWord:onCancelled", databaseError.toException());
                finish(); // Activity 종료
            }
        });
    }

    private void startTimer() {
        resetTimer(); //이전 타이머 초기화

        countDownTimer = new CountDownTimer(TIMER_DURATION, TIMER_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                if (!isExplanationShown) {
                    isExplanationShown = true;
                    life --; // 체력 감소
                    updateHearts(); // 하트 상태 업데이트

                    if (life > 0) {
                        // 해설 페이지 호출 (시간 초과로 인한 오답 처리)
                        handleTimeOutExplanation();
                    } else {
                        // 체력이 0일 때 게임 종료
                        handleGameOverExplanation();
                    }
                }
            }
        };
        countDownTimer.start();
    }

    private void handleTimeOutExplanation() {
        if (isExplanationShown)
            return;
        isExplanationShown = true;

        resetTimer();

        // 시간 초과로 인한 해설 화면 호출
//        getExplanationForAnswer("", false, new ExplanationCallback() {
//            @Override
//            public void onExplanationFound(String word, String meaning, String example) {
        Intent intent = new Intent(CorrectWordActivity.this, CorrectExplanation.class);

        // 추가 데이터를 Bundle로 전달
        Bundle bundle = new Bundle();
        bundle.putString("MY_ANSWER", "시간 초과"); // 사용자가 선택하지 않은 상태
        bundle.putBoolean("IS_CORRECT", false); // 시간 초과는 정답이 아님
        bundle.putInt("LIFE_REMAINING", life); // 남은 하트 수 전달
        bundle.putBoolean("SHOW_RETRY", life == 0); // 재시작 버튼 표시 여부

        intent.putExtras(bundle);
        startActivityForResult(intent, 100); // 해설 화면 호출
    }

    private void handleGameOverExplanation() {
        if(isExplanationShown)
            return;
        isExplanationShown = true;

        resetTimer();
        //체력이 0이 되어 게임 종료 시 해설 화면 호출
//        getExplanationForAnswer("", false, new ExplanationCallback() {
//            @Override
//            public void onExplanationFound(String word, String meaning, String example) {
        Intent intent = new Intent(CorrectWordActivity.this, CorrectExplanation.class);

        //추가 데이터를 Bundle로 전달
        Bundle bundle = new Bundle();
        bundle.putString("MY_ANSWER", "시간 초과"); //사용자가 선택하지 않은 상태
        bundle.putBoolean("IS_CORRECT", false); //시간 초과는 정답 처리 x
        bundle.putInt("LIFE_REMAINING", life); //남은 하트 수 전달
        bundle.putBoolean("SHOW_RETRY", true); //재시작 버튼 표시 여부

        intent.putExtras(bundle);
        startActivityForResult(intent, 100); //해설 화면 호출
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null; //타이머 초기화
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        isExplanationShown = false; //해설 화면 완료 후 플래그 초기화

        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("LIFE_REMAINING")) {
                life = data.getIntExtra("LIFE_REMAINING", life);
                updateHearts();
            }
            if (life > 0) {
                //새로운 문제 로드
                loadNewQuestion();
            } else {
                Toast.makeText(this, "Game Over! 모든 체력을 소진했습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void updateHearts() {
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setImageResource(i < life ? R.drawable.full_life : R.drawable.no_life);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity 종료 시 타이머 정지
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
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
        if (wordList.isEmpty()) {
            Toast.makeText(this, "문제가 없습니다. 나중에 다시 시도해주세요!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (currentQuestionIndex >= wordList.size()) {
            Toast.makeText(this, "모든 문제를 완료했습니다!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        resetButtons(); // 보기버튼 상태 초기화
        resetTimer(); //기존 타이머 초기화

        // 현재 인덱스에 해당하는 문제를 가져옴
        Word questionWord = wordList.get(currentQuestionIndex);
        currentQuestionIndex++; // 다음 문제를 위한 인덱스 증가

//        textView.setText(questionWord.getWord());  // 문제 단어를 텍스트뷰에 표시
//        saveKey = textView.getText().toString();
        saveKey = questionWord.getWord(); // saveKey 초기화
        textView.setText(saveKey);

        startTimer(); //새 타이머 시작

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
        btn1.setText(pairedOptions.get(0).first);
        btn2.setText(pairedOptions.get(1).first);
        btn3.setText(pairedOptions.get(2).first);
        btn4.setText(pairedOptions.get(3).first);

        // 버튼 클릭 리스너에 Bundle 전달
        setOptionButtonListeners(questionWord.getMeaning(), bundle);
    }

    private void setOptionButtonListeners(String correctAnswer, Bundle bundle) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        View.OnClickListener listener = v -> {
            resetTimer(); //타이머 중단
            Button clickedButton = (Button) v;
            String chosenAnswer = clickedButton.getText().toString();

            // 정답 여부 확인
            boolean isCorrect = chosenAnswer.equals(correctAnswer);

            // 선택한 버튼 번호 가져오기
            String chosenOptionNumber;
            String correctOptionNumber;
            if (v == btn1) {
                chosenOptionNumber = "1";
            } else if (v == btn2) {
                chosenOptionNumber = "2";
            } else if (v == btn3) {
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
                //정답일 경우 레벨을 증가시키고 다음 문제로 이동
                updateLevel();
            } else {
                life--; //체력 감소
                updateHearts(); //하트 상태 업데이트

                // 오답일 경우 버튼 배경색 변경 및 'X' 표시
                if (!chosenAnswer.startsWith("X")) {
                    clickedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                    clickedButton.setText("X " + chosenAnswer);

                    if (life > 0) {
                        //해설 화면 호출
                        getExplanationForAnswer(chosenAnswer, false, new ExplanationCallback() {
                            @Override
                            public void onExplanationFound(String word, String meaning, String example) {
                                Intent intent = new Intent(CorrectWordActivity.this, CorrectExplanation.class);
                                bundle.putInt("LIFE_REMAINING", life);
                                bundle.putBoolean("SHOW_RETRY", false);
                                intent.putExtras(bundle);
                                startActivityForResult(intent, 100);
                            }
                        });
                    } else {
                        handleGameOverExplanation();
                    }

                    Word questionWord = wordList.get(currentQuestionIndex);
                    String userId = firebaseUser.getUid();
                    String word = saveKey;
                    String answer = correctAnswer;

                    wrongWord(userId, word, answer);

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
        resetTimer();
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("LIFE_REMAINING", life);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void finishGame() {
        //게임 종료 처리
        if (countDownTimer != null) {
            countDownTimer.cancel();

        }
        countDownTimer = null; //타이머 초기화

        Intent intent = new Intent(this, CorrectExplanation.class);
        intent.putExtra("Score", currentLevel);
        startActivity(intent);
        finish();
    }

    private void wrongWord(String userId, String word, String answer) {
        if(userId == null || word == null || answer == null) {
            Log.e("Firebase", "Invalid data for wrongWord: userId=" + userId + ", word=" + word + ", answer=" + answer);
            return; // 잘못된 값이 있으면 실행 중단
        }

        DatabaseReference userRef = databaseReference.child(userId).child("mistakeNotes");
        String key = databaseReference.push().getKey();

        if (key != null) {
            userRef.child(word).setValue(answer)
                    .addOnSuccessListener(aVoid ->
                        // 성공 시 처리
                        Log.d("Firebase", "단어 저장 성공: " + word))
                    .addOnFailureListener(e ->
                        // 실패 시 처리
                        Log.e("Firebase", "단어 저장 실패", e));
        }
    }
}
