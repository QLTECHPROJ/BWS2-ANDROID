package com.brainwellnessspa.membershipModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;

import com.brainwellnessspa.invoicemodule.activities.InvoiceActivity;
import com.brainwellnessspa.manageModule.SleepTimeActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.utility.CONSTANTS;
import com.brainwellnessspa.utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityThankYouMembershipBinding;
import com.segment.analytics.Properties;

import static com.brainwellnessspa.invoicemodule.activities.InvoiceActivity.invoiceToDashboard;
import static com.brainwellnessspa.invoicemodule.activities.InvoiceActivity.invoiceToRecepit;

public class ThankYouMpActivity extends AppCompatActivity {
    ActivityThankYouMembershipBinding binding;
    private long mLastClickTime = 0;
    String userId, coUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_thank_you_membership);
        MeasureRatio measureRatio = BWSApplication.measureRatio(ThankYouMpActivity.this, 0,
                5, 6, 0.4f, 0);
        binding.ivLogo.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivLogo.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivLogo.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.ivLogo.setImageResource(R.drawable.ic_mp_thanku);

        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        userId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        coUserId = (shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, ""));

        Properties p = new Properties();
        p.putValue("coUserId", coUserId);
        BWSApplication.addToSegment("Thank You Screen Viewed", p, CONSTANTS.screen);
        binding.btnExplore.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            invoiceToDashboard = 1;
            Intent i = new Intent(ThankYouMpActivity.this, SleepTimeActivity.class);
            startActivity(i);
            finish();
        });

        binding.tvViewInvoice.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            invoiceToRecepit = 0;
            Intent i = new Intent(ThankYouMpActivity.this, InvoiceActivity.class);
            i.putExtra("ComeFrom", "1");
            startActivity(i);
            finish();
        });
    }
}