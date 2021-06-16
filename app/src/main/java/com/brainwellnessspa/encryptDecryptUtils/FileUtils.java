package com.brainwellnessspa.encryptDecryptUtils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.brainwellnessspa.utility.CONSTANTS;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.brainwellnessspa.utility.CONSTANTS.TEMP_FILE_NAME;

public class FileUtils {
    public static void saveFile(byte[] encodedBytes, String path) {
        try {
            File file = new File(path);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(encodedBytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static byte[] readFile(String filePath) {
        byte[] contents;
        File file = new File(filePath);
        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }

 public static File readFile1(String filePath) {
        File file = new File(filePath);
        return file;
    }

    @NonNull
    public static File createTempFile(Context context, byte[] decrypted) throws IOException {
        File tempFile = File.createTempFile(TEMP_FILE_NAME, CONSTANTS.FILE_EXT, context.getCacheDir());
//        tempFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(decrypted);
        fos.close();
        return tempFile;
    }

    public static FileDescriptor getTempFileDescriptor(Context context, byte[] decrypted) throws IOException {
        File tempFile = FileUtils.createTempFile(context, decrypted);
        FileInputStream fis = new FileInputStream(tempFile);
        return fis.getFD();
    }
    public static File getTempFileDescriptor1(Context context, byte[] decrypted) throws IOException {
        File tempFile = FileUtils.createTempFile(context, decrypted);
        FileInputStream fis = new FileInputStream(tempFile);
        return tempFile;
    }

    public static final String getDirPath(Context context) {
        return context.getDir("Audio", Context.MODE_PRIVATE).getAbsolutePath();
    }
    public static final String getDirPath1(Context context) {
        return context.getDir("PDF", Context.MODE_PRIVATE).getAbsolutePath();
    }

    public static final String getFilePath(Context context,String FILE_NAME) {
        return getDirPath(context) + File.separator + FILE_NAME +CONSTANTS.FILE_EXT;
    }

    public static final String getFilePath1(Context context,String FILE_NAME) {
        return getDirPath1(context) + File.separator + FILE_NAME + ".pdf";
    }

    public static final void deleteDownloadedFile(Context context,String FILE_NAME) {
        File file = new File(getFilePath(context,FILE_NAME+CONSTANTS.FILE_EXT));
        if (null != file && file.exists()) {
            if (file.delete()) Log.i("FileUtils", "File Deleted.");
        }
    }
    public static final void deleteDownloadedFile1(Context context,String FILE_NAME) {
        File file = new File(getFilePath(context,FILE_NAME));
        if (null != file && file.exists()) {
            if (file.delete()) Log.i("FileUtils", "File Deleted.");
        }
    }
}
