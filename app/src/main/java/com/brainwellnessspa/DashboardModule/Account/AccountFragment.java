package com.brainwellnessspa.DashboardModule.Account;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.brainwellnessspa.LikeModule.Activities.LikeActivity;
import com.brainwellnessspa.ReferralModule.Activities.ReferFriendActivity;
import com.brainwellnessspa.ReferralModule.Model.ContactlistModel;
import com.brainwellnessspa.databinding.FragmentAccountBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.downloader.PRDownloader;
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
import com.brainwellnessspa.UserModule.Activities.UserProfileActivity;
import com.brainwellnessspa.UserModule.Models.ProfileViewModel;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.google.firebase.installations.FirebaseInstallations;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.brainwellnessspa.BWSApplication.deleteCache;
import static com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity.invoiceToRecepit;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;
import static com.brainwellnessspa.SplashModule.SplashScreenActivity.analytics;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.myAudioId;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.addToRecentPlayId;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadIdOne;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.filename;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.tutorial;

public class AccountFragment extends Fragment {
    public static int ComeScreenReminder = 0;
    public static int ComeScreenAccount = 0;
    public static boolean logout = false;
    FragmentAccountBinding binding;
    String UserID, MobileNo, Email, DeviceType, DeviceID, Name, UserName, AccountFirstLogin = "0";
    FancyShowCaseView fancyShowCaseView11, fancyShowCaseView21, fancyShowCaseView31;
    FancyShowCaseQueue queue;
    private long mLastClickTime = 0;
    List<ContactlistModel> userList = new ArrayList<>();
    Dialog logoutDialog, supportDialog;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        MobileNo = (shared1.getString(CONSTANTS.PREF_KEY_MobileNo, ""));
        Name = (shared1.getString(CONSTANTS.PREF_KEY_Name, ""));
        Email = (shared1.getString(CONSTANTS.PREF_KEY_Email, ""));
        DeviceType = (shared1.getString(CONSTANTS.PREF_KEY_DeviceType, ""));
        DeviceID = (shared1.getString(CONSTANTS.PREF_KEY_DeviceID, ""));
        ComeScreenAccount = 1;
        comefromDownload = "0";
       /* MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 10,
                1, 1, 0.2f, 10);
        binding.civProfile.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.civProfile.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());*/
        /*binding.rvContactList.setHasFixedSize(true);
        RecyclerView.LayoutManager mListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvContactList.setLayoutManager(mListLayoutManager);
        binding.rvContactList.setItemAnimator(new DefaultItemAnimator());*/
        /*  String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER*//*, ContactsContract.CommonDataKinds.Phone.PHOTO_URI*//*};
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        String lastPhoneName = " ";
        if (phones.getCount() > 0) {
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if (!name.equalsIgnoreCase(lastPhoneName)) {
                    lastPhoneName = name;
                    ContactlistModel user = new ContactlistModel();
                    user.setContactName(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                    user.setContactNumber(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    userList.add(user);
                }
            }
        }
        phones.close();
        ContactListAdapter contactListAdapter = new ContactListAdapter(userList);
        binding.rvContactList.setAdapter(contactListAdapter);*/
      /*  MeasureRatio measureRatios = BWSApplication.measureRatio(getActivity(), 10,
                1, 1, 0.2f, 10);
        binding.civLetter.getLayoutParams().height = (int) (measureRatios.getHeight() * measureRatios.getRatio());
        binding.civLetter.getLayoutParams().width = (int) (measureRatios.getWidthImg() * measureRatios.getRatio());*/
        Properties p = new Properties();
        p.putValue("userId", UserID);
        BWSApplication.addToSegment("Account Screen Viewed", p, CONSTANTS.screen);
        binding.tvVersion.setText("Version " + BuildConfig.VERSION_NAME);

