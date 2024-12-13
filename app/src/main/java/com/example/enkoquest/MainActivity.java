package com.example.enkoquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.user.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonMoveLogin;
    TextView textView01, textView02;
    ImageButton btn01;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private boolean playing = true;
    private ImageButton musicBtn;
    ImageView imageView;

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
        textView02 = findViewById(R.id.buttonChallengeMode);
        textView01 = findViewById(R.id.buttonWordBook);
        buttonMoveLogin = findViewById(R.id.buttonMoveLogin);
        imageView = findViewById(R.id.imageView);
        musicBtn = findViewById(R.id.musicBtn);
        musicBtn.setImageResource(R.drawable.pause);

        btn01.setOnClickListener(this);
        textView02.setOnClickListener(this);
        textView01.setOnClickListener(this);
        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playing) {
                    stopMusicService();
                    musicBtn.setImageResource(R.drawable.play);
                } else {
                    startMusicService();
                    musicBtn.setImageResource(R.drawable.pause);
                }
                playing = !playing;
            }
        });
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();  // 로그인 상태를 확인

        updateLoginButton();  // 로그인 상태에 따른 버튼 상태 업데이트

        // 액티비티 시작 시 음악 자동 재생
        startMusicService();  // 음악 서비스 시작
    }

    private void updateLoginButton() {
        if (user != null) {
            // 로그인 되어 있으면 '로그아웃' 버튼
            buttonMoveLogin.setText("로그아웃");
            imageView.setImageResource(R.drawable.logout);
            buttonMoveLogin.setOnClickListener(v -> {
                mAuth.signOut();  // 로그아웃 처리
                user = null;  // user 상태 업데이트
                Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();  // 로그아웃 메시지
                updateLoginButton();  // 버튼 상태를 '로그인'으로 변경
            });
        } else {
            // 로그인 안 되어 있으면 '로그인' 버튼
            buttonMoveLogin.setText("로그인");
            imageView.setImageResource(R.drawable.login);
            buttonMoveLogin.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);  // 로그인 화면으로 이동
            });
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonMyPage) {
            if (user == null) {
                // 로그인 안 되어 있으면 로그인 화면으로 이동
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            } else {
                // 로그인 되어 있으면 마이페이지로 이동
                Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        }
        if (view.getId() == R.id.buttonChallengeMode) {
            if (user == null) {
                // 로그인 안 되어 있으면 로그인 화면으로 이동
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            } else {
                // 로그인 되어 있으면 챌린지 모드로 이동
                Intent intent = new Intent(this, EngGmActivity.class);
                startActivity(intent);
            }
        }

        if (view.getId() == R.id.buttonWordBook) {
            Intent intent = new Intent(MainActivity.this, WordBook.class);
            startActivity(intent);
        }
    }

    private void startMusicService() {
        Intent intent = new Intent(this, BGMService.class);
        startService(intent); // 서비스 시작
    }

    // 서비스 중지
    private void stopMusicService() {
        Intent intent = new Intent(this, BGMService.class);
        stopService(intent); // 서비스 중지
    }
}
