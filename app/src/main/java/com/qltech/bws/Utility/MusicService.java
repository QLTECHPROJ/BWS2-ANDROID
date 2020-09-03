package com.qltech.bws.Utility;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.FileDescriptor;
import java.io.IOException;

public class MusicService extends Service {
    static MediaPlayer mediaPlayer;
    static private Handler handler;
    static boolean isPLAYING;

    public MusicService(Handler handler) {
        this.handler = handler;
    }

    private static void initMediaPlayer() {
        if (null == mediaPlayer) {
            mediaPlayer = new MediaPlayer();
            Log.e("Playinggggg", "Playinggggg");
        }
    }

    public static void play(Context conext, Uri AudioFile) {
        initMediaPlayer();
        stopMedia();
        playAudio(conext, AudioFile);
    }

    public static void playAudio(Context conext, Uri AudioFile) {
//        if (!isPLAYING) {
//            isPLAYING = true;
        try {
            mediaPlayer.setDataSource(conext, AudioFile);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes
                                .Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build());
            }
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Failedddddddd", "prepare() failed");
        }
//        }else {
//            isPLAYING = false;
//            stopPlaying();
//        }
    }

    private static void stopPlaying() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.e("Playinggggg", "Startinggg");
                mediaPlayer.start();
            });
        }
    }

    public static void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            Log.e("Playinggggg", "stoppppp");
            mediaPlayer.stop();
        }
    }

    public static void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
//            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
//            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    private void releasePlayer() {
        if (null != mediaPlayer) {
            mediaPlayer.release();
        }
    }

    public void destroyPlayer() {
        stopMedia();
        releasePlayer();
        mediaPlayer = null;
    }
}
