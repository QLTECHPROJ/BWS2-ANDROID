package com.qltech.bws.DashboardModule.Appointment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.android.material.tabs.TabLayout;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Appointment.AppointmentDetails.AptAnswersFragment;
import com.qltech.bws.DashboardModule.Appointment.AppointmentDetails.AptAudioFragment;
import com.qltech.bws.DashboardModule.Appointment.AppointmentDetails.AptBookletFragment;
import com.qltech.bws.DashboardModule.Appointment.AppointmentDetails.AptDetailsFragment;
import com.qltech.bws.DashboardModule.Models.AppointmentDetailModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.OnBackPressed;
import com.qltech.bws.databinding.FragmentAppointmentDetailsBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentDetailsFragment extends Fragment implements OnBackPressed {
    FragmentAppointmentDetailsBinding binding;
    Activity activity;
    String UserId, appointmentTypeId, appointmentName, appointmentMainName, appointmentImage, AudioFlag;
    AppointmentDetailModel global_appointmentDetailModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_appointment_details, container, false);
        View view = binding.getRoot();
        activity = getActivity();
        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);

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
        /*view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                callBack();
                return true;
            }
            return false;
        });*/

        binding.llBack.setOnClickListener(view1 -> callBack());
        RefreshData();
        getAppointmentData();

        return view;
    }

    private void callBack() {
        Bundle bundle = new Bundle();
        Fragment sessionsFragment = new SessionsFragment();
        bundle.putString("appointmentId", appointmentTypeId);
        bundle.putString("appointmentMainName", appointmentMainName);
        bundle.putString("appointmentName", appointmentName);
        bundle.putString("appointmentImage", appointmentImage);
        sessionsFragment.setArguments(bundle);
        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .replace(R.id.flSession, sessionsFragment)
                .commit();
    }

    private void RefreshData() {
        if (!AudioFlag.equalsIgnoreCase("0")) {
            Fragment fragment = new TransparentPlayerFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flSession, fragment)
                    .commit();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 260);
            binding.llSpace.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 50);
            binding.llSpace.setLayoutParams(params);
        }
    }

    private void getAppointmentData() {
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
            Call<AppointmentDetailModel> listCall = APIClient.getClient().getAppointmentDetails(UserId, appointmentTypeId);
            listCall.enqueue(new Callback<AppointmentDetailModel>() {
                @Override
                public void onResponse(Call<AppointmentDetailModel> call, Response<AppointmentDetailModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        AppointmentDetailModel appointmentDetailModel = response.body();
                        global_appointmentDetailModel = appointmentDetailModel;

                        if (appointmentDetailModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            if (appointmentDetailModel.getResponseData().getAudio().size() == 0
                                    && appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                                    && appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                                binding.viewPager.setOffscreenPageLimit(1);
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
                            } else if (appointmentDetailModel.getResponseData().getAudio().size() != 0
                                    && appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                                    && appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                                binding.viewPager.setOffscreenPageLimit(2);
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"));
                            } else if (appointmentDetailModel.getResponseData().getAudio().size() != 0
                                    && !appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                                    && appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                                binding.viewPager.setOffscreenPageLimit(3);
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"));
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Booklet"));
                            } else if (appointmentDetailModel.getResponseData().getAudio().size() != 0
                                    && appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                                    && !appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                                binding.viewPager.setOffscreenPageLimit(3);
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"));
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My answers"));
                            } else if (appointmentDetailModel.getResponseData().getAudio().size() == 0
                                    && !appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                                    && !appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                                binding.viewPager.setOffscreenPageLimit(3);
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Booklet"));
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My answers"));
                            } else if (appointmentDetailModel.getResponseData().getAudio().size() != 0
                                    && !appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                                    && !appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                                binding.viewPager.setOffscreenPageLimit(4);
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"));
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Booklet"));
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My answers"));
                            }
                            binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                            TabAdapter adapter = new TabAdapter(getActivity().getSupportFragmentManager(), getActivity(), binding.tabLayout.getTabCount());
                            binding.viewPager.setAdapter(adapter);
                            binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

                            binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                @Override
                                public void onTabSelected(TabLayout.Tab tab) {
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
                }

                @Override
                public void onFailure(Call<AppointmentDetailModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    @Override
    public void onBackPressed() {
        callBack();
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
            bundle.putParcelable("AppointmentDetail", global_appointmentDetailModel.getResponseData());
            bundle2.putParcelable("AppointmentDetail", global_appointmentDetailModel.getResponseData());
            bundle2.putParcelableArrayList("AppointmentDetailList", global_appointmentDetailModel.getResponseData().getAudio());
            if (global_appointmentDetailModel.getResponseData().getAudio().size() == 0
                    && global_appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                    && global_appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                switch (position) {
                    case 0:
                        aptDetailsFragment.setArguments(bundle);
                        return aptDetailsFragment;
                }
            } else if (global_appointmentDetailModel.getResponseData().getAudio().size() != 0
                    && global_appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                    && global_appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
                switch (position) {
                    case 0:
                        aptDetailsFragment.setArguments(bundle);
                        return aptDetailsFragment;
                    case 1:
                        aptAudioFragment.setArguments(bundle2);
                        return aptAudioFragment;
                }
            } else if (global_appointmentDetailModel.getResponseData().getAudio().size() != 0
                    && !global_appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                    && global_appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
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
            } else if (global_appointmentDetailModel.getResponseData().getAudio().size() != 0
                    && global_appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                    && !global_appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
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
            } else if (global_appointmentDetailModel.getResponseData().getAudio().size() == 0
                    && !global_appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                    && !global_appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
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
            } else if (global_appointmentDetailModel.getResponseData().getAudio().size() != 0
                    && !global_appointmentDetailModel.getResponseData().getBooklet().equalsIgnoreCase("")
                    && !global_appointmentDetailModel.getResponseData().getMyAnswers().equalsIgnoreCase("")) {
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