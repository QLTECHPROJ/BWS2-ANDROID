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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Audio.AudioFragment;
import com.qltech.bws.DashboardModule.Models.SessionListModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.OnBackPressed;
import com.qltech.bws.databinding.FragmentSessionsBinding;
import com.qltech.bws.databinding.SessionListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionsFragment extends Fragment {
    FragmentSessionsBinding binding;
    public FragmentManager f_manager;
    Activity activity;
    View view;
    String UserId, appointmentName, appointmentMainName, appointmentImage, appointmentTypeId, AudioFlag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sessions, container, false);
        view = binding.getRoot();
        activity = getActivity();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (getArguments() != null) {
            appointmentTypeId = getArguments().getString("appointmentId");
            appointmentName = getArguments().getString("appointmentName");
            appointmentImage = getArguments().getString("appointmentImage");
            appointmentMainName = getArguments().getString("appointmentMainName");
        }
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                callBack();
                return true;
            }
            return false;
        });
        binding.llBack.setOnClickListener(view1 -> callBack());
        Glide.with(getActivity()).load(appointmentImage).thumbnail(0.05f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);

        RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvSessionList.setLayoutManager(recentlyPlayed);
        binding.rvSessionList.setItemAnimator(new DefaultItemAnimator());
        RefreshData();
        prepareSessionList();
        return view;
    }

    private void callBack() {
        Bundle bundle = new Bundle();
        Fragment appointmentFragment = new AppointmentFragment();
        bundle.putString("appointmentMainName", appointmentMainName);
        bundle.putString("appointmentName", appointmentName);
        bundle.putString("appointmentImage", appointmentImage);
        appointmentFragment.setArguments(bundle);
        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .replace(R.id.flMainLayout, appointmentFragment)
                .commit();
    }

    private void RefreshData() {
        if (!AudioFlag.equalsIgnoreCase("0")) {
            Fragment fragment = new TransparentPlayerFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flMainLayout, fragment)
                    .commit();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 10, 0, 260);
            binding.llSpace.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 10, 0, 50);
            binding.llSpace.setLayoutParams(params);
        }
    }

    private void prepareSessionList() {
        try {
            if (BWSApplication.isNetworkConnected(getActivity())) {
                BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                Call<SessionListModel> listCall = APIClient.getClient().getAppointmentSession(UserId, appointmentName);
                listCall.enqueue(new Callback<SessionListModel>() {
                    @Override
                    public void onResponse(Call<SessionListModel> call, Response<SessionListModel> response) {
                        if (response.isSuccessful()) {
                            try {
                                BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                                SessionListModel listModel = response.body();
                                if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                    binding.tvSessionTitle.setText(listModel.getResponseData().get(0).getCatName());
                                    Glide.with(getActivity()).load(listModel.getResponseData().get(0).getImage()).thumbnail(0.05f)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                                    SessionListAdapter appointmentsAdapter = new SessionListAdapter(listModel.getResponseData(), getActivity(), f_manager);
                                    binding.rvSessionList.setAdapter(appointmentsAdapter);
                                } else {
                                    BWSApplication.showToast(listModel.getResponseMessage(), activity);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SessionListModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                    }
                });
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshData();
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
            holder.binding.tvSubTitle.setText(listModel.getDesc());
            if (listModel.getDate().equalsIgnoreCase("") &&
                    listModel.getStatus().equalsIgnoreCase("") &&
                    listModel.getDuration().equalsIgnoreCase("") &&
                    listModel.getTime().equalsIgnoreCase("")) {
                holder.binding.llDateTime.setVisibility(View.GONE);
            } else {
                holder.binding.llDateTime.setVisibility(View.VISIBLE);
            }
            holder.binding.tvDate.setText(listModel.getDate());

            holder.binding.tvTime.setText(listModel.getTime());
            holder.binding.tvHourGlass.setText(listModel.getDuration());

            if (listModel.getStatus().equalsIgnoreCase("Booked")) {
                holder.binding.tvStatus.setText(listModel.getStatus());
                holder.binding.tvStatus.setBackgroundResource(R.drawable.text_background);
            } else if (listModel.getStatus().equalsIgnoreCase("Arrive")) {
                holder.binding.tvStatus.setText(listModel.getStatus());
                holder.binding.tvStatus.setBackgroundResource(R.drawable.green_text_background);
            } else if (listModel.getStatus().equalsIgnoreCase("Did_Not_Arrive")) {
                holder.binding.tvStatus.setText("Did Not Arrive");
                holder.binding.tvStatus.setBackgroundResource(R.drawable.green_text_background);
            }

            holder.binding.cvSetSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment appointmentDetailsFragment = new AppointmentDetailsFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putString("appointmentId", listModel.getId());
                    bundle.putString("appointmentMainName", appointmentMainName);
                    bundle.putString("appointmentName", appointmentName);
                    bundle.putString("appointmentImage", appointmentImage);
                    appointmentDetailsFragment.setArguments(bundle);
                    fragmentManager1.beginTransaction()
                            .add(R.id.flMainLayout, appointmentDetailsFragment).commit();
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