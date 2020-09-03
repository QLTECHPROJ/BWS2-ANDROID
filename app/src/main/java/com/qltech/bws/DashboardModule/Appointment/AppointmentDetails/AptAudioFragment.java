package com.qltech.bws.DashboardModule.Appointment.AppointmentDetails;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.AppointmentDetailModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.AudioAptListLayoutBinding;
import com.qltech.bws.databinding.FragmentAptAudioBinding;

import java.util.ArrayList;

public class AptAudioFragment extends Fragment {
    FragmentAptAudioBinding binding;
    public FragmentManager f_manager;
    ArrayList<AppointmentDetailModel.Audio> appointmentDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_audio, container, false);
        View view = binding.getRoot();
        appointmentDetail = new ArrayList<>();
        if (getArguments() != null) {
            appointmentDetail = getArguments().getParcelableArrayList("AppointmentDetailList");
        }
        if(appointmentDetail.size() == 0){

        }else{
            AudioListAdapter appointmentsAdapter = new AudioListAdapter(appointmentDetail, getActivity(), f_manager);
            RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            binding.rvAudioList.setLayoutManager(recentlyPlayed);
            binding.rvAudioList.setItemAnimator(new DefaultItemAnimator());
            binding.rvAudioList.setAdapter(appointmentsAdapter);
        }

        return view;
    }
    public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.MyViewHolder>{
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
            holder.binding.ivRestaurantImage.setImageResource(R.drawable.square_logo);
            holder.binding.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);

            Glide.with(getActivity()).load(audiolist.getImageFile()).thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage);
        }

        @Override
        public int getItemCount() {
            return 1;
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