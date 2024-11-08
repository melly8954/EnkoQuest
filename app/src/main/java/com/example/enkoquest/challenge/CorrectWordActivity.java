package com.example.enkoquest.challenge;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.R;
import com.example.enkoquest.user.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class CorrectWordActivity extends AppCompatActivity {
    TextView question;
    TextView optionA;
    TextView optionB;
    TextView optionC;
    TextView optionD;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_correct_word);

        question.findViewById(R.id.question);
        optionA.findViewById(R.id.optionA);
        optionB.findViewById(R.id.optionB);
        optionC.findViewById(R.id.optionC);
        optionD.findViewById(R.id.optionD);

        // 0부터 2000 사이의 무작위 숫자 생성
        int randomKey = new Random().nextInt(2001); // 2001은 상한 값 (0-2000)


        databaseReference = FirebaseDatabase.getInstance().getReference("word2000/word_2000"+ randomKey);

        // Firebase에서 데이터 읽어오기
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // 각 필드 데이터 가져오기
                    String word = snapshot.child("단어").getValue(String.class);
                    String number = snapshot.child("번호").getValue(String.class);
                    String meaning = snapshot.child("의미").getValue(String.class);

                    // 데이터 확인 및 UI에 설정
                    if (word != null && number != null && meaning != null) {
                        question.setText("다음 단어의 뜻으로 알맞은 것은?\n" + word);
                        optionA.setText(meaning);  // 정답을 Option A에 표시 (예제)
                        optionB.setText("다른 의미 1");
                        optionC.setText("다른 의미 2");
                        optionD.setText("다른 의미 3");
                    } else {
                        Toast.makeText(CorrectWordActivity.this, "데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CorrectWordActivity.this, "해당 키에 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // 데이터베이스 오류 처리
                Toast.makeText(CorrectWordActivity.this, "데이터 읽기 실패: " + error.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("CorrectWordActivity", "Database error: " + error.getMessage());
            }
        });

    }

}