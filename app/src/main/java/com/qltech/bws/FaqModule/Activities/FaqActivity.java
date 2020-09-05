package com.qltech.bws.FaqModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.qltech.bws.BWSApplication;
import com.qltech.bws.FaqModule.Adapters.AudioFaqAdapter;
import com.qltech.bws.FaqModule.Models.FaqListModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.databinding.ActivityFaqBinding;

import java.util.ArrayList;
import java.util.List;

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
        BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
        if (BWSApplication.isNetworkConnected(this)) {
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
            Toast.makeText(this, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.llAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modelList.clear();
                modelList = new ArrayList<>();
                for (int i = 0; i < faqListModel.getResponseData().size(); i++) {
                    if (faqListModel.getResponseData().get(i).getCategory().contains("Audio")) {
                        modelList.add(faqListModel.getResponseData().get(i));
                    }
                }
                Intent i = new Intent(FaqActivity.this, AudioFaqActivity.class);
                i.putParcelableArrayListExtra("faqListModel", modelList);
                startActivity(i);
            }
        });

        binding.llHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modelList.clear();
                modelList = new ArrayList<>();
                for (int i = 0; i < faqListModel.getResponseData().size(); i++) {
                    if (faqListModel.getResponseData().get(i).getCategory().contains("Help")) {
                        modelList.add(faqListModel.getResponseData().get(i));
                    }
                }
                Intent i = new Intent(FaqActivity.this, AudioFaqActivity.class);
                i.putExtra("faqListModel", modelList);
                startActivity(i);
            }
        });

        binding.llPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modelList.clear();
                modelList = new ArrayList<>();
                for (int i = 0; i < faqListModel.getResponseData().size(); i++) {
                    if (faqListModel.getResponseData().get(i).getCategory().contains("Playlist")) {
                        modelList.add(faqListModel.getResponseData().get(i));
                    }
                }
                Intent i = new Intent(FaqActivity.this, AudioFaqActivity.class);
                i.putExtra("faqListModel", modelList);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}