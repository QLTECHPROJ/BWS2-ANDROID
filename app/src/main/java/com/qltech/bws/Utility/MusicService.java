package com.qltech.bws.Utility;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
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

import com.google.gson.Gson;
import com.qltech.bws.DashboardModule.Models.AddToQueueModel;
import com.qltech.bws.R;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {
    public static MediaPlayer mediaPlayer;
    public static boolean isPrepare = false, songComplete = false, isMediaStart = false, isStop = false;
    public static boolean isPause = false;
    public static boolean isResume = false;
    public static int oTime = 0, startTime = 0, endTime = 0, forwardTime = 30000, backwardTime = 30000;
    static public Handler handler;
    static boolean isPlaying = false;

    public static void initMediaPlayer() {
        if (null == mediaPlayer) {
            mediaPlayer = new MediaPlayer();
            Log.e("Playinggggg", "Playinggggg");
        }
    }

    public static void play(Uri AudioFile) {
        initMediaPlayer();
        stopMedia();
        playAudio( AudioFile);
    }

    public static void playAudio(Uri AudioFile) {
//        if (!isPLAYING) {
//            isPLAYING = true;
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(String.valueOf(AudioFile));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes
                                .Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build());
            }
            mediaPlayer.prepareAsync();
            isPrepare = true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Failedddddddd", "prepare() failed");
        }
//        }else {
//            isPLAYING = false;
//            stopPlaying();
//        }
    }

    public static void PhoneCall() {
/*
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    //INCOMING call
                    //do all necessary action to pause the audio
                    if (mediaPlayer != null) {//check mp
                        setPlayerButton(true, false, true);

                        if (mediaPlayer.isPlaying()) {

                            mediaPlayer.pause();
                        }
                    }

                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not IN CALL
                    //do anything if the phone-state is idle
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                    //do all necessary action to pause the audio
                    //do something here
                    if (mediaPlayer != null) {//check mp
                        setPlayerButton(true, false, true);

                        if (mediaPlayer.isPlaying()) {

                            mediaPlayer.pause();
                        }
                    }
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
*/
    }

    public static int getEndTime() {
        endTime = mediaPlayer.getDuration();
        return endTime;
    }

    public static int getStartTime() {
        try {
            if (!isMediaStart) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    startTime = 0;
                } else {
                    startTime = 0;
                }
            } else if (isPause) {
                startTime = mediaPlayer.getCurrentPosition();
            } else {
                startTime = mediaPlayer.getCurrentPosition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return startTime;
    }

    public static void ToRepeat(boolean Status) {
        mediaPlayer.setLooping(Status);
    }

    public static void ToForward(Context conext) {
        endTime = getEndTime();
        startTime = mediaPlayer.getCurrentPosition();
        if ((startTime + forwardTime) <= endTime) {
            startTime = startTime + forwardTime;
            mediaPlayer.seekTo(startTime);
        } else {
            showToast("Please wait", conext);
        }
    }

    public static void ToBackward(Context conext) {
        startTime = mediaPlayer.getCurrentPosition();
        if ((startTime - backwardTime) > 0) {
            startTime = startTime - backwardTime;
            mediaPlayer.seekTo(startTime);
        } else {
            showToast("Please wait", conext);
        }
    }

    public static void SeekTo(int CurruntTime) {
        mediaPlayer.seekTo(CurruntTime);
    }

    public static void showToast(String message, Context conext) {
        Toast toast = new Toast(conext);
        View view = LayoutInflater.from(conext).inflate(R.layout.toast_layout, null);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 35);
        toast.setView(view);
        toast.show();
    }

    public static void stopPlaying() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.e("Playinggggg", "Startinggg");
                mediaPlayer.start();
                isMediaStart = true;
            });
        }
    }

    public static void stopMedia() {
        try {
            if (mediaPlayer == null) return;
            if (mediaPlayer.isPlaying()) {
                Log.e("Playinggggg", "stoppppp");
                mediaPlayer.stop();
                isMediaStart = false;
                isPrepare = false;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public static void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            Log.e("Playinggggg", "pauseeeeeee");
            mediaPlayer.pause();
            isPause = true;
//            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    public static boolean isPlaying() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                isPlaying = true;
            }
        } else {
            isPlaying = false;
        }
        return isPlaying;
    }

    public static void savePrefQueue(int position, boolean queue, boolean audio, ArrayList<AddToQueueModel> addToQueueModelList, Context ctx) {
        SharedPreferences shared11 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared11.edit();
        Gson gson11 = new Gson();
        String json11 = gson11.toJson(addToQueueModelList);
        editor.putString(CONSTANTS.PREF_KEY_queueList, json11);
        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, queue);
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, audio);
        editor.commit();
    }

    public static void resumeMedia() {
        if (isMediaStart) {
            if (!mediaPlayer.isPlaying()) {
                Log.e("Playinggggg", "resumeeeeeee");
//            mediaPlayer.seekTo(resumePosition);
                isResume = true;
                mediaPlayer.start();
            }
        }
    }

    public static void releasePlayer() {
        if (null != mediaPlayer) {
            mediaPlayer.release();
        }
    }

    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = totalDuration / 1000;
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void destroyPlayer() {
        stopMedia();
        releasePlayer();
        mediaPlayer = null;
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

}
