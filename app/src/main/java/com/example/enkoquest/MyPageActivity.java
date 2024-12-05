package com.example.enkoquest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.user.ChallengeLevel;
import com.example.enkoquest.user.ChangePwActivity;
import com.example.enkoquest.user.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyPageActivity extends AppCompatActivity implements View

        .OnClickListener {

    private Button buttonMoveChangePW, btn02, btn03, btn04, buttonSave;
    private ImageButton imageButtonBack;
    private TextView text01, text02, text03;
    private EditText selfProduce;
    private ImageView imageView;
    private Uri selectedImageUri;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount");
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String userId = currentUser.getUid();

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

        buttonMoveChangePW = findViewById(R.id.buttonMoveChangePW);
        btn02 = findViewById(R.id.buttonMy);
        btn03 = findViewById(R.id.buttonNote);
        btn04 = findViewById(R.id.buttonComunitty);
        buttonSave = findViewById(R.id.buttonSave);
        imageView = findViewById(R.id.imageViewProfilImg);
        imageButtonBack = findViewById(R.id.imageButtonBack);

        text01 = findViewById(R.id.textId);
        text01.setEnabled(false);
        text02 = findViewById(R.id.textNick);
        text02.setEnabled(false);

        text03 = findViewById(R.id.textChal);
        text03.setOnClickListener(this);

        selfProduce = findViewById(R.id.editTextSelfProduce);

        buttonMoveChangePW.setOnClickListener(this);
        btn02.setOnClickListener(this);
        btn03.setOnClickListener(this);
        btn04.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        imageButtonBack.setOnClickListener(this);

        imageView.setOnClickListener(view -> openImagePickerFromDrawable());

        if (currentUser != null) {
            loadUserData(userId);
        } else {
            Toast.makeText(this, "로그인 필요", Toast.LENGTH_SHORT).show();
        }

        loadProfileImage();
    }

    private void openImagePickerFromDrawable() {
        final int[] imageResources = {
                R.drawable.woman1, R.drawable.woman2, R.drawable.woman3
                , R.drawable.man1, R.drawable.man2, R.drawable.man3
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("프로필 사진 선택");
        String[] imageNames = {"woman1", "woman2", "woman3", "man1", "man2", "man3"};
        builder.setItems(imageNames, (dialog, which) -> {
            imageView.setImageResource(imageResources[which]);
            saveProfileImageToDatabase(imageNames[which]);
        });
        builder.show();
    }

    private void saveProfileImageToDatabase(String selectedImage) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount");
        databaseReference.child(userId).child("profileImage").setValue(selectedImage)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MyPageActivity.this, "프로필 사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyPageActivity.this, "저장 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadProfileImage() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount");
        databaseReference.child(userId).child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profileImage = snapshot.getValue(String.class);

                    // Firebase에 저장된 이미지 파일명을 바탕으로 이미지 설정
                    if (profileImage != null) {
                        int imageResId = getResources().getIdentifier(profileImage, "drawable", getPackageName());
                        imageView.setImageResource(imageResId);
                    }
                } else {
                    // 프로필 이미지가 없을 경우 기본 이미지 설정
                    imageView.setImageResource(R.drawable.download);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "데이터를 불러오는 도중 오류 발생", error.toException());
                Toast.makeText(MyPageActivity.this, "데이터를 불러오는 도중 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData(String userId) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    if (userAccount != null) {
                        text01.setText("ID : " + userAccount.getIdToken());
                        text02.setText("닉네임 : " + userAccount.getNickname());
                        text03.setText("챌린지 점수 보러가기");
                        selfProduce.setText(userAccount.getSelfProduce());
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
        if (view.getId() == R.id.buttonSave) {
            saveIntroduction();
        }

        if (view.getId() == R.id.buttonMoveChangePW) {
            Intent intent = new Intent(this, ChangePwActivity.class);
            startActivity(intent);
        }

        if (view.getId() == R.id.buttonNote) {
            Intent intent = new Intent(this, MistakeNotes.class);
            startActivity(intent);
        }

        if (view.getId() == R.id.imageButtonBack) {
            Intent intent = new Intent(this, MainActivity.class);
            finish();
            startActivity(intent);
        }

        // 챌린지 점수 모달창
        if (view.getId() == R.id.textChal) {
            showChallengeInfoDialog();
        }
    }

    private void saveIntroduction() {
        // EditText에서 자기소개 텍스트 가져오기
        String introduction = selfProduce.getText().toString().trim();

        // 입력값 검증: 자기소개가 비어 있는 경우
        if (introduction.isEmpty()) {
            Toast.makeText(this, "자기 소개를 입력하세요.", Toast.LENGTH_SHORT).show();
        } else {
            // Firebase Authentication을 통해 현재 로그인한 사용자의 UID 가져오기
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Firebase Realtime Database의 사용자 계정 노드에 접근
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount");

            // 자기소개 저장
            databaseReference.child(userId).child("selfProduce").setValue(introduction)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // 저장 성공 시 사용자에게 알림
                                Toast.makeText(MyPageActivity.this, "자기소개가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                // 저장 실패 시 에러 메시지 출력
                                Toast.makeText(MyPageActivity.this, "저장 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void showChallengeInfoDialog() {
        // AlertDialog에 사용할 커스텀 레이아웃을 설정
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_challenge_info, null);

        // AlertDialog Builder 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // 다이얼로그 생성 및 표시
        AlertDialog dialog = builder.create();
        dialog.show();

        // 커스텀 레이아웃의 뷰 요소에 접근
        TextView textChallengeInfo = dialogView.findViewById(R.id.textChallengeInfo);
        Button buttonClose = dialogView.findViewById(R.id.buttonClose);

        // 챌린지 정보 설정
        databaseReference.child(userId).child("challengeLevel").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ChallengeLevel challengeLevel = snapshot.getValue(ChallengeLevel.class);
                    if (challengeLevel != null) {
                        // correctLevel이 0이라면 1로 설정
                        Integer correctLevel = (challengeLevel.getCorrectLevel() == null) ? 1 : challengeLevel.getCorrectLevel();
                        // blankLevel이 0이라면 1로 설정
                        Integer blankLevel = (challengeLevel.getBlankLevel() == null) ? 1 : challengeLevel.getBlankLevel();
                        // bwriteLevel이 0이라면 1로 설정
                        Integer bwriteLevel = (challengeLevel.getBwriteLevel() == null) ? 1 : challengeLevel.getBwriteLevel();

                        textChallengeInfo.setText("현재 챌린지 단계 " + "\n\n"
                                + "초급 : " + correctLevel + "단계 \n"
                                + "중급 : " + blankLevel + "단계 \n"
                                + "고급 : " + bwriteLevel + "단계 \n");
                    } else {
                        Toast.makeText(MyPageActivity.this, "사용자 데이터가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    textChallengeInfo.setText("아직 도전하지 않은 상태입니다.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FireBase", "데이터를 불러오는중 오류 발생", error.toException());
                Toast.makeText(MyPageActivity.this, "데이터를 불러오는 도중 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
        // 닫기 버튼 클릭 리스너
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // 다이얼로그 닫기
            }
        });
    }

}