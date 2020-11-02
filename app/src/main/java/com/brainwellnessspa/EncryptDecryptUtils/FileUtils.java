package com.brainwellnessspa.EncryptDecryptUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.player;
import static com.brainwellnessspa.Utility.CONSTANTS.TEMP_FILE_NAME;
import static com.brainwellnessspa.Utility.MusicService.isCompleteStop;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.oTime;

public class FileUtils {
    static File tempFile = null;
    static byte[] contents;
    static FileInputStream fis;
    public static void saveFile(byte[] encodedBytes, String path) {
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
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
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }
        }
        GetMedia st = new GetMedia();
        st.execute();
    }

    public static byte[] readFile(String filePath) {
        return contents = getMediaContents(filePath);
    }

    private static byte[] getMediaContents(String filePath) {
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
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
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }
        GetMedia st = new GetMedia();
        st.execute();
        return contents;
    }

    @NonNull
    public static void createTempFile(Context context, byte[] decrypted) throws IOException {

    }

    public static FileDescriptor getTempFileDescriptor(Context context, byte[] decrypted) throws IOException {
//        FileUtils.createTempFile(context, decrypted);
        rightfile(context,decrypted);
        return fis.getFD();
    }

    private static void rightfile(Context context, byte[] decrypted) {
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {

                try {
            tempFile = File.createTempFile(TEMP_FILE_NAME, CONSTANTS.FILE_EXT, context.getCacheDir());
            tempFile.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(decrypted);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    fis = new FileInputStream(tempFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                super.onPostExecute(aVoid);
            }
        }
        GetMedia st = new GetMedia();
        st.execute();
    }

    public static final String getDirPath(Context context) {
        return context.getDir("Audio", Context.MODE_PRIVATE).getAbsolutePath();
    }

    public static final String getFilePath(Context context,String FILE_NAME) {
        return getDirPath(context) + File.separator + FILE_NAME;
    }

    public static final void deleteDownloadedFile(Context context,String FILE_NAME) {
        File file = new File(getFilePath(context,FILE_NAME));
        if (null != file && file.exists()) {
            if (file.delete()) Log.i("FileUtils", "File Deleted.");
        }
    }
}
