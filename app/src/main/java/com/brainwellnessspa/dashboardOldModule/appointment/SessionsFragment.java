package com.brainwellnessspa.dashboardOldModule.appointment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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

import com.brainwellnessspa.dashboardOldModule.transParentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.services.GlobalInitExoPlayer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.dashboardOldModule.models.SessionListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.utility.APIClient;
import com.brainwellnessspa.utility.CONSTANTS;
import com.brainwellnessspa.utility.MeasureRatio;
import com.brainwellnessspa.databinding.FragmentSessionsBinding;
import com.brainwellnessspa.databinding.SessionListLayoutBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.dashboardOldModule.appointment.AppointmentDetailsFragment.ComeFromAppointmentDetail;
import static com.brainwellnessspa.dashboardOldModule.appointment.AppointmentDetailsFragment.ComesessionScreen;

public class SessionsFragment extends Fragment {
    FragmentSessionsBinding binding;
    public FragmentManager f_manager;
    Activity activity;
    View view;
    Properties p;
    GsonBuilder gsonBuilder;
    Gson gson;
    ArrayList<String> section;
    String UserID, appointmentName, appointmentMainName, appointmentImage, appointmentTypeId, AudioFlag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sessions, container, false);
        view = binding.getRoot();
        activity = getActivity();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
        if (getArguments() != null) {
            appointmentName = getArguments().getString("appointmentName");
            appointmentImage = getArguments().getString("appointmentImage");
            appointmentMainName = getArguments().getString("appointmentMainName");
        }
        section = new ArrayList<>();
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        binding.llBack.setOnClickListener(view1 -> callBack());
        Glide.with(getActivity()).load(appointmentImage).thumbnail(0.05f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
        RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvSessionList.setLayoutManager(recentlyPlayed);
        binding.rvSessionList.setItemAnimator(new DefaultItemAnimator());
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                callBack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        return view;
    }

    @Override
    public void onResume() {
        prepareSessionList();
        if (view == null) {
            return;
        }

        if (ComeFromAppointmentDetail == 1) {
            prepareSessionList();
            ComeFromAppointmentDetail = 0;
        }
        super.onResume();
    }

    private void callBack() {
        if (ComesessionScreen == 1) {
            Bundle bundle = new Bundle();
            Fragment appointmentFragment = new AppointmentFragment();
            bundle.putString("appointmentMainName", appointmentMainName);
            bundle.putString("appointmentName", appointmentName);
            bundle.putString("appointmentImage", appointmentImage);
            appointmentFragment.setArguments(bundle);
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.flContainer, appointmentFragment)
                    .commit();
        }
        Bundle bundle = new Bundle();
        Fragment appointmentFragment = new AppointmentFragment();
        bundle.putString("appointmentMainName", appointmentMainName);
        bundle.putString("appointmentName", appointmentName);
        bundle.putString("appointmentImage", appointmentImage);
        appointmentFragment.setArguments(bundle);
        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .replace(R.id.flContainer, appointmentFragment)
                .commit();
    }

    private void prepareSessionList() {
        try {
            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
            globalInitExoPlayer.UpdateMiniPlayer(getActivity(),getActivity());
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {

                Fragment fragment = new MiniPlayerFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .commit();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 6, 0, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 6, 0, 50);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
  /*      try {
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


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 10, 0, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 10, 0, 50);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        try {
            if (BWSApplication.isNetworkConnected(getActivity())) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                Call<SessionListModel> listCall = APIClient.getClient().getAppointmentSession(UserID, appointmentName);
                listCall.enqueue(new Callback<SessionListModel>() {
                    @Override
                    public void onResponse(Call<SessionListModel> call, Response<SessionListModel> response) {
                            try {
                                SessionListModel listModel = response.body();
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                    binding.tvSessionTitle.setText(listModel.getResponseData().get(0).getCatName());
                                    MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                                            5, 3, 1f, 0);
                                    binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                                    binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                                    binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
                                    Glide.with(getActivity()).load(listModel.getResponseData().get(0).getImage()).thumbnail(0.05f)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                                    SessionListAdapter appointmentsAdapter = new SessionListAdapter(listModel.getResponseData(), getActivity(), f_manager);
                                    binding.rvSessionList.setAdapter(appointmentsAdapter);

                                    p = new Properties();
                                    p.putValue("userId", UserID);
                                    for (int i = 0; i < listModel.getResponseData().size(); i++) {
                                        section.add(listModel.getResponseData().get(i).getId());
                                        section.add(listModel.getResponseData().get(i).getCatName());
                                        section.add(listModel.getResponseData().get(i).getName());
                                        section.add(listModel.getResponseData().get(i).getDescInfusion());
                                        section.add(listModel.getResponseData().get(i).getDesc());
                                        section.add(listModel.getResponseData().get(i).getCatMenual());
                                        section.add(listModel.getResponseData().get(i).getDate());
                                        section.add(listModel.getResponseData().get(i).getDuration());
                                        section.add(listModel.getResponseData().get(i).getTime());
                                        section.add(listModel.getResponseData().get(i).getStatus());
                                    }
                                    p.putValue("appointmentSessions", gson.toJson(section));
                                    BWSApplication.addToSegment("Appointment Session Listing Viewed", p, CONSTANTS.screen);
                                } else {
                                    BWSApplication.showToast(listModel.getResponseMessage(), activity);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }

                    @Override
                    public void onFailure(Call<SessionListModel> call, Throwable t) {
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

            if (listModel.getDesc().equalsIgnoreCase("")) {
                holder.binding.tvSubTitle.setVisibility(View.GONE);
            } else {
                holder.binding.tvSubTitle.setVisibility(View.VISIBLE);
                holder.binding.tvSubTitle.setText(listModel.getDesc());
            }

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

            holder.binding.cvSetSession.setOnClickListener(view -> {
                Fragment appointmentDetailsFragment = new AppointmentDetailsFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("appointmentId", listModel.getId());
                bundle.putString("appointmentMainName", appointmentMainName);
                bundle.putString("appointmentName", appointmentName);
                bundle.putString("appointmentImage", appointmentImage);
                appointmentDetailsFragment.setArguments(bundle);
                fragmentManager1.beginTransaction()
                        .addToBackStack("AppointmentDetailsFragment")
                        .replace(R.id.flContainer, appointmentDetailsFragment).commit();
                p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("sessionId", listModel.getId());
                p.putValue("sessionName", listModel.getName());
                BWSApplication.addToSegment("Session List Item Clicked", p, CONSTANTS.track);
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