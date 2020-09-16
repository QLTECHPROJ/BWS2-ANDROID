package com.qltech.bws.EncryptDecryptUtils;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.qltech.bws.BWSApplication;

import static com.qltech.bws.Utility.CONSTANTS.FILE_EXT;


public class DownloadMedia extends AppCompatActivity implements OnDownloadListener {
    Context context;

    public DownloadMedia(Context context) {
        this.context = context;
    }

    public boolean encrypt(String DOWNLOAD_AUDIO_URL, String FILE_NAME) {
        BWSApplication.showToast("Encrypting file...", context);
        try {
            PRDownloader.download(DOWNLOAD_AUDIO_URL, FileUtils.getDirPath(context), FILE_NAME+FILE_EXT).build().start(this);
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context,FILE_NAME+FILE_EXT));
            byte[] encodedBytes = EncryptDecryptUtils.encode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
            FileUtils.saveFile(encodedBytes, FileUtils.getFilePath(context,FILE_NAME+FILE_EXT));
            BWSApplication.showToast("File Encryption done", context);

            return true;
        } catch (Exception e) {
            BWSApplication.showToast("File Encryption failed.\nException: " + e.getMessage(), context);
            Log.e("errrrrrrrrrr",e.getMessage());
        }
        return false;
    }

    public byte[] decrypt(String FILE_NAME) {
        BWSApplication.showToast("Decrypting file...", context);
        try {
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(context,FILE_NAME+FILE_EXT));
            byte[] decryptedBytes = EncryptDecryptUtils.decode(EncryptDecryptUtils.getInstance(context).getSecretKey(), fileData);
            BWSApplication.showToast("File Decryption Done", context);

            return decryptedBytes;
        } catch (Exception e) {
            BWSApplication.showToast("File Decryption failed.\nException: " + e.getMessage(), context);
            Log.e("erssssssssssss",e.getMessage());

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
