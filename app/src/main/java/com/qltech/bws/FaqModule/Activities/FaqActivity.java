package com.qltech.bws.FaqModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.qltech.bws.BWSApplication;
import com.qltech.bws.FaqModule.Models.FaqListModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.databinding.ActivityFaqBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaqActivity extends AppCompatActivity {
    ActivityFaqBinding binding;
    FaqListModel faqListModel;
    private ArrayList<FaqListModel.ResponseData> modelList;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_faq);
        activity = FaqActivity.this;
        modelList = new ArrayList<>();
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
            Call<FaqListModel> listCall = APIClient.getClient().getFaqLists();
            listCall.enqueue(new Callback<FaqListModel>() {
                @Override
                public void onResponse(Call<FaqListModel> call, Response<FaqListModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        FaqListModel listModel = response.body();
                        faqListModel = listModel;
                    }
                }

                @Override
                public void onFailure(Call<FaqListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this);
        }

        binding.llBack.setOnClickListener(view -> finish());

        binding.llAudio.setOnClickListener(view -> {
            try {
                modelList.clear();
                modelList = new ArrayList<>();
                for (int i = 0; i < faqListModel.getResponseData().size(); i++) {
                    if (faqListModel.getResponseData().get(i).getCategory().contains("Audio")) {
                        modelList.add(faqListModel.getResponseData().get(i));
                    }
                }
                Intent i = new Intent(FaqActivity.this, AudioFaqActivity.class);
                i.putExtra("Flag", "Audio");
                i.putParcelableArrayListExtra("faqListModel", modelList);
                startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.llHelp.setOnClickListener(view -> {
            try {
                modelList.clear();
                modelList = new ArrayList<>();
                for (int i = 0; i < faqListModel.getResponseData().size(); i++) {
                    if (faqListModel.getResponseData().get(i).getCategory().contains("Help")) {
                        modelList.add(faqListModel.getResponseData().get(i));
                    }
                }
                Intent i = new Intent(FaqActivity.this, AudioFaqActivity.class);
                i.putExtra("Flag", "Help");
                i.putExtra("faqListModel", modelList);
                startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.llPlaylists.setOnClickListener(view -> {
            try {
                modelList.clear();
                modelList = new ArrayList<>();
                for (int i = 0; i < faqListModel.getResponseData().size(); i++) {
                    if (faqListModel.getResponseData().get(i).getCategory().contains("Playlist")) {
                        modelList.add(faqListModel.getResponseData().get(i));
                    }
                }
                Intent i = new Intent(FaqActivity.this, AudioFaqActivity.class);
                i.putExtra("Flag", "Playlist");
                i.putExtra("faqListModel", modelList);
                startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}