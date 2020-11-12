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
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BuildConfig;
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity;
import com.brainwellnessspa.LoginModule.Activities.LoginActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.SplashModule.Models.VersionModel;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.AppSignatureHashHelper;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivitySplashScreenBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.BWSApplication.getKey;


public class SplashScreenActivity extends AppCompatActivity {
    ActivitySplashScreenBinding binding;
    public static String key = "", UserID;
    String flag, id, title, message, IsLock;

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

        getLatasteUpdate(SplashScreenActivity.this);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.ivBackground);
        binding.ivBackground.setMediaController(null);
        binding.ivBackground.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_video));
        binding.ivBackground.requestFocus();
        binding.ivBackground.start();
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
                                    callDashboard();
                                    dialog.dismiss();
                                });
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
                        callDashboard();
                    }
                }

                @Override
                public void onFailure(Call<VersionModel> call, Throwable t) {
                }
            });
        } else {
            callDashboard();
            BWSApplication.showToast(context.getString(R.string.no_server_found), context);
        }
    }
}