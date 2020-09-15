package com.qltech.bws.EncryptDecryptUtils;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.qltech.bws.BWSApplication;


public class DownloadMedia extends AppCompatActivity implements OnDownloadListener {
    Context context;

    public DownloadMedia(Context context) {
        this.context = context;
    }

    public boolean encrypt(String DOWNLOAD_AUDIO_URL, String FILE_NAME, Context context) {
        BWSApplication.showToast("Encrypting file...", context);
        try {
            PRDownloader.download(DOWNLOAD_AUDIO_URL, FileUtils.getDirPath(this), FILE_NAME).build().start(this);
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(this));
            byte[] encodedBytes = EncryptDecryptUtils.encode(EncryptDecryptUtils.getInstance(this).getSecretKey(), fileData);
            FileUtils.saveFile(encodedBytes, FileUtils.getFilePath(this));
            return true;
        } catch (Exception e) {
            BWSApplication.showToast("File Encryption failed.\nException: " + e.getMessage(), context);
            Log.e("errrrrrrrrrr",e.getMessage());
        }
        return false;
    }

    public byte[] decrypt() {
        BWSApplication.showToast("Decrypting file...", context);
        try {
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(this));
            byte[] decryptedBytes = EncryptDecryptUtils.decode(EncryptDecryptUtils.getInstance(this).getSecretKey(), fileData);
            return decryptedBytes;
        } catch (Exception e) {
            BWSApplication.showToast("File Decryption failed.\nException: " + e.getMessage(), context);
            Log.e("errrrrrrrssssssssssss",e.getMessage());

        }
        return null;
    }

    @Override
    public void onDownloadComplete() {
        BWSApplication.showToast("File Download complete", context);
    }

    @Override
    public void onError(Error error) {
        BWSApplication.showToast("File Download Error", context);
    }
}
