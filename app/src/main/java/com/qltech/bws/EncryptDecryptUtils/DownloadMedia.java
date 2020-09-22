package com.qltech.bws.EncryptDecryptUtils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.qltech.bws.BWSApplication;

import static com.qltech.bws.EncryptDecryptUtils.FileUtils.saveFile;


public class DownloadMedia extends AppCompatActivity implements OnDownloadListener {
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
        BWSApplication.showProgressBar(imgV,progressBarHolder,activity);
        try {
            PRDownloader.download(DOWNLOAD_AUDIO_URL, FileUtils.getDirPath(context), FILE_NAME).build().start(this);
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context,FILE_NAME));
            encodedBytes = EncryptDecryptUtils.encode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
            saveFile(encodedBytes, FileUtils.getFilePath(context,FILE_NAME));
            BWSApplication.showToast("Download Complete...", context);
            BWSApplication.hideProgressBar(imgV,progressBarHolder,activity);
            return encodedBytes;
        } catch (Exception e) {
            BWSApplication.hideProgressBar(imgV,progressBarHolder,activity);
            Log.e("errrrrrrrrrr",e.getMessage());
        }
        return encodedBytes;
    }

    public byte[] decrypt(String FILE_NAME) {
        byte[] decryptedBytes=null;
        BWSApplication.showToast("Retrieve file...", context);
        BWSApplication.showProgressBar(imgV,progressBarHolder,activity);
        try {
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context,FILE_NAME));
            decryptedBytes = EncryptDecryptUtils.decode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
            BWSApplication.showToast("File Retrieve Done", context);
            BWSApplication.hideProgressBar(imgV,progressBarHolder,activity);
            return decryptedBytes;
        } catch (Exception e) {
//            BWSApplication.showToast("File Decryption failed.\nException: " + e.getMessage(), context);
            Log.e("erssssssssssss",e.getMessage());
            BWSApplication.hideProgressBar(imgV,progressBarHolder,activity);
        }
        return decryptedBytes;
    }

    @Override
    public void onDownloadComplete() {
//        BWSApplication.showToast("File Download complete", context);
    }

    @Override
    public void onError(Error error) {
//        BWSApplication.showToast("File Download Error", context);
    }
}
