package com.brainwellnessspa.DashboardModule.Appointment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.brainwellnessspa.DashboardModule.Models.NextSessionViewModel;
import com.brainwellnessspa.DashboardModule.Models.PreviousAppointmentsModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.FragmentAppointmentBinding;
import com.brainwellnessspa.databinding.PreviousAppointmentsLayoutBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.releasePlayer;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;

public class AppointmentFragment extends Fragment {
    FragmentAppointmentBinding binding;
    String UserID, appointmentName, appointmentMainName, appointmentImage, AudioFlag;
    Activity activity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_appointment, container, false);
        View view = binding.getRoot();
        activity = getActivity();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            appointmentName = bundle.getString("appointmentName");
            appointmentImage = bundle.getString("appointmentImage");
            appointmentMainName = bundle.getString("appointmentMainName");
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
//        preparePreviousAppointmentsData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        preparePreviousAppointmentsData();
    }

    private void preparePreviousAppointmentsData() {
        try {
            if (!IsLock.equalsIgnoreCase("0") && (AudioFlag.equalsIgnoreCase("MainAudioList")
                    || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
                String audioFile = "";
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                Gson gson = new Gson();
                String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
                Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                }.getType();
                ArrayList<MainPlayModel> arrayList = gson.fromJson(json, type);

                if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                    arrayList.remove(0);
                }
                audioFile = arrayList.get(0).getName();

                if (audioFile.equalsIgnoreCase("Hope") || audioFile.equalsIgnoreCase("Mindfulness")) {

                } else {
                    SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorr = sharedm.edit();
                    editorr.remove(CONSTANTS.PREF_KEY_modelList);
                    editorr.remove(CONSTANTS.PREF_KEY_audioList);
                    editorr.remove(CONSTANTS.PREF_KEY_position);
                    editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                    editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                    editorr.remove(CONSTANTS.PREF_KEY_AudioFlag);
                    editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                    editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                    editorr.clear();
                    editorr.commit();
                    if(isMediaStart){
                        stopMedia();
                        releasePlayer();
                    }
                }

            } else if (!IsLock.equalsIgnoreCase("0") && !AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = sharedm.edit();
                editorr.remove(CONSTANTS.PREF_KEY_modelList);
                editorr.remove(CONSTANTS.PREF_KEY_audioList);
                editorr.remove(CONSTANTS.PREF_KEY_position);
                editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                editorr.remove(CONSTANTS.PREF_KEY_AudioFlag);
                editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                editorr.clear();
                editorr.commit();
                if(isMediaStart){
                    stopMedia();
                    releasePlayer();
                }
            }
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                Fragment fragment = new TransparentPlayerFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .commit();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 8, 10, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 8, 10, 50);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (BWSApplication.isNetworkConnected(getActivity())) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                Call<NextSessionViewModel> listCall = APIClient.getClient().getNextSessionVIew(UserID);
                listCall.enqueue(new Callback<NextSessionViewModel>() {
                    @Override
                    public void onResponse(Call<NextSessionViewModel> call, Response<NextSessionViewModel> response) {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            NextSessionViewModel listModel = response.body();
                            binding.tvNextSessionTitle.setText(R.string.Next_Session);
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

                                if (listModel.getResponseData().getTask().getSubtitle().equalsIgnoreCase("")) {
                                    binding.tvSubTitle.setVisibility(View.GONE);
                                } else {
                                    binding.tvSubTitle.setVisibility(View.VISIBLE);
                                    binding.tvSubTitle.setText(listModel.getResponseData().getTask().getSubtitle());
                                }
                                if (listModel.getResponseData().getTask().getTitle().equalsIgnoreCase("")) {
                                    binding.tvNextSession.setVisibility(View.GONE);
                                    binding.llCheckBox1.setVisibility(View.GONE);
                                    binding.llCheckBox2.setVisibility(View.GONE);
                                } else {
                                    binding.tvNextSession.setVisibility(View.VISIBLE);
                                    binding.llCheckBox1.setVisibility(View.VISIBLE);
                                    binding.llCheckBox2.setVisibility(View.VISIBLE);
                                }
                                binding.tvNextSession.setText(listModel.getResponseData().getTask().getTitle());
                                binding.cbTask1.setEnabled(false);
                                binding.cbTask1.setClickable(false);
                                binding.cbTask2.setEnabled(false);
                                binding.cbTask2.setClickable(false);
                                if (listModel.getResponseData().getTask().getAudioTask().equalsIgnoreCase("")) {
                                    binding.cbTask1.setVisibility(View.GONE);
                                    binding.tvTaskTitle1.setVisibility(View.GONE);
                                } else {
                                    binding.cbTask1.setVisibility(View.VISIBLE);
                                    binding.tvTaskTitle1.setVisibility(View.VISIBLE);
                                    binding.tvTaskTitle1.setText(listModel.getResponseData().getTask().getAudioTask());
                                }
                                if (listModel.getResponseData().getTask().getBookletTask().equalsIgnoreCase("")) {
                                    binding.cbTask2.setVisibility(View.GONE);
                                    binding.tvTaskTitle2.setVisibility(View.GONE);
                                } else {
                                    binding.cbTask2.setVisibility(View.VISIBLE);
                                    binding.tvTaskTitle2.setVisibility(View.VISIBLE);
                                    binding.tvTaskTitle2.setText(listModel.getResponseData().getTask().getBookletTask());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NextSessionViewModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    }
                });
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }

            if (BWSApplication.isNetworkConnected(getActivity())) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                Call<PreviousAppointmentsModel> listCall1 = APIClient.getClient().getAppointmentVIew(UserID);
                listCall1.enqueue(new Callback<PreviousAppointmentsModel>() {
                    @Override
                    public void onResponse(Call<PreviousAppointmentsModel> call, Response<PreviousAppointmentsModel> response) {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            PreviousAppointmentsModel listModel = response.body();
                            binding.tvPreviousAppointments.setText(R.string.Previous_Appointments);
                            PreviousAppointmentsAdapter appointmentsAdapter = new PreviousAppointmentsAdapter(listModel.getResponseData(), getActivity());
                            binding.rvPreviousData.setAdapter(appointmentsAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<PreviousAppointmentsModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    }
                });
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class PreviousAppointmentsAdapter extends RecyclerView.Adapter<PreviousAppointmentsAdapter.MyViewHolder> {
        private List<PreviousAppointmentsModel.ResponseData> listModel;
        Context ctx;

        public PreviousAppointmentsAdapter(List<PreviousAppointmentsModel.ResponseData> listModel, Context ctx) {
            this.listModel = listModel;
            this.ctx = ctx;
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
                    Bundle bundle = new Bundle();
                    Fragment sessionsFragment = new SessionsFragment();
                    bundle.putString("appointmentMainName", listModel.get(position).getCategory());
                    bundle.putString("appointmentName", listModel.get(position).getCatMenual());
                    bundle.putString("appointmentImage", listModel.get(position).getImage());
                    sessionsFragment.setArguments(bundle);
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .replace(R.id.flContainer, sessionsFragment).commit();
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