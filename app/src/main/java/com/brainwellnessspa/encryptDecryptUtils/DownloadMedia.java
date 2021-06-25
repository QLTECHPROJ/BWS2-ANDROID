package com.brainwellnessspa.encryptDecryptUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.brainwellnessspa.roomDataBase.AudioDatabase;
import com.brainwellnessspa.roomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.utility.CONSTANTS;
import com.brainwellnessspa.utility.MyNetworkReceiver;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Status;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.brainwellnessspa.BWSApplication.DB;
import static com.brainwellnessspa.BWSApplication.addToSegment;
import static com.brainwellnessspa.BWSApplication.appStatus;
import static com.brainwellnessspa.BWSApplication.getAudioDataBase;
import static com.brainwellnessspa.BWSApplication.logout;
import static com.brainwellnessspa.BWSApplication.showToast;
import static com.brainwellnessspa.BWSApplication.PlayerStatus;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.GetCurrentAudioPosition;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.GetDeviceVolume;

public class DownloadMedia implements OnDownloadListener {
    public static int downloadError = 2, downloadIdOne;
    public static String filename = "";
    public static int downloadProgress = 0;
    public static boolean isDownloading = false;
    public static Status status;
    List<DownloadAudioDetails> notDownloadedData;
    int downloadProgress2 = 0;
    List<DownloadAudioDetails> Myaudiolist;
    Context ctx;
    Activity act;
    String CoUserID;
    byte[] encodedBytes;
    Properties p;
    String UserID;
    LocalBroadcastManager lBM;
    Intent localIntent;
    MyNetworkReceiver myNetworkReceiver;
    List<String> fileNameList, audioFile, playlistDownloadId;

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    public DownloadMedia(Context ctx, Activity act) {
        this.ctx = ctx;
        this.act = act;
    }

    public byte[] encrypt1(List<String> DOWNLOAD_AUDIO_URL, List<String> FILE_NAME, List<String> PLAYLIST_ID) {
        //            showToast("Downloading file...",act);

        Log.e("Downloading file..", String.valueOf(downloadProgress));
        DB = getAudioDataBase(ctx);
        localIntent = new Intent("DownloadProgress");
        ctx.registerReceiver(myNetworkReceiver = new MyNetworkReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, AppCompatActivity.MODE_PRIVATE);
        UserID = sharedx.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "");
        CoUserID = sharedx.getString(CONSTANTS.PREFE_ACCESS_UserId, "");
        lBM = LocalBroadcastManager.getInstance(ctx);
        // Setting timeout globally for the download network requests:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder().setDatabaseEnabled(true).build();
        PRDownloader.initialize(ctx, config);
        isDownloading = true;
        fileNameList = FILE_NAME;
        audioFile = DOWNLOAD_AUDIO_URL;
        playlistDownloadId = PLAYLIST_ID;
        filename = FILE_NAME.get(0);

