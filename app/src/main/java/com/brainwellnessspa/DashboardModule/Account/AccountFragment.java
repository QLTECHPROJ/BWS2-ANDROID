package com.brainwellnessspa.DashboardModule.Account;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.BillingOrderActivity;
import com.brainwellnessspa.BuildConfig;
import com.brainwellnessspa.DashboardModule.Models.LogoutModel;
import com.brainwellnessspa.DownloadModule.Activities.DownloadsActivity;
import com.brainwellnessspa.FaqModule.Activities.FaqActivity;
import com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity;
import com.brainwellnessspa.LoginModule.Activities.LoginActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ReminderModule.Activities.ReminderDetailsActivity;
import com.brainwellnessspa.ResourceModule.Activities.ResourceActivity;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.UserModule.Activities.UserProfileActivity;
import com.brainwellnessspa.UserModule.Models.ProfileViewModel;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.Utility.MusicService;
import com.brainwellnessspa.databinding.FragmentAccountBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.mediaPlayer;
import static com.brainwellnessspa.Utility.MusicService.releasePlayer;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;

public class AccountFragment extends Fragment {
    public static String IsLock = "";
    public static int ComeScreenReminder = 0;
    FragmentAccountBinding binding;
    String UserID;
    private long mLastClickTime = 0;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 10,
                1, 1, 0.2f, 10);
        binding.civProfile.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.civProfile.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());

        MeasureRatio measureRatios = BWSApplication.measureRatio(getActivity(), 10,
                1, 1, 0.2f, 10);
        binding.civLetter.getLayoutParams().height = (int) (measureRatios.getHeight() * measureRatios.getRatio());
        binding.civLetter.getLayoutParams().width = (int) (measureRatios.getWidthImg() * measureRatios.getRatio());
        profileViewData(getActivity());

        binding.tvVersion.setText("Version " + BuildConfig.VERSION_NAME);

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
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), InvoiceActivity.class);
                i.putExtra("ComeFrom", "");
                startActivity(i);
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
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llResource.setOnClickListener(view17 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), ResourceActivity.class);
                startActivity(i);
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
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });
        binding.llFaq.setOnClickListener(view18 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), FaqActivity.class);
                startActivity(i);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llLogout.setOnClickListener(view19 -> {
            if (BWSApplication.isNetworkConnected(getActivity())) {
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
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
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
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<LogoutModel> listCall = APIClient.getClient().getLogout(UserID, fcm_id, CONSTANTS.FLAG_ONE);
            listCall.enqueue(new Callback<LogoutModel>() {
                @Override
                public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                    if (response.isSuccessful()) {
                        LogoutModel loginModel = response.body();
                        dialog.hide();
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
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
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    void DeleteCall() {
        DeletallLocalCart();
        DeletallLocalCart1();
        if(isMediaStart){
            stopMedia();
            releasePlayer();
        }
        SharedPreferences preferences = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(CONSTANTS.PREF_KEY_UserID);
        edit.remove(CONSTANTS.PREF_KEY_MobileNo);
        edit.remove(CONSTANTS.PREF_KEY_IsRepeat);
        edit.remove(CONSTANTS.PREF_KEY_IsShuffle);
        edit.clear();
        edit.commit();
        SharedPreferences preferences11 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit1 = preferences11.edit();
        edit1.remove(CONSTANTS.PREF_KEY_DownloadName);
        edit1.remove(CONSTANTS.PREF_KEY_DownloadUrl);
        edit1.remove(CONSTANTS.PREF_KEY_DownloadPlaylistId);
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
        editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
        editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
        editorr.clear();
        editorr.commit();
    }

    public void DeletallLocalCart() {
        class DeletallCart extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient
                        .getInstance(getActivity())
                        .getaudioDatabase()
                        .taskDao()
                        .deleteAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                DeletallLocalCart1();
                super.onPostExecute(aVoid);
            }
        }
        DeletallCart st = new DeletallCart();
        st.execute();
    }

    public void DeletallLocalCart1() {
        class DeletallCart extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient
                        .getInstance(getActivity())
                        .getaudioDatabase()
                        .taskDao()
                        .deleteAllPlalist();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }
        DeletallCart st = new DeletallCart();
        st.execute();
    }

    void profileViewData(Context ctx) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<ProfileViewModel> listCall = APIClient.getClient().getProfileView(UserID);
            listCall.enqueue(new Callback<ProfileViewModel>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<ProfileViewModel> call, Response<ProfileViewModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        ProfileViewModel viewModel = response.body();
                        binding.tvViewProfile.setVisibility(View.VISIBLE);

                        if (viewModel.getResponseData().getName().equalsIgnoreCase("") ||
                                viewModel.getResponseData().getName().equalsIgnoreCase(" ") ||
                                viewModel.getResponseData().getName() == null) {
                            binding.tvName.setText(R.string.Guest);
                        } else {
                            binding.tvName.setText(viewModel.getResponseData().getName());
                        }
                        String Name = viewModel.getResponseData().getName();
                        String Letter = Name.substring(0, 1);
                        String profilePicPath = viewModel.getResponseData().getImage();
                        IsLock = viewModel.getResponseData().getIsLock();
                        if (profilePicPath.equalsIgnoreCase("")) {
                            binding.civProfile.setVisibility(View.GONE);
                            binding.rlLetter.setVisibility(View.VISIBLE);
                            binding.tvLetter.setText(Letter);
                        } else {
                            binding.civProfile.setVisibility(View.VISIBLE);
                            binding.rlLetter.setVisibility(View.GONE);
                            Glide.with(ctx).load(profilePicPath).thumbnail(1f).dontAnimate().into(binding.civProfile);
                        }


                        binding.llUserProfile.setOnClickListener(view13 -> {
//                            if (viewModel.getResponseData().getPatientid().equalsIgnoreCase("1")){
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Intent i = new Intent(getActivity(), UserProfileActivity.class);
                            startActivity(i);
                           /* }else if (viewModel.getResponseData().getPatientid().equalsIgnoreCase("0")) {
                                final Dialog dialog = new Dialog(ctx);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.patient_popup);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                final RelativeLayout rlGoBack = dialog.findViewById(R.id.rlGoBack);
                                dialog.setOnKeyListener((v, keyCode, event) -> {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        dialog.dismiss();
                                        return true;
                                    }
                                    return false;
                                });

                                rlGoBack.setOnClickListener(v -> {
                                    dialog.dismiss();
                                });
                                dialog.show();
                                dialog.setCancelable(false);
                            }*/
                        });

                        if (viewModel.getResponseData().getOrderTotal().equalsIgnoreCase("")) {
                            binding.tvCrtPlan.setText("Current plan: $0.00 / month");
                        } else {
                            if (viewModel.getResponseData().getPlanperiod().equalsIgnoreCase("")) {
                                binding.tvCrtPlan.setText("Current plan: " + viewModel.getResponseData().getOrderTotal());
                            } else {
                                binding.tvCrtPlan.setText("Current plan: " + viewModel.getResponseData().getOrderTotal() + " / " +
                                        viewModel.getResponseData().getPlanperiod());
                            }
                        }

                    } else {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                    }
                }

                @Override
                public void onFailure(Call<ProfileViewModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        }
    }
}