package com.example.enkoquest.quest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enkoquest.R;

import java.util.List;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {
    private List<Quest> questList;
    private OnRewardClickListener rewardClickListener;

    public QuestAdapter(List<Quest> questList, OnRewardClickListener rewardClickListener) {
        this.questList = questList;
        this.rewardClickListener = rewardClickListener;
    }

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_quest, parent, false);
        return new QuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {
        Quest quest = questList.get(position);
        holder.bind(quest);

        holder.rewardButton.setOnClickListener(v -> {
            if (rewardClickListener != null) {
                rewardClickListener.onRewardClick(quest, position);
            }
        });

        holder.rewardButton.setEnabled(quest.isRewardAvailable());
    }

    @Override
    public int getItemCount() {
        return questList.size();
    }

    public static class QuestViewHolder extends RecyclerView.ViewHolder {
        TextView questTitle, questDescription;
        Button rewardButton;

        public QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            questTitle = itemView.findViewById(R.id.questTitle);
            questDescription = itemView.findViewById(R.id.questDescription);
            rewardButton = itemView.findViewById(R.id.rewardButton);
        }

        public void bind(Quest quest) {
            questTitle.setText(quest.getTitle());
            questDescription.setText(quest.getDescription());
            rewardButton.setText("보상받기");
            rewardButton.setEnabled(quest.isRewardAvailable());
        }
    }

    public interface OnRewardClickListener {
        void onRewardClick(Quest quest, int position);
    }
}

