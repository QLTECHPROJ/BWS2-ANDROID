package com.qltech.bws.AddPaymentModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.qltech.bws.R;
import com.qltech.bws.databinding.ActivityAddPaymentBinding;

public class AddPaymentActivity extends AppCompatActivity {
    ActivityAddPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_payment);

        binding.llBack.setOnClickListener(view -> finish());
        binding.btnSave.setOnClickListener(view -> {
            Toast.makeText(AddPaymentActivity.this, "Saved Sucessfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}