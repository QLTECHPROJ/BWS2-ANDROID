package com.qltech.bws.InvoiceModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.InvoiceModule.Fragments.AppointmentInvoiceFragment;
import com.qltech.bws.InvoiceModule.Fragments.MembershipInvoiceFragment;
import com.qltech.bws.InvoiceModule.Models.InvoiceListModel;
import com.qltech.bws.LoginModule.Activities.LoginActivity;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityInvoiceBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceActivity extends AppCompatActivity {
    ActivityInvoiceBinding binding;
    ArrayList<InvoiceListModel.Appointment> appointmentList;
    ArrayList<InvoiceListModel.MemberShip> memberShipList;
    String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoice);
        Glide.with(InvoiceActivity.this).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        prepareData();
    }

    void prepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            showProgressBar();
            Call<InvoiceListModel> listCall = APIClient.getClient().getInvoicelistPlaylist(UserID, "1");
            listCall.enqueue(new Callback<InvoiceListModel>() {
                @Override
                public void onResponse(Call<InvoiceListModel> call, Response<InvoiceListModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        InvoiceListModel listModel = response.body();
                        appointmentList = new ArrayList<>();
                        memberShipList = new ArrayList<>();
                        appointmentList = listModel.getResponseData().getAppointment();
                        memberShipList = listModel.getResponseData().getMemberShip();
                        binding.viewPager.setOffscreenPageLimit(2);
                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Membership"));
                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Appointment"));
                        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), InvoiceActivity.this, binding.tabLayout.getTabCount());
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
                    }
                }

                @Override
                public void onFailure(Call<InvoiceListModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public class TabAdapter extends FragmentStatePagerAdapter {
        int totalTabs;
        private Context myContext;
        Callback<InvoiceListModel> modelCallback;

        public TabAdapter(FragmentManager fm, Context myContext, int totalTabs) {
            super(fm);
            this.myContext = myContext;
            this.totalTabs = totalTabs;
        }

        public TabAdapter(FragmentManager fm, Callback<InvoiceListModel> callback, int totalTabs) {
            super(fm);
            this.modelCallback = callback;
            this.totalTabs = totalTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Bundle bundle = new Bundle();
                    MembershipInvoiceFragment membershipInvoiceFragment = new MembershipInvoiceFragment();
                    bundle.putParcelableArrayList("membershipInvoiceFragment", memberShipList);
                    membershipInvoiceFragment.setArguments(bundle);
                    return membershipInvoiceFragment;
                case 1:
                    bundle = new Bundle();
                    AppointmentInvoiceFragment appointmentInvoiceFragment = new AppointmentInvoiceFragment();
                    bundle.putParcelableArrayList("appointmentInvoiceFragment", appointmentList);
                    appointmentInvoiceFragment.setArguments(bundle);
                    return appointmentInvoiceFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return totalTabs;
        }
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}