package com.brainwellnessspa.FaqModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.FaqModule.Models.FaqListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityFaqBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.segment.analytics.Properties;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaqActivity extends AppCompatActivity {
    ActivityFaqBinding binding;
    FaqListModel faqListModel;
    private ArrayList<FaqListModel.ResponseData> modelList;
    Activity activity;
    String UserID;
    ArrayList<String> section;
    GsonBuilder gsonBuilder;
    Gson gson;
    Properties p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_faq);
        activity = FaqActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        modelList = new ArrayList<>();
        section = new ArrayList<>();
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        PrepareData();
        binding.llBack.setOnClickListener(view -> finish());
        p = new Properties();
        p.putValue("userId", UserID);
        section.add("Audio");
        section.add("Playlist");
        section.add("General");
        p.putValue("faqCategories", gson.toJson(section));
        BWSApplication.addToSegment("FAQ Viewed", p, CONSTANTS.screen);
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
                overridePendingTransition(0, 0);
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
                overridePendingTransition(0, 0);
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
                overridePendingTransition(0, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void PrepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<FaqListModel> listCall = APIClient.getClient().getFaqLists();
            listCall.enqueue(new Callback<FaqListModel>() {
                @Override
                public void onResponse(Call<FaqListModel> call, Response<FaqListModel> response) {
                    try {
                        FaqListModel listModel = response.body();
                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
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
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}