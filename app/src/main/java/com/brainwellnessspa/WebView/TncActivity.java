package com.brainwellnessspa.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.AppUtils;
import com.brainwellnessspa.databinding.ActivityTncBinding;

import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class TncActivity extends AppCompatActivity {
    ActivityTncBinding binding;
    String Web;
    Activity activity;
    String Tnc = AppUtils.tncs_url;
    String PrivacyPolicy = AppUtils.privacy_policy_url;
    String HowReferWorks = AppUtils.how_refer_works_url;
    private int numStarted = 0;
    int stackStatus = 0;
    boolean myBackPress = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tnc);
        activity = TncActivity.this;
        if (getIntent() != null) {
            Web = getIntent().getStringExtra("Web");
        }
        binding.webView.clearHistory();
        binding.webView.clearCache(true);

        binding.llBack.setOnClickListener(view -> {
            myBackPress = true;
            finish();
        });
        binding.webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                binding.progressBar.setProgress(progress);
                if (progress == 100) {
                    binding.progressBar.setVisibility(View.GONE);

                } else {
                    binding.progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebViewClient(new CustomWebViewClient());

        if (Web.equalsIgnoreCase("Tnc")) {
            binding.tvTitle.setText(R.string.t_n_csf);
            binding.webView.loadUrl(Tnc);
        } else if (Web.equalsIgnoreCase("PrivacyPolicy")) {
            binding.tvTitle.setText(R.string.privacy_policy);
            binding.webView.loadUrl(PrivacyPolicy);
        } else if (Web.equalsIgnoreCase("HowReferWorks")) {
            binding.tvTitle.setText(R.string.how_refer_works);
            binding.webView.loadUrl(HowReferWorks);
        } /*else {
            binding.tvTitle.setText(Web);
            binding.webView.loadUrl(HowReferWorks);
        }*/
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myBackPress = true;
        finish();
    }

    class AppLifecycleCallback implements Application.ActivityLifecycleCallbacks {


        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                stackStatus = 1;
                Log.e("APPLICATION", "APP IN FOREGROUND");
                //app went to foreground
            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            numStarted--;
            if (numStarted == 0) {
                if(!myBackPress) {
                    Log.e("APPLICATION", "Back press false");
                    stackStatus = 2;
                }else{
                    myBackPress = true;
                    stackStatus = 1;
                    Log.e("APPLICATION", "back press true ");
                }
                Log.e("APPLICATION", "App is in BACKGROUND");
                // app went to background
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationId);
                relesePlayer(getApplicationContext());
            }else{
                Log.e("Destroy", "Activity go in main activity");
            }
        }
    }
}