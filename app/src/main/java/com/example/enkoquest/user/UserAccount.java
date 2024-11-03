package com.example.enkoquest.user;

public class UserAccount{
    private String idToken; // Firebase UID
    private String nickname;
    private String email;   // 사용자 이메일
    private String password; // 사용자 비밀번호
    private String tel;

    // 기본 생성자 (Firebase에서 데이터를 읽어올 때 필요)
    public UserAccount() {}

    // 매개변수를 받는 생성자
    public UserAccount(String idToken,String nickName, String email, String password,String tel) {
        this.idToken = idToken;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.tel = tel;
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

    public void setNickname(String nickName){
        this.nickname = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}