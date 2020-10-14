package com.brainwellnessspa.DashboardModule.Appointment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.tabs.TabLayout;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Appointment.AppointmentDetails.AptAnswersFragment;
import com.brainwellnessspa.DashboardModule.Appointment.AppointmentDetails.AptAudioFragment;
import com.brainwellnessspa.DashboardModule.Appointment.AppointmentDetails.AptBookletFragment;
import com.brainwellnessspa.DashboardModule.Appointment.AppointmentDetails.AptDetailsFragment;
import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.FragmentAppointmentDetailsBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Appointment.AppointmentDetails.AptAudioFragment.comeRefreshData;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;

public class AppointmentDetailsFragment extends Fragment {
    FragmentAppointmentDetailsBinding binding;
    Activity activity;
    View view;
    public static int ComeFromAppointmentDetail = 0;
    public static int ComesessionScreen = 0;
    String UserId, appointmentTypeId, appointmentName, appointmentMainName, appointmentImage, AudioFlag;
    AppointmentDetailModel globalAppointmentDetailModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_appointment_details, container, false);
        view = binding.getRoot();
        activity = getActivity();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (getArguments() != null) {
            appointmentTypeId = getArguments().getString("appointmentId");
            appointmentMainName = getArguments().getString("appointmentMainName");
            appointmentName = getArguments().getString("appointmentName");
            appointmentImage = getArguments().getString("appointmentImage");
        }

        binding.llBack.setOnClickListener(view1 -> callBack());
        getAppointmentData();
        RefreshData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshData();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                callBack();
                return true;
            }
            return false;
        });
    }

    private void callBack() {
        ComeFromAppointmentDetail = 1;
        ComesessionScreen = 1;
        /*Bundle bundle = new Bundle();
        Fragment appointmentFragment = new SessionsFragment();
        bundle.putString("appointmentMainName", appointmentMainName);
        bundle.putString("appointmentName", appointmentName);
        bundle.putString("appointmentImage", appointmentImage);
        appointmentFragment.setArguments(bundle);
        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .replace(R.id.flContainer, appointmentFragment)
                .commit();*/
        FragmentManager fm = getActivity()
                .getSupportFragmentManager();
        fm.popBackStack("AppointmentDetailsFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void RefreshData() {
        try {
            if (IsLock.equalsIgnoreCase("1") && !AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = sharedm.edit();
                editorr.remove(CONSTANTS.PREF_KEY_modelList);
                editorr.remove(CONSTANTS.PREF_KEY_position);
                editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                editorr.remove(CONSTANTS.PREF_KEY_AudioFlag);
                editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                editorr.clear();
                editorr.commit();
            }
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                Fragment fragment = new TransparentPlayerFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .commit();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 50);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAppointmentData() {
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<AppointmentDetailModel> listCall = APIClient.getClient().getAppointmentDetails(UserId, appointmentTypeId);
            listCall.enqueue(new Callback<AppointmentDetailModel>() {
                @Override
                public void onResponse(Call<AppointmentDetailModel> call, Response<AppointmentDetailModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            AppointmentDetailModel appointmentDetailModel = response.body();
                            globalAppointmentDetailModel = appointmentDetailModel;

                            if (appointmentDetailModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                if (appointmentDetailModel.getResponseData().getAudio().size() == 0
                                        && appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                                        && appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                                    binding.llViewOne.setVisibility(View.GONE);
                                    binding.llViewTwo.setVisibility(View.VISIBLE);
                                    binding.viewPager.setOffscreenPageLimit(1);
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
                                } else if (appointmentDetailModel.getResponseData().getAudio().size() != 0
                                        && appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                                        && appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                                    binding.llViewOne.setVisibility(View.VISIBLE);
                                    binding.llViewTwo.setVisibility(View.GONE);
                                    binding.viewPager.setOffscreenPageLimit(2);
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"));
                                } else if (appointmentDetailModel.getResponseData().getAudio().size() == 0
                                        && !appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                                        && appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                                    binding.llViewOne.setVisibility(View.VISIBLE);
                                    binding.llViewTwo.setVisibility(View.GONE);
                                    binding.viewPager.setOffscreenPageLimit(2);
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Booklet"));
                                } else if (appointmentDetailModel.getResponseData().getAudio().size() == 0
                                        && appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                                        && !appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                                    binding.llViewOne.setVisibility(View.VISIBLE);
                                    binding.llViewTwo.setVisibility(View.GONE);
                                    binding.viewPager.setOffscreenPageLimit(2);
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My answers"));
                                } else if (appointmentDetailModel.getResponseData().getAudio().size() != 0
                                        && !appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                                        && appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                                    binding.llViewOne.setVisibility(View.VISIBLE);
                                    binding.llViewTwo.setVisibility(View.GONE);
                                    binding.viewPager.setOffscreenPageLimit(3);
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"));
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Booklet"));
                                } else if (appointmentDetailModel.getResponseData().getAudio().size() != 0
                                        && appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                                        && !appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                                    binding.llViewOne.setVisibility(View.VISIBLE);
                                    binding.llViewTwo.setVisibility(View.GONE);
                                    binding.viewPager.setOffscreenPageLimit(3);
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"));
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My answers"));
                                } else if (appointmentDetailModel.getResponseData().getAudio().size() == 0
                                        && !appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                                        && !appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                                    binding.llViewOne.setVisibility(View.VISIBLE);
                                    binding.llViewTwo.setVisibility(View.GONE);
                                    binding.viewPager.setOffscreenPageLimit(3);
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Booklet"));
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My answers"));
                                } else if (appointmentDetailModel.getResponseData().getAudio().size() != 0
                                        && !appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                                        && !appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                                    binding.llViewOne.setVisibility(View.VISIBLE);
                                    binding.llViewTwo.setVisibility(View.GONE);
                                    binding.viewPager.setOffscreenPageLimit(4);
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"));
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Booklet"));
                                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My answers"));
                                }

                                MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 10,
                                        1, 1, 0.24f, 10);
                                binding.civProfile.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                                binding.civProfile.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                                binding.tvTilte.setText(globalAppointmentDetailModel.getResponseData().getName());

                                binding.tvFacilitator.setText(globalAppointmentDetailModel.getResponseData().getFacilitator());
                                binding.tvUserName.setText(globalAppointmentDetailModel.getResponseData().getUserName());
                                binding.tvSubTitle.setText(globalAppointmentDetailModel.getResponseData().getDesc());
                                binding.tvDate.setText(globalAppointmentDetailModel.getResponseData().getDate());
                                binding.tvTime.setText(globalAppointmentDetailModel.getResponseData().getTime());
                                Glide.with(getActivity()).load(globalAppointmentDetailModel.getResponseData().getImage()).thumbnail(0.05f)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.civProfile);

                                if (globalAppointmentDetailModel.getResponseData().getDate().equalsIgnoreCase("")
                                        && globalAppointmentDetailModel.getResponseData().getUserName().equalsIgnoreCase("")
                                        && globalAppointmentDetailModel.getResponseData().getTime().equalsIgnoreCase("")) {
                                    binding.llDetails.setVisibility(View.GONE);
                                } else {
                                    binding.llDetails.setVisibility(View.VISIBLE);
                                }

                                if (globalAppointmentDetailModel.getResponseData().getBookUrl().equalsIgnoreCase("")) {
                                    binding.btnCompletes.setVisibility(View.GONE);
                                } else {
                                    binding.btnCompletes.setVisibility(View.VISIBLE);
                                }

                                binding.btnCompletes.setOnClickListener(view1 -> {
                                    BWSApplication.showToast("Book Now", getActivity());
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(globalAppointmentDetailModel.getResponseData().getBookUrl()));
                                    startActivity(i);
                                });

                                binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                                TabAdapter adapter = new TabAdapter(getActivity().getSupportFragmentManager(), getActivity(), binding.tabLayout.getTabCount());
                                binding.viewPager.setAdapter(adapter);

                                binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
                                binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                    @Override
                                    public void onTabSelected(TabLayout.Tab tab) {
                                        if (comeRefreshData == 1) {
                                            RefreshData();
                                        }
                                        binding.viewPager.setCurrentItem(tab.getPosition());
                                    }

                                    @Override
                                    public void onTabUnselected(TabLayout.Tab tab) {
                                    }

                                    @Override
                                    public void onTabReselected(TabLayout.Tab tab) {
                                    }
                                });
                            } else {
                                BWSApplication.showToast(appointmentDetailModel.getResponseMessage(), getActivity());
                            }
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AppointmentDetailModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    public class TabAdapter extends FragmentStatePagerAdapter {
        int totalTabs;
        Context myContext;

        public TabAdapter(FragmentManager fm, Context myContext, int totalTabs) {
            super(fm);
            this.myContext = myContext;
            this.totalTabs = totalTabs;
        }

        @Override
        public Fragment getItem(int position) {
            AptDetailsFragment aptDetailsFragment = new AptDetailsFragment();
            AptAudioFragment aptAudioFragment = new AptAudioFragment();
            AptBookletFragment aptBookletFragment = new AptBookletFragment();
            AptAnswersFragment aptAnswersFragment = new AptAnswersFragment();
            Bundle bundle = new Bundle();
            Bundle bundle2 = new Bundle();
            bundle.putParcelable("AppointmentDetail", globalAppointmentDetailModel.getResponseData());
            bundle2.putParcelable("AppointmentDetail", globalAppointmentDetailModel.getResponseData());
            bundle2.putParcelableArrayList("AppointmentDetailList", globalAppointmentDetailModel.getResponseData().getAudio());
            if (globalAppointmentDetailModel.getResponseData().getAudio().size() == 0
                    && globalAppointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                    && globalAppointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                switch (position) {
                    case 0:
                        aptDetailsFragment.setArguments(bundle);
                        return aptDetailsFragment;
                }
            } else if (globalAppointmentDetailModel.getResponseData().getAudio().size() != 0
                    && globalAppointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                    && globalAppointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                switch (position) {
                    case 0:
                        aptDetailsFragment.setArguments(bundle);
                        return aptDetailsFragment;
                    case 1:
                        aptAudioFragment.setArguments(bundle2);
                        return aptAudioFragment;
                }
            } else if (globalAppointmentDetailModel.getResponseData().getAudio().size() == 0
                    && !globalAppointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                    && globalAppointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                switch (position) {
                    case 0:
                        aptDetailsFragment.setArguments(bundle);
                        return aptDetailsFragment;
                    case 1:
                        aptBookletFragment.setArguments(bundle);
                        return aptBookletFragment;
                }
            } else if (globalAppointmentDetailModel.getResponseData().getAudio().size() == 0
                    && globalAppointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                    && !globalAppointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                switch (position) {
                    case 0:
                        aptDetailsFragment.setArguments(bundle);
                        return aptDetailsFragment;
                    case 1:
                        aptAnswersFragment.setArguments(bundle);
                        return aptAnswersFragment;
                }
            } else if (globalAppointmentDetailModel.getResponseData().getAudio().size() != 0
                    && !globalAppointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                    && globalAppointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                switch (position) {
                    case 0:
                        aptDetailsFragment.setArguments(bundle);
                        return aptDetailsFragment;
                    case 1:
                        aptAudioFragment.setArguments(bundle2);
                        return aptAudioFragment;
                    case 2:
                        aptBookletFragment.setArguments(bundle);
                        return aptBookletFragment;
                }
            } else if (globalAppointmentDetailModel.getResponseData().getAudio().size() != 0
                    && globalAppointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                    && !globalAppointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                switch (position) {
                    case 0:
                        aptDetailsFragment.setArguments(bundle);
                        return aptDetailsFragment;
                    case 1:
                        aptAudioFragment.setArguments(bundle2);
                        return aptAudioFragment;
                    case 2:
                        aptAnswersFragment.setArguments(bundle);
                        return aptAnswersFragment;
                }
            } else if (globalAppointmentDetailModel.getResponseData().getAudio().size() == 0
                    && !globalAppointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                    && !globalAppointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                switch (position) {
                    case 0:
                        aptDetailsFragment.setArguments(bundle);
                        return aptDetailsFragment;
                    case 1:
                        aptBookletFragment.setArguments(bundle);
                        return aptBookletFragment;
                    case 2:
                        aptAnswersFragment.setArguments(bundle);
                        return aptAnswersFragment;
                }
            } else if (globalAppointmentDetailModel.getResponseData().getAudio().size() != 0
                    && !globalAppointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                    && !globalAppointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                switch (position) {
                    case 0:
                        aptDetailsFragment.setArguments(bundle);
                        return aptDetailsFragment;
                    case 1:
                        aptAudioFragment.setArguments(bundle2);
                        return aptAudioFragment;
                    case 2:
                        aptBookletFragment.setArguments(bundle);
                        return aptBookletFragment;
                    case 3:
                        aptAnswersFragment.setArguments(bundle);
                        return aptAnswersFragment;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return totalTabs;
        }
    }
}