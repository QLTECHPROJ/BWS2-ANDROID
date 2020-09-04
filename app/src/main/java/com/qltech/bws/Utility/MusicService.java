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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.qltech.bws.R;

import java.io.FileDescriptor;
import java.io.IOException;

public class MusicService extends Service {
    static MediaPlayer mediaPlayer;
    static private Handler handler;
    static boolean isPLAYING, isPause = false;
    private static int oTime = 0, startTime = 0, endTime = 0, forwardTime = 30000, backwardTime = 30000;

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
        mediaPlayer = new MediaPlayer();
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

    public static int getEndTime() {
        endTime = mediaPlayer.getDuration();
        return endTime;
    }

    public static int getStartTime() {
        startTime = mediaPlayer.getCurrentPosition();
        return startTime;
    }

    public static void ToForward(Context conext) {
        if ((startTime + forwardTime) <= endTime) {
            startTime = startTime + forwardTime;
            mediaPlayer.seekTo(startTime);
        } else {
            showToast("Please wait", conext);
        }
    }

    public static void ToBackward(Context conext) {
        if ((startTime - backwardTime) > 0) {
            startTime = startTime - backwardTime;
            mediaPlayer.seekTo(startTime);
        } else {
            showToast("Please wait", conext);
        }
    }

    public static void ToSeek(int endTime, int startTime) {
        endTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
    }

    static void showToast(String message, Context conext) {
        Toast toast = new Toast(conext);
        View view = LayoutInflater.from(conext).inflate(R.layout.toast_layout, null);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 35);
        toast.setView(view);
        toast.show();
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
            isPause = true;
//            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    public static boolean isPlaying() {
        boolean playing;
        if (mediaPlayer.isPlaying()) {
            playing = true;
        } else {
            playing = false;
        }
        return playing;
    }

    public static void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
//            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    public static void releasePlayer() {
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
