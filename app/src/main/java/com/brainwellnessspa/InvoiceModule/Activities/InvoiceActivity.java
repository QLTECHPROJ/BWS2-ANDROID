package com.brainwellnessspa.InvoiceModule.Activities;

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

import com.brainwellnessspa.DashboardModule.Models.SegmentPlaylist;
import com.brainwellnessspa.DownloadModule.Activities.DownloadsActivity;
import com.brainwellnessspa.InvoiceModule.Models.SegmentMembership;
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
import com.google.gson.Gson;
import com.segment.analytics.Properties;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class InvoiceActivity extends AppCompatActivity {
    ActivityInvoiceBinding binding;
    ArrayList<InvoiceListModel.Appointment> appointmentList;
    ArrayList<InvoiceListModel.MemberShip> memberShipList;
    String UserID, ComeFrom = "";
    Context context;
    Activity activity;
    public static int invoiceToDashboard = 0;
    public static int invoiceToRecepit = 0;
    Properties p;
    private int numStarted = 0;
    int stackStatus = 0;
    boolean myBackPress = false ;

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

        Properties p = new Properties();
        p.putValue("userId", UserID);
        BWSApplication.addToSegment("Invoices Screen Viewed", p, CONSTANTS.screen);

        binding.llBack.setOnClickListener(view -> {
            myBackPress = true;
            if (invoiceToRecepit == 0) {
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
            } else if (invoiceToRecepit == 1) {
                ComeScreenAccount = 1;
                comefromDownload = "0";
                Intent i = new Intent(context, DashboardActivity.class);
                startActivity(i);
                finish();
            } else {
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }
        prepareData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void prepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<InvoiceListModel> listCall = APIClient.getClient().getInvoicelistPlaylist(UserID, "1");
            listCall.enqueue(new Callback<InvoiceListModel>() {
                @Override
                public void onResponse(Call<InvoiceListModel> call, Response<InvoiceListModel> response) {
                    try {
                        InvoiceListModel listModel = response.body();
                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            appointmentList = new ArrayList<>();
                            memberShipList = new ArrayList<>();
                            appointmentList = listModel.getResponseData().getAppointment();
                            memberShipList = listModel.getResponseData().getMemberShip();
                            binding.viewPager.setOffscreenPageLimit(2);
                            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Membership"));
                            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Appointment"));
                            binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                            TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), binding.tabLayout.getTabCount());
                            binding.viewPager.setAdapter(adapter);
                            binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

                            binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                @Override
                                public void onTabSelected(TabLayout.Tab tab) {
                                    binding.viewPager.setCurrentItem(tab.getPosition());
                                    p = new Properties();
                                    p.putValue("userId", UserID);
                                    if (tab.getPosition() == 0) {
                                        p.putValue("invoiceType", "Memebrship");
                                        ArrayList<SegmentMembership> section1 = new ArrayList<>();
                                        SegmentMembership e = new SegmentMembership();
                                        Gson gson = new Gson();
                                        for (int i = 0; i < memberShipList.size(); i++) {
                                            e.setInvoiceId(memberShipList.get(i).getInvoiceId());
                                            e.setInvoiceAmount(memberShipList.get(i).getAmount());
                                            e.setInvoiceDate(memberShipList.get(i).getDate());
                                            e.setInvoiceCurrency("");
                                            e.setPlan("");
                                            e.setPlanStartDt("");
                                            e.setPlanExpiryDt("");
                                            section1.add(e);
                                        }
                                        p.putValue("membership", gson.toJson(section1));
                                    } else if (tab.getPosition() == 1) {
                                        p.putValue("invoiceType", "Appointment");
                                    }
                                    BWSApplication.addToSegment("Invoice Screen Viewed", p, CONSTANTS.screen);
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
        myBackPress = true;
        if (invoiceToRecepit == 0) {
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
        } else if (invoiceToRecepit == 1) {
            ComeScreenAccount = 1;
            comefromDownload = "0";
            Intent i = new Intent(context, DashboardActivity.class);
            startActivity(i);
            finish();
        } else {

        }
    }

    public class TabAdapter extends FragmentStatePagerAdapter {
        int totalTabs;

        public TabAdapter(FragmentManager fm, int totalTabs) {
            super(fm);
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
                if(!myBackPress) {
                    Log.e("APPLICATION", "Back press false");
                    stackStatus = 2;
                }else{
                    myBackPress = true;
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
            }else{
                Log.e("Destroy", "Activity go in main activity");
            }
        }
    }

}