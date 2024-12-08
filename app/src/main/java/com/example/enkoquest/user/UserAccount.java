package com.example.enkoquest.user;

import java.util.Map;

public class UserAccount {
    private String idToken; // Firebase UID
    private String nickname;
    private String email;   // 사용자 이메일
    private String tel;
    private String selfProduce;
    private ChallengeLevel challengeLevel;
    private Map<String, String> savedWord;
    private String profileImage;

    // 기본 생성자 (Firebase에서 데이터를 읽어올 때 필요)
    public UserAccount() {
    }

    // 매개변수를 받는 생성자
    public UserAccount(String idToken, String nickname
            , String email, String tel
            , String selfProduce, ChallengeLevel challengeLevel
            , Map<String, String> savedWord
            , String profileImage) {
        this.idToken = idToken;
        this.nickname = nickname;
        this.email = email;
        this.tel = tel;
        this.selfProduce = selfProduce;
        this.challengeLevel = challengeLevel;
        this.savedWord = savedWord;
        this.profileImage = profileImage;
    }

    // Getter와 Setter
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSelfProduce() {
        return selfProduce;
    }

    public ChallengeLevel getChallengeLevel() {
        return challengeLevel;
    }

    public void setChallengeLevel(ChallengeLevel challengeLevel) {
        this.challengeLevel = challengeLevel;
    }

    public void setSelfProduce(String selfProduce) {
        this.selfProduce = selfProduce;
    }

    public void setSavedWord(Map<String, String> savedWord) {
        this.savedWord = savedWord;
    }

    public String getProfileImage(){
        return profileImage;
    }
    public void setProfileImage(String profileImage){
        this.profileImage = profileImage;
    }

}