        try {
            downloadIdOne = PRDownloader.download(DOWNLOAD_AUDIO_URL.get(0), FileUtils.getDirPath(ctx), FILE_NAME.get(0) + CONSTANTS.FILE_EXT).build().setOnProgressListener(progress -> {
                long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                downloadProgress = (int) progressPercent;
                if (downloadProgress == 0 || downloadProgress == 1) {
                    updateMediaByDownloadProgress(fileNameList.get(0), playlistDownloadId.get(0), downloadProgress, "Start");
                    downloadProgress2 = downloadProgress;
                } else if (downloadProgress == downloadProgress2 + 10) {
                       /* localIntent.putExtra("Progress", downloadProgress);
                        localIntent.putExtra("name", FILE_NAME.get(0));
                        lBM.sendBroadcast(localIntent);*/
                    updateMediaByDownloadProgress(fileNameList.get(0), playlistDownloadId.get(0), downloadProgress, "Start");
                    downloadProgress2 = downloadProgress;
                }
            })/*.setOnStartOrResumeListener(() -> {
//                    if (Status.PAUSED == status) {
//                        PRDownloader.resume(downloadIdOne);
//                        status = Status.RUNNING;
//                    }
                })*/.setOnPauseListener(() -> {
                if (Status.RUNNING == status) {
                    PRDownloader.pause(downloadIdOne);
                    status = Status.PAUSED;
                }
            }).setOnCancelListener(() -> {
                downloadIdOne = 0;
                LocalBroadcastManager.getInstance(ctx).unregisterReceiver(listener);
                filename = "";
                if (logout) {
                    SharedPreferences preferences11 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit1 = preferences11.edit();
                    edit1.remove(CONSTANTS.PREF_KEY_DownloadName);
                    edit1.remove(CONSTANTS.PREF_KEY_DownloadUrl);
                    edit1.remove(CONSTANTS.PREF_KEY_DownloadPlaylistId);
                    edit1.clear();
                    edit1.commit();
                } else {
                    fileNameList = new ArrayList<>();
                    audioFile = new ArrayList<>();
                    playlistDownloadId = new ArrayList<>();
                    SharedPreferences sharedy1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                    Gson gson = new Gson();
                    String jsony1 = sharedy1.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
                    String json11 = sharedy1.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
                    String jsonq1 = sharedy1.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
                    if (!jsony1.equalsIgnoreCase(String.valueOf(gson))) {
                        Type type = new TypeToken<List<String>>() {
                        }.getType();
                        fileNameList = gson.fromJson(jsony1, type);
                        audioFile = gson.fromJson(json11, type);
                        playlistDownloadId = gson.fromJson(jsonq1, type);
                    }
                    fileNameList.remove(0);
                    audioFile.remove(0);
                    playlistDownloadId.remove(0);
                    filename = "";
                    downloadProgress = 0;
                    downloadProgress2 = 0;
                    SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    String urlJson = gson.toJson(audioFile);
                    String nameJson = gson.toJson(fileNameList);
                    String playlistIdJson = gson.toJson(playlistDownloadId);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                    editor.commit();
                    if (fileNameList.size() == 0) {
                        isDownloading = false;
                        filename = "";
                        downloadProgress = 0;
                        downloadProgress2 = 0;
                    } else if (fileNameList.size() != 0) {
                        encrypt1(audioFile, fileNameList, playlistDownloadId);
                    }
                }
            }).start(this);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return encodedBytes;
    }