        binding.llUserProfile.setOnClickListener(view13 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(getActivity(), UserProfileActivity.class);
            startActivity(i);
            getActivity().overridePendingTransition(0, 0);
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

        binding.llFavorites.setOnClickListener(view12 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), LikeActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
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
                getActivity().finish();
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

        binding.llResource.setOnClickListener(view17 -> {
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

        binding.llRefer.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), ReferFriendActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
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
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llSupport.setOnClickListener(view18 -> {
            supportDialog = new Dialog(getActivity());
            supportDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            supportDialog.setContentView(R.layout.support_layout);
            supportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            supportDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final TextView tvEmail = supportDialog.findViewById(R.id.tvEmail);
            final LinearLayout llClose = supportDialog.findViewById(R.id.llClose);
            tvEmail.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] recipients = {"support@brainwellnessapp.com"};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                intent.putExtra(Intent.EXTRA_CC, "");
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                try {
                    startActivity(Intent.createChooser(intent, "Send mail"));
                } catch (android.content.ActivityNotFoundException ex) {
                    BWSApplication.showToast("There are no email clients installed.", getActivity());
                }
            });

            supportDialog.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    supportDialog.dismiss();
                    return true;
                }
                return false;
            });

            llClose.setOnClickListener(v -> supportDialog.dismiss());
            supportDialog.show();
            supportDialog.setCancelable(false);
        });

        binding.llLogout.setOnClickListener(view19 -> {
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
                    BWSApplication.showProgressBar(progressBar, progressBarHolder, getActivity());
                    relesePlayer(getActivity());
                    DeleteCall(logoutDialog, progressBar, progressBarHolder);
                });

                tvGoBack.setOnClickListener(v -> logoutDialog.hide());
                logoutDialog.show();
                logoutDialog.setCancelable(false);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        showTooltips();
        return view;
    }

    private void callLogoutApi(Dialog dialog, ProgressBar progressBar, FrameLayout progressBarHolder) {
        SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE);
        String fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "");
        if (TextUtils.isEmpty(fcm_id)) {
            FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(getActivity(), task -> {
                String newToken = task.getResult().getToken();
                Log.e("newToken", newToken);
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE).edit();
                editor.putString(CONSTANTS.Token, newToken); //Friend
                editor.apply();
                editor.commit();
            });
            fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "");
        }

        Call<LogoutModel> listCall = APIClient.getClient().getLogout(UserID, fcm_id, CONSTANTS.FLAG_ONE);
        listCall.enqueue(new Callback<LogoutModel>() {
            @Override
            public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                LogoutModel loginModel = response.body();
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                        dialog.hide();
                        analytics.flush();
                        analytics.reset();
                        Properties p1 = new Properties();
                        p1.putValue("userId", UserID);
                        p1.putValue("deviceId", DeviceID);
                        p1.putValue("deviceType", "Android");
                        p1.putValue("userName", Name);
                        p1.putValue("mobileNo", MobileNo);
                        BWSApplication.addToSegment("Signed Out", p1, CONSTANTS.track);
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LogoutModel> call, Throwable t) {
                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            }
        });

    }

    @Override
    public void onResume() {
        ComeScreenAccount = 1;
        comefromDownload = "0";
        profileViewData(getActivity());
        super.onResume();
    }

    private void showTooltips() {
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        AccountFirstLogin = (shared1.getString(CONSTANTS.PREF_KEY_AccountFirstLogin, "0"));

        if (AccountFirstLogin.equalsIgnoreCase("1")) {
            Animation enterAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_top);
            Animation exitAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_bottom);
            fancyShowCaseView11 = new FancyShowCaseView.Builder(getActivity())
                    .customView(R.layout.layout_account_downloads, view -> {
                        RelativeLayout rlNext = view.findViewById(R.id.rlNext);
                        rlNext.setOnClickListener(v -> fancyShowCaseView11.hide());
                    }).focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .enterAnimation(enterAnimation).exitAnimation(exitAnimation)
                    .focusOn(binding.llDownloads).closeOnTouch(false).build();

            fancyShowCaseView21 = new FancyShowCaseView.Builder(getActivity())
                    .customView(R.layout.layout_account_billingorder, view -> {
                        RelativeLayout rlNext = view.findViewById(R.id.rlNext);
                        rlNext.setOnClickListener(v -> fancyShowCaseView21.hide());
                    }).focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .enterAnimation(enterAnimation)
                    .exitAnimation(exitAnimation).focusOn(binding.llBillingOrder)
                    .closeOnTouch(false).build();

            fancyShowCaseView31 = new FancyShowCaseView.Builder(getActivity())
                    .customView(R.layout.layout_account_resources, view -> {
                        RelativeLayout rlDone = view.findViewById(R.id.rlDone);
                        rlDone.setOnClickListener(v -> {
                            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString(CONSTANTS.PREF_KEY_AccountFirstLogin, "0");
                            editor.commit();
                            invoiceToRecepit = 0;
                            fancyShowCaseView31.hide();
                            tutorial = true;
                        });
                    }).focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .enterAnimation(enterAnimation).exitAnimation(exitAnimation)
                    .focusOn(binding.llResource).closeOnTouch(false).build();

            queue = new FancyShowCaseQueue()
                    .add(fancyShowCaseView11)
                    .add(fancyShowCaseView21)
                    .add(fancyShowCaseView31);
            queue.show();

            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        } else {
            OnBackPressedCallback callback = new OnBackPressedCallback(false) {
                @Override
                public void handleOnBackPressed() {

                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        }
        tutorial = false;
    }

    void DeleteCall(Dialog dialog, ProgressBar progressBar, FrameLayout progressBarHolder) {
        myAudioId = "";
        isDisclaimer = 0;
        addToRecentPlayId = "";
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGOUT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorcv = shared.edit();
        editorcv.putString(CONSTANTS.PREF_KEY_LOGOUT_UserID, UserID);
        editorcv.putString(CONSTANTS.PREF_KEY_LOGOUT_MobileNO, MobileNo);
        editorcv.commit();

        Log.e("Old UserId MobileNo", UserID + "....." + MobileNo);

        SharedPreferences preferences = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(CONSTANTS.PREF_KEY_UserID);
        edit.remove(CONSTANTS.PREF_KEY_MobileNo);
        edit.remove(CONSTANTS.PREF_KEY_Name);
        edit.remove(CONSTANTS.PREF_KEY_IsDisclimer);
        edit.remove(CONSTANTS.PREF_KEY_ExpDate);
        edit.remove(CONSTANTS.PREF_KEY_IsLock);
        edit.remove(CONSTANTS.PREF_KEY_PlayerFirstLogin);
        edit.remove(CONSTANTS.PREF_KEY_AudioFirstLogin);
        edit.remove(CONSTANTS.PREF_KEY_PlaylistFirstLogin);
        edit.remove(CONSTANTS.PREF_KEY_AccountFirstLogin);
        edit.remove(CONSTANTS.PREF_KEY_ReminderFirstLogin);
        edit.remove(CONSTANTS.PREF_KEY_SearchFirstLogin);
        edit.remove(CONSTANTS.PREF_KEY_Email);
        edit.remove(CONSTANTS.PREF_KEY_DeviceType);
        edit.remove(CONSTANTS.PREF_KEY_Identify);
        edit.remove(CONSTANTS.PREF_KEY_DeviceID);
//        edit.remove(CONSTANTS.PREF_KEY_UserPromocode);
//        edit.remove(CONSTANTS.PREF_KEY_ReferLink);
        edit.clear();
        edit.commit();

        SharedPreferences preferencesx = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        SharedPreferences.Editor editx = preferencesx.edit();
        editx.remove(CONSTANTS.PREF_KEY_IsRepeat);
        editx.remove(CONSTANTS.PREF_KEY_IsShuffle);
        editx.clear();
        editx.commit();
        PRDownloader.cancel(downloadIdOne);
        filename = "";
        logout = true;
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
        editorr.remove(CONSTANTS.PREF_KEY_audioList);
        editorr.remove(CONSTANTS.PREF_KEY_position);
        editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
        editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
        editorr.remove(CONSTANTS.PREF_KEY_AudioFlag);
        editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
        editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
        editorr.clear();
        editorr.commit();


        deleteCache(getActivity());
        callLogoutApi(dialog, progressBar, progressBarHolder);
    }

    void profileViewData(Context ctx) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<ProfileViewModel> listCall = APIClient.getClient().getProfileView(UserID);
            listCall.enqueue(new Callback<ProfileViewModel>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<ProfileViewModel> call, Response<ProfileViewModel> response) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        ProfileViewModel viewModel = response.body();
                        binding.tvViewProfile.setVisibility(View.VISIBLE);
                        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_Referral, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString(CONSTANTS.PREF_KEY_UserPromocode, viewModel.getResponseData().getUserReferCode());
                        editor.putString(CONSTANTS.PREF_KEY_ReferLink, viewModel.getResponseData().getReferLink());
                        editor.commit();
                        if (viewModel.getResponseData().getName().equalsIgnoreCase("") ||
                                viewModel.getResponseData().getName().equalsIgnoreCase(" ") ||
                                viewModel.getResponseData().getName() == null) {
                            binding.tvName.setText(R.string.Guest);
                            UserName = "Guest";
                            String Letter = "G";
                            String profilePicPath = viewModel.getResponseData().getImage();
                            if (profilePicPath.equalsIgnoreCase("")) {
                                binding.civProfile.setVisibility(View.GONE);
                                binding.rlLetter.setVisibility(View.VISIBLE);
                                binding.tvLetter.setText(Letter);
                            } else {
                                binding.civProfile.setVisibility(View.VISIBLE);
                                binding.rlLetter.setVisibility(View.GONE);
                                Glide.with(ctx).load(profilePicPath).thumbnail(0.10f)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(126))).dontAnimate().into(binding.civProfile);
                            }
                        } else {
                            binding.tvName.setText(viewModel.getResponseData().getName());
                            UserName = viewModel.getResponseData().getName();
                            String Name = viewModel.getResponseData().getName();
                            String Letter = Name.substring(0, 1);
                            String profilePicPath = viewModel.getResponseData().getImage();
                            if (profilePicPath.equalsIgnoreCase("")) {
                                binding.civProfile.setVisibility(View.GONE);
                                binding.rlLetter.setVisibility(View.VISIBLE);
                                binding.tvLetter.setText(Letter);
                            } else {
                                binding.civProfile.setVisibility(View.VISIBLE);
                                binding.rlLetter.setVisibility(View.GONE);
                                Glide.with(ctx).load(profilePicPath).thumbnail(0.10f)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(126))).dontAnimate().into(binding.civProfile);
                            }
                        }
                        IsLock = viewModel.getResponseData().getIsLock();

                        if (viewModel.getResponseData().getOrderTotal().equalsIgnoreCase("")) {
                            binding.tvCrtPlan.setText("Premium Team Plan one");
                        } else {
                            if (viewModel.getResponseData().getPlanperiod().equalsIgnoreCase("")) {
                                binding.tvCrtPlan.setText("Current plan: " + viewModel.getResponseData().getOrderTotal());
                            } else {
                                binding.tvCrtPlan.setText("Current plan: " + viewModel.getResponseData().getOrderTotal() + " / " + viewModel.getResponseData().getPlanperiod());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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