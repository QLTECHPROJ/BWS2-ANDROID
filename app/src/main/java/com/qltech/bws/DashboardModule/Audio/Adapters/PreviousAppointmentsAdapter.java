/*
package com.qltech.bws.DashboardModule.Audio.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.DashboardModule.Appointment.SessionsFragment;
import com.qltech.bws.DashboardModule.Models.PreviousAppointmentsModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.PreviousAppointmentsLayoutBinding;

import java.util.List;

public class PreviousAppointmentsAdapter extends RecyclerView.Adapter<PreviousAppointmentsAdapter.MyViewHolder> {
    private List<PreviousAppointmentsModel> listModelList;
    Context ctx;
    public FragmentManager f_manager;

    public PreviousAppointmentsAdapter(List<PreviousAppointmentsModel> listModelList,Context ctx, FragmentManager f_manager) {
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
        holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SessionsFragment();
                f_manager = ctx.getSupportFragmentManager();
                f_manager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
                Bundle bundle = new Bundle();
                bundle.putString("key", "key");
                fragment.setArguments(bundle);
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
*/
