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
    public static String key = "";
    String flag, id, title, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);

        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(this);
        key = appSignatureHashHelper.getAppSignatures().get(0);

        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Splash, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(CONSTANTS.PREF_KEY_SplashKey, appSignatureHashHelper.getAppSignatures().get(0));
        editor.commit();
        if(key.equalsIgnoreCase("")){
            key = getKey(SplashScreenActivity.this);
        }

        getLatasteUpdate(SplashScreenActivity.this);

        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        String UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.ivBackground);
        binding.ivBackground.setMediaController(null);
        binding.ivBackground.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_video));
        binding.ivBackground.requestFocus();
        binding.ivBackground.start();

        if (UserID.equalsIgnoreCase("")) {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }, 2 * 1000);
        } else if (getIntent().hasExtra("flag")) {
            Intent resultIntent = null;
            flag = getIntent().getStringExtra("flag");
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            message = getIntent().getStringExtra("message");
            if (flag != null && flag.equalsIgnoreCase("Playlist")) {
                resultIntent = new Intent(this, DashboardActivity.class);
                resultIntent.putExtra("Goplaylist", "1");
                resultIntent.putExtra("PlaylistID", id);
                resultIntent.putExtra("PlaylistName", title);
                resultIntent.putExtra("PlaylistImage", "");
                startActivity(resultIntent);
                finish();
            }
        }else {
            new Handler().postDelayed(() -> {
                Intent i = new Intent(SplashScreenActivity.this, DashboardActivity.class);
                startActivity(i);
                finish();
            }, 2 * 1000);
        }
    }

    public static void getLatasteUpdate(Context context) {
        String appURI = "https://play.google.com/store/apps/details?id=com.brainwellnessspa";
        if (BWSApplication.isNetworkConnected(context)) {
            Call<VersionModel> listCall = APIClient.getClient().getVersionDatas(BuildConfig.VERSION_NAME, CONSTANTS.FLAG_ONE);
            listCall.enqueue(new Callback<VersionModel>() {
                @Override
                public void onResponse(Call<VersionModel> call, Response<VersionModel> response) {
                    if (response.isSuccessful()) {
                        VersionModel versionModel = response.body();
                        if (versionModel.getResponseData().getIsForce().equalsIgnoreCase("0")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("New Version");
                            builder.setCancelable(false);
                            builder.setMessage("There is a newer version available for download! Please update the app by visiting the Play Store")
                                    .setPositiveButton("Update", (dialog, id) -> {
                                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appURI)));
                                        dialog.cancel();
                                    })
                                    .setNegativeButton("Later", (dialog, id) -> dialog.dismiss());
                            builder.create().show();
                        } else if (versionModel.getResponseData().getIsForce().equalsIgnoreCase("1")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("New Version");
                            builder.setCancelable(false);
                            builder.setMessage("There is a newer version available for download! Please update the app by visiting the Play Store")
                                    .setCancelable(false)
                                    .setPositiveButton("Update", (dialog, id) -> context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appURI))));
                            builder.create().show();
                            AlertDialog alertDialog = null;
                            alertDialog.setCanceledOnTouchOutside(false);
                        } else if (versionModel.getResponseData().getIsForce().equalsIgnoreCase("")) {
                        }
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

}