package com.example.enkoquest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyPageActivity extends AppCompatActivity implements View

        .OnClickListener {

    Button btn01, btn02, btn03, btn04;
    TextView text01, text02, text03, text04;
    ImageView imageView;
    private Uri selectedImageUri;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imageView.setImageURI(selectedImageUri); // 선택한 이미지 표시
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn01 = findViewById(R.id.buttonChangePW);
        btn02 = findViewById(R.id.buttonMy);
        btn03 = findViewById(R.id.buttonNote);
        btn04 = findViewById(R.id.buttonComunitty);
        imageView = findViewById(R.id.imageViewProfilImg);

        text01 = findViewById(R.id.textId);
        text02 = findViewById(R.id.textNick);
        text03 = findViewById(R.id.textChal);
        text04 = findViewById(R.id.textProduce);

        btn01.setOnClickListener(this);
        btn02.setOnClickListener(this);
        btn03.setOnClickListener(this);
        btn04.setOnClickListener(this);
        imageView.setOnClickListener(view -> openImagePicker());
        setDefaultImage();

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            loadUserData(userId);
        } else {
            Toast.makeText(this, "로그인 필요", Toast.LENGTH_SHORT).show();
        }

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void setDefaultImage() {
        if (selectedImageUri == null) {
            imageView.setImageResource(R.drawable.image1);
        }
    }

    private void loadUserData(String userId) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    if (userAccount != null) {
                        text01.setText("ID : " + userAccount.getIdToken());
                        text02.setText("닉네임 : " + userAccount.getUserName());
                        text03.setText("챌린지 정보");
                        text04.setText("한줄 소개");
                    } else {
                        Toast.makeText(MyPageActivity.this, "사용자 데이터가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(MyPageActivity.this, "데이터를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FireBase", "데이터를 불러오는중 오류 발생", error.toException());
                Toast.makeText(MyPageActivity.this, "데이터를 불러오는 도중 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View view) {

    }
}