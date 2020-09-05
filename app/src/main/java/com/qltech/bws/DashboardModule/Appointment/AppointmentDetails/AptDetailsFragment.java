package com.qltech.bws.DashboardModule.Appointment.AppointmentDetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.DashboardModule.Models.AppointmentDetailModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentAptDetailsBinding;

public class AptDetailsFragment extends Fragment {
    FragmentAptDetailsBinding binding;
    AppointmentDetailModel.ResponseData appointmentDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_details, container, false);
        View view = binding.getRoot();

        MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 10,
                1, 1, 0.24f, 10);
        binding.civProfile.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.civProfile.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        if (getArguments() != null) {
            appointmentDetail = getArguments().getParcelable("AppointmentDetail");
        }
        if(appointmentDetail.getDate().equalsIgnoreCase("")
                &&appointmentDetail.getUserName().equalsIgnoreCase("")
                &&appointmentDetail.getTime().equalsIgnoreCase("")){
            binding.llDetails.setVisibility(View.GONE);
        }else{
            binding.llDetails.setVisibility(View.VISIBLE);
        }
        binding.tvTilte.setText(appointmentDetail.getName());
        binding.tvFacilitator.setText(appointmentDetail.getFacilitator());
        binding.tvUserName.setText(appointmentDetail.getUserName());
        binding.tvSubTitle.setText(appointmentDetail.getDesc());
        binding.tvDate.setText(appointmentDetail.getDate());
        binding.tvTime.setText(appointmentDetail.getTime());
        Glide.with(getActivity()).load(appointmentDetail.getImage()).thumbnail(0.05f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.civProfile);

        binding.btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Book Now", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(appointmentDetail.getBookUrl()));
                startActivity(i);
//                Fragment sessionsFragment = new SessionsFragment();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.flSession, sessionsFragment)
//                        .commit();
            }
        });

        return view;
    }
}