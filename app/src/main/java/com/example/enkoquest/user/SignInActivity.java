package com.example.enkoquest.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.MainPageActivity;
import com.example.enkoquest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity implements View

        .OnClickListener {

    private FirebaseAuth auth;
    private EditText email, password;
    private Button loginButton, buttonMoveFindView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance(); // Firebase 인스턴스 초기화
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        email = findViewById(R.id.EmailAddress);
        password = findViewById(R.id.Password);
        loginButton = findViewById(R.id.buttonLogin);
        buttonMoveFindView = findViewById(R.id.buttonMoveFindView);

        loginButton.setOnClickListener(view -> loginUser());
        buttonMoveFindView.setOnClickListener(this);
    }

    private void loginUser() { // XML에서 ID를 가져온다

        String strEmail = email.getText().toString();
        String strPassword = password.getText().toString();

        try {
            auth.signInWithEmailAndPassword(strEmail, strPassword)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // 로그인 성공 시
                            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show(); // 로그인 성공 메시지
                            Intent intent = new Intent(this, MainPageActivity.class); // 메인 페이지 액티비티로 이동
                            startActivity(intent);
                            finish();
                        } else {
                            // 로그인 실패 시
                            Toast.makeText(this, "로그인 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonMoveFindView) {
            Intent intent = new Intent(this, FindIdActivity.class);
            startActivity(intent);
        }
    }
}