package com.qltech.bws.DashboardModule.Appointment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.PreviousAppointmentsModel;
import com.qltech.bws.DashboardModule.Models.SessionListModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.FragmentSessionsBinding;
import com.qltech.bws.databinding.PreviousAppointmentsLayoutBinding;
import com.qltech.bws.databinding.SessionListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionsFragment extends Fragment {
    List<SessionListModel> sessionList = new ArrayList<>();
    FragmentSessionsBinding binding;
    public FragmentManager f_manager;
    Activity activity;
    String UserId,appointmentName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sessions, container, false);
        View view = binding.getRoot();
        activity = getActivity();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            appointmentName = bundle.getString("appointmentName");
        }
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                fm.popBackStack("SessionsFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvSessionList.setLayoutManager(recentlyPlayed);
        binding.rvSessionList.setItemAnimator(new DefaultItemAnimator());

        prepareSessionList();
        return view;
    }

    private void prepareSessionList() {
        BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
        if (BWSApplication.isNetworkConnected(getActivity())) {
            Call<SessionListModel> listCall = APIClient.getClient().getAppointmentSession(UserId,appointmentName);
            listCall.enqueue(new Callback<SessionListModel>() {
                @Override
                public void onResponse(Call<SessionListModel> call, Response<SessionListModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        SessionListModel listModel = response.body();
                        if(listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))){
                        SessionListAdapter appointmentsAdapter = new SessionListAdapter(listModel.getResponseData(), getActivity(), f_manager);
                        binding.rvSessionList.setAdapter(appointmentsAdapter);}
                        else {
                            Toast.makeText(activity, listModel.getResponseMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SessionListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                }
            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }
    }

    public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.MyViewHolder> {
        private List<SessionListModel.ResponseData> listModelList;
        Context ctx;
        public FragmentManager f_manager;

        public SessionListAdapter(List<SessionListModel.ResponseData> listModelList, Context ctx, FragmentManager f_manager) {
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
            SessionListModel.ResponseData listModel = listModelList.get(position);
            holder.binding.tvTitle.setText(listModel.getName());
            if (listModel.getDate().equalsIgnoreCase("") &&
                    listModel.getStatus().equalsIgnoreCase("") &&
                    listModel.getDuration().equalsIgnoreCase("") &&
                    listModel.getTime().equalsIgnoreCase("")) {
                holder.binding.llDateTime.setVisibility(View.GONE);
              /*  LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                buttonLayoutParams.setMargins(0, 16, 0, 16);

                holder.binding.llMainLayout.setLayoutParams(buttonLayoutParams);*/
            } else {
    /*            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                buttonLayoutParams.setMargins(0, 16, 0, 0);

                holder.binding.llMainLayout.setLayoutParams(buttonLayoutParams);*/
                holder.binding.llDateTime.setVisibility(View.VISIBLE);
            }
            holder.binding.tvDate.setText(listModel.getDate());
            holder.binding.tvStatus.setText(listModel.getStatus());
            holder.binding.tvTime.setText(listModel.getTime());
            holder.binding.tvHourGlass.setText(listModel.getDuration());

            if (listModel.getStatus().equalsIgnoreCase("Booked")) {
                holder.binding.tvStatus.setBackgroundResource(R.drawable.text_background);
            } else if (listModel.getStatus().equalsIgnoreCase("Arrived")) {
                holder.binding.tvStatus.setBackgroundResource(R.drawable.green_text_background);
            }

            holder.binding.cvSetSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment appointmentDetailsFragment = new AppointmentDetailsFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putString("appointmentId",listModel.getId());
                    appointmentDetailsFragment.setArguments(bundle);
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