    public byte[] decrypt(String FILE_NAME) {
        byte[] decryptedBytes = null;
        //        showToast("Retrieve file...", context);
        try {
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(ctx, FILE_NAME));
            decryptedBytes = EncryptDecryptUtils.decode(EncryptDecryptUtils.getInstance(ctx).getSecretKey(), fileData);
            //            showToast("File Retrieve Done", context);
            return decryptedBytes;
        } catch (Exception e) {
            //            showToast("File Decryption failed.\nException: " + e.getMessage(), context);
            try {
                byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(ctx, FILE_NAME));
                decryptedBytes = EncryptDecryptUtils.decode(EncryptDecryptUtils.getInstance(ctx).getSecretKey(), fileData);
                //            showToast("File Retrieve Done", context);
                return decryptedBytes;
            } catch (Exception ez) {
                //            showToast("File Decryption failed.\nException: " + e.getMessage(), context);
                Log.e("erssssssssssss", ez.getMessage());
            }
            Log.e("erssssssssssss", e.getMessage());
        }
        return decryptedBytes;
    }

    @Override
    public void onDownloadComplete() {
        downloadProgress2 = 0;
        try {
            SharedPreferences sharedxx = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, AppCompatActivity.MODE_PRIVATE);
            UserID = sharedxx.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "");
            CoUserID = sharedxx.getString(CONSTANTS.PREFE_ACCESS_UserId, "");
            updateMediaByDownloadProgress(fileNameList.get(0), playlistDownloadId.get(0), 100, "Complete");
            SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
            Gson gson1 = new Gson();
            String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson1));
            String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson1));
            String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson1));
            fileNameList.clear();
            audioFile.clear();
            playlistDownloadId.clear();
            fileNameList = new ArrayList<>();
            audioFile = new ArrayList<>();
            playlistDownloadId = new ArrayList<>();
            if (!json1.equalsIgnoreCase(String.valueOf(gson1))) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                List<String> fileNameList1 = gson1.fromJson(json, type);
                List<String> audioFile1 = gson1.fromJson(json1, type);
                List<String> playlistId1 = gson1.fromJson(json2, type);
                if (fileNameList1.size() != 0) {
                    fileNameList.addAll(fileNameList1);
                    audioFile.addAll(audioFile1);
                    playlistDownloadId.addAll(playlistId1);
                }
            }
            if (playlistDownloadId.get(0).equalsIgnoreCase("")) {
                p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("audioName", fileNameList.get(0));
                p.putValue("playerType", PlayerStatus);
                p.putValue("audioService", appStatus(ctx));
                p.putValue("position", GetCurrentAudioPosition());
                p.putValue("source", "Downloaded Audios");
                p.putValue("bitRate", "");
                p.putValue("sound", GetDeviceVolume(ctx));
                addToSegment("Audio Download Completed", p, CONSTANTS.track);
                showToast("Your audio has been downloaded", act);
            } else if (!playlistDownloadId.get(0).equalsIgnoreCase("")) {
                DB = getAudioDataBase(ctx);
                DB = getAudioDataBase(ctx);
                DB.taskDao().getNotDownloadPlayListData("Complete", CoUserID, playlistDownloadId.get(0)).observe((LifecycleOwner) ctx, audioList -> {
                    if (audioList.size() == 0) {
                        showToast("Your playlist has been downloaded", act);
                        p = new Properties();
                        p.putValue("userId", UserID);
                        p.putValue("playlistId", playlistDownloadId);
                        p.putValue("playerType", PlayerStatus);
                        p.putValue("source", "Downloaded Playlists");
                        p.putValue("audioService", appStatus(ctx));
                        p.putValue("sound", GetDeviceVolume(ctx));
                        addToSegment("Playlist Download Completed", p, CONSTANTS.track);
                    }
                    DB.taskDao().getNotDownloadPlayListData("Complete", CoUserID, playlistDownloadId.get(0)).removeObserver(audioListx -> {
                    });
                });
            }
            fileNameList.remove(0);
            audioFile.remove(0);
            playlistDownloadId.remove(0);
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String nameJson = gson.toJson(fileNameList);
            String urlJson = gson.toJson(audioFile);
            String playlistIdJson = gson.toJson(playlistDownloadId);
            editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
            editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
            editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
            editor.commit();
            if (fileNameList.size() != 0) {
                filename = fileNameList.get(0);
                downloadProgress = 0;
                downloadProgress2 = 0;
                updateMediaByDownloadProgress(fileNameList.get(0), playlistDownloadId.get(0), downloadProgress, "Start");
                encrypt1(audioFile, fileNameList, playlistDownloadId);
            } else {
                //                showToast("Download Complete...", (Activity) ctx);
                Log.e("Downloading file..", String.valueOf(downloadProgress));
                downloadProgress = 0;
                filename = "";
                isDownloading = false;
                getPending(ctx);
            }
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
            downloadError = 1;
            isDownloading = false;
            Log.e("error in encrypt", e.getMessage());
        }
    }

    @Override
    public void onError(Error error) {
    }

    private void updateMediaByDownloadProgress(String filename, String PlaylistId, int progress, String Status) {
        DB = getAudioDataBase(ctx);
        try {
            AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().updateMediaByDownloadProgress(Status, progress, PlaylistId, filename, CoUserID));
            localIntent.putExtra("Progress", downloadProgress);
            lBM.sendBroadcast(localIntent);
        } catch (Exception | OutOfMemoryError e) {
            System.out.println(e.getMessage());
        }
    }

    private void getPending(Context ctx) {
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, AppCompatActivity.MODE_PRIVATE);
        UserID = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "");
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "");
        DB = getAudioDataBase(ctx);
        DB.taskDao().getNotDownloadData("Complete", CoUserID).observe((LifecycleOwner) ctx, audioList -> {

            notDownloadedData = new ArrayList<>();
            if (audioList != null) {
                notDownloadedData.addAll(audioList);
                if (notDownloadedData.size() != 0) {
                    fileNameList = new ArrayList<>();
                    audioFile = new ArrayList<>();
                    playlistDownloadId = new ArrayList<>();
                    //                    Log.e("not downlodedData", TextUtils.join(",", notDownloadedData));
                    for (int i = 0; i < notDownloadedData.size(); i++) {
                        audioFile.add(notDownloadedData.get(i).getAudioFile());
                        fileNameList.add(notDownloadedData.get(i).getName());
                        playlistDownloadId.add(notDownloadedData.get(i).getPlaylistId());
                    }
                    SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedx.edit();
                    Gson gson = new Gson();
                    String nameJson = gson.toJson(fileNameList);
                    String urlJson = gson.toJson(audioFile);
                    String playlistIdJson = gson.toJson(playlistDownloadId);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                    editor.commit();
                    if (fileNameList.size() != 0) {
                        isDownloading = true;
                        DownloadMedia downloadMedia = new DownloadMedia(ctx.getApplicationContext(), act);
                        downloadMedia.encrypt1(audioFile, fileNameList, playlistDownloadId/*, playlistSongs*/);
                    }
                }
            }
            DB.taskDao().getNotDownloadData("Complete", CoUserID).removeObserver(audioListx -> {
            });
        });
    }
}
