package com.example.enkoquest;

import android.app.Service; // 올바른 Service 클래스
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;


public class BGMService extends Service {
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = true;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.background_bgm);
        mediaPlayer.setLooping(true); //반복 재생 설정
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //음악 재생 시작
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        return START_STICKY; // 서비스 유지
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause(); //음악 중지
                isPlaying = false;
            }
            mediaPlayer.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; //바인딩 사용 x
    }

    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    public void resumeMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
        }
    }
}
