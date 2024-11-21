package com.example.enkoquest.quest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enkoquest.R;

import java.util.ArrayList;
import java.util.List;

public class DailyQuestActivity extends AppCompatActivity {
    private RecyclerView questRecyclerView;
    private Button collectAllButton;
    private QuestAdapter questAdapter;
    private List<Quest> questList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_quest);

        questRecyclerView = findViewById(R.id.questRecyclerView);
        collectAllButton = findViewById(R.id.collectAllButton);

        // Dummy data for quests
        questList.add(new Quest("로그인 감사 보상", "하루 한 번 접속 시 보상", true));
        questList.add(new Quest("초보 탐험가", "초급 탐험 완료", false));
        questList.add(new Quest("정예 탐험가", "정예 탐험 완료", false));

        questAdapter = new QuestAdapter(questList, (quest, position) -> {
            if (quest.isRewardAvailable()) {
                quest.setRewardAvailable(false); // 보상을 수령하면 비활성화
                Toast.makeText(this, quest.getTitle() + " 보상을 수령했습니다.", Toast.LENGTH_SHORT).show();
                questAdapter.notifyItemChanged(position);
                updateCollectAllButtonState();
            }
        });

        questRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questRecyclerView.setAdapter(questAdapter);

        collectAllButton.setOnClickListener(v -> {
            for (Quest quest : questList) {
                if (quest.isRewardAvailable()) {
                    quest.setRewardAvailable(false);
                }
            }
            Toast.makeText(this, "모든 보상을 수령했습니다.", Toast.LENGTH_SHORT).show();
            questAdapter.notifyDataSetChanged();
            updateCollectAllButtonState();
        });

        updateCollectAllButtonState();
    }

    private void updateCollectAllButtonState() {
        boolean anyRewardAvailable = false;
        for (Quest quest : questList) {
            if (quest.isRewardAvailable()) {
                anyRewardAvailable = true;
                break;
            }
        }
        collectAllButton.setEnabled(anyRewardAvailable);
    }
}
