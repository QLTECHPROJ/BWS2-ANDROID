package com.brainwellnessspa.FaqModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.brainwellnessspa.FaqModule.Adapters.AudioFaqAdapter;
import com.brainwellnessspa.FaqModule.Models.FaqListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.ActivityAudioFaqBinding;

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
        binding.llBack.setOnClickListener(view -> finish());

        if (Flag.equalsIgnoreCase("Audio")) {
            binding.tvTitle.setText(R.string.Audio);
        } else if (Flag.equalsIgnoreCase("General")) {
            binding.tvTitle.setText("General");
        } else if (Flag.equalsIgnoreCase("Playlist")) {
            binding.tvTitle.setText(R.string.Playlist);
        }

        RecyclerView.LayoutManager serachList = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvFaqList.setLayoutManager(serachList);
        binding.rvFaqList.setItemAnimator(new DefaultItemAnimator());

        if (faqListModel.size() == 0){
            binding.tvFound.setVisibility(View.VISIBLE);
            binding.rvFaqList.setVisibility(View.GONE);
        }else {
            binding.tvFound.setVisibility(View.GONE);
            binding.rvFaqList.setVisibility(View.VISIBLE);
            adapter = new AudioFaqAdapter(faqListModel, ctx, binding.rvFaqList, binding.tvFound);
            binding.rvFaqList.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}