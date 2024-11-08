package com.example.enkoquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.user.SignInActivity;
import com.example.enkoquest.user.SignUpActivity;

public class LoginIndexActivity extends AppCompatActivity implements View.OnClickListener{
    Button registerButton;
    Button loginButton;
    ImageButton imageButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_index);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerButton = findViewById(R.id.signUpButton);
        loginButton = findViewById(R.id.signInButton);
        imageButtonBack = findViewById(R.id.imageButtonBack);

        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        imageButtonBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signUpButton){
            Intent intent = new Intent(LoginIndexActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.signInButton){
            Intent intent = new Intent(LoginIndexActivity.this, SignInActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.imageButtonBack){
            Intent intent = new Intent(LoginIndexActivity.this, MainActivity.class);
            startActivity(intent);
        }



    }
}