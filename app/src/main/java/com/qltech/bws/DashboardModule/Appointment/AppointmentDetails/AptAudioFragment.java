package com.qltech.bws.DashboardModule.Appointment.AppointmentDetails;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Activities.AddPlaylistActivity;
import com.qltech.bws.DashboardModule.Models.AppointmentDetailModel;
import com.qltech.bws.DashboardModule.Models.DownloadPlaylistModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;

import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;

import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.AudioAptListLayoutBinding;
import com.qltech.bws.databinding.FragmentAptAudioBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AptAudioFragment extends Fragment {
    FragmentAptAudioBinding binding;
    public FragmentManager f_manager;
    String PlaylistId, UserID;
    ArrayList<AppointmentDetailModel.Audio> appointmentDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_audio, container, false);
        View view = binding.getRoot();

        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        appointmentDetail = new ArrayList<>();
        if (getArguments() != null) {
            appointmentDetail = getArguments().getParcelableArrayList("AppointmentDetailList");
        }
        if (appointmentDetail.size() == 0) {

        } else {
            AudioListAdapter appointmentsAdapter = new AudioListAdapter(appointmentDetail, getActivity(), f_manager);
            RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            binding.rvAudioList.setLayoutManager(recentlyPlayed);
            binding.rvAudioList.setItemAnimator(new DefaultItemAnimator());
            binding.rvAudioList.setAdapter(appointmentsAdapter);
        }
        return view;
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.MyViewHolder> {
        private ArrayList<AppointmentDetailModel.Audio> listModelList;
        Context ctx;
        public FragmentManager f_manager;

        public AudioListAdapter(ArrayList<AppointmentDetailModel.Audio> listModelList, Context ctx, FragmentManager f_manager) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.f_manager = f_manager;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AudioAptListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.audio_apt_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            AppointmentDetailModel.Audio audiolist = listModelList.get(position);
            holder.binding.tvTitle.setText(audiolist.getName());

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.13f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);

            Glide.with(getActivity()).load(audiolist.getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    player = 1;
                    Fragment fragment = new TransparentPlayerFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.flMainLayout, fragment)
                            .addToBackStack("TransparentPlayerFragment")
                            .commit();

                    SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(listModelList.get(position));
                    editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                    editor.putInt(CONSTANTS.PREF_KEY_position, position);
                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "AppointmentDetailList");
                    editor.commit();
                }
            });

            holder.binding.llDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        showProgressBar();
                        Call<DownloadPlaylistModel> listCall = APIClient.getClient().getDownloadlistPlaylist(UserID, audiolist.getID(), PlaylistId);
                        listCall.enqueue(new Callback<DownloadPlaylistModel>() {
                            @Override
                            public void onResponse(Call<DownloadPlaylistModel> call, Response<DownloadPlaylistModel> response) {
                                if (response.isSuccessful()) {
                                    hideProgressBar();
                                    DownloadPlaylistModel model = response.body();
                                    if (model.getResponseData().getFlag().equalsIgnoreCase("0")
                                            || model.getResponseData().getFlag().equalsIgnoreCase("")) {
                                        holder.binding.llDownload.setClickable(true);
                                        holder.binding.llDownload.setEnabled(true);
                                        holder.binding.ivDownload.setImageResource(R.drawable.ic_download_white_icon);
                                    } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                                        holder.binding.ivDownload.setImageResource(R.drawable.ic_download_white_icon);
                                        holder.binding.ivDownload.setColorFilter(Color.argb(99, 99, 99, 99));
                                        holder.binding.ivDownload.setAlpha(255);
                                        holder.binding.llDownload.setClickable(false);
                                        holder.binding.llDownload.setEnabled(false);
                                    }
                                    BWSApplication.showToast(model.getResponseMessage(), getActivity());
                                }
                            }

                            @Override
                            public void onFailure(Call<DownloadPlaylistModel> call, Throwable t) {
                                hideProgressBar();
                            }
                        });

                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                    }
                }
            });

            holder.binding.llRemoveAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ctx, AddPlaylistActivity.class);
                    i.putExtra("AudioId", listModelList.get(position).getID());
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            AudioAptListLayoutBinding binding;

            public MyViewHolder(AudioAptListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}