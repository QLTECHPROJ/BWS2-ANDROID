package com.brainwellnessspa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.media.MediaSessionManager;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.SplashModule.Models.VersionModel;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.AppSignatureHashHelper;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.CryptLib;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.MusicService;
import com.brainwellnessspa.Utility.PlaybackStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.sql.DriverManager.println;

public class BWSApplication extends Application {
    private static Context mContext;
    private static BWSApplication BWSApplication;
    private static List<DownloadAudioDetails> downloadAudioDetailsList;
    private static final int NOTIFICATION_ID = 101;
    public static final String ACTION_PLAY = "com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.ACTION_NEXT";
    public static final String ACTION_STOP = "com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.ACTION_STOP";
    private static Bitmap myBitmap;

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    public static Context getContext() {
        return mContext;
    }

    public static MeasureRatio measureRatio(Context context, float outerMargin, float aspectX, float aspectY,
                                            float proportion, float innerMargin) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        try {
            WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        } catch (Exception e) {
            e.printStackTrace();
        }
        float width = displayMetrics.widthPixels / displayMetrics.density;
        float widthImg = ((width - outerMargin) * proportion) - innerMargin;
        float height = widthImg * aspectY / aspectX;
        //Log.e("width.........", "" + context.getClass().getSimpleName()+","+width);
//        //Log.e("widthImg.........", "" + context.getClass().getSimpleName()+","+widthImg);
//        //Log.e("height...........", "" + context.getClass().getSimpleName()+","+height);
//        //Log.e("displayMetrics.density...........", "" + context.getClass().getSimpleName()+","+displayMetrics.density);
        return new MeasureRatio(widthImg, height, displayMetrics.density, proportion);
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'playlist_table' ADD COLUMN 'PlaylistImageDetails' TEXT");
        }
    };

    public static void simple_Notification(PlaybackStatus playbackStatus, ArrayList<MainPlayModel> mainPlayModelList, Activity activity, int position) {
      /*  Bitmap mCurrTrackCover;
        boolean showWhen = false;
        var notifWhen = 0L*/

        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(1, activity);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction = playbackAction(0, activity);
        }
        /*try {
            URL url = new URL(mainPlayModelList.get(position).getImageFile());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

          try {
            URL url = new URL(mainPlayModelList.get(position).getImageFile());
            myBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(activity)
                .setShowWhen(false)
                // Set the Notification style
//                .setStyle(new NotificationCompat().MediaStyle()
                // Attach our MediaSession token
//                .setMediaSession(mediaSession.getSessionToken())
                // Show our playback controls in the compact notification view.
//                .setShowActionsInCompactView(0, 1, 2))
                .setColor(activity.getResources().getColor(R.color.blue))
                // Set the large and small icons
                .setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.drawable.square_app_icon))
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentText(mainPlayModelList.get(position).getAudioDirection())
                .setContentTitle(mainPlayModelList.get(position).getName())
                .setContentInfo("Brain Wellness Spa")
                .setSound(null)
                // Add playback actions
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3, activity))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2, activity));

        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

//Android 8 introduced a new requirement of setting the channelId property by using a NotificationChannel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

        /*NotificationCompat.Builder notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
                .setContentTitle(mainPlayModelList.get(position).getName())
                .setContentText(mainPlayModelList.get(position).getAudioDirection())
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                .setLargeIcon(mCurrTrackCover)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setWhen(notifWhen)
                .setShowWhen(showWhen)
                .setUsesChronometer(usesChronometer)
                .setContentIntent(getContentIntent())
//                .setOngoing(ongoing)
                .setChannelId(NOTIFICATION_CHANNEL)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mMediaSession ?.sessionToken))
            .setDeleteIntent(notificationDismissedPendingIntent)
                .addAction(android.R.drawable.ic_media_previous, getString(R.string.previous), playbackAction(3, activity))
                .addAction(playPauseIcon, getString(R.string.playpause), play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, getString(R.string.next), playbackAction(2, activity))

        startForeground(NOTIFICATION_ID, notification.build())*/
    }


    public static PendingIntent playbackAction(int actionNumber, Activity activity) {
        Intent playbackAction = new Intent(activity, MusicService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(activity, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(activity, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(activity, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(activity, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void removeNotification(Activity activity) {
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }
/*    private void skipToNext() {
        if (audioIndex == audioList.size() - 1) {
            //if last in playlist
            audioIndex = 0;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get next in playlist
            activeAudio = audioList.get(++audioIndex);
        }

        //Update stored index
        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }*/

    /* private void skipToPrevious() {
         if (audioIndex == 0) {
             //if first in playlist
             //set index to the last of audioList
             audioIndex = audioList.size() - 1;
             activeAudio = audioList.get(audioIndex);
         } else {
             //get previous in playlist
             activeAudio = audioList.get(--audioIndex);
         }

         //Update stored index
         new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

         stopMedia();
         //reset mediaPlayer
         mediaPlayer.reset();
         initMediaPlayer();
     }*/

    /* TODO Need this code Can't delete
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
      try {
          //Load data from SharedPreferences
          StorageUtil storage = new StorageUtil(getApplicationContext());
          audioList = storage.loadAudio();
          audioIndex = storage.loadAudioIndex();

          if (audioIndex != -1 && audioIndex < audioList.size()) {
              //index is in a valid range
              activeAudio = audioList.get(audioIndex);
          } else {
              stopSelf();
          }
      } catch (NullPointerException e) {
          stopSelf();
      }

      //Request audio focus
      if (requestAudioFocus() == false) {
          //Could not gain focus
          stopSelf();
      }

      if (mediaSessionManager == null) {
          try {
              initMediaSession();
              initMediaPlayer();
          } catch (RemoteException e) {
              e.printStackTrace();
              stopSelf();
          }
          buildNotification(PlaybackStatus.PLAYING);
      }

      //Handle Intent action from MediaSession.TransportControls
      handleIncomingActions(intent);
      return super.onStartCommand(intent, flags, startId);
  }*/
/* @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Resume your media player here
                resumeMedia();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (isMediaStart) {
                    pauseMedia();
//                    binding.ivPlay.setVisibility(View.VISIBLE);
//                    binding.ivPause.setVisibility(View.GONE);
                }
//                MusicService.pauseMedia();// Pause your media player here
                break;
        }
    }*/

  /*  public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    public static void getLatasteUpdate(Context context) {
        String appURI = "https://play.google.com/store/apps/details?id=com.brainwellnessspa";
        if (BWSApplication.isNetworkConnected(context)) {
            Call<VersionModel> listCall = APIClient.getClient().getVersionDatas(String.valueOf(BuildConfig.VERSION_CODE), CONSTANTS.FLAG_ONE);
            listCall.enqueue(new Callback<VersionModel>() {
                @Override
                public void onResponse(Call<VersionModel> call, Response<VersionModel> response) {
                    if (response.isSuccessful()) {
                        VersionModel versionModel = response.body();
//                    if (versionModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                        if (versionModel.getResponseData().getIsForce().equalsIgnoreCase("0")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Update Brain Wellness Spa");
                            builder.setCancelable(false);
                            builder.setMessage("Brain Wellness Spa recommends that you update to the latest version")
                                    .setPositiveButton("UPDATE", (dialog, id) -> {
                                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appURI)));
                                        dialog.cancel();
                                    })
                                    .setNegativeButton("NOT NOW", (dialog, id) -> dialog.dismiss());
                            builder.create().show();
                        } else if (versionModel.getResponseData().getIsForce().equalsIgnoreCase("1")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Update Required");
                            builder.setCancelable(false);
                            builder.setMessage("To keep using Brain Wellness Spa, download the latest version")
                                    .setCancelable(false)
                                    .setPositiveButton("UPDATE", (dialog, id) -> context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appURI))));
                            builder.create().show();
                        } else if (versionModel.getResponseData().getIsForce().equalsIgnoreCase("")) {
                        }
                    }
                    /*} else {
                    }*/
                }

                @Override
                public void onFailure(Call<VersionModel> call, Throwable t) {
                }
            });
        } else {
            BWSApplication.showToast(context.getString(R.string.no_server_found), context);
        }
    }

    public static String getKey(Context context) {
        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(context);
        String key = appSignatureHashHelper.getAppSignatures().get(0);

        SharedPreferences shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_Splash, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(CONSTANTS.PREF_KEY_SplashKey, appSignatureHashHelper.getAppSignatures().get(0));
        editor.commit();
        return key;
    }

    public static void showToast(String message, Context context) {
        Toast toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 35);
        toast.setView(view);
        toast.show();
    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }
/*    public static List<DownloadAudioDetails> GetAllMedia(Context ctx) {

        class GetTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                downloadAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllData();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }
        }

        GetTask st = new GetTask();
        st.execute();
        return downloadAudioDetailsList;
    }*/

    public static synchronized BWSApplication getInstance() {
        return BWSApplication;
    }

    public static void hideProgressBar(ProgressBar progressBar, FrameLayout progressBarHolder, Activity ctx) {
        try {
            progressBarHolder.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            ctx.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showProgressBar(ProgressBar progressBar, FrameLayout progressBarHolder, Activity ctx) {
        try {
            progressBarHolder.setVisibility(View.VISIBLE);
            ctx.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,3}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        BWSApplication = this;
    }

    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            boolean flag = false;
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

//For 3G check
            boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .isConnectedOrConnecting();
//For WiFi Check
            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting();

            flag = !(!is3g && !isWifi);
            return flag;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static String securityKey() {
        String key;
        String DeviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String AES = "OsEUHhecSs4gRGcy2vMQs1s/XajBrLGADR71cKMRNtA=";
        String RSA = "KlWxBHfKPGkkeTjkT7IEo32bZW8GlVCPq/nvVFuYfIY=";
        String TDES = "1dpra0SZhVPpiUQvikMvkDxEp7qLLJL9pe9G6Apg01g=";
        String SHA1 = "Ey8rBCHsqITEbh7KQKRmYObCGBXqFnvtL5GjMFQWHQo=";
        String MD5 = "/qc2rO3RB8Z/XA+CmHY0tCaJch9a5BdlQW1xb7db+bg=";

        Calendar calendar = Calendar.getInstance();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date());
        SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateAsString = outputFmt.format(calendar.getTime());
        //        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //2019-11-21 06:45:32
//        String currentDateandTime = sdf.format(new Date());
        String finalKey = "";
        HashMap<String, String> hash_map = new HashMap<String, String>();
        hash_map.put("AES", AES);
        hash_map.put("RSA", RSA);
        hash_map.put("TDES", TDES);
        hash_map.put("SHA1", SHA1);
        hash_map.put("MD5", MD5);

        Random random = new Random();
        List<String> keys = new ArrayList<String>(hash_map.keySet());
        String randomKey = keys.get(random.nextInt(keys.size()));
        String value = hash_map.get(randomKey);
        key = DeviceId + "." + dateAsString + "." + randomKey + "." + value;

        try {
            finalKey = ProgramForAES(key);
            System.out.println(finalKey);
        } catch (Exception e) {
        }
        return finalKey;
    }

    public static String ProgramForAES(String baseString) {
        String cipher = "";
        try {
            String key = "5785abf057d4eea9e59151f75a6fadb724768053df2acdfabb68f2b946b972c6";
            CryptLib cryptLib = new CryptLib();
            cipher = cryptLib.encryptPlainTextWithRandomIV(baseString, key);
            println("cipherText" + cipher);
            String decryptedString = cryptLib.decryptCipherTextWithRandomIV(cipher, key);
            println("decryptedString" + decryptedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipher;
    }


}