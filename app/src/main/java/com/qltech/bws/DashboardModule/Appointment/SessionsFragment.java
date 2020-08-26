package com.qltech.bws.DashboardModule.Appointment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qltech.bws.DashboardModule.Models.PreviousAppointmentsModel;
import com.qltech.bws.DashboardModule.Models.SessionListModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentSessionsBinding;
import com.qltech.bws.databinding.PreviousAppointmentsLayoutBinding;
import com.qltech.bws.databinding.SessionListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class SessionsFragment extends Fragment {
    List<SessionListModel> sessionList = new ArrayList<>();
    FragmentSessionsBinding binding;
    public FragmentManager f_manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sessions, container, false);
        View view = binding.getRoot();

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Fragment appointmentFragment = new AppointmentFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.flMainLayout, appointmentFragment)
                        .commit();*/
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                fm.popBackStack ("SessionsFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        SessionListAdapter appointmentsAdapter = new SessionListAdapter(sessionList, getActivity(), f_manager);
        RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvSessionList.setLayoutManager(recentlyPlayed);
        binding.rvSessionList.setItemAnimator(new DefaultItemAnimator());
        binding.rvSessionList.setAdapter(appointmentsAdapter);

        prepareSessionList();
        return view;
    }

    private void prepareSessionList() {
        SessionListModel list = new SessionListModel("Emotional empowerment program session 1","Booked");
        sessionList.add(list);
        list = new SessionListModel("Emotional empowerment program session 2", "Arrived");
        sessionList.add(list);
        list = new SessionListModel("Emotional empowerment program session 3","Booked");
        sessionList.add(list);
        list = new SessionListModel("Emotional empowerment program session 4", "Arrived");
        sessionList.add(list);
    }
    public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.MyViewHolder> {
        private List<SessionListModel> listModelList;
        Context ctx;
        public FragmentManager f_manager;

        public SessionListAdapter(List<SessionListModel> listModelList, Context ctx, FragmentManager f_manager) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.f_manager = f_manager;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SessionListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.session_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            SessionListModel listModel = listModelList.get(position);
            holder.binding.tvTitle.setText(listModel.getTitle());
            holder.binding.tvStatus.setText(listModel.getSuTitle());

            if (listModel.getSuTitle().equalsIgnoreCase("Booked")){
                holder.binding.tvStatus.setBackgroundResource(R.drawable.text_background);
            }else if (listModel.getSuTitle().equalsIgnoreCase("Arrived")){
                holder.binding.tvStatus.setBackgroundResource(R.drawable.green_text_background);
            }

            holder.binding.cvSetSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment appointmentDetailsFragment = new AppointmentDetailsFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .replace(R.id.flSession, appointmentDetailsFragment).
                            addToBackStack("AppointmentDetailsFragment")
                            .commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            SessionListLayoutBinding binding;

            public MyViewHolder(SessionListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}