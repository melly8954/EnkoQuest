package com.example.enkoquest.user;

public class ChallengeLevel {
    private Integer blankLevel;
    private Integer correctLevel;

    public ChallengeLevel() {
    }

    public ChallengeLevel(Integer blankLevel, Integer correctLevel) {
        this.blankLevel = blankLevel;
        this.correctLevel = correctLevel;
    }

    public Integer getBlankLevel() {
        return blankLevel;
    }

    public void setBlankLevel(Integer blankLevel){
        this.blankLevel = blankLevel;
    }


    public Integer getCorrectLevel() {
        return correctLevel;
    }

    public void setCorrectLevel(Integer correctLevel) {
        this.correctLevel = correctLevel;
    }
}
