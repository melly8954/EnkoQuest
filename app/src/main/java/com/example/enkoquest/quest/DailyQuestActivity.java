package com.example.enkoquest.quest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DailyQuestActivity extends AppCompatActivity {

    //퀘스트를 보여줄 RecyclerView
    private RecyclerView questRecyclerView;
    //"일괄 수령" 버튼
    private Button collectAllButton;
    //RecyclerView의 어댑터
    private QuestAdapter questAdapter;
    //퀘스트 데이터를 저장할 리스트
    private List<Quest> questList = new ArrayList<>();
    //FireBase database 참조
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_quest);

        //View 초기화
        questRecyclerView = findViewById(R.id.questRecyclerView);
        collectAllButton = findViewById(R.id.collectAllButton);

        //RecyclerView 설정
        //RecyclerView를 세로로 나열
        questRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questAdapter = new QuestAdapter(questList, this::onRewardCollected);
        questRecyclerView.setAdapter(questAdapter);

        //Firebase에서 "DailyQuests" 노드 참조
        databaseReference = FirebaseDatabase.getInstance().getReference("DailyQuests/quests");

        //Firebase에서 퀘스트 데이터 로드
        loadQuestsFromFirebase();

        //"일괄 수령" 버튼 클릭 이벤트 처리
        collectAllButton.setOnClickListener(v -> collectAllRewards());
    }

    //Firebase에서 퀘스트 데이터를 불러오는 메소드
    private void loadQuestsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questList.clear(); //기존 리스트 초기화
                //Firebase에서 가져온 데이터를 반복 처리
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Quest quest = snapshot.getValue(Quest.class);
                    if(quest != null) {
                        questList.add(quest);
                    }
                }
                questAdapter.notifyDataSetChanged(); //데이터 변경을 어댑터에 알림
                updateCollectAllButtonState(); //"일괄 수령" 버튼 상태 업데이트
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //데이터 로드 실패 시 사용자에게 알림
                Toast.makeText(DailyQuestActivity.this, "데이터 로드 실패: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //개별 퀘스트 보상 수령 처리
    private void onRewardCollected(int position) {
        //리스트에서 해당 퀘스트 가져오기
        Quest quest = questList.get(position);
        //퀘스트 상태가 완료인 상태에만 보상 수령 가능
        if("completed".equals(quest.getStatus())) {
            //퀘스트 상태를 rewarded로 변경
            quest.setStatus("rewarded");
            //Firebase 업데이트
            databaseReference.child(quest.getId()).child("status").setValue("rewarded");
            questAdapter.notifyItemChanged(position);
            updateCollectAllButtonState();
            Toast.makeText(this, quest.getTitle() + "보상을 받았습니다!", Toast.LENGTH_SHORT).show();
        }
    }

    //"일괄 수령" 버튼 클릭 시 모든 완료된 퀘스트 보상 수령
    private void collectAllRewards() {
        boolean collectedAny = false; //보상 수령 여부 추적
        for(Quest quest : questList) {
            if("completed".equals(quest.getStatus())) { //상태가 "completed" 이면
                quest.setStatus("rewarded"); //상태를 "rewarded"로 변경
                databaseReference.child(quest.getId()).child("status").setValue("rewarded"); //Firebase 업데이트
                collectedAny = true; //보상을 수령한 것으로 변경
            }
        }
        if(collectedAny) { //보상이 하나 이상 수령된 경우
            questAdapter.notifyDataSetChanged(); //전쳬 UI 업데이트
            updateCollectAllButtonState(); //버튼 상태 업데이트
            Toast.makeText(this, "모든 보상을 수령했습니다!", Toast.LENGTH_SHORT).show();
        }
    }

    //"일괄 수령" 버튼 활성화 상태 업데이트
    private void updateCollectAllButtonState() {
        boolean hasCollectible = false; //수령 가능한 보상 있는지 추적
        for (Quest quest : questList) {
            if("completed".equals(quest.getStatus())) { //완료된 퀘스트가 있을 경우
                hasCollectible = true;
                break;
            }
        }
        collectAllButton.setEnabled(hasCollectible); //"일괄 수령" 버튼 활성화/비활성화
    }
}
