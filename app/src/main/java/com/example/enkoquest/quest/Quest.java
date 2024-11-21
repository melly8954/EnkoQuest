package com.example.enkoquest.quest;

public class Quest {

    private String id;
    private String title;
    private String description;
    private String status;
    private int reward;

    public Quest() {

    }

    public Quest(String id, String title, String description, String status, int reward) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.reward = reward;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }
}

