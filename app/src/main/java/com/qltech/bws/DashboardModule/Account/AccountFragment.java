package com.qltech.bws.DashboardModule.Account;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.qltech.bws.BillingOrderModule.Activities.BillingOrderActivity;
import com.qltech.bws.BuildConfig;
import com.qltech.bws.DashboardModule.Models.LogoutModel;
import com.qltech.bws.DownloadModule.Activities.DownloadsActivity;
import com.qltech.bws.FaqModule.Activities.FaqActivity;
import com.qltech.bws.InvoiceModule.Activities.InvoiceActivity;
import com.qltech.bws.LoginModule.Activities.LoginActivity;
import com.qltech.bws.R;
import com.qltech.bws.ReminderModule.Activities.ReminderActivity;
import com.qltech.bws.ResourceModule.Activities.ResourceActivity;
import com.qltech.bws.UserModule.Activities.UserProfileActivity;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.UserModule.Models.ProfileViewModel;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.Utility.MusicService;
import com.qltech.bws.databinding.FragmentAccountBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountFragment extends Fragment {
    FragmentAccountBinding binding;
    private AccountViewModel accountViewModel;
    String UserID;
    private long mLastClickTime = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountViewModel =
                ViewModelProviders.of(this).get(AccountViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 10,
                1, 1, 0.2f, 10);
        binding.civProfile.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.civProfile.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        profileViewData(getActivity());

        binding.tvVersion.setText("Version " + BuildConfig.VERSION_NAME);

        binding.llUserProfile.setOnClickListener(view13 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(getActivity(), UserProfileActivity.class);
            startActivity(i);
        });

        binding.llDownloads.setOnClickListener(view12 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(getActivity(), DownloadsActivity.class);
            startActivity(i);
        });

        binding.llInvoices.setOnClickListener(view14 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(getActivity(), InvoiceActivity.class);
            startActivity(i);
        });

        binding.llBillingOrder.setOnClickListener(view15 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(getActivity(), BillingOrderActivity.class);
            startActivity(i);
        });

        binding.llReminder.setOnClickListener(view16 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(getActivity(), ReminderActivity.class);
            startActivity(i);
        });

        binding.llResource.setOnClickListener(view17 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(getActivity(), ResourceActivity.class);
            startActivity(i);
        });

        binding.llFaq.setOnClickListener(view18 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(getActivity(), FaqActivity.class);
            startActivity(i);
        });

        binding.llLogout.setOnClickListener(view19 -> {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.logout_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
            final Button Btn = dialog.findViewById(R.id.Btn);

            dialog.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.hide();
                    return true;
                }
                return false;
            });

            Btn.setOnTouchListener((view1, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Button views = (Button) view1;
                        views.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view1.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        clearData(dialog);
                    case MotionEvent.ACTION_CANCEL: {
                        Button views = (Button) view1;
                        views.getBackground().clearColorFilter();
                        views.invalidate();
                        break;
                    }
                }
                return true;
            });

            tvGoBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hide();
                }
            });
            dialog.show();
            dialog.setCancelable(false);
        });

        accountViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        profileViewData(getActivity());
    }

    void clearData(Dialog dialog) {
        DeleteCall();
        MusicService.stopMedia();
        SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE);
        String fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "");
        if (TextUtils.isEmpty(fcm_id)) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(getActivity(), new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String newToken = instanceIdResult.getToken();
                    Log.e("newToken", newToken);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE).edit();
                    editor.putString(CONSTANTS.Token, newToken); //Friend
                    editor.apply();
                    editor.commit();
                }
            });
            fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "");
        }

        if (BWSApplication.isNetworkConnected(getActivity())) {
            Call<LogoutModel> listCall = APIClient.getClient().getLogout(UserID, fcm_id, CONSTANTS.FLAG_ONE);
            listCall.enqueue(new Callback<LogoutModel>() {
                @Override
                public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                    if (response.isSuccessful()) {
                        LogoutModel loginModel = response.body();
                        dialog.hide();
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i);
                    } else {
                        BWSApplication.showToast(response.message(), getActivity());
                    }
                }

                @Override
                public void onFailure(Call<LogoutModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    void DeleteCall() {
        SharedPreferences preferences = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(CONSTANTS.PREF_KEY_UserID);
        edit.remove(CONSTANTS.PREF_KEY_MobileNo);
        edit.remove(CONSTANTS.PREF_KEY_IsRepeat);
        edit.remove(CONSTANTS.PREF_KEY_IsShuffle);
        edit.clear();
        edit.commit();

        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_ReminderStatus, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit1 = shared.edit();
        edit1.remove(CONSTANTS.PREF_KEY_ReminderStatus);
        edit1.remove(CONSTANTS.PREF_KEY_MobileNo);
        edit1.clear();
        edit1.commit();

        SharedPreferences shareds = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_CardID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shareds.edit();
        editor.remove(CONSTANTS.PREF_KEY_CardID);
        editor.clear();
        editor.commit();

        SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorr = sharedm.edit();
        editorr.remove(CONSTANTS.PREF_KEY_modelList);
        editorr.remove(CONSTANTS.PREF_KEY_position);
        editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
        editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
        editorr.remove(CONSTANTS.PREF_KEY_AudioFlag);
        editor.remove(CONSTANTS.PREF_KEY_PlaylistId);
        editor.remove(CONSTANTS.PREF_KEY_myPlaylist);
        editorr.clear();
        editorr.commit();
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void profileViewData(Context ctx) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            showProgressBar();
            Call<ProfileViewModel> listCall = APIClient.getClient().getProfileView(UserID);
            listCall.enqueue(new Callback<ProfileViewModel>() {
                @Override
                public void onResponse(Call<ProfileViewModel> call, Response<ProfileViewModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        ProfileViewModel viewModel = response.body();
                        binding.tvName.setText(viewModel.getResponseData().getName());
                        String profilePicPath = viewModel.getResponseData().getImage();

                        Glide.with(ctx).load(profilePicPath)
                                .placeholder(R.drawable.default_profile)
                                .thumbnail(1f)
                                .dontAnimate().into(binding.civProfile);

                        if (viewModel.getResponseData().getOrderTotal().equalsIgnoreCase("")) {
                            binding.tvCrtPlan.setText("Current plan: $0.00 / month");
                        } else {
                            binding.tvCrtPlan.setText("Current plan: $" + viewModel.getResponseData().getOrderTotal() + " / month");
                        }
                    } else {
                        hideProgressBar();
                    }
                }

                @Override
                public void onFailure(Call<ProfileViewModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        }
    }
}