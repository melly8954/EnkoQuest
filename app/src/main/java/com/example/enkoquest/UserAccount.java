package com.example.enkoquest;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserAccount{
    private String idToken; // Firebase UID
    private String userName;
    private String email;   // 사용자 이메일
    private String password; // 사용자 비밀번호
    private String tel;

    // 기본 생성자 (Firebase에서 데이터를 읽어올 때 필요)
    public UserAccount() {}

    // 매개변수를 받는 생성자
    public UserAccount(String idToken, String email, String password) {
        this.idToken = idToken;
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
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