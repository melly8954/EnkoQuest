package com.example.enkoquest.quest;

public class Quest {
    private String title;
    private String description;
    private boolean rewardAvailable;

    public Quest(String title, String description, boolean rewardAvailable) {
        this.title = title;
        this.description = description;
        this.rewardAvailable = rewardAvailable;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRewardAvailable() {
        return rewardAvailable;
    }

    public void setRewardAvailable(boolean rewardAvailable) {
        this.rewardAvailable = rewardAvailable;
    }
}

