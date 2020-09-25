package com.qltech.bws.EncryptDecryptUtils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

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


public class DownloadMedia extends AppCompatActivity implements OnDownloadListener{
    Context context;
    byte[] encodedBytes;
    ImageView imgV;
    FrameLayout progressBarHolder;
    Activity activity;
    long refid;
    private DownloadManager downloadManager;
    private BroadcastReceiver onComplete;
    String fileName;
    public static int downloadError = 2;

    public DownloadMedia(Context context, ImageView imgV, FrameLayout progressBarHolder, Activity activity) {
        this.context = context;
        this.imgV = imgV;
        this.progressBarHolder = progressBarHolder;
        this.activity = activity;
    }

    public byte[] encrypt(String DOWNLOAD_AUDIO_URL, String FILE_NAME) {
       /* downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));*/
        fileName = FILE_NAME;
        PRDownloader.download(DOWNLOAD_AUDIO_URL, FileUtils.getDirPath(context), FILE_NAME).build().start(this);
        BWSApplication.showToast("Downloading file...", context);
//        encodedBytes = DownloadMedia(context, DOWNLOAD_AUDIO_URL, FILE_NAME);
        return encodedBytes;
    }
    public byte[] encrypt1(List<String> DOWNLOAD_AUDIO_URL, List<String> FILE_NAME, ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs) {
        BWSApplication.showToast("Downloading file...", context);
        encodedBytes = DownloadMedia1(context, DOWNLOAD_AUDIO_URL, FILE_NAME,playlistSongs);
        return encodedBytes;
    }

    public byte[] DownloadMedia(Context ctx, String DOWNLOAD_AUDIO_URL, String FILE_NAME) {

        class GetTask extends AsyncTask<Void, Void, Void>{

            @Override
            protected Void doInBackground(Void... voids) {
             /*   DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DOWNLOAD_AUDIO_URL));

                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setAllowedOverRoaming(false);

                //Set the title of this download, to be displayed in notifications (if enabled).
                request.setTitle("Demo Downloading " + FILE_NAME);

                //Set a description of this download, to be displayed in notifications (if enabled)
                request.setDescription("Downloading " + FILE_NAME);

                //Set whether this download should be displayed in the system's Downloads UI. True by default.
                request.setVisibleInDownloadsUi(true);

                //Set the local destination for the downloaded file to a path within the public external storage directory (as returned by getExternalStoragePublicDirectory(String)).
                request.setDestinationInExternalPublicDir("/data/user/0/com.qltech.bws/app_Audio/", FILE_NAME);

                refid = downloadManager.enqueue(request);*/
               /* int count;
                try {
                    URL url = new URL(DOWNLOAD_AUDIO_URL);
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                    // this will be useful so that you can show a tipical 0-100% progress bar
                    int lenghtOfFile = conexion.getContentLength();

                    // downlod the file
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream("/data/user/0/com.qltech.bws/app_Audio/"+FILE_NAME);

                    encodedBytes = new byte[1024];

                    long total = 0;

                    while ((count = input.read(encodedBytes)) != -1) {
                        total += count;
                        // publishing the progress....
                        publishProgress((int)(total*100/lenghtOfFile));
                        output.write(encodedBytes, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e) {}*/
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                super.onPostExecute(aVoid);

            }

        }

        GetTask st = new GetTask();
        st.execute();
        return encodedBytes;
    }
    public byte[] DownloadMedia1(Context ctx, List<String> DOWNLOAD_AUDIO_URL, List<String> FILE_NAME, ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs) {

        class GetTask extends AsyncTask<Void, Void, Void> implements OnDownloadListener {

            @Override
            protected Void doInBackground(Void... voids) {

//                for(int i=0;i<FILE_NAME.size();i++) {
//                    PRDownloader.download(DOWNLOAD_AUDIO_URL.get(i), FileUtils.getDirPath(context), FILE_NAME.get(i)).build().start(this);
//                }
                for (int i = 0; i < FILE_NAME.size(); i++) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DOWNLOAD_AUDIO_URL.get(i)));

                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                    request.setAllowedOverRoaming(false);

                    //Set the title of this download, to be displayed in notifications (if enabled).
                    request.setTitle("Demo Downloading " + FILE_NAME.get(i));

                    //Set a description of this download, to be displayed in notifications (if enabled)
                    request.setDescription("Downloading " + FILE_NAME.get(i));

                    //Set whether this download should be displayed in the system's Downloads UI. True by default.
                    request.setVisibleInDownloadsUi(true);

                    //Set the local destination for the downloaded file to a path within the public external storage directory (as returned by getExternalStoragePublicDirectory(String)).
                    request.setDestinationInExternalPublicDir("/data/user/0/com.qltech.bws/app_Audio/", FILE_NAME.get(i));

                    refid = downloadManager.enqueue(request);


                    Log.e("OUTNM", "" + refid);


                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    for(int i=0;i<FILE_NAME.size();i++) {
                        byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context, FILE_NAME.get(i)));
                        encodedBytes = EncryptDecryptUtils.encode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
                        saveFile(encodedBytes, FileUtils.getFilePath(context, FILE_NAME.get(i)));
                        saveAllMedia(playlistSongs,encodedBytes, FileUtils.getFilePath(context, FILE_NAME.get(i)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                BWSApplication.showToast("Download Complete...", context);
                super.onPostExecute(aVoid);

            }

            @Override
            public void onDownloadComplete() {

            }

            @Override
            public void onError(Error error) {

            }
        }

