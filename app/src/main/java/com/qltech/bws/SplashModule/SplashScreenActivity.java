package com.qltech.bws.SplashModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.BuildConfig;
import com.qltech.bws.LoginModule.Activities.LoginActivity;
import com.qltech.bws.R;
import com.qltech.bws.SplashModule.Models.VersionModel;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivitySplashScreenBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {
    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);

        Glide.with(SplashScreenActivity.this).load(R.drawable.splash).asGif().into(binding.ivBackground);

//        getLatasteUpdate(SplashScreenActivity.this);

        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }, 2 * 1000);
    }

    public static void getLatasteUpdate(Context context) {
        String appURI = "";
        int ver_code = BuildConfig.VERSION_CODE;

        if (BWSApplication.isNetworkConnected(context)) {
            Call<VersionModel> listCall = APIClient.getClient().getVersionDatas(String.valueOf(ver_code), CONSTANTS.FLAG_ONE);
            listCall.enqueue(new Callback<VersionModel>() {
                @Override
                public void onResponse(Call<VersionModel> call, Response<VersionModel> response) {
                    if (response.isSuccessful()) {
                        VersionModel versionModel = response.body();
                        if (versionModel.getResponseCode().equalsIgnoreCase(context.getString(R.string.ResponseCodesuccess))) {
                            if (versionModel.getResponseData().getIsForce().equalsIgnoreCase("0")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("New Version");
                                builder.setCancelable(false);
                                builder.setMessage("There is a newer version available for download! Please update the app by visiting the Play Store")
                                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appURI)));
                                                dialog.cancel();
                                            }
                                        })
                                        .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });
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
                }

                @Override
                public void onFailure(Call<VersionModel> call, Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(context, context.getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }
    }
}