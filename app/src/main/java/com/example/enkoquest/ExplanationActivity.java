package com.example.enkoquest;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.enkoquest.R;

public class ExplanationActivity extends AppCompatActivity {

    private TextView explanationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explanation);

        explanationTextView = findViewById(R.id.explanationTextView);

        // Intent로 전달된 해설 데이터를 받아서 표시
        String explanation = getIntent().getStringExtra("EXPLANATION");
        if (explanation != null) {
            explanationTextView.setText(explanation);
        }
    }
}
