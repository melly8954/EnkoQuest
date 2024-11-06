package com.example.enkoquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enkoquest.user.SignInActivity;
import com.example.enkoquest.user.SignUpActivity;

public class IntroActivity extends AppCompatActivity implements View.OnClickListener{
    Button registerButton;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerButton = findViewById(R.id.signUpButton);
        loginButton = findViewById(R.id.signInButton);

        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signUpButton){
            Intent intent = new Intent(IntroActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.signInButton){
            Intent intent = new Intent(IntroActivity.this, SignInActivity.class);
            startActivity(intent);
        }


    }
}