package com.example.enkoquest.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.MyPageActivity;
import com.example.enkoquest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindIdActivity extends AppCompatActivity implements View.OnClickListener {
    TextView idView;
    EditText findNickname, findTel;
    Button buttonFindId, moveLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_find_id);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findNickname = findViewById(R.id.editTextNickname);
        findTel = findViewById(R.id.editTextTel);
        idView = findViewById(R.id.textViewIdView);
        buttonFindId = findViewById(R.id.buttonFindId);
        moveLogin = findViewById(R.id.buttonMoveLogin);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount");

        buttonFindId.setOnClickListener(view -> findId());
        moveLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonMoveLogin) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void findId() {
        String nickname = findNickname.getText().toString();
        String tel = findTel.getText().toString();
        if (nickname.isEmpty() && tel.isEmpty()) {
            findNickname.setError("Nickname is required");
            findTel.setError("Tel is required");
            return;
        }

        // Firebase Database Reference 설정
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount");

        // 닉네임을 기준으로 먼저 필터링하고, 이후 전화번호가 일치하는지 확인
        databaseReference.orderByChild("nickname").equalTo(nickname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userFound = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String retrievedTel = snapshot.child("tel").getValue(String.class);

                    // 전화번호도 일치하는 경우
                    if (retrievedTel != null && retrievedTel.equals(tel)) {
                        String email = snapshot.child("email").getValue(String.class);
                        idView.setText("Your Email: " + email);
                        userFound = true;
                        break;
                    }
                }

                if (!userFound) {
                    idView.setText("No user found with this nickname and telephone number.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database", "Database error: " + databaseError.getMessage());
            }
        });
    }
}