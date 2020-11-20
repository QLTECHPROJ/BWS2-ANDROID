package com.brainwellnessspa.FaqModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.FaqModule.Models.FaqListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.databinding.ActivityFaqBinding;

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
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<FaqListModel> listCall = APIClient.getClient().getFaqLists();
            listCall.enqueue(new Callback<FaqListModel>() {
                @Override
                public void onResponse(Call<FaqListModel> call, Response<FaqListModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            FaqListModel listModel = response.body();
                            faqListModel = listModel;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<FaqListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
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
                    if (faqListModel.getResponseData().get(i).getCategory().contains("General")) {
                        modelList.add(faqListModel.getResponseData().get(i));
                    }
                }
                Intent i = new Intent(FaqActivity.this, AudioFaqActivity.class);
                i.putExtra("Flag", "General");
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