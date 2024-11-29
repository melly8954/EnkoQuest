package com.example.enkoquest.quest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enkoquest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DailyQuestActivity extends AppCompatActivity {

    // UI 요소
    private RecyclerView questRecyclerView;
    private Button collectAllButton;

    // Firebase 및 데이터
    private DatabaseReference databaseReference;
    private List<Quest> questList = new ArrayList<>();
    private QuestAdapter questAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_quest);

        // View 초기화
        questRecyclerView = findViewById(R.id.questRecyclerView);
        collectAllButton = findViewById(R.id.collectAllButton);

        // RecyclerView 설정
        questRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questAdapter = new QuestAdapter(questList, this::onRewardCollected);
        questRecyclerView.setAdapter(questAdapter);

        // Firebase 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference("DailyQuests/quests");

        // Firebase 데이터 로드
        fetchQuestsFromFirebase();

        // 일괄 수령 버튼 클릭 이벤트
        collectAllButton.setOnClickListener(v -> collectAllRewards());
    }

    // Firebase에서 데이터 가져오기
    private void fetchQuestsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questList.clear(); // 기존 리스트 초기화

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Quest quest = snapshot.getValue(Quest.class); // Firebase 데이터를 Quest 객체로 매핑
                    if (quest != null) {
                        questList.add(quest);
                    }
                }

                questAdapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
                updateCollectAllButtonState(); // 버튼 활성화 상태 업데이트
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DailyQuestActivity.this, "퀘스트 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 개별 퀘스트 보상 수령 처리
    private void onRewardCollected(int position) {
        Quest quest = questList.get(position);
        if ("completed".equals(quest.getStatus())) {
            quest.setStatus("rewarded");
            databaseReference.child(quest.getId()).child("status").setValue("rewarded"); // Firebase 업데이트
            questAdapter.notifyItemChanged(position); // 해당 항목 업데이트
            updateCollectAllButtonState(); // 버튼 상태 업데이트
            Toast.makeText(this, quest.getTitle() + " 보상을 받았습니다!", Toast.LENGTH_SHORT).show();
        }
    }

    // 일괄 수령 버튼 클릭 처리
    private void collectAllRewards() {
        boolean collectedAny = false;
        for (Quest quest : questList) {
            if ("completed".equals(quest.getStatus())) {
                quest.setStatus("rewarded");
                databaseReference.child(quest.getId()).child("status").setValue("rewarded");
                collectedAny = true;
            }
        }
        if (collectedAny) {
            questAdapter.notifyDataSetChanged();
            updateCollectAllButtonState();
            Toast.makeText(this, "모든 보상을 수령했습니다!", Toast.LENGTH_SHORT).show();
        }
    }

    // 일괄 수령 버튼 활성화/비활성화 업데이트
    private void updateCollectAllButtonState() {
        boolean hasCollectible = false;
        for (Quest quest : questList) {
            if ("completed".equals(quest.getStatus())) {
                hasCollectible = true;
                break;
            }
        }
        collectAllButton.setEnabled(hasCollectible); // 버튼 상태 업데이트
    }
}
