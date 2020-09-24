package com.qltech.bws.EncryptDecryptUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.qltech.bws.BWSApplication;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static com.qltech.bws.EncryptDecryptUtils.FileUtils.saveFile;


public class DownloadMedia extends AppCompatActivity {
    Context context;
    byte[] encodedBytes;
    ImageView imgV;
    FrameLayout progressBarHolder;
    Activity activity;

    public DownloadMedia(Context context, ImageView imgV, FrameLayout progressBarHolder, Activity activity) {
        this.context = context;
        this.imgV = imgV;
        this.progressBarHolder = progressBarHolder;
        this.activity = activity;
    }

    public byte[] encrypt(String DOWNLOAD_AUDIO_URL, String FILE_NAME) {
        BWSApplication.showToast("Downloading file...", context);
        encodedBytes = DownloadMedia(context, DOWNLOAD_AUDIO_URL, FILE_NAME);
        return encodedBytes;
    }

    public byte[] DownloadMedia(Context ctx, String DOWNLOAD_AUDIO_URL, String FILE_NAME) {

        class GetTask extends AsyncTask<Void, Void, Void> implements OnDownloadListener {

            @Override
            protected Void doInBackground(Void... voids) {
//                PRDownloader.download(DOWNLOAD_AUDIO_URL, FileUtils.getDirPath(context), FILE_NAME).build().start(this);
                int count;
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
                } catch (Exception e) {}
                return null;
            }

            private void publishProgress(int i) {
                ProgressDialog progressBar = new ProgressDialog(ctx);
                progressBar.setCancelable(true);//you can cancel it by pressing back button
                progressBar.setMessage("File downloading ...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBar.setProgress(i);//initially progress is 0
                progressBar.setMax(i);//sets the maximum value 100
                progressBar.show();//displays the progress bar
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context, FILE_NAME));
                    encodedBytes = EncryptDecryptUtils.encode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
                    saveFile(encodedBytes, FileUtils.getFilePath(context, FILE_NAME));
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

}
