package com.qltech.bws.DashboardModule.Appointment;

import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.qltech.bws.BillingOrderModule.Activities.BillingOrderActivity;
import com.qltech.bws.BillingOrderModule.Fragments.BillingAddressFragment;
import com.qltech.bws.BillingOrderModule.Fragments.CurrentPlanFragment;
import com.qltech.bws.BillingOrderModule.Fragments.PaymentFragment;
import com.qltech.bws.DashboardModule.Appointment.AppointmentDetails.AptAnswersFragment;
import com.qltech.bws.DashboardModule.Appointment.AppointmentDetails.AptAudioFragment;
import com.qltech.bws.DashboardModule.Appointment.AppointmentDetails.AptBookletFragment;
import com.qltech.bws.DashboardModule.Appointment.AppointmentDetails.AptDetailsFragment;
import com.qltech.bws.DownloadModule.Models.DownloadsHistoryModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentAppointmentDetailsBinding;

import retrofit2.Callback;

public class AppointmentDetailsFragment extends Fragment {
    FragmentAppointmentDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_appointment_details, container, false);
        View view = binding.getRoot();

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment sessionsFragment = new SessionsFragment();
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                fm.popBackStack ("AppointmentDetailsFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        binding.viewPager.setOffscreenPageLimit(4);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Booklet"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My answers"));
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
        return view;
    }
    public class TabAdapter extends FragmentStatePagerAdapter {

        int totalTabs;
        private Context myContext;
        Callback<DownloadsHistoryModel> downloadsHistoryModelCallback;

        public TabAdapter(FragmentManager fm, Context myContext, int totalTabs) {
            super(fm);
            this.myContext = myContext;
            this.totalTabs = totalTabs;
        }

        public TabAdapter(FragmentManager fm, Callback<DownloadsHistoryModel> transactionHistoryModelCallback, int totalTabs) {
            super(fm);
            this.downloadsHistoryModelCallback = transactionHistoryModelCallback;
            this.totalTabs = totalTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    AptDetailsFragment aptDetailsFragment = new AptDetailsFragment();
                    Bundle bundle = new Bundle();
                    aptDetailsFragment.setArguments(bundle);
                    return aptDetailsFragment;
                case 1:
                    AptAudioFragment aptAudioFragment = new AptAudioFragment();
                    bundle = new Bundle();
                    aptAudioFragment.setArguments(bundle);
                    return aptAudioFragment;
                case 2:
                    AptBookletFragment aptBookletFragment = new AptBookletFragment();
                    bundle = new Bundle();
                    aptBookletFragment.setArguments(bundle);
                    return aptBookletFragment;
                case 3:
                    AptAnswersFragment aptAnswersFragment = new AptAnswersFragment();
                    bundle = new Bundle();
                    aptAnswersFragment.setArguments(bundle);
                    return aptAnswersFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return totalTabs;
        }
    }

}