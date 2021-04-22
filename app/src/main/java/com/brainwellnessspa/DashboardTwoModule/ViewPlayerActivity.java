package com.brainwellnessspa.DashboardTwoModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.brainwellnessspa.DashboardModule.Models.AddToQueueModel;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.DashboardTwoModule.Model.HomeDataModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityViewPlayerBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;

public class ViewPlayerActivity extends AppCompatActivity {
    ActivityViewPlayerBinding binding;
    String AudioPlayerFlag="";
    ArrayList<MainPlayModel> mainPlayModelList = new ArrayList<>();
    int position = 0,listSize=0;
    Gson gson = new Gson();
    Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_player);
        ctx = ViewPlayerActivity.this;
        makePlayerArray();
    }

    private void makePlayerArray() {
        audioClick = true;
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        String json = shared.getString(CONSTANTS.PREF_KEY_MainAudioList, String.valueOf(gson));
        AudioPlayerFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
        MainPlayModel mainPlayModel;
        mainPlayModelList = new ArrayList<>();
        position = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
        if (AudioPlayerFlag.equalsIgnoreCase("MainAudioList")) {
            Type type = new TypeToken<HomeDataModel.ResponseData.Audio.Detail>() {
            }.getType();
            ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList,  gson.toJson(mainPlayModelList));
            editor.apply();
        }
    }
//    private void getPrepareShowData() {
//        callButtonText(position);
//
//            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
//            globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList);
//            initializePlayer();
//        setPlayerCtrView();
//    }

    private void callButtonText(int position) {
    }

}