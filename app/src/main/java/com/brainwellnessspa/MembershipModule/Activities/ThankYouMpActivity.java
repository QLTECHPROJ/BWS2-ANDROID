package com.brainwellnessspa.MembershipModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;

import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity;
import com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityThankYouMembershipBinding;
import com.segment.analytics.Properties;

import static com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity.invoiceToDashboard;
import static com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity.invoiceToRecepit;

public class ThankYouMpActivity extends AppCompatActivity {
    ActivityThankYouMembershipBinding binding;
    private long mLastClickTime = 0;

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

        binding.btnExplore.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            invoiceToDashboard = 1;
            Intent i = new Intent(ThankYouMpActivity.this, DashboardActivity.class);
            startActivity(i);
            finish();
        });

        Properties p = new Properties();
        BWSApplication.addToSegment("Thank You Screen Viewed", p, CONSTANTS.screen);
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