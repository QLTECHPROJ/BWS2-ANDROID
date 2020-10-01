package com.qltech.bws.EncryptDecryptUtils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.SubPlayListModel;
import com.qltech.bws.RoomDataBase.DatabaseClient;
import com.qltech.bws.RoomDataBase.DownloadAudioDetails;

import java.util.ArrayList;
import java.util.List;

import static com.qltech.bws.EncryptDecryptUtils.FileUtils.saveFile;


public class DownloadMedia extends AppCompatActivity implements OnDownloadListener {
    public static int downloadError = 2;
    Context context;
    byte[] encodedBytes;
    long refid;
    String fileName;
    boolean loop = false;
    List<String> fileNameList;
    List<String> audioFile;
    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongsList;
    boolean frist = true;
    int i = 0;
    private DownloadManager downloadManager;
    private BroadcastReceiver onComplete;

    public DownloadMedia(Context context) {
        this.context = context;
    }

    public byte[] encrypt(String DOWNLOAD_AUDIO_URL, String FILE_NAME) {
       /* downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));*/
        fileName = FILE_NAME;
        loop = false;
        PRDownloader.download(DOWNLOAD_AUDIO_URL, FileUtils.getDirPath(context), FILE_NAME).build().start(this);
        BWSApplication.showToast("Downloading file...", context);
//        encodedBytes = DownloadMedia(context, DOWNLOAD_AUDIO_URL, FILE_NAME);
        return encodedBytes;
    }

    public byte[] encrypt1(List<String> DOWNLOAD_AUDIO_URL, List<String> FILE_NAME, ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs) {
        BWSApplication.showToast("Downloading file...", context);
        fileNameList = FILE_NAME;
        audioFile = DOWNLOAD_AUDIO_URL;
        playlistSongsList = new ArrayList<>();
        playlistSongsList = playlistSongs;
        loop = true;
        PRDownloader.download(DOWNLOAD_AUDIO_URL.get(0), FileUtils.getDirPath(context), FILE_NAME.get(0)).build().start(this);
        return encodedBytes;
    }

    public byte[] decrypt(String FILE_NAME) {
        byte[] decryptedBytes = null;
        BWSApplication.showToast("Retrieve file...", context);
        try {
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context, FILE_NAME));
            decryptedBytes = EncryptDecryptUtils.decode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
            BWSApplication.showToast("File Retrieve Done", context);
            return decryptedBytes;
        } catch (Exception e) {
//            BWSApplication.showToast("File Decryption failed.\nException: " + e.getMessage(), context);
            Log.e("erssssssssssss", e.getMessage());
        }
        return decryptedBytes;
    }

    @Override
    public void onDownloadComplete() {
        if (loop) {
            try {
                for (int a = 0; a < fileNameList.size(); a++) {
                }
                byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context, fileNameList.get(0)));
                encodedBytes = EncryptDecryptUtils.encode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
                saveFile(encodedBytes, FileUtils.getFilePath(context, fileNameList.get(0)));
//                    saveAllMedia(playlistSongsList, encodedBytes, FileUtils.getFilePath(context, fileNameList.get(i)));
                    fileNameList.remove(0);
                    audioFile.remove(0);
                if(fileNameList.size()!=0){
                    encrypt1(audioFile,fileNameList,playlistSongsList);
                }else{
                    BWSApplication.showToast("Download Complete...", context);
                }

            } catch (Exception e) {
                e.printStackTrace();
                downloadError = 1;
                Log.e("error in encrypt", e.getMessage());
            }
        } else {
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
        }
    }

    @Override
    public void onError(Error error) {
        downloadError = 1;
    }

    private void saveAllMedia(ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs, byte[] encodedBytes, String filePath) {
        class SaveMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
                for (int i = 0; i < playlistSongs.size(); i++) {
                    downloadAudioDetails.setID(playlistSongs.get(i).getID());
                    downloadAudioDetails.setName(playlistSongs.get(i).getName());
                    downloadAudioDetails.setAudioFile(playlistSongs.get(i).getAudioFile());
                    downloadAudioDetails.setAudioDirection(playlistSongs.get(i).getAudioDirection());
                    downloadAudioDetails.setAudiomastercat(playlistSongs.get(i).getAudiomastercat());
                    downloadAudioDetails.setAudioSubCategory(playlistSongs.get(i).getAudioSubCategory());
                    downloadAudioDetails.setImageFile(playlistSongs.get(i).getImageFile());
                    downloadAudioDetails.setLike(playlistSongs.get(i).getLike());
                    downloadAudioDetails.setDownload("1");
                    downloadAudioDetails.setAudioDuration(playlistSongs.get(i).getAudioDuration());
                    downloadAudioDetails.setIsSingle("0");
                    downloadAudioDetails.setPlaylistId(playlistSongs.get(i).getPlaylistID());
                    downloadAudioDetails.setEncodedBytes(encodedBytes);
                    downloadAudioDetails.setDirPath(filePath);
                    DatabaseClient.getInstance(context)
                            .getaudioDatabase()
                            .taskDao()
                            .insertMedia(downloadAudioDetails);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
//                llDownload.setClickable(false);
//                llDownload.setEnabled(false);
                super.onPostExecute(aVoid);
            }
        }

        SaveMedia st = new SaveMedia();
        st.execute();
    }

}
