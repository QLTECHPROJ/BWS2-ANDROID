package com.brainwellnessspa.EncryptDecryptUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Status;
import com.google.gson.Gson;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.logout;
import static com.brainwellnessspa.EncryptDecryptUtils.FileUtils.saveFile;


public class DownloadMedia implements OnDownloadListener{
    public static int downloadError = 2,downloadIdOne;
    public static String filename = "";
    public static int downloadProgress = 0;
    public static boolean isDownloading = false;
    Context context;
    byte[] encodedBytes;
    List<String> fileNameList, audioFile, playlistDownloadId;

    public DownloadMedia(Context context) {
        this.context = context;
    }

    /*    public byte[] encrypt(String DOWNLOAD_AUDIO_URL, String FILE_NAME) {
     *//* downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));*//*
        fileName = FILE_NAME;
        loop = false;
        PRDownloader.download(DOWNLOAD_AUDIO_URL, FileUtils.getDirPath(context), FILE_NAME).build().start(this);
        BWSApplication.showToast("Downloading file...", context);
//        encodedBytes = DownloadMedia(context, DOWNLOAD_AUDIO_URL, FILE_NAME);
        return encodedBytes;
    }*/


    public byte[] encrypt1(List<String> DOWNLOAD_AUDIO_URL, List<String> FILE_NAME, List<String> PLAYLIST_ID/*, ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs*/) {
        BWSApplication.showToast("Downloading file...", context);

// Setting timeout globally for the download network requests:
  /*      PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .build();
        PRDownloader.initialize(context, config);*/
        isDownloading = true;
        fileNameList = FILE_NAME;
        audioFile = DOWNLOAD_AUDIO_URL;
        playlistDownloadId = PLAYLIST_ID;
        filename = FILE_NAME.get(0);
       downloadIdOne = PRDownloader.download(DOWNLOAD_AUDIO_URL.get(0), FileUtils.getDirPath(context), FILE_NAME.get(0)).build().setOnProgressListener(progress -> {
            long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
            downloadProgress = (int) progressPercent;
            updateMediaByDownloadProgress(fileNameList.get(0), playlistDownloadId.get(0), downloadProgress,"Start");
//        progressBarOne.setProgress((int) progressPercent);
//        textViewProgressOne.setText(BWSApplication.getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
//        progressBarOne.setIndeterminate(false);
       }).setOnCancelListener(new OnCancelListener() {
           @Override
           public void onCancel() {
               downloadIdOne = 0;
               filename = "";
               if(logout){
                   SharedPreferences preferences11 = context.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                   SharedPreferences.Editor edit1 = preferences11.edit();
                   edit1.remove(CONSTANTS.PREF_KEY_DownloadName);
                   edit1.remove(CONSTANTS.PREF_KEY_DownloadUrl);
                   edit1.remove(CONSTANTS.PREF_KEY_DownloadPlaylistId);
                   edit1.clear();
                   edit1.commit();
               }else {
                   SharedPreferences sharedy1 = context.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
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
                   SharedPreferences shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                   SharedPreferences.Editor editor = shared.edit();
                   String urlJson = gson.toJson(audioFile);
                   String nameJson = gson.toJson(fileNameList);
                   String playlistIdJson = gson.toJson(playlistDownloadId);
                   editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                   editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                   editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                   editor.commit();
                   SharedPreferences sharedy = context.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                   String jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
                   String json1 = sharedy.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
                   String jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
                   if (!jsony.equalsIgnoreCase(String.valueOf(gson))) {
                       Type type = new TypeToken<List<String>>() {
                       }.getType();
                       fileNameList = gson.fromJson(jsony, type);
                       audioFile = gson.fromJson(json1, type);
                       playlistDownloadId = gson.fromJson(jsonq, type);
                   }
                   if (fileNameList.size() != 0) {
                       encrypt1(audioFile, fileNameList, playlistDownloadId);
                   }

               }
           }
       }).start(this);
       return encodedBytes;
    }

/*    private void callPauseResume() {

        if (Status.RUNNING == PRDownloader.getStatus(downloadIdOne)) {
            PRDownloader.pause(downloadIdOne);
        }

        if (Status.PAUSED == PRDownloader.getStatus(downloadIdOne)) {
            PRDownloader.resume(downloadIdOne);
        }
    }*/

    public byte[] decrypt(String FILE_NAME) {
        byte[] decryptedBytes = null;
//        BWSApplication.showToast("Retrieve file...", context);
        try {
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context, FILE_NAME));
            decryptedBytes = EncryptDecryptUtils.decode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
//            BWSApplication.showToast("File Retrieve Done", context);
            return decryptedBytes;
        } catch (Exception e) {
//            BWSApplication.showToast("File Decryption failed.\nException: " + e.getMessage(), context);
            Log.e("erssssssssssss", e.getMessage());
        }
        return decryptedBytes;
    }

    @Override
    public void onDownloadComplete() {
        try {
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context, fileNameList.get(0)));
            encodedBytes = EncryptDecryptUtils.encode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
            saveFile(encodedBytes, FileUtils.getFilePath(context, fileNameList.get(0)));
            updateMediaByDownloadProgress(fileNameList.get(0), playlistDownloadId.get(0), 100, "Complete");
            fileNameList.remove(0);
            audioFile.remove(0);
            playlistDownloadId.remove(0);
            SharedPreferences shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String urlJson = gson.toJson(audioFile);
            String nameJson = gson.toJson(fileNameList);
            String playlistIdJson = gson.toJson(playlistDownloadId);
            editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
            editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
            editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
            editor.commit();
            if (fileNameList.size() != 0) {
                encrypt1(audioFile, fileNameList, playlistDownloadId);
            } else {
                downloadProgress = 0;
                filename = "";
                isDownloading = false;
                BWSApplication.showToast("Download Complete...", context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            downloadError = 1;
            isDownloading = false;
            Log.e("error in encrypt", e.getMessage());
        } /*else {
            try {
                downloadError = 0;
                byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context, fileName));
                encodedBytes = EncryptDecryptUtils.encode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
                saveFile(encodedBytes, FileUtils.getFilePath(context, fileName));
                BWSApplication.showToast("Download Complete...", context);
            } catch (Exception e) {
//            BWSApplication.showToast("File Decryption failed.\nException: " + e.getMessage(), context);
                e.printStackTrace();
                downloadError = 1;
                Log.e("error in encrypt", e.getMessage());
            }
        }*/
    }

    @Override
    public void onError(Error error) {
        downloadError = 1;
//        encrypt1(audioFile, fileNameList, playlistDownloadId);
    }

    private void updateMediaByDownloadProgress(String filename, String PlaylistId, int progress, String Status) {
        class SaveMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(context)
                        .getaudioDatabase()
                        .taskDao()
                        .updateMediaByDownloadProgress(Status, progress, PlaylistId, filename);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(!PlaylistId.equalsIgnoreCase("")){

                }

                super.onPostExecute(aVoid);
            }
        }

        SaveMedia st = new SaveMedia();
        st.execute();
    }
}
