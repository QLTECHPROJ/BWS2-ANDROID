package com.qltech.bws.DashboardModule.Appointment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qltech.bws.DashboardModule.Models.PreviousAppointmentsModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentAppointmentBinding;
import com.qltech.bws.databinding.PreviousAppointmentsLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class AppointmentFragment extends Fragment {
    List<PreviousAppointmentsModel> previousAppointmentsList = new ArrayList<>();
    FragmentAppointmentBinding binding;
    private AppointmentViewModel appointmentViewModel;
    public FragmentManager f_manager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        appointmentViewModel =
                ViewModelProviders.of(this).get(AppointmentViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_appointment, container, false);
        View view = binding.getRoot();

        binding.ivStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.ivStatus.setImageResource(R.drawable.ic_play_icon);
            }
        });

        PreviousAppointmentsAdapter appointmentsAdapter = new PreviousAppointmentsAdapter(previousAppointmentsList, getActivity(), f_manager);
        RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvPreviousData.setLayoutManager(recentlyPlayed);
        binding.rvPreviousData.setItemAnimator(new DefaultItemAnimator());
        binding.rvPreviousData.setAdapter(appointmentsAdapter);

        binding.cvSetSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.cvShowSession.setVisibility(View.VISIBLE);
            }
        });
        appointmentViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                binding.textAppointment.setText(s);
            }
        });
        preparePreviousAppointmentsData();
        return view;
    }

    private void preparePreviousAppointmentsData() {
        PreviousAppointmentsModel list = new PreviousAppointmentsModel("Emotional empowerment program");
        previousAppointmentsList.add(list);
        list = new PreviousAppointmentsModel("Conditioned series");
        previousAppointmentsList.add(list);
        list = new PreviousAppointmentsModel("Non-spa menu");
        previousAppointmentsList.add(list);
        list = new PreviousAppointmentsModel("Phone emotional empowerment program");
        previousAppointmentsList.add(list);
        list = new PreviousAppointmentsModel("Emotional empowerment program");
        previousAppointmentsList.add(list);
        list = new PreviousAppointmentsModel("Conditioned series");
        previousAppointmentsList.add(list);
        list = new PreviousAppointmentsModel("Non-spa menu");
        previousAppointmentsList.add(list);
        list = new PreviousAppointmentsModel("Phone emotional empowerment program");
        previousAppointmentsList.add(list);

    }

    public class PreviousAppointmentsAdapter extends RecyclerView.Adapter<PreviousAppointmentsAdapter.MyViewHolder> {
        private List<PreviousAppointmentsModel> listModelList;
        Context ctx;
        public FragmentManager f_manager;

        public PreviousAppointmentsAdapter(List<PreviousAppointmentsModel> listModelList, Context ctx, FragmentManager f_manager) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.f_manager = f_manager;
        }

        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            PreviousAppointmentsLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.previous_appointments_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            PreviousAppointmentsModel listModel = listModelList.get(position);
            holder.binding.tvTitle.setText(listModel.getTitle());

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.11f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivRestaurantImage.setImageResource(R.drawable.square_logo);

            holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment sessionsFragment = new SessionsFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .replace(R.id.flMainLayout, sessionsFragment).
                            addToBackStack("SessionsFragment")
                            .commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            PreviousAppointmentsLayoutBinding binding;

            public MyViewHolder(PreviousAppointmentsLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

}