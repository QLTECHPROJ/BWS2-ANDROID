package com.qltech.bws.DashboardModule.Appointment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.DashboardModule.Models.NextSessionViewModel;
import com.qltech.bws.DashboardModule.Models.PreviousAppointmentsModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentAppointmentBinding;
import com.qltech.bws.databinding.PreviousAppointmentsLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentFragment extends Fragment {
    FragmentAppointmentBinding binding;
    private AppointmentViewModel appointmentViewModel;
    public FragmentManager f_manager;
    String UserID;
    Activity activity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        appointmentViewModel =
                ViewModelProviders.of(this).get(AppointmentViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_appointment, container, false);
        View view = binding.getRoot();

        activity = getActivity();
        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

        if (!AudioFlag.equalsIgnoreCase("0")) {
            Fragment fragment = new TransparentPlayerFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flMainLayout, fragment)
                    .addToBackStack("TransparentPlayerFragment")
                    .commit();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 8, 10, 121);
            binding.llSpace.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 8, 10, 84);
            binding.llSpace.setLayoutParams(params);
        }

        RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvPreviousData.setLayoutManager(recentlyPlayed);
        binding.rvPreviousData.setItemAnimator(new DefaultItemAnimator());

        binding.cvSetSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://brainwellnessspa.com.au/bookings/services.php"));
                startActivity(i);
            }
        });
        appointmentViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        preparePreviousAppointmentsData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        preparePreviousAppointmentsData();
    }

    private void preparePreviousAppointmentsData() {
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
            Call<PreviousAppointmentsModel> listCall1 = APIClient.getClient().getAppointmentVIew(UserID);
            listCall1.enqueue(new Callback<PreviousAppointmentsModel>() {
                @Override
                public void onResponse(Call<PreviousAppointmentsModel> call, Response<PreviousAppointmentsModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        PreviousAppointmentsModel listModel = response.body();
                        PreviousAppointmentsAdapter appointmentsAdapter = new PreviousAppointmentsAdapter(listModel.getResponseData(), getActivity(), f_manager);
                        binding.rvPreviousData.setAdapter(appointmentsAdapter);
                    }
                }

                @Override
                public void onFailure(Call<PreviousAppointmentsModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }

        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
            Call<NextSessionViewModel> listCall = APIClient.getClient().getNextSessionVIew(UserID);
            listCall.enqueue(new Callback<NextSessionViewModel>() {
                @Override
                public void onResponse(Call<NextSessionViewModel> call, Response<NextSessionViewModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        NextSessionViewModel listModel = response.body();
                        if (listModel.getResponseData().getResponse().equalsIgnoreCase("")) {
                            binding.cvShowSession.setVisibility(View.GONE);
                            binding.cvSetSession.setVisibility(View.VISIBLE);
                        } else if (listModel.getResponseData().getResponse().equalsIgnoreCase("1")) {
                            binding.cvShowSession.setVisibility(View.VISIBLE);
                            binding.cvSetSession.setVisibility(View.GONE);
                            binding.tvTitle.setText(listModel.getResponseData().getName());
                            binding.tvDate.setText(listModel.getResponseData().getDate());
                            binding.tvTime.setText(listModel.getResponseData().getTime());
                            binding.tvHourGlass.setText(listModel.getResponseData().getDuration());

                            if (listModel.getResponseData().getTask().getAudioTask().equalsIgnoreCase("")) {
                                binding.cbTask1.setVisibility(View.GONE);
                                binding.tvTaskTitle1.setVisibility(View.GONE);
                            } else {
                                binding.cbTask1.setVisibility(View.VISIBLE);
                                binding.tvTaskTitle1.setVisibility(View.VISIBLE);
                                binding.tvTitle.setText(listModel.getResponseData().getTask().getAudioTask());
                            }
                            if (listModel.getResponseData().getTask().getBookletTask().equalsIgnoreCase("")) {
                                binding.cbTask2.setVisibility(View.GONE);
                                binding.tvTaskTitle2.setVisibility(View.GONE);
                            } else {
                                binding.cbTask2.setVisibility(View.VISIBLE);
                                binding.tvTaskTitle2.setVisibility(View.VISIBLE);
                                binding.tvTitle.setText(listModel.getResponseData().getTask().getBookletTask());
                            }
                        }

                    }
                }

                @Override
                public void onFailure(Call<NextSessionViewModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    public class PreviousAppointmentsAdapter extends RecyclerView.Adapter<PreviousAppointmentsAdapter.MyViewHolder> {
        private List<PreviousAppointmentsModel.ResponseData> listModel;
        Context ctx;
        public FragmentManager f_manager;

        public PreviousAppointmentsAdapter(List<PreviousAppointmentsModel.ResponseData> listModel, Context ctx, FragmentManager f_manager) {
            this.listModel = listModel;
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
            holder.binding.tvTitle.setText(listModel.get(position).getCategory());
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.11f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(listModel.get(position).getImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment sessionsFragment = new SessionsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("appointmentMainName", listModel.get(position).getCategory());
                    bundle.putString("appointmentName", listModel.get(position).getCatMenual());
                    bundle.putString("appointmentImage", listModel.get(position).getImage());
                    sessionsFragment.setArguments(bundle);
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .replace(R.id.flMainLayout, sessionsFragment).commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listModel.size();
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