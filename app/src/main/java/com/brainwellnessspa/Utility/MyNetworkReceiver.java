package com.brainwellnessspa.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.downloader.PRDownloader;
import com.downloader.Status;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity.AudioInterrupted;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadIdOne;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;

public class MyNetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        boolean status = BWSApplication.isNetworkConnected(context);

        Log.d("network",String.valueOf(status));
        if(!status) {
            if (isDownloading) {
                PRDownloader.pause(downloadIdOne);
                isDownloading = false;
//                BWSApplication.showToast(String.valueOf(status)+Status.valueOf(PRDownloader.getStatus(downloadIdOne).name()),context);
            }
        }else {
            if(player!=null){
//                for(int i = 0;i<5;i++) {
                    if (player.getPlaybackState() == ExoPlayer.STATE_IDLE && AudioInterrupted) {
                        AudioInterrupted = false;
                        player.setPlayWhenReady(true);
                        player.prepare();
                        player.seekTo(player.getCurrentPosition());
                        Log.e("Exo PLayer Net:", "Player Resume after Net");
                    }
//                }
            }
            if (!isDownloading) {
                List<String> fileNameList, audioFile, playlistDownloadId;
                if (BWSApplication.isNetworkConnected(context)) {
                    SharedPreferences sharedx = context.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                    Gson gson = new Gson();
                    String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
                    String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
                    String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
                    if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
                        Type type = new TypeToken<List<String>>() {
                        }.getType();
                        fileNameList = gson.fromJson(json, type);
                        audioFile = gson.fromJson(json1, type);
                        playlistDownloadId = gson.fromJson(json2, type);
                   /* fileNameList = new ArrayList<>();
                    audioFile = new ArrayList<>();
                    playlistDownloadId = new ArrayList<>();
                    SharedPreferences sharedxxx = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedxxx.edit();
                    String nameJson = gson.toJson(fileNameList);
                    String urlJson = gson.toJson(audioFile);
                    String playlistIdJson = gson.toJson(playlistDownloadId);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                    editor.commit();*/
                        if (fileNameList.size() != 0) {
                            isDownloading = true;
                            DownloadMedia downloadMedia = new DownloadMedia(context.getApplicationContext());
                            downloadMedia.encrypt1(audioFile, fileNameList, playlistDownloadId/*, playlistSongs*/);
                        }
                    }
                }
            }
        }
    }
}