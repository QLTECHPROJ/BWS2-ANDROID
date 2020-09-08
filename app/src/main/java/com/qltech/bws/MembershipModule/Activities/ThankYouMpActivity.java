package com.qltech.bws.MembershipModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;

import com.qltech.bws.DashboardModule.Activities.DashboardActivity;
import com.qltech.bws.InvoiceModule.Activities.InvoiceActivity;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityThankYouMembershipBinding;

public class ThankYouMpActivity extends AppCompatActivity {
    ActivityThankYouMembershipBinding binding;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_thank_you_membership);

        MeasureRatio measureRatio = BWSApplication.measureRatio(ThankYouMpActivity.this, 20,
                5, 6, 0.4f, 20);
        binding.ivLogo.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivLogo.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivLogo.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.ivLogo.setImageResource(R.drawable.ic_mp_thanku);

        binding.btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(ThankYouMpActivity.this, DashboardActivity.class);
                startActivity(i);
                finish();
            }
        });

        binding.tvViewInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(ThankYouMpActivity.this, InvoiceActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}