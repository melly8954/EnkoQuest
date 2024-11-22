package com.example.enkoquest.quest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {

    private final List<Quest> questList; // 퀘스트 데이터를 담을 리스트
    private final OnRewardCollectedListener listener; // 보상 수령 이벤트 리스너

    // 생성자
    public QuestAdapter(List<Quest> questList, OnRewardCollectedListener listener) {
        this.questList = questList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // quest_item.xml을 인플레이트하여 ViewHolder 생성
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quest_item, parent, false);
        return new QuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {
        // 현재 퀘스트 데이터 가져오기
        Quest quest = questList.get(position);

        // 데이터를 ViewHolder에 바인딩
        holder.titleTextView.setText(quest.getTitle());
        holder.descriptionTextView.setText(quest.getDescription());
        holder.rewardTextView.setText("보상: " + quest.getReward() + "포인트");

        // 상태에 따라 버튼 활성화/비활성화 및 텍스트 변경
        if (quest.getStatus().equals("completed")) {
            holder.collectButton.setEnabled(true); // 버튼 활성화
            holder.collectButton.setText("보상 받기");
        } else if (quest.getStatus().equals("rewarded")) {
            holder.collectButton.setEnabled(false); // 버튼 비활성화
            holder.collectButton.setText("보상 완료");
        } else {
            holder.collectButton.setEnabled(false); // 기본 비활성화
            holder.collectButton.setText("진행 중");
        }

        // "보상 받기" 버튼 클릭 이벤트 처리
        holder.collectButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRewardCollected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questList.size(); // 퀘스트 개수 반환
    }

    // ViewHolder 내부 클래스
    static class QuestViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, rewardTextView;
        Button collectButton;

        public QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            // quest_item.xml의 뷰 연결
            titleTextView = itemView.findViewById(R.id.questTitle);
            descriptionTextView = itemView.findViewById(R.id.questDescription);
            rewardTextView = itemView.findViewById(R.id.questReward);
            collectButton = itemView.findViewById(R.id.collectButton);
        }
    }

    // 보상 수령 이벤트를 처리하는 인터페이스
    public interface OnRewardCollectedListener {
        void onRewardCollected(int position); // 퀘스트의 위치를 기반으로 이벤트 처리
    }
}
