package com.brainwellnessspa.BillingOrderModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.brainwellnessspa.Utility.CONSTANTS;
import com.google.android.material.tabs.TabLayout;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Fragments.BillingAddressFragment;
import com.brainwellnessspa.BillingOrderModule.Fragments.CurrentPlanFragment;
import com.brainwellnessspa.BillingOrderModule.Fragments.PaymentFragment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.ActivityBillingOrderBinding;
import com.segment.analytics.Properties;

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;

public class BillingOrderActivity extends AppCompatActivity {
    ActivityBillingOrderBinding binding;
    int payment = 0;
    String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_billing_order);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        binding.llBack.setOnClickListener(view -> {
            ComeScreenAccount = 1;
            comefromDownload = "0";
            finish();
        });

        /*Properties p = new Properties();
        p.putValue("userId", UserID);
        BWSApplication.addToSegment("Billing & Order Screen Viewed", p, CONSTANTS.screen);*/

        binding.viewPager.setOffscreenPageLimit(3);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Current Plan"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Payment"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Billing Address"));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        if (BWSApplication.isNetworkConnected(this)) {
            TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), this, binding.tabLayout.getTabCount());
            binding.viewPager.setAdapter(adapter);
            binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this);
        }

        if (getIntent().hasExtra("payment")) {
            payment = getIntent().getIntExtra("payment", 0);
        }

        if (payment != 0) {
            binding.viewPager.setCurrentItem(1);
        } else {
            binding.viewPager.setCurrentItem(0);
        }

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

    @Override
    public void onBackPressed() {
        ComeScreenAccount = 1;
        comefromDownload = "0";
        finish();
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
            Bundle bundle;
            switch (position) {
                case 0:
                    CurrentPlanFragment currentPlanFragment = new CurrentPlanFragment();
                    bundle = new Bundle();
                    currentPlanFragment.setArguments(bundle);
                    return currentPlanFragment;
                case 1:
                    PaymentFragment paymentFragment = new PaymentFragment();
                    bundle = new Bundle();
                    paymentFragment.setArguments(bundle);
                    return paymentFragment;
                case 2:
                    BillingAddressFragment billingAddressFragment = new BillingAddressFragment();
                    bundle = new Bundle();
                    billingAddressFragment.setArguments(bundle);
                    return billingAddressFragment;
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