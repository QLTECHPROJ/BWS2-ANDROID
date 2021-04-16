package com.brainwellnessspa.DashboardTwoModule.profile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.BillingOrderActivity;
import com.brainwellnessspa.BuildConfig;
import com.brainwellnessspa.DashboardTwoModule.ViewPlayerActivity;
import com.brainwellnessspa.ProfileTwoModule.AccountInfoActivity;
import com.brainwellnessspa.DownloadModule.Activities.DownloadsActivity;
import com.brainwellnessspa.FaqModule.Activities.FaqActivity;
import com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ReminderModule.Activities.ReminderDetailsActivity;
import com.brainwellnessspa.ResourceModule.Activities.ResourceActivity;
import com.brainwellnessspa.UserModuleTwo.Activities.AddProfileActivity;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentProfileBinding;

import static com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity.invoiceToRecepit;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    private long mLastClickTime = 0;
    Dialog logoutDialog;
    public static int ComeScreenReminder = 0;
    String USERID, UserName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        USERID = (shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, ""));
        UserName = (shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, ""));
        binding.tvName.setText(UserName);
        binding.tvVersion.setText("Version " + BuildConfig.VERSION_NAME);
        binding.llUserProfile.setOnClickListener(view15 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), AddProfileActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.tvName.setOnClickListener(view15 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), ViewPlayerActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llAcInfo.setOnClickListener(view15 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), AccountInfoActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llDownloads.setOnClickListener(view12 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(getActivity(), DownloadsActivity.class);
            startActivity(i);
            getActivity().overridePendingTransition(0, 0);
        });

        binding.llInvoices.setOnClickListener(view14 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                invoiceToRecepit = 1;
                Intent i = new Intent(getActivity(), InvoiceActivity.class);
                i.putExtra("ComeFrom", "");
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llBillingOrder.setOnClickListener(view15 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), BillingOrderActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llReminder.setOnClickListener(view16 -> {
            ComeScreenReminder = 1;
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), ReminderDetailsActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llResources.setOnClickListener(view17 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), ResourceActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llFAQ.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), FaqActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llLogOut.setOnClickListener(view19 -> {
            if (BWSApplication.isNetworkConnected(getActivity())) {
                logoutDialog = new Dialog(getActivity());
                logoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                logoutDialog.setContentView(R.layout.logout_layout);
                logoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                logoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                final TextView tvGoBack = logoutDialog.findViewById(R.id.tvGoBack);
                final Button Btn = logoutDialog.findViewById(R.id.Btn);
                final ProgressBar progressBar = logoutDialog.findViewById(R.id.progressBar);
                final FrameLayout progressBarHolder = logoutDialog.findViewById(R.id.progressBarHolder);

                logoutDialog.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        logoutDialog.hide();
                        return true;
                    }
                    return false;
                });

                Btn.setOnClickListener(v -> {
                    logoutDialog.hide();
//                    BWSApplication.showProgressBar(progressBar, progressBarHolder, getActivity());
                });

                tvGoBack.setOnClickListener(v -> logoutDialog.hide());
                logoutDialog.show();
                logoutDialog.setCancelable(false);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });
        return view;
    }
}