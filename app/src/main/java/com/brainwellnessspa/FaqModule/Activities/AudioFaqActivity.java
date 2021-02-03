package com.brainwellnessspa.FaqModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.FaqModule.Models.FaqListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityAudioFaqBinding;
import com.brainwellnessspa.databinding.AudioFaqLayoutBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

public class AudioFaqActivity extends AppCompatActivity {
    ActivityAudioFaqBinding binding;
    Context ctx;
    AudioFaqAdapter adapter;
    ArrayList<FaqListModel.ResponseData> faqListModel;
    String Flag;
    String UserID;
    ArrayList<String> section;
    GsonBuilder gsonBuilder;
    Gson gson;
    Properties p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_faq);
        ctx = AudioFaqActivity.this;
        faqListModel = new ArrayList<>();
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        if (getIntent() != null) {
            faqListModel = getIntent().getParcelableArrayListExtra("faqListModel");
            Flag = getIntent().getStringExtra("Flag");
        }
        binding.llBack.setOnClickListener(view -> finish());
        section = new ArrayList<>();
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
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

        if (faqListModel.size() == 0) {
            binding.tvFound.setVisibility(View.VISIBLE);
            binding.rvFaqList.setVisibility(View.GONE);
        } else {
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

    public class AudioFaqAdapter extends RecyclerView.Adapter<AudioFaqAdapter.MyViewHolder> {
        private List<FaqListModel.ResponseData> modelList;
        Context ctx;
        RecyclerView rvFaqList;
        TextView tvFound;

        public AudioFaqAdapter(List<FaqListModel.ResponseData> modelList, Context ctx, RecyclerView rvFaqList, TextView tvFound) {
            this.modelList = modelList;
            this.ctx = ctx;
            this.rvFaqList = rvFaqList;
            this.tvFound = tvFound;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AudioFaqLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.audio_faq_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            p = new Properties();
            p.putValue("userId", UserID);
            if (Flag.equalsIgnoreCase("Audio")) {
                p.putValue("faqCategory", "Audio");
            } else if (Flag.equalsIgnoreCase("General")) {
                p.putValue("faqCategory", "General");
            } else if (Flag.equalsIgnoreCase("Playlist")) {
                p.putValue("faqCategory", "Playlist");
            }
            for (int i = 0; i < modelList.size(); i++) {
                section.add(modelList.get(position).getTitle());
                section.add(modelList.get(position).getTitle());
            }
            p.putValue("faqDescription", modelList.get(position).getDesc());
            BWSApplication.addToSegment("FAQ Clicked", p, CONSTANTS.screen);
            holder.binding.tvTitle.setText(modelList.get(position).getTitle());
            holder.binding.tvDesc.setText(modelList.get(position).getDesc());
            holder.binding.ivClickRight.setOnClickListener(view -> {
                holder.binding.llMainLayout.setBackgroundResource(R.color.discalimer_gray);
                holder.binding.tvDesc.setFocusable(true);
                holder.binding.tvDesc.requestFocus();
                holder.binding.tvDesc.setVisibility(View.VISIBLE);
                holder.binding.ivClickRight.setVisibility(View.GONE);
                holder.binding.ivClickDown.setVisibility(View.VISIBLE);
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_down_black_icon);
            });

            holder.binding.ivClickDown.setOnClickListener(view -> {
                holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                holder.binding.tvDesc.setVisibility(View.GONE);
                holder.binding.ivClickRight.setVisibility(View.VISIBLE);
                holder.binding.ivClickDown.setVisibility(View.GONE);
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_back_black_icon);
            });

            if (modelList.size() == 0) {
                tvFound.setVisibility(View.VISIBLE);
                rvFaqList.setVisibility(View.GONE);
            } else {
                tvFound.setVisibility(View.GONE);
                rvFaqList.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            AudioFaqLayoutBinding binding;

            public MyViewHolder(AudioFaqLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}