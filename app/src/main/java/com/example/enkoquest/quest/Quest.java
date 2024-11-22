package com.example.enkoquest.quest;

public class Quest {
    private String id;           // 퀘스트 고유 ID
    private String title;        // 퀘스트 제목
    private String description;  // 퀘스트 설명
    private int reward;          // 보상 (정수형)
    private String status;       // 상태 (e.g., "pending", "completed", "rewarded")

    // 기본 생성자 (Firebase에서 객체로 변환 시 필요)
    public Quest() {}

    // 모든 필드를 초기화하는 생성자
    public Quest(String id, String title, String description, int reward, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.reward = reward;
        this.status = status;
    }

    // Getter 및 Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
