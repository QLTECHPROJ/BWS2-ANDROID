package com.qltech.bws.DashboardModule.Account;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qltech.bws.BillingOrderModule.Activities.BillingOrderActivity;
import com.qltech.bws.BuildConfig;
import com.qltech.bws.DownloadModule.Activities.DownloadsActivity;
import com.qltech.bws.FaqModule.Activities.FaqActivity;
import com.qltech.bws.InvoiceModule.Activities.InvoiceActivity;
import com.qltech.bws.R;
import com.qltech.bws.ReminderModule.ReminderActivity;
import com.qltech.bws.ResourceModule.Activities.ResourceActivity;
import com.qltech.bws.UserModule.Activities.UserProfileActivity;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {
    FragmentAccountBinding binding;
    private AccountViewModel accountViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountViewModel =
                ViewModelProviders.of(this).get(AccountViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        View view = binding.getRoot();

        MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 10,
                1, 1, 0.2f, 10);
        binding.civProfile.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.civProfile.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());

        binding.tvVersion.setText("Version " +BuildConfig.VERSION_NAME);
        binding.llUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(i);
            }
        });

        binding.llDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), DownloadsActivity.class);
                startActivity(i);
            }
        });

        binding.llInvoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), InvoiceActivity.class);
                startActivity(i);
            }
        });

        binding.llBillingOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BillingOrderActivity.class);
                startActivity(i);
            }
        });

        binding.llReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ReminderActivity.class);
                startActivity(i);
            }
        });

        binding.llResource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ResourceActivity.class);
                startActivity(i);
            }
        });

        binding.llFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), FaqActivity.class);
                startActivity(i);
            }
        });
        binding.llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.cancel_membership);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
                final TextView tvSubTitle = dialog.findViewById(R.id.tvSubTitle);
                final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                final RelativeLayout tvconfirm = dialog.findViewById(R.id.tvconfirm);

                tvTitle.setText(R.string.logout);
                tvSubTitle.setText(R.string.logout_quotes);
                tvconfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                tvGoBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.setCancelable(false);
            }
        });

        accountViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                binding.tvName.setText(s);
            }
        });
        return view;
    }
}