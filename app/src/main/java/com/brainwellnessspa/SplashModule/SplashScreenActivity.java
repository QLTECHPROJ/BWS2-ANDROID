package com.brainwellnessspa.SplashModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BuildConfig;
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.LoginModule.Activities.LoginActivity;
import com.brainwellnessspa.LoginModule.Activities.OtpActivity;
import com.brainwellnessspa.MembershipModule.Activities.ThankYouMpActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.AudioDatabase;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.SplashModule.Models.VersionModel;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.AppSignatureHashHelper;
import com.brainwellnessspa.Utility.AppUtils;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivitySplashScreenBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.segment.analytics.Analytics;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.BWSApplication.MIGRATION_1_2;
import static com.brainwellnessspa.BWSApplication.getKey;

public class SplashScreenActivity extends AppCompatActivity {
    ActivitySplashScreenBinding binding;
    public static String key = "", UserID;
    String flag, id, title, message, IsLock;
    public static Analytics analytics;
    FirebaseOptions options;
    AudioDatabase DB;
    String FirebaseAppName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(this);
        key = appSignatureHashHelper.getAppSignatures().get(0);
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Splash, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(CONSTANTS.PREF_KEY_SplashKey, appSignatureHashHelper.getAppSignatures().get(0));
        editor.commit();
        if (key.equalsIgnoreCase("")) {
            key = getKey(SplashScreenActivity.this);
        }
        DB = Room.databaseBuilder(SplashScreenActivity.this,
                AudioDatabase.class,
                "Audio_database")
                .addMigrations(MIGRATION_1_2)
                .build();
        /*if(AppUtils.BASE_URL.equalsIgnoreCase("http://brainwellnessspa.com.au/bwsapi/api/staging/v1/")) {
            options = new FirebaseOptions.Builder()
                    .setProjectId("brain-wellness-spa-d4ac0")
                    .setApplicationId("1:139951188296:android:e98df657440cfaa3722b7d")
                    .setApiKey("AIzaSyBOpWjRnWxRqQhOXcLW1hwFt9sGna5jy_Q")
                     .setStorageBucket("brain-wellness-spa-d4ac0.appspot.com")
                    .build();
            FirebaseAppName = "Staging";
        }else if(AppUtils.BASE_URL.equalsIgnoreCase("http://brainwellnessspa.com.au/bwsapi/api/live/v1/")) {
            options = new FirebaseOptions.Builder()
                    .setProjectId("brain-wellness-app-live")
                    .setApplicationId("1:753168671199:android:036d8f6ad0f156399aa46a")
                    .setApiKey("AIzaSyArmVHOmwZlXApRM4mNsUqcRbSkd6due-0")
                     .setStorageBucket("brain-wellness-app-live.appspot.com")
                    .build();
            FirebaseAppName = "Live";
        }
        FirebaseApp.initializeApp(this, options, FirebaseAppName);

// Retrieve secondary FirebaseApp
        FirebaseApp firebaseApp = FirebaseApp.getInstance(FirebaseAppName);*/
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FirebaseCrashlytics.getInstance().setUserId(UserID);
//        BWSApplication.turnOffDozeMode(SplashScreenActivity.this);
        getLatasteUpdate(SplashScreenActivity.this);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.ivBackground);
        binding.ivBackground.setMediaController(null);
        binding.ivBackground.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_video));
        binding.ivBackground.requestFocus();
        binding.ivBackground.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (requestCode == 15695) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                boolean isIgnoringBatteryOptimizations = false;
                isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
                if (isIgnoringBatteryOptimizations) {
                    // Ignoring battery optimization
                    callDashboard();
                } else {
                    // Not ignoring battery optimization
                    callDashboard();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void callDashboard() {
        if (UserID.equalsIgnoreCase("")) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }, 2 * 800);
        } else if (getIntent().hasExtra("flag")) {
            Intent resultIntent = null;
            flag = getIntent().getStringExtra("flag");
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            message = getIntent().getStringExtra("message");
            IsLock = getIntent().getStringExtra("IsLock");
            if (flag != null && flag.equalsIgnoreCase("Playlist")) {
                if (!IsLock.equalsIgnoreCase("0")) {
                    resultIntent = new Intent(this, DashboardActivity.class);
                    startActivity(resultIntent);
                    finish();
                } else {
                    resultIntent = new Intent(this, DashboardActivity.class);
                    resultIntent.putExtra("Goplaylist", "1");
                    resultIntent.putExtra("New", "0");
                    resultIntent.putExtra("PlaylistID", id);
                    resultIntent.putExtra("PlaylistName", title);
                    resultIntent.putExtra("PlaylistImage", "");
                    startActivity(resultIntent);
                    finish();
                }
            }
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent i = new Intent(SplashScreenActivity.this, DashboardActivity.class);
                startActivity(i);
                finish();
               /* SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putBoolean(CONSTANTS.PREF_KEY_Identify, true);
                editor.putBoolean(CONSTANTS.PREF_KEY_IdentifyAgain, true);
                editor.putString(CONSTANTS.PREF_KEY_UserID, "453");
                editor.putString(CONSTANTS.PREF_KEY_IsDisclimer, "0");
                editor.putString(CONSTANTS.PREF_KEY_PlayerFirstLogin, "0");
                editor.putString(CONSTANTS.PREF_KEY_AudioFirstLogin, "0");
                editor.putString(CONSTANTS.PREF_KEY_PlaylistFirstLogin, "0");
                editor.putString(CONSTANTS.PREF_KEY_AccountFirstLogin, "1");
                editor.putString(CONSTANTS.PREF_KEY_ReminderFirstLogin, "0");
                editor.putString(CONSTANTS.PREF_KEY_SearchFirstLogin, "0");
                editor.commit();

                SharedPreferences sharedm = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = sharedm.edit();
                editorr.remove(CONSTANTS.PREF_KEY_modelList);
                editorr.remove(CONSTANTS.PREF_KEY_audioList);
                editorr.remove(CONSTANTS.PREF_KEY_position);
                editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                editorr.remove(CONSTANTS.PREF_KEY_AudioFlag);
                editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                editorr.clear();
                editorr.commit();

                Intent i = new Intent(SplashScreenActivity.this, ThankYouMpActivity.class);
                startActivity(i);
                finish();*/
            }, 2 * 800);
        }
    }

    public void setAnalytics() {  
        try {
//     TODO : Live segment key
//                            analytics = new Analytics.Builder(getApplication(), "Al8EubbxttJtx0GvcsQymw9ER1SR2Ovy")//live
            analytics = new Analytics.Builder(getApplication(), getString(R.string.segment_key_real))//foram
                    .trackApplicationLifecycleEvents()
                    .logLevel(Analytics.LogLevel.VERBOSE).trackAttributionInformation()
                    .trackAttributionInformation()
                    .trackDeepLinks()
                    .collectDeviceId(true)
                    .build();
            /*.use(FirebaseIntegration.FACTORY) */
            Analytics.setSingletonInstance(analytics);
        } catch (Exception e) {
//            incatch = true;
//            Log.e("in Catch", "True");
//            Properties p = new Properties();
//            p.putValue("Application Crashed", e.toString());
//            YupITApplication.addtoSegment("Application Crashed", p,  CONSTANTS.track);
        }
    }


    public void getLatasteUpdate(Context context) {
        String appURI = "https://play.google.com/store/apps/details?id=com.brainwellnessspa";

        if (BWSApplication.isNetworkConnected(context)) {
            Call<VersionModel> listCall = APIClient.getClient().getVersionDatas(String.valueOf(BuildConfig.VERSION_CODE), CONSTANTS.FLAG_ONE);
            listCall.enqueue(new Callback<VersionModel>() {
                @Override
                public void onResponse(Call<VersionModel> call, Response<VersionModel> response) {
                    VersionModel versionModel = response.body();
                    try {
                        setAnalytics();
                        if (versionModel.getResponseData().getIsForce().equalsIgnoreCase("0")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Update Brain Wellness Spa");
                            builder.setCancelable(false);
                            builder.setMessage("Brain Wellness Spa recommends that you update to the latest version")
                                    .setPositiveButton("UPDATE", (dialog, id) -> {
                                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appURI)));
                                        dialog.cancel();
                                    })
                                    .setNegativeButton("NOT NOW", (dialog, id) -> {
                                        askBattryParmition();
                                        dialog.dismiss();
                                    });
                            builder.create().show();
                        } else if (versionModel.getResponseData().getIsForce().equalsIgnoreCase("1")) {
                            GetAllMedia();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Update Required");
                            builder.setCancelable(false);
                            builder.setMessage("To keep using Brain Wellness Spa, download the latest version")
                                    .setCancelable(false)
                                    .setPositiveButton("UPDATE", (dialog, id) -> context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appURI))));
                            builder.create().show();
                        } else if (versionModel.getResponseData().getIsForce().equalsIgnoreCase("")) {
                            askBattryParmition();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<VersionModel> call, Throwable t) {
                }
            });
        } else {
            setAnalytics();
            askBattryParmition();
            BWSApplication.showToast(context.getString(R.string.no_server_found), context);
        }
    }

    private void askBattryParmition() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(packageName);
            if (!isIgnoringBatteryOptimizations) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivityForResult(intent, 15695);
            } else {
                callDashboard();
            }
        } else {
            callDashboard();
        }
    }

    public void GetAllMedia() {
        DatabaseClient
                .getInstance(this)
                .getaudioDatabase()
                .taskDao()
                .geAllData12().observe(this, audioList -> {
            if (audioList.size() != 0) {
                for (int i = 0; i < audioList.size(); i++) {
                    FileUtils.deleteDownloadedFile(getApplicationContext(), audioList.get(i).getName());
                }
            }
            SharedPreferences preferences11 = getSharedPreferences(CONSTANTS.PREF_KEY_Logout_DownloadPlaylist, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit1 = preferences11.edit();
            edit1.remove(CONSTANTS.PREF_KEY_Logout_DownloadName);
            edit1.remove(CONSTANTS.PREF_KEY_Logout_DownloadUrl);
            edit1.remove(CONSTANTS.PREF_KEY_Logout_DownloadPlaylistId);
            edit1.clear();
            edit1.commit();
            DeletallLocalCart();

        });
    }

    private void DeletallLocalCart() {
        AudioDatabase.databaseWriteExecutor.execute(() -> {
            DB.taskDao().deleteAll();
        });
        AudioDatabase.databaseWriteExecutor.execute(() -> {
            DB.taskDao().deleteAllPlalist();
        });
    }
}