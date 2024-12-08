package com.example.enkoquest.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.MyPageActivity;
import com.example.enkoquest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePwActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextNewPw, editTextCheckNewPw;
    private TextView textViewChangePw;
    private FirebaseAuth mAuth;
    private ImageButton imageButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_pw);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // FirebaseAuth 초기화
        mAuth = FirebaseAuth.getInstance();

        // UI 요소 초기화
        editTextNewPw = findViewById(R.id.editTextNewPw);
        editTextCheckNewPw = findViewById(R.id.editTextCheckNewPw);
        textViewChangePw = findViewById(R.id.textViewChangePw);
        imageButtonBack = findViewById(R.id.imageButtonBack);

        // 버튼 클릭 리스너 설정
        textViewChangePw.setOnClickListener(this);
        imageButtonBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textViewChangePw) {
            changePassword();
        }
        if (view.getId() == R.id.imageButtonBack) {
            Intent intent = new Intent(this, MyPageActivity.class);
            finish();
            startActivity(intent);
        }
    }
    private void changePassword() {
        String newPassword = editTextNewPw.getText().toString().trim();
        String confirmPassword = editTextCheckNewPw.getText().toString().trim();

        // 비밀번호와 비밀번호 확인이 동일한지 체크
        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(ChangePwActivity.this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(ChangePwActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase에서 로그인한 사용자 가져오기
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // 비밀번호 변경
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangePwActivity.this, "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                            finish(); // Activity 종료 후 이전 화면으로 돌아가기
                        } else {
                            Toast.makeText(ChangePwActivity.this, "비밀번호 변경 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(ChangePwActivity.this, "로그인된 사용자가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}