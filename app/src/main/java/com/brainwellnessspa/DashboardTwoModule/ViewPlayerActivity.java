package com.brainwellnessspa.DashboardTwoModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.ActivityViewPlayerBinding;

public class ViewPlayerActivity extends AppCompatActivity {
    ActivityViewPlayerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_player);

    }
}