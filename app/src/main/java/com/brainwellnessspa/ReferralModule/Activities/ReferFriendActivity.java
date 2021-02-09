package com.brainwellnessspa.ReferralModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.WebView.TncActivity;
import com.brainwellnessspa.databinding.ActivityReferFriendBinding;
import com.segment.analytics.Properties;

public class ReferFriendActivity extends AppCompatActivity {
    ActivityReferFriendBinding binding;
    Context ctx;
    Activity activity;
    String UserPromocode = "", ReferLink = "", UserID;
    Properties p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refer_friend);
        ctx = ReferFriendActivity.this;
        activity = ReferFriendActivity.this;
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shareded = getSharedPreferences(CONSTANTS.PREF_KEY_Referral, Context.MODE_PRIVATE);
        UserPromocode = (shareded.getString(CONSTANTS.PREF_KEY_UserPromocode, ""));
        ReferLink = (shareded.getString(CONSTANTS.PREF_KEY_ReferLink, ""));
        p = new Properties();
        p.putValue("userId", UserID);
        p.putValue("referLink", ReferLink);
        p.putValue("userReferCode", UserPromocode);
        BWSApplication.addToSegment("Refer A Friend Screen Viewed", p, CONSTANTS.screen);
        prepareData();
        binding.tvCodeCopy.setText(UserPromocode);

        binding.llBack.setOnClickListener(v -> {
            finish();
        });

        if (UserPromocode.equalsIgnoreCase("")) {
            binding.tvCodeCopy.setVisibility(View.INVISIBLE);
        } else {
            binding.tvCodeCopy.setVisibility(View.VISIBLE);
        }

        binding.llCodeCopy.setOnClickListener(v -> {
            if (UserPromocode.equalsIgnoreCase("")) {
                BWSApplication.showToast(getString(R.string.not_available), ctx);
            } else {
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", binding.tvCodeCopy.getText());
                if (manager != null) {
                    manager.setPrimaryClip(clipData);
                }
                BWSApplication.showToast("Promo Code Copied", ctx);
                p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("userReferCode", UserPromocode);
                BWSApplication.addToSegment("Promo Code Copied", p, CONSTANTS.track);
            }
        });

        binding.tvInviteRules.setOnClickListener(v -> {
            Intent i = new Intent(ctx, TncActivity.class);
            i.putExtra(CONSTANTS.Web, "HowReferWorks");
            startActivity(i);
            p = new Properties();
            p.putValue("userId", UserID);
            p.putValue("invitationRuleUrl", "");
            BWSApplication.addToSegment("Invitation Rules Viewed", p, CONSTANTS.screen);
        });

        binding.llShare.setOnClickListener(v -> {
            if (ReferLink.equalsIgnoreCase("")) {
                BWSApplication.showToast(getString(R.string.not_available), ctx);
            } else {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, I am loving using the Brain Wellness App. You can develop yourself " +
                        "in the comfort of your home while you sleep and gain access to over 75 audio programs helping you " +
                        "to live inspired and improve your mental wellbeing. I would like to invite you to try it. " +
                        "Sign up using the link and get 30 days free trial\n" + ReferLink);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
                p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("referLink", ReferLink);
                p.putValue("userReferCode", UserPromocode);
                p.putValue("shareText", "Hey, I am loving using the Brain Wellness App. You can develop yourself " +
                        "in the comfort of your home while you sleep and gain access to over 75 audio programs helping you " +
                        "to live inspired and improve your mental wellbeing. I would like to invite you to try it. " +
                        "Sign up using the link and get 30 days free trial\n" + ReferLink);
                BWSApplication.addToSegment("Share Clicked", p, CONSTANTS.track);
            }
        });

/*
        binding.llCopyLink.setOnClickListener(v -> {
            if (UserPromocode.equalsIgnoreCase("")) {
                BWSApplication.showToast(getString(R.string.not_available), ctx);
            } else {
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", binding.tvCodeCopy.getText());
                if (manager != null) {
                    manager.setPrimaryClip(clipData);
                }
                BWSApplication.showToast("Promo Code Copied", ctx);
                p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("userReferCode", UserPromocode);
                BWSApplication.addToSegment("Promo Code Copied", p, CONSTANTS.track);
            }
        });
*/

        binding.btnReferred.setOnClickListener(v -> {
            Intent i = new Intent(ctx, ContactBookActivity.class);
            startActivity(i);
        });
    }

    private void prepareData() {
        binding.tvTitle.setText(getString(R.string.refer_title));
        binding.tvDesc.setText(getString(R.string.refer_desc));
        binding.tvInviteTitle.setText(getString(R.string.refer_invite_title));
        binding.tvInviteRules.setText(getString(R.string.refer_invite_rules_click));
        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                5, 3, 1f, 40);
        binding.ivReferImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivReferImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivReferImage.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.ivReferImage.setImageResource(R.drawable.refer_friend_banner);
    }
}