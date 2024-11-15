package com.example.enkoquest.user;

public class UserAccount {
    private String idToken; // Firebase UID
    private String nickname;
    private String email;   // 사용자 이메일
    private String tel;
    private String selfProduce;
    private String challengeLevel;

    // 기본 생성자 (Firebase에서 데이터를 읽어올 때 필요)
    public UserAccount() {
    }

    // 매개변수를 받는 생성자
    public UserAccount(String idToken, String nickName
            , String email, String tel
            , String selfProduce, String challengeLevel) {
        this.idToken = idToken;
        this.nickname = nickname;
        this.email = email;
        this.tel = tel;
        this.selfProduce = selfProduce;
        this.challengeLevel = challengeLevel;
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

    public void setNickname(String nickName) {
        this.nickname = nickName;
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

    public void setSelfProduce(String selfProduce) {
        this.selfProduce = selfProduce;
    }

    public void setChallengeLevel(String challengeLevel) {
        this.challengeLevel = challengeLevel;
    }
}