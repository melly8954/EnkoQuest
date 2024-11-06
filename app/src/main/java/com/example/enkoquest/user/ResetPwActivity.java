package com.example.enkoquest.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class ResetPwActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextEmail;
    Button buttonResetPw;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String strEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_pw);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextEmail = findViewById(R.id.editTextEmail);
        buttonResetPw = findViewById(R.id.buttonResetPw);

        buttonResetPw.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View view) {
        strEmail = editTextEmail.getText().toString().trim();
        if (!TextUtils.isEmpty(strEmail)) {
            ResetPassword();
        } else {
            editTextEmail.setError("Email field can't be empty");
        }
    }
    private void ResetPassword() {
        mAuth.sendPasswordResetEmail(strEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ResetPwActivity.this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPwActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ResetPwActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}