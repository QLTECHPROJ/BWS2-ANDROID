package com.brainwellnessspa.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.AppUtils;
import com.brainwellnessspa.databinding.ActivityTncBinding;

import java.util.List;

public class TncActivity extends AppCompatActivity {
    ActivityTncBinding binding;
    String Web;
    Activity activity;
    String Tnc = AppUtils.tncs_url;
    String PrivacyPolicy = AppUtils.privacy_policy_url;
    String HowReferWorks = AppUtils.how_refer_works_url;

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
        finish();
    }
}