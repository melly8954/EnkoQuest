package com.example.enkoquest.user;

public class ChallengeLevel {
    private Integer blankLevel;
    private Integer correctLevel;
    private Integer bwriteLevel;

    public ChallengeLevel() {
    }

    public ChallengeLevel(Integer blankLevel, Integer correctLevel, Integer bwriteLevel) {
        this.blankLevel = blankLevel;
        this.correctLevel = correctLevel;
        this.bwriteLevel = bwriteLevel;
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

    public Integer getBwriteLevel() {
        return bwriteLevel;
    }

    public void setBwriteLevel(Integer bwriteLevel) {
        this.bwriteLevel = bwriteLevel;
    }
}
