package com.brainwellnessspa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaSessionManager;
import androidx.media.session.MediaButtonReceiver;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Services.NotificationActionService;
import com.brainwellnessspa.Services.PlayerJobService;
import com.brainwellnessspa.SplashModule.Models.VersionModel;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.AppSignatureHashHelper;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.CryptLib;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.Utility.Track;
import com.segment.analytics.Properties;

import java.io.File;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.SplashModule.SplashScreenActivity.analytics;
import static java.sql.DriverManager.println;

public class BWSApplication extends Application {
    public static final String CHANNEL_ID = "channel1";
    public static final String ACTION_PREVIUOS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'playlist_table' ADD COLUMN 'PlaylistImageDetails' TEXT");
        }
    };
    private static final int NOTIFICATION_ID = 101;
    public static MediaSessionCompat mMediaSession = null;
    public static PendingIntent play_pauseAction = null;
    public static boolean usesChronometer = false;
    public static boolean showWhen = false;
    public static Long notifWhen = 0L;
    public static MediaSessionManager mediaSessionManager;
    public static MediaSessionCompat mediaSession;
    public static MediaControllerCompat.TransportControls transportControls;
    public static Notification notification;
    //    public static NotificationManager notificationManager;
    private static Context mContext;
    private static BWSApplication BWSApplication;
    private static List<DownloadAudioDetails> downloadAudioDetailsList;
    private static Bitmap myBitmap;
    private static Service service;
    private static Bitmap mCurrTrackCover;
    private static Track track;

    public static Context getContext() {
        return mContext;
    }

    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, PlayerJobService.class);
        JobInfo.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder = new JobInfo.Builder(0, serviceComponent);
            builder.setMinimumLatency(1 * 1000); // wait at least
            builder.setOverrideDeadline(3 * 1000); // maximum delay
            //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
            //builder.setRequiresDeviceIdle(true); // device should be idle
            //builder.setRequiresCharging(false); // we don't care if the device is charging or not
            JobScheduler jobScheduler = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                jobScheduler = context.getSystemService(JobScheduler.class);
            }
            jobScheduler.schedule(builder.build());
        }
    }

    public static MeasureRatio measureRatio(Context context, float outerMargin, float aspectX, float aspectY, float proportion, float innerMargin) {
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

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static void addToSegment(String TagName, Properties properties, String methodName) {
        try {
            if (methodName.equalsIgnoreCase("track")) {
                analytics.track(TagName, properties);
            } else if (methodName.equalsIgnoreCase("screen")) {
                analytics.screen(TagName, properties);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
  /*  public static void createChannel(Context ctx) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Brain Wellness App", NotificationManager.IMPORTANCE_LOW);

                notificationManager = ctx.getSystemService(NotificationManager.class);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            } else {
                notificationManager = ctx.getSystemService(NotificationManager.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cancelNotification(Context ctx) {
        notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID); // Notification ID to cancel
    }*/

    public static void getLatasteUpdate(Context context) {
        String appURI = "https://play.google.com/store/apps/details?id=com.brainwellnessspa";
        if (BWSApplication.isNetworkConnected(context)) {
            Call<VersionModel> listCall = APIClient.getClient().getVersionDatas(String.valueOf(BuildConfig.VERSION_CODE), CONSTANTS.FLAG_ONE);
            listCall.enqueue(new Callback<VersionModel>() {
                @Override
                public void onResponse(Call<VersionModel> call, Response<VersionModel> response) {
                    try {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            Toast toast = new Toast(context);
            View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
            TextView tvMessage = view.findViewById(R.id.tvMessage);
            tvMessage.setText(message);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 35);
            toast.setView(view);
            toast.show();
        } else {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }

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

    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            boolean flag = false;
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//For 3G check
            boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
//For WiFi Check
            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
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

    /*    public static ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
                player = binder.getService();
                serviceBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                serviceBound = false;
            }
        };*/
   /* public boolean isMyServiceRunning(GlobleInItExoPlayer serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }*/
    public static void turnOffDozeMode(Context context) {  //you can use with or without passing context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm.isIgnoringBatteryOptimizations(packageName)) // if you want to desable doze mode for this package
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            else { // if you want to enable doze mode
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            }
            context.startActivity(intent);
        }
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        String packageName = context.getPackageName();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
            if(!isIgnoringBatteryOptimizations){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, MY_IGNORE_OPTIMIZATION_REQUEST);
        }
        }*/

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        BWSApplication = this;
    }


}