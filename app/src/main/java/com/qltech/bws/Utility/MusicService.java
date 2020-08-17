/*
package com.qltech.bws.Utility;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;

import java.util.ArrayList;

public class MusicService extends Service {

    private final MediaPlayer mp = new MediaPlayer();

    private final IBinder localBinder = new LocalBinder();

    ArrayList<Song> songs = new ArrayList<>();
    String url = "https://brainwellnessspa.com.au/Bws-consumer-panel/html/audio_file/Brain_Wellness_Spa_The_Happiness_Promise.mp3";

    boolean firstAttempt = true;



    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;

    }

    public class LocalBinder extends Binder {
        MusicService getService(){
            return  MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setSongsList();
        initMediaPlayer();
    }

    void initMediaPlayer(){
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {

                    if(firstAttempt){
                        firstAttempt = false;
                    }else{
                        nextSong();
                        playSong();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    void setSongsList(){

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.MediaColumns.DISPLAY_NAME+"";

        String[] projection = {
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };


        Cursor cursor = getApplicationContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder);

        while(cursor.moveToNext()){
            songs.add(new Song(cursor.getString(1), cursor.getString(0), cursor.getString(3), cursor.getString(2)));
        }
    }

    Song getPlayingSong(){
        return songs.get(nowPlaying);
    }



//--------Song handlers start here--------

    int nowPlaying = 0;
    int seekLength = 0;

    void playSong(int index) throws Exception {
        if(index != nowPlaying){
            seekLength = 0;
        }
        nowPlaying = index;
        playSong();
    }

    void playSong() throws Exception {
        mp.reset();
        Uri path = Uri.parse(url);
        mp.setDataSource(String.valueOf(path));
        mp.prepare();
        mp.seekTo(seekLength);
        mp.start();
    }

    void pauseSong(){
        mp.pause();
        seekLength = mp.getCurrentPosition();
    }


    void nextSong() throws Exception {
        nowPlaying = nowPlaying+1;
        if (nowPlaying == songs.size()){
            nowPlaying = 0;
        }
        seekLength = 0;
        if(mp.isPlaying()){
            playSong();
        }
    }

    void prevSong() throws Exception {
        nowPlaying = nowPlaying-1;
        if(nowPlaying < 0){
            nowPlaying = songs.size()-1;
        }
        seekLength = 0;
        if(mp.isPlaying()){
            playSong();
        }
    }

    void queueSong(int index){

    }




//-------------Returning MediaPlayer details-------------

    boolean isPlaying(){
        return mp.isPlaying();
    }

    int getDuration(){
        return mp.getDuration();
    }


    int getCurrentPosition(){
        return mp.getCurrentPosition();
    }


    void seekTo(int length){
        seekLength = length;
        mp.seekTo(length);
    }


//------------Returning Song details----------

    Song getSongByIndex(int index){
        return songs.get(index);
    }
    }*/
