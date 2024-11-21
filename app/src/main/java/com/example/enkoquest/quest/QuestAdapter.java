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

    private List<Quest> questList; //퀘스트 리스트
    private OnRewardClickListener rewardClickListener; //보상받기 이벤트 리스너

    //보상받기 버튼 클릭 리스너 인터페이스 정의
    public interface RewardListener {
        void onRewardCollected(int position);
    }

    public QuestAdapter(List<Quest> questList, RewardListener rewardListener) {
        this.questList = questList;
        this.rewardListener = rewardListener;
    }

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_quest, parent, false);
        return new QuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {
        //퀘스트 데이터를 ViewHolder에 바인딩
        Quest quest = questList.get(position);
        holder.titleTextView.setText(quest.getTitle());
        holder.statusTextView.setText(quest.getStatus());

        //상태에 따라 보상받기 버튼 상태를 업데이트
        if("completed".equals(quest.getStatus())) {
            holder.rewardButton.setEnabled(true);
        } else {
            holder.rewardButton.setEnabled(false);
        }

        //보상받기 버튼 클릭 이벤트 설정
        holder.rewardButton.setOnClickListener(v -> {
            if (rewardListener != null) {
                rewardListener.onRewardCollected(position); // 클릭된 퀘스트 위치를 리스너에 전달
            }
        });
    }

    @Override
    public int getItemCount() {
        return questList.size(); //퀘스트 개수 반환
    }

    //ViewHolder 내부 클래스
    public static class QuestViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, statusTextView;
        Button rewardButton;

        public QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.questTitle);
            statusTextView = itemView.findViewById(R.id.questStatus);
            rewardButton = itemView.findViewById(R.id.rewardButton);
        }
    }
}