        GetTask st = new GetTask();
        st.execute();
        return encodedBytes;
    }

    private void saveAllMedia(ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs, byte[] encodedBytes, String filePath) {
        class SaveMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
                for(int i = 0;i<playlistSongs.size();i++) {
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
                    if (playlistSongs.get(i).getPlaylistID().equalsIgnoreCase("")) {
                        downloadAudioDetails.setIsSingle("1");
                        downloadAudioDetails.setPlaylistId("");
                    } else {
                        downloadAudioDetails.setIsSingle("0");
                        downloadAudioDetails.setPlaylistId(playlistSongs.get(i).getPlaylistID());
                    }
                    downloadAudioDetails.setEncodedBytes(encodedBytes);
                    downloadAudioDetails.setDirPath(filePath);
                    DatabaseClient.getInstance(activity)
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

    public byte[] decrypt(String FILE_NAME) {
        byte[] decryptedBytes = null;
        BWSApplication.showToast("Retrieve file...", context);
        BWSApplication.showProgressBar(imgV, progressBarHolder, activity);
        try {
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context, FILE_NAME));
            decryptedBytes = EncryptDecryptUtils.decode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
            BWSApplication.showToast("File Retrieve Done", context);
            BWSApplication.hideProgressBar(imgV, progressBarHolder, activity);
            return decryptedBytes;
        } catch (Exception e) {
//            BWSApplication.showToast("File Decryption failed.\nException: " + e.getMessage(), context);
            Log.e("erssssssssssss", e.getMessage());
            BWSApplication.hideProgressBar(imgV, progressBarHolder, activity);
        }
        return decryptedBytes;
    }

    @Override
    public void onDownloadComplete() {
        try {
            downloadError = 0;
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context, fileName));
            encodedBytes = EncryptDecryptUtils.encode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
            saveFile(encodedBytes, FileUtils.getFilePath(context, fileName));
            BWSApplication.showToast("Download Complete...", context);
        } catch (Exception e) {
//            BWSApplication.showToast("File Decryption failed.\nException: " + e.getMessage(), context);
            downloadError = 1;
            Log.e("error in encrypt", e.getMessage());
            BWSApplication.hideProgressBar(imgV, progressBarHolder, activity);
        }
    }

    @Override
    public void onError(Error error) {
        downloadError = 1;
    }
}
