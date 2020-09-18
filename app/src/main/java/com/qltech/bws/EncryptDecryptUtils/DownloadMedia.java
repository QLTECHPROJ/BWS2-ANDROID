package com.qltech.bws.EncryptDecryptUtils;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.qltech.bws.BWSApplication;

import static com.qltech.bws.EncryptDecryptUtils.FileUtils.saveFile;


public class DownloadMedia extends AppCompatActivity implements OnDownloadListener {
    Context context;
    byte[] encodedBytes;

    public DownloadMedia(Context context) {
        this.context = context;
    }

    public byte[] encrypt(String DOWNLOAD_AUDIO_URL, String FILE_NAME) {
        BWSApplication.showToast("Encrypting file...", context);
        try {
            PRDownloader.download(DOWNLOAD_AUDIO_URL, FileUtils.getDirPath(context), FILE_NAME).build().start(this);
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context,FILE_NAME));
            encodedBytes = EncryptDecryptUtils.encode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
            saveFile(encodedBytes, FileUtils.getFilePath(context,FILE_NAME));
            return encodedBytes;
        } catch (Exception e) {
            Log.e("errrrrrrrrrr",e.getMessage());
        }
        return encodedBytes;
    }

    public byte[] decrypt(String FILE_NAME) {
        byte[] decryptedBytes=null;
        BWSApplication.showToast("Decrypting file...", context);
        try {
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context,FILE_NAME));
            decryptedBytes = EncryptDecryptUtils.decode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
            BWSApplication.showToast("File Decryption Done", context);
            return decryptedBytes;
        } catch (Exception e) {
            BWSApplication.showToast("File Decryption failed.\nException: " + e.getMessage(), context);
            Log.e("erssssssssssss",e.getMessage());
        }
        return decryptedBytes;
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
