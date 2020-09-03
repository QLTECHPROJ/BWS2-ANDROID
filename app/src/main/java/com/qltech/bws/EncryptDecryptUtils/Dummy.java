package com.qltech.bws.EncryptDecryptUtils;

import androidx.appcompat.app.AppCompatActivity;

public class Dummy extends AppCompatActivity {

    private boolean encrypt() {
//        updateUI("Encrypting file...");
        try {
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(this));
            byte[] encodedBytes = EncryptDecryptUtils.encode(EncryptDecryptUtils.getInstance(this).getSecretKey(), fileData);
            FileUtils.saveFile(encodedBytes, FileUtils.getFilePath(this));
            return true;
        } catch (Exception e) {
//            updateUI("File Encryption failed.\nException: " + e.getMessage());
        }
        return false;
    }

    private byte[] decrypt() {
//        updateUI("Decrypting file...");
        try {
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(this));
            byte[] decryptedBytes = EncryptDecryptUtils.decode(EncryptDecryptUtils.getInstance(this).getSecretKey(), fileData);
            return decryptedBytes;
        } catch (Exception e) {
//            updateUI("File Decryption failed.\nException: " + e.getMessage());
        }
        return null;
    }
}
