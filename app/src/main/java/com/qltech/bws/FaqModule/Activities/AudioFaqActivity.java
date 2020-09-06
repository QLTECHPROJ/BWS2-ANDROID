package com.qltech.bws.FaqModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.qltech.bws.FaqModule.Adapters.AudioFaqAdapter;
import com.qltech.bws.FaqModule.Models.FaqListModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.ActivityAudioFaqBinding;

import java.util.ArrayList;

public class AudioFaqActivity extends AppCompatActivity {
    ActivityAudioFaqBinding binding;
    Context ctx;
    AudioFaqAdapter adapter;
    ArrayList<FaqListModel.ResponseData> faqListModel;
    String Flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_faq);
        ctx = AudioFaqActivity.this;

        faqListModel = new ArrayList<>();
        if (getIntent() != null) {
            faqListModel = getIntent().getParcelableArrayListExtra("faqListModel");
            Flag = getIntent().getStringExtra("Flag");
        }
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (Flag.equalsIgnoreCase("Audio")) {
            binding.tvTitle.setText(R.string.Audio);
        } else if (Flag.equalsIgnoreCase("Help")) {
            binding.tvTitle.setText(R.string.Help);
        } else if (Flag.equalsIgnoreCase("Playlist")) {
            binding.tvTitle.setText(R.string.Playlist);
        }

        RecyclerView.LayoutManager serachList = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvFaqList.setLayoutManager(serachList);
        binding.rvFaqList.setItemAnimator(new DefaultItemAnimator());
        adapter = new AudioFaqAdapter(faqListModel, ctx, binding.rvFaqList, binding.tvFound);
        binding.rvFaqList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}