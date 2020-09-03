package com.qltech.bws.DashboardModule.TransparentPlayer.Fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.DashboardModule.Activities.PlayWellnessActivity;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.DashboardModule.Models.ViewAllAudioListModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MusicService;
import com.qltech.bws.databinding.FragmentTransparentPlayerBinding;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;

public class TransparentPlayerFragment extends Fragment {
    FragmentTransparentPlayerBinding binding;
    String UserID, AudioFlag;
    int position = 0;
    ArrayList<MainAudioModel.ResponseData.Detail> mainAudio;
    ArrayList<ViewAllAudioListModel.ResponseData.Detail> viewAllAudioListModel;
    ArrayList<MainPlayModel> mainAudioModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transparent_player, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        ArrayList<CartModel.LocalCart> cartList = new ArrayList<>();
        for (int i = 0; i < cartProductDetailslist.size(); i++) {
            CartModel.LocalCart cart = new CartModel.LocalCart();
            cart.setuID(cartProductDetailslist.get(i).getUid());
            cart.setItemID(cartProductDetailslist.get(i).getProductID());
            cart.setQuantity(cartProductDetailslist.get(i).getProductQty());
            cart.setItemName(cartProductDetailslist.get(i).getProductName());
            cart.setProductDescription(cartProductDetailslist.get(i).getProductDes());
            cart.setIsDetail(cartProductDetailslist.get(i).getIsDetail());
            cart.setItem_Image(cartProductDetailslist.get(i).getProductImage());
            cart.setCurruntPrice(cartProductDetailslist.get(i).getCurrentPrice());
            cartList.add(cart);
//            cartModel_global.getResponseData().getResult().setCart();
            cartModel_global.getResponseData().getResult().setLocalCart(cartList);
        }
        if (getArguments() != null) {
            position = getArguments().getInt("position", 0);
            AudioFlag = getArguments().getString("AudioFlag");
            if (AudioFlag.equalsIgnoreCase("MainAudio")) {
                mainAudio = getArguments().getParcelableArrayList("modelList");

                MusicService.play(getActivity(), Uri.parse(mainAudioModel.get(position).getAudioFile()));
                MusicService.playMedia();
                Glide.with(getActivity()).load(mainAudioModel.get(position).getImageFile()).thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);

                binding.tvTitle.setText(mainAudioModel.get(position).getName());
                binding.tvSubTitle.setText("Play the " + mainAudioModel.get(position).getName() + " every night on a low volume.");

                binding.llPlayearMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getActivity(), PlayWellnessActivity.class);
                        i.putParcelableArrayListExtra("modelList", mainAudioModel);
                        i.putExtra("position", position);
                        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        getActivity().startActivity(i);
                    }
                });
            } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
                viewAllAudioListModel = getArguments().getParcelableArrayList("modelList");

                MusicService.play(getActivity(), Uri.parse(viewAllAudioListModel.get(position).getAudioFile()));
                MusicService.playMedia();
                Glide.with(getActivity()).load(viewAllAudioListModel.get(position).getImageFile()).thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);

                binding.tvTitle.setText(viewAllAudioListModel.get(position).getName());
                binding.tvSubTitle.setText("Play the " + viewAllAudioListModel.get(position).getName() + " every night on a low volume.");

                binding.llPlayearMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getActivity(), PlayWellnessActivity.class);
                        i.putParcelableArrayListExtra("modelList", viewAllAudioListModel);
                        i.putExtra("position", position);
                        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        getActivity().startActivity(i);
                    }
                });
            }else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {

            }
        }


        binding.ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.ivPause.setVisibility(View.GONE);
                binding.ivPlay.setVisibility(View.VISIBLE);
                MusicService.pauseMedia();
            }
        });

        binding.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.ivPlay.setVisibility(View.GONE);
                binding.ivPause.setVisibility(View.VISIBLE);
                MusicService.playMedia();
            }
        });

        return view;
    }
}