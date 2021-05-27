package com.brainwellnessspa.DashboardOldModule.Appointment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardOldModule.Models.NextSessionViewModel;
import com.brainwellnessspa.DashboardOldModule.Models.PreviousAppointmentsModel;
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.FragmentAppointmentBinding;
import com.brainwellnessspa.databinding.PreviousAppointmentsLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;

public class AppointmentFragment extends Fragment {
    FragmentAppointmentBinding binding;
    String UserID, appointmentName, appointmentMainName, appointmentImage, AudioFlag;
    Activity activity;
    Properties p;
    ArrayList<String> section, previoussection;
    GsonBuilder gsonBuilder;
    Gson gson;
    NextSessionViewModel nextSessionViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_appointment, container, false);
        View view = binding.getRoot();
        activity = getActivity();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            appointmentName = bundle.getString("appointmentName");
            appointmentImage = bundle.getString("appointmentImage");
            appointmentMainName = bundle.getString("appointmentMainName");
        }
        section = new ArrayList<>();
        previoussection = new ArrayList<>();
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvPreviousData.setLayoutManager(recentlyPlayed);
        binding.rvPreviousData.setItemAnimator(new DefaultItemAnimator());
        comefromDownload = "0";
        binding.cvSetSession.setOnClickListener(view1 -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://brainwellnessspa.com.au/bookings/services.php"));
            startActivity(i);
            p = new Properties();
            p.putValue("userId", UserID);
            p.putValue("bookingLink", "https://brainwellnessspa.com.au/bookings/services.php");
            BWSApplication.addToSegment("Book a New Appointment Clicked", p, CONSTANTS.track);
        });
        return view;
    }

    @Override
    public void onResume() {
        comefromDownload = "0";
        preparePreviousAppointmentsData();
        super.onResume();
    }

    private void preparePreviousAppointmentsData() {
        try {
            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
            globalInitExoPlayer.UpdateMiniPlayer(getActivity(),getActivity());
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                callAddTransFrag();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 6, 0, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 6, 0, 130);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        try {
            SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            SharedPreferences shared2 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
            Gson gson1 = new Gson();
            Type type1 = new TypeToken<List<String>>() {
            }.getType();
            List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
            if (!IsLock.equalsIgnoreCase("0") && (AudioFlag.equalsIgnoreCase("MainAudioList")
                    || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
                String audioID = "";
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
                Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                }.getType();
                ArrayList<MainPlayModel> arrayList = gson.fromJson(json, type);

                if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                    arrayList.remove(0);
                }
                audioID = arrayList.get(0).getID();
                if (UnlockAudioList.contains(audioID)) {
                } else {
                    SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorr = sharedm.edit();
                    editorr.remove(CONSTANTS.PREF_KEY_MainAudioList);
                    editorr.remove(CONSTANTS.PREF_KEY_PlayerAudioList);
                    editorr.remove(CONSTANTS.PREF_KEY_PlayerPosition);
                    editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                    editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                    editorr.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag);
                    editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                    editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                    editorr.clear();
                    editorr.commit();
                    callNewPlayerRelease();
                }
            } else if (!IsLock.equalsIgnoreCase("0") && !AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = sharedm.edit();
                editorr.remove(CONSTANTS.PREF_KEY_MainAudioList);
                editorr.remove(CONSTANTS.PREF_KEY_PlayerAudioList);
                editorr.remove(CONSTANTS.PREF_KEY_PlayerPosition);
                editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                editorr.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag);
                editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                editorr.clear();
                editorr.commit();
                callNewPlayerRelease();
            }
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
               callAddTransFrag();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 8, 10, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 8, 10, 130);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
*/

        try {
            if (BWSApplication.isNetworkConnected(getActivity())) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                Call<NextSessionViewModel> listCall = APIClient.getClient().getNextSessionVIew(UserID);
                listCall.enqueue(new Callback<NextSessionViewModel>() {
                    @Override
                    public void onResponse(Call<NextSessionViewModel> call, Response<NextSessionViewModel> response) {
                        try {
                            NextSessionViewModel listModel = response.body();
                            if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                nextSessionViewModel = listModel;
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
                        } catch (Exception e) {
                            e.printStackTrace();
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
                        try {
                            PreviousAppointmentsModel listModel = response.body();
                            if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                binding.tvPreviousAppointments.setText(R.string.Previous_Appointments);
                                PreviousAppointmentsAdapter appointmentsAdapter = new PreviousAppointmentsAdapter(listModel.getResponseData(), getActivity());
                                binding.rvPreviousData.setAdapter(appointmentsAdapter);
                                p = new Properties();
                                p.putValue("userId", UserID);
                                if (nextSessionViewModel.getResponseData().getResponse().equalsIgnoreCase("")) {
                                    p.putValue("nextSession", "");
                                }else {
                                    section.add(nextSessionViewModel.getResponseData().getId());
                                    section.add(nextSessionViewModel.getResponseData().getName());
                                    section.add(nextSessionViewModel.getResponseData().getDate());
                                    section.add(nextSessionViewModel.getResponseData().getDuration());
                                    section.add(nextSessionViewModel.getResponseData().getTime());
                                    section.add(nextSessionViewModel.getResponseData().getTask().getTitle());
                                    section.add(nextSessionViewModel.getResponseData().getTask().getSubtitle());
                                    section.add(nextSessionViewModel.getResponseData().getTask().getAudioTask());
                                    section.add(nextSessionViewModel.getResponseData().getTask().getBookletTask());
                                    p.putValue("nextSession", gson.toJson(section));
                                }

                                for (int i = 0; i < listModel.getResponseData().size(); i++) {
                                    previoussection.add(listModel.getResponseData().get(i).getCategory());
                                    previoussection.add(listModel.getResponseData().get(i).getCatMenual());
                                }
                                p.putValue("previousAppointments", gson.toJson(previoussection));
                                BWSApplication.addToSegment("Appointment Screen Viewed", p, CONSTANTS.screen);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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

    private void callAddTransFrag() {
        Fragment fragment = new MiniPlayerFragment();
        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .add(R.id.flContainer, fragment)
                .commit();
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
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(12))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.llMainLayout.setOnClickListener(view -> {
                p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("appointmentName", listModel.get(position).getCategory());
                p.putValue("appointmentCategory", listModel.get(position).getCatMenual());
                BWSApplication.addToSegment("Appointment Item Clicked", p, CONSTANTS.track);
                Bundle bundle = new Bundle();
                Fragment sessionsFragment = new SessionsFragment();
                bundle.putString("appointmentMainName", listModel.get(position).getCategory());
                bundle.putString("appointmentName", listModel.get(position).getCatMenual());
                bundle.putString("appointmentImage", listModel.get(position).getImage());
                sessionsFragment.setArguments(bundle);
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.flContainer, sessionsFragment).commit();
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