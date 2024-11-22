package com.example.enkoquest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MistakeNotes extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase db;
    private TextView textViewResult;
    private DatabaseReference myRef;
    private ImageView backBtn;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mistake_notes);

        textViewResult = findViewById(R.id.textViewResult);
        db = FirebaseDatabase.getInstance();
        backBtn = findViewById(R.id.imageButtonBack);
        backBtn.setOnClickListener(this);

        if (user == null) {
            Log.e("Firebase", "사용자가 로그인되지 않았습니다.");
            textViewResult.setText("로그인 후 다시 시도해주세요.");
            return;
        }

        myRef = db.getReference("UserAccount").child(user.getUid()).child("mistakeNotes");

        fetchMistakes();
    }

    private void fetchMistakes() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 키-값 쌍을 텍스트로 변환
                StringBuilder result = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // 각 문서의 데이터 가져오기
                    String question = snapshot.getKey(); // 질문 (key)
                    String answer = snapshot.getValue(String.class); // 정답 (value)

                    // 텍스트로 결과 구성
                    result.append(question)
                            .append(" : ")
                            .append(answer)
                            .append("\n");
                }
                textViewResult.setText(result.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textViewResult.setText("데이터 가져오기 실패");
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageButtonBack) {
            Intent intent = new Intent(this, MyPageActivity.class);
            finish();
            startActivity(intent);
        }
    }
}
