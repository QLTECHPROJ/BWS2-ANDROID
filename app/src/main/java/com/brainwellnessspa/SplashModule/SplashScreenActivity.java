package com.brainwellnessspa.SplashModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.SplashModule.Models.VersionModel;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.AppSignatureHashHelper;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivitySplashScreenBinding;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.BWSApplication.getKey;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;

public class SplashScreenActivity extends AppCompatActivity {
    ActivitySplashScreenBinding binding;
    public static String key = "", UserID;
    String flag, id, title, message, IsLock;
    public static Analytics analytics;
    List<String> downloadAudioDetailsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
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
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
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
            new Handler().postDelayed(() -> {
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
                if (IsLock.equalsIgnoreCase("1")) {
                    resultIntent = new Intent(this, DashboardActivity.class);
                    startActivity(resultIntent);
                    finish();
                } else if (IsLock.equalsIgnoreCase("2")) {
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
            new Handler().postDelayed(() -> {
                Intent i = new Intent(SplashScreenActivity.this, DashboardActivity.class);
                startActivity(i);
                finish();
            }, 2 * 800);
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
                        try {
//     TODO :                         Live segment key
//                            analytics = new Analytics.Builder(getApplication(), "Al8EubbxttJtx0GvcsQymw9ER1SR2Ovy")//live
                            analytics = new Analytics.Builder(getApplication(), getString(R.string.segment_key_foram))//foram
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
                            Log.e("in Catch", "True");
//            Properties p = new Properties();
//            p.putValue("Application Crashed", e.toString());
//            YupITApplication.addtoSegment("Application Crashed", p,  CONSTANTS.track);

                        }
                        if (versionModel.getResponseData().getIsForce().equalsIgnoreCase("0")) {

                            GetAllMedia();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Update Brain Wellness Spa");
                            builder.setCancelable(false);
                            builder.setMessage("Brain Wellness Spa recommends that you update to the latest version")
                                    .setPositiveButton("UPDATE", (dialog, id) -> {
                                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appURI)));
                                        dialog.cancel();
                                    })
                                    .setNegativeButton("NOT NOW", (dialog, id) -> {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<VersionModel> call, Throwable t) {
                }
            });
        } else {
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
            BWSApplication.showToast(context.getString(R.string.no_server_found), context);
        }
    }

    public void GetAllMedia() {
        class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadAudioDetailsList = DatabaseClient
                        .getInstance(SplashScreenActivity.this)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllDataBYDownloaded1();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                for (int i = 0; i < downloadAudioDetailsList.size(); i++) {
                    FileUtils.deleteDownloadedFile1(getApplicationContext(), downloadAudioDetailsList.get(i));
                }
                DeletallLocalCart();
                super.onPostExecute(aVoid);
            }
        }
        GetTask st = new GetTask();
        st.execute();
    }

    private void DeletallLocalCart() {
        class DeletallCart extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient
                        .getInstance(SplashScreenActivity.this)
                        .getaudioDatabase()
                        .taskDao()
                        .deleteAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                DeletallLocalCart1();
                super.onPostExecute(aVoid);
            }
        }
        DeletallCart st = new DeletallCart();
        st.execute();
    }

    public void DeletallLocalCart1() {
        class DeletallCart extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient
                        .getInstance(SplashScreenActivity.this)
                        .getaudioDatabase()
                        .taskDao()
                        .deleteAllPlalist();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                super.onPostExecute(aVoid);
            }
        }
        DeletallCart st = new DeletallCart();
        st.execute();
    }
}