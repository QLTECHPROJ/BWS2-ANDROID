package com.brainwellnessspa.billingOrderModule.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.billingOrderModule.fragments.PaymentFragment;
import com.google.android.material.tabs.TabLayout;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.billingOrderModule.fragments.BillingAddressFragment;
import com.brainwellnessspa.billingOrderModule.fragments.CurrentPlanFragment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.ActivityBillingOrderBinding;
import com.segment.analytics.Properties;

import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class BillingOrderActivity extends AppCompatActivity {
    ActivityBillingOrderBinding binding;
    int payment = 0;
    String userId, coUserId;
    private int numStarted = 0;
    int stackStatus = 0;
    Activity activity;
    public static boolean myBackPressbill = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_billing_order);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        userId = (shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, ""));
        coUserId = (shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, ""));
        activity = BillingOrderActivity.this;
        binding.llBack.setOnClickListener(view -> {
            myBackPressbill = true;
            comefromDownload = "0";
            finish();
        });

        binding.btnUpgradePlan.setOnClickListener(v -> {
            Intent i = new Intent(activity, UpgradePlanActivity.class);
            startActivity(i);
        });

        binding.tvCancel.setOnClickListener(v -> {
            Intent i = new Intent(activity, CancelMembershipActivity.class);
            startActivity(i);
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }
        Properties p = new Properties();
        p.putValue("userId", userId);
        p.putValue("plan", "");
        p.putValue("planStatus", "");
        p.putValue("planStartDt", "");
        p.putValue("planExpiryDt", "");
        p.putValue("planAmount", "");
        BWSApplication.addToSegment("Billing & Order Screen Viewed", p, CONSTANTS.screen);

        binding.viewPager.setOffscreenPageLimit(2);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Current Plan"));
//        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Payment"));
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
        myBackPressbill = true;
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
//                case 1:
//                    PaymentFragment paymentFragment = new PaymentFragment();
//                    bundle = new Bundle();
//                    paymentFragment.setArguments(bundle);
//                    return paymentFragment;
                case 1:
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

    class AppLifecycleCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                stackStatus = 1;
                Log.e("APPLICATION", "APP IN FOREGROUND");
                //app went to foreground
            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            numStarted--;
            if (numStarted == 0) {
                if (!myBackPressbill) {
                    Log.e("APPLICATION", "Back press false");
                    stackStatus = 2;
                } else {
                    myBackPressbill = true;
                    stackStatus = 1;
                    Log.e("APPLICATION", "back press true ");
                }
                Log.e("APPLICATION", "App is in BACKGROUND");
                // app went to background
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationId);
                relesePlayer(getApplicationContext());
            } else {
                Log.e("Destroy", "Activity go in main activity");
            }
        }
    }
}