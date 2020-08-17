package com.qltech.bws.OtpModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.qltech.bws.DashboardModule.Activities.DashboardActivity;
import com.qltech.bws.R;
import com.qltech.bws.databinding.ActivityOtpBinding;

public class OtpActivity extends AppCompatActivity {

    ActivityOtpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp);

        binding.tvSendCodeText.setText("We sent an SMS with a 4-digit code to" + " +612134567890");

        binding.btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OtpActivity.this, DashboardActivity.class);
                startActivity(i);
            }
        });
    }
}