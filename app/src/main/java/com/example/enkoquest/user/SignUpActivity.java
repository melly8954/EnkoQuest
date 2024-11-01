package com.example.enkoquest.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.IndexActivity;
import com.example.enkoquest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;


public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth; // Firebase 인증 인스턴스
    private DatabaseReference databaseReference; // Firebase Realtime Database 참조
    private EditText username,email,password,checkPw,tel;
    private Button registerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance(); // Firebase 인스턴스 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference();

        username = findViewById(R.id.editTextUsername);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        checkPw = findViewById(R.id.editTextCheckPw);
        tel = findViewById(R.id.editTextPhone);
        registerButton = findViewById(R.id.buttonRegister);

        registerButton.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String strUsername = username.getText().toString();
        String strEmail = email.getText().toString();
        String strPassword = password.getText().toString();
        String strCheckPw = checkPw.getText().toString();
        String strTel = tel.getText().toString();

        if (strUsername.isEmpty() || strEmail.isEmpty() || strPassword.isEmpty() || strCheckPw.isEmpty() || strTel.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!strPassword.equals(strCheckPw)) {
            Toast.makeText(SignUpActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase를 사용하여 이메일과 비밀번호로 사용자 생성
        auth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 사용자 인증 성공
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        UserAccount account = new UserAccount();
                        account.setIdToken(firebaseUser.getUid());
                        account.setUserName(strUsername);
                        account.setEmail(firebaseUser.getEmail());
                        account.setPassword(strPassword);
                        account.setTel(strTel);

                        databaseReference.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                        // 회원가입 성공 메시지 표시
                        Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                        // 메인 액티비티로 이동
                        navigateToMainActivity();
                    } else {
                        // 회원가입 실패 시 사용자에게 메시지 표시
                        Toast.makeText(SignUpActivity.this, "회원가입 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, IndexActivity.class); // 메인 액티비티로 이동
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }
}