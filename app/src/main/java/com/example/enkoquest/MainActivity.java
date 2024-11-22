package com.example.enkoquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn01, btn02, btn03, buttonMoveLogin;
    ImageButton buttonQuest;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn01 = findViewById(R.id.buttonMyPage);
        btn02 = findViewById(R.id.buttonChallengeMode);
        btn03 = findViewById(R.id.buttonStoryMode);
        buttonMoveLogin = findViewById(R.id.buttonMoveLogin);
        buttonQuest = findViewById(R.id.questButton);

        btn01.setOnClickListener(this);
        btn02.setOnClickListener(this);
        btn03.setOnClickListener(this);

        buttonQuest.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DailyQuestActivity.class);
            startActivity(intent);
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();  // 로그인 상태를 확인

        updateLoginButton();  // 로그인 상태에 따른 버튼 상태 업데이트
    }

    private void updateLoginButton() {
        if (user != null) {
            // 로그인 되어 있으면 '로그아웃' 버튼
            buttonMoveLogin.setText("로그아웃");
            buttonMoveLogin.setOnClickListener(v -> {
                mAuth.signOut();  // 로그아웃 처리
                user = null;  // user 상태 업데이트
                Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();  // 로그아웃 메시지
                updateLoginButton();  // 버튼 상태를 '로그인'으로 변경
            });
        } else {
            // 로그인 안 되어 있으면 '로그인' 버튼
            buttonMoveLogin.setText("로그인");
            buttonMoveLogin.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, LoginIndexActivity.class);
                startActivity(intent);  // 로그인 화면으로 이동
            });
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonMyPage) {
            if (user == null) {
                // 로그인 안 되어 있으면 로그인 화면으로 이동
                Intent intent = new Intent(MainActivity.this, LoginIndexActivity.class);
                startActivity(intent);
            } else {
                // 로그인 되어 있으면 마이페이지로 이동
                Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        }
        if (view.getId() == R.id.buttonChallengeMode) {
            Intent intent = new Intent(this, SelectWordActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.buttonStoryMode) {
            Intent intent = new Intent(MainActivity.this, SelectWordActivity.class);
            startActivity(intent);
        }
    }
}
