package com.brainwellnessspa.InvoiceModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity;
import com.brainwellnessspa.InvoiceModule.Fragments.AppointmentInvoiceFragment;
import com.brainwellnessspa.InvoiceModule.Fragments.MembershipInvoiceFragment;
import com.brainwellnessspa.InvoiceModule.Models.InvoiceListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityInvoiceBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;

public class InvoiceActivity extends AppCompatActivity {
    ActivityInvoiceBinding binding;
    ArrayList<InvoiceListModel.Appointment> appointmentList;
    ArrayList<InvoiceListModel.MemberShip> memberShipList;
    String UserID, ComeFrom = "";
    Context context;
    Activity activity;
    public static int invoiceToDashboard = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoice);
        context = InvoiceActivity.this;
        activity = InvoiceActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        if (getIntent() != null) {
            ComeFrom = getIntent().getStringExtra("ComeFrom");
        }

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ComeFrom.equalsIgnoreCase("1")) {
                    invoiceToDashboard = 1;
                    Intent i = new Intent(context, DashboardActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    ComeScreenAccount = 1;
                    comefromDownload = "0";
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareData();
    }

    public void prepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<InvoiceListModel> listCall = APIClient.getClient().getInvoicelistPlaylist(UserID, "1");
            listCall.enqueue(new Callback<InvoiceListModel>() {
                @Override
                public void onResponse(Call<InvoiceListModel> call, Response<InvoiceListModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<InvoiceListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this);
        }
    }

    @Override
    public void onBackPressed() {
        if (ComeFrom.equalsIgnoreCase("1")) {
            invoiceToDashboard = 1;
            Intent i = new Intent(context, DashboardActivity.class);
            startActivity(i);
            finish();
        } else {
            ComeScreenAccount = 1;
            comefromDownload = "0";
            finish();
        }
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
}