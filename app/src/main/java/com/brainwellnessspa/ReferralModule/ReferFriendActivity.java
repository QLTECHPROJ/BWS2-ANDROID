package com.brainwellnessspa.ReferralModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.MembershipModule.Activities.MembershipActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityReferFriendBinding;

public class ReferFriendActivity extends AppCompatActivity {
    ActivityReferFriendBinding binding;
    Context ctx;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refer_friend);
        ctx = ReferFriendActivity.this;
        activity = ReferFriendActivity.this;
        prepareData();

        binding.llCodeCopy.setOnClickListener(v -> {
            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", binding.tvCodeCopy.getText());
            if (manager != null) {
                manager.setPrimaryClip(clipData);
            }
            BWSApplication.showToast("Promcode copied", ctx);
        });

        binding.llShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, download this app using this link\n" + "https://www.google.com/");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        binding.llCopyLink.setOnClickListener(v -> {
            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", binding.tvCodeCopy.getText());
            if (manager != null) {
                manager.setPrimaryClip(clipData);
            }
            BWSApplication.showToast("Promcode copied", ctx);
        });

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