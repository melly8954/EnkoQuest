package com.example.enkoquest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
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

    private Button buttonMoveChangePW, btn03, buttonSave, btnLevel;
    private ImageButton imageButtonBack;
    private TextView text01, text02;
    private EditText selfProduce;
    private ImageView imageView;
    private Uri selectedImageUri;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
        btn03 = findViewById(R.id.buttonNote);
        buttonSave = findViewById(R.id.buttonSave);
        btnLevel = findViewById(R.id.btnLevel);
        imageView = findViewById(R.id.imageViewProfilImg);
        imageButtonBack = findViewById(R.id.imageButtonBack);

        text01 = findViewById(R.id.textId);
        text01.setEnabled(false);
        text02 = findViewById(R.id.textNick);
        text02.setEnabled(false);


        selfProduce = findViewById(R.id.editTextSelfProduce);

        buttonMoveChangePW.setOnClickListener(this);
        btn03.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        btnLevel.setOnClickListener(this);
        imageButtonBack.setOnClickListener(this);

        imageView.setOnClickListener(view -> openImagePickerFromDrawable());

        if (currentUser != null) {
            loadUserData(userId);
        } else {
            Toast.makeText(this, "로그인 필요", Toast.LENGTH_SHORT).show();
        }

        loadProfileImage();
    }

    // 이미지 선택 기능을 여기에 구현합니다.
    private void openImagePickerFromDrawable() {
        final int[] imageResources = {
                R.drawable.woman1, R.drawable.woman2, R.drawable.woman3,
                R.drawable.man1, R.drawable.man2, R.drawable.man3
        };
        String[] imageNames = {"woman1", "woman2", "woman3", "man1", "man2", "man3"};

        // 다이얼로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_profile_image_picker, null);
        builder.setView(dialogView);

        builder.setCancelable(true);

        // GridLayout 설정
        GridLayout gridLayout = dialogView.findViewById(R.id.gridImagePicker);
        gridLayout.setColumnCount(3);  // 3개의 열로 설정

        // GridLayout에 이미지 버튼 추가
        for (int i = 0; i < imageResources.length; i++) {
            final int imageResId = imageResources[i];
            final String imageName = imageNames[i];

            // 동적으로 ImageView 생성
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageResId);
            imageView.setPadding(16, 16, 16, 16);

            // 이미지 크기 조정
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;  // 가로 크기를 0dp로 설정하여 비율을 맞춤
            params.height = 200;  // 이미지의 높이를 고정값으로 설정 (여기서는 200dp)
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // 세로 비율 설정
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // 가로 비율 설정
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // 이미지 크기 조정

            // 클릭 이벤트: 이미지 선택
            imageView.setOnClickListener(v -> {
                this.imageView.setImageResource(imageResId); // 프로필 이미지 설정
                saveProfileImageToDatabase(imageName); // 선택한 이미지 저장
                Toast.makeText(this, "선택한 사진: " + imageName, Toast.LENGTH_SHORT).show();
            });

            // GridLayout에 추가
            gridLayout.addView(imageView);
        }

        // 다이얼로그 표시
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // 선택된 이미지 저장
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
    // 프로필 이미지 불러오기
    private void loadProfileImage() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount");
        databaseReference.child(userId).child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profileImage = snapshot.getValue(String.class);
                    if (profileImage != null) {
                        int imageResId = getResources().getIdentifier(profileImage, "drawable", getPackageName());
                        imageView.setImageResource(imageResId); // 저장된 이미지 적용
                    }
                } else {
                    imageView.setImageResource(R.drawable.man1); // 기본 이미지 설정
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyPageActivity.this, "데이터 불러오기 오류", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(this, FavoriteWord.class);
            startActivity(intent);
        }

        if (view.getId() == R.id.imageButtonBack) {
            Intent intent = new Intent(this, MainActivity.class);
            finish();
            startActivity(intent);
        }

        // 챌린지 점수 모달창
        if (view.getId() == R.id.btnLevel) {
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