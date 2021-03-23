package com.brainwellnessspa.DashboardModule.Audio;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BuildConfig;
import com.brainwellnessspa.DashboardModule.Audio.Adapters.DownloadAdapter;
import com.brainwellnessspa.DashboardModule.Audio.Adapters.LibraryAdapter;
import com.brainwellnessspa.DashboardModule.Audio.Adapters.PopularPlayedAdapter;
import com.brainwellnessspa.DashboardModule.Audio.Adapters.RecentlyPlayedAdapter;
import com.brainwellnessspa.DashboardModule.Audio.Adapters.RecommendedAdapter;
import com.brainwellnessspa.DashboardModule.Audio.Adapters.TopCategoriesAdapter;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.Models.UnlockAudioList;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.UserModuleTwo.Activities.SplashActivity;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentAudioBinding;
import com.brainwellnessspa.databinding.MainAudioLayoutBinding;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.OnViewInflateListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DashboardModule.Audio.ViewAllAudioFragment.viewallAudio;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;
import static com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity.invoiceToRecepit;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.getSpace;
import static com.brainwellnessspa.SplashModule.SplashScreenActivity.analytics;

public class AudioFragment extends Fragment {
    public static String IsLock = "0";
    FragmentAudioBinding binding;
    String UserID, AudioFlag, expDate, AudioFirstLogin = "0";
    boolean Identify = false, AgainIdentify = false;
    long mySpace = 0;
    List<String> fileNameList = new ArrayList<>(), audioFile = new ArrayList<>(), playlistDownloadId = new ArrayList<>();
    FancyShowCaseView fancyShowCaseView1, fancyShowCaseView2, fancyShowCaseView3;
    FancyShowCaseQueue queue;
    MainAudioListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio, container, false);
        View view = binding.getRoot();
        viewallAudio = false;
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        FirebaseCrashlytics.getInstance().setUserId(UserID);
        ComeScreenAccount = 0;
        comefromDownload = "0";
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvMainAudioList.setLayoutManager(manager);
        binding.rvMainAudioList.setItemAnimator(new DefaultItemAnimator());
        mySpace = getSpace();
        prepareDisplayData("onCreateView");

        if (!isDownloading) {
            if (BWSApplication.isNetworkConnected(getActivity())) {
                SharedPreferences sharedx = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
                Gson gson = new Gson();
                String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
                String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
                String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
                if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    fileNameList = gson.fromJson(json, type);
                    audioFile = gson.fromJson(json1, type);
                    playlistDownloadId = gson.fromJson(json2, type);
                    /*fileNameList = new ArrayList<>();
                    audioFile = new ArrayList<>();
                    playlistDownloadId = new ArrayList<>();
                    SharedPreferences sharedxxx = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedxxx.edit();
                    String nameJson = gson.toJson(fileNameList);
                    String urlJson = gson.toJson(audioFile);
                    String playlistIdJson = gson.toJson(playlistDownloadId);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                    editor.commit();*/
                    if (fileNameList.size() != 0) {
                        isDownloading = true;
                        DownloadMedia downloadMedia = new DownloadMedia(getActivity().getApplicationContext());
                        downloadMedia.encrypt1(audioFile, fileNameList, playlistDownloadId/*, playlistSongs*/);
                    }
                }
            }
        }

        binding.tvExplore.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), SplashActivity.class);
            startActivity(i);
        });
        showTooltips();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (requestCode == 15695) {
                PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
                boolean isIgnoringBatteryOptimizations = false;
                isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getActivity().getPackageName());
                if (isIgnoringBatteryOptimizations) {
                    // Ignoring battery optimization

                } else {
                    // Not ignoring battery optimization

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        ComeScreenAccount = 0;
        comefromDownload = "0";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getActivity().getPackageName();
            PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
            boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(packageName);
            if (!isIgnoringBatteryOptimizations) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivityForResult(intent, 15695);
            }
        }
        prepareDisplayData("onResume");
        prepareData();
        super.onResume();
    }

    private void prepareDisplayData(String comeFrom) {
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<MainAudioModel> listCall = APIClient.getClient().getMainAudioLists(UserID);
            listCall.enqueue(new Callback<MainAudioModel>() {
                @Override
                public void onResponse(Call<MainAudioModel> call, Response<MainAudioModel> response) {
                    MainAudioModel listModel = response.body();
                    try {
                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                            IsLock = listModel.getResponseData().get(0).getIsLock();
                            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString(CONSTANTS.PREF_KEY_ExpDate, listModel.getResponseData().get(0).getExpireDate());
                            editor.putString(CONSTANTS.PREF_KEY_IsLock, listModel.getResponseData().get(0).getIsLock());
                            editor.commit();
//                        adapter = new MainAudioListAdapter(listModel.getResponseData(),getActivity());
//                        binding.rvMainAudioList.setAdapter(adapter);
//                        GetAllMedia(getActivity(), listModel.getResponseData());
                            ArrayList<String> section = new ArrayList<>();
                            for (int i = 0; i < listModel.getResponseData().size(); i++) {
                                section.add(listModel.getResponseData().get(i).getView());
                            }
                            if (comeFrom.equalsIgnoreCase("onResume")) {
                                Properties p = new Properties();
                                p.putValue("userId", UserID);
                                Gson gson;
                                GsonBuilder gsonBuilder = new GsonBuilder();
                                gson = gsonBuilder.create();
                                p.putValue("sections", gson.toJson(section));
                                p.putValue("device space", mySpace);
                                BWSApplication.addToSegment("Explore Screen Viewed", p, CONSTANTS.screen);
                            }
                            callObserverMethod(listModel.getResponseData());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<MainAudioModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    private void callObserverMethod(List<MainAudioModel.ResponseData> listModel) {
        DatabaseClient
                .getInstance(getActivity())
                .getaudioDatabase()
                .taskDao()
                .geAllDataz("").observe(getActivity(), downloadAudioDetails -> {
            ArrayList<MainAudioModel.ResponseData.Detail> details = new ArrayList<>();

            if (downloadAudioDetails.size() != 0) {
                for (int i = 0; i < downloadAudioDetails.size(); i++) {
                    MainAudioModel.ResponseData.Detail detail = new MainAudioModel.ResponseData.Detail();
                    detail.setID(downloadAudioDetails.get(i).getID());
                    detail.setName(downloadAudioDetails.get(i).getName());
                    detail.setAudioFile(downloadAudioDetails.get(i).getAudioFile());
                    detail.setAudioDirection(downloadAudioDetails.get(i).getAudioDirection());
                    detail.setAudiomastercat(downloadAudioDetails.get(i).getAudiomastercat());
                    detail.setAudioSubCategory(downloadAudioDetails.get(i).getAudioSubCategory());
                    detail.setImageFile(downloadAudioDetails.get(i).getImageFile());
                    detail.setLike(downloadAudioDetails.get(i).getLike());
                    detail.setDownload(downloadAudioDetails.get(i).getDownload());
                    detail.setAudioDuration(downloadAudioDetails.get(i).getAudioDuration());
                    details.add(detail);
                }
                for (int i = 0; i < listModel.size(); i++) {
                    if (listModel.get(i).getView().equalsIgnoreCase("My Downloads")) {
                        listModel.get(i).setDetails(details);
                    }
                }
                adapter = new MainAudioListAdapter(listModel, getActivity());
                binding.rvMainAudioList.setAdapter(adapter);
            } else {
                if (BWSApplication.isNetworkConnected(getActivity())) {
                    adapter = new MainAudioListAdapter(listModel, getActivity());
                    binding.rvMainAudioList.setAdapter(adapter);
                }
            }
        });
    }

    private void prepareData() {
        if (BWSApplication.isNetworkConnected(getActivity())) {
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

                SharedPreferences sharedPreferences3 = getActivity().getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE);
                fcm_id = sharedPreferences3.getString(CONSTANTS.Token, "");
            }
            String deviceid = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

            Call<UnlockAudioList> listCall1 = APIClient.getClient().getUnLockAudioList(UserID, fcm_id, CONSTANTS.FLAG_ONE, deviceid, String.valueOf(BuildConfig.VERSION_CODE));
            listCall1.enqueue(new Callback<UnlockAudioList>() {
                @Override
                public void onResponse(Call<UnlockAudioList> call, Response<UnlockAudioList> response) {
                    UnlockAudioList listModel = response.body();
                    try {
                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            IsLock = listModel.getResponseData().getIsLock();
                            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString(CONSTANTS.PREF_KEY_IsLock, listModel.getResponseData().getIsLock());
                            editor.putString(CONSTANTS.PREF_KEY_IsDisclimer, listModel.getResponseData().getShouldPlayDisclaimer());
                            Gson gson = new Gson();
                            editor.putString(CONSTANTS.PREF_KEY_UnLockAudiList, gson.toJson(listModel.getResponseData().getID()));

                            SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
                            Identify = (shared1.getBoolean(CONSTANTS.PREF_KEY_Identify, false));
                            AgainIdentify = (shared1.getBoolean(CONSTANTS.PREF_KEY_IdentifyAgain, false));
                            if (!Identify || !AgainIdentify) {
                                String Uname = "";
                                if (listModel.getResponseData().getUserData().getName().equalsIgnoreCase("")) {
                                    Uname = "Guest";
                                } else {
                                    Uname = listModel.getResponseData().getUserData().getName();
                                }
                                analytics.identify(new Traits()
                                        .putEmail(listModel.getResponseData().getUserData().getEmail())
                                        .putName(Uname)
                                        .putPhone(listModel.getResponseData().getUserData().getPhoneNumber())
                                        .putValue("userId", UserID)
                                        .putValue("id", UserID)
                                        .putValue("deviceId", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID))
                                        .putValue("deviceType", "Android")
                                        .putValue("countryCode", listModel.getResponseData().getUserData().getCountryCode())
                                        .putValue("countryName", "")
                                        .putValue("name", Uname)
                                        .putValue("phone", listModel.getResponseData().getUserData().getPhoneNumber())
                                        .putValue("email", listModel.getResponseData().getUserData().getEmail())
                                        .putValue("plan", listModel.getResponseData().getUserData().getPlan())
                                        .putValue("planStatus", listModel.getResponseData().getUserData().getPlanStatus())
                                        .putValue("planStartDt", listModel.getResponseData().getUserData().getPlanStartDt())
                                        .putValue("planExpiryDt", listModel.getResponseData().getUserData().getPlanExpiryDate())
                                        .putValue("clinikoId", listModel.getResponseData().getUserData().getClinikoId()));
                                editor.putBoolean(CONSTANTS.PREF_KEY_Identify, true);
                                editor.putBoolean(CONSTANTS.PREF_KEY_IdentifyAgain, true);
                                Identify = true;
                                AgainIdentify = true;
                            }
                            editor.commit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<UnlockAudioList> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            expDate = (shared1.getString(CONSTANTS.PREF_KEY_ExpDate, ""));
//            expDate = "2020-09-29 06:34:10";
            Log.e("Exp Date !!!!", expDate);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date Expdate = new Date();
            try {
                Expdate = format.parse(expDate);
                Log.e("Exp Date Expdate!!!!", String.valueOf(Expdate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date currdate = Calendar.getInstance().getTime();
            Date currdate1 = new Date();
            String currantDateTime = simpleDateFormat1.format(currdate);
            try {
                currdate1 = format.parse(currantDateTime);
                Log.e("currant currdate !!!!", String.valueOf(currdate1));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.e("currant Date !!!!", currantDateTime);
            if (Expdate.before(currdate1)) {
                Log.e("app", "Date1 is before Date2");
                IsLock = "1";
            } else if (Expdate.after(currdate1)) {
                Log.e("app", "Date1 is after Date2");
                IsLock = "0";
            } else if (Expdate == currdate1) {
                Log.e("app", "Date1 is equal Date2");
                IsLock = "1";
            }
            ArrayList<MainAudioModel.ResponseData> responseData = new ArrayList<>();
            ArrayList<MainAudioModel.ResponseData.Detail> details = new ArrayList<>();
            MainAudioModel.ResponseData listModel = new MainAudioModel.ResponseData();
            listModel.setHomeID("1");
            listModel.setDetails(details);
            listModel.setView("My Downloads");
            listModel.setHomeID("1");
            listModel.setUserID(UserID);
            listModel.setIsLock(IsLock);
            responseData.add(listModel);
            callObserverMethod(responseData);
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }

        try {
            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
            globalInitExoPlayer.UpdateMiniPlayer(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

        if (!AudioFlag.equalsIgnoreCase("0")) {
            Fragment fragment = new MiniPlayerFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flContainer, fragment)
                    .commit();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 6, 0, 260);
            binding.llSpace.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 6, 0, 50);
            binding.llSpace.setLayoutParams(params);
        }
    }

    private void showTooltips() {
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        AudioFirstLogin = (shared1.getString(CONSTANTS.PREF_KEY_AudioFirstLogin, "0"));
        if (AudioFirstLogin.equalsIgnoreCase("1") && invoiceToRecepit != 1) {
            Animation enterAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_top);
            Animation exitAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_bottom);

            fancyShowCaseView1 = new FancyShowCaseView.Builder(getActivity())
                    .customView(R.layout.layout_audio_librarys, view -> {
                        ImageView ivLibraryImage = view.findViewById(R.id.ivLibraryImage);
                        RelativeLayout rlNext = view.findViewById(R.id.rlNext);
                        final ValueAnimator anim = ValueAnimator.ofFloat(0.9f, 1f);
                        anim.setDuration(1500);
                        anim.addUpdateListener(animation -> {
                            ivLibraryImage.setScaleX((Float) animation.getAnimatedValue());
                            ivLibraryImage.setScaleY((Float) animation.getAnimatedValue());
                        });
                        anim.setRepeatCount(ValueAnimator.INFINITE);
                        anim.setRepeatMode(ValueAnimator.REVERSE);
                        anim.start();
                        rlNext.setOnClickListener(v -> fancyShowCaseView1.hide());

                      /*  MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                                1, 1, 1f, 10);
                        ivLibraryImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                        ivLibraryImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                        ivLibraryImage.setScaleType(ImageView.ScaleType.FIT_XY);
                         Glide.with(getActivity()).load(R.drawable.audio_main_icon).thumbnail(0.05f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(ivLibraryImage);*/
                   /* RelativeLayout rlShowMeHow = view.findViewById(R.id.rlShowMeHow);
                    RelativeLayout rlNoThanks = view.findViewById(R.id.rlNoThanks);
                    rlShowMeHow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fancyShowCaseView11.hide();
                        }
                    });
                    rlNoThanks.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            queue.cancel(true);
                        }
                    });*/

                    }).focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .enterAnimation(enterAnimation).exitAnimation(exitAnimation)
                    .closeOnTouch(false).build();

            fancyShowCaseView2 = new FancyShowCaseView.Builder(getActivity())
                    .customView(R.layout.layout_audio_addplaylist, (OnViewInflateListener) view -> {
                        RelativeLayout rlNext = view.findViewById(R.id.rlNext);
                        ImageView ivLibraryImage = view.findViewById(R.id.ivLibraryImage);
                        final ValueAnimator anim = ValueAnimator.ofFloat(0.9f, 1f);
                        anim.setDuration(1500);
                        anim.addUpdateListener(animation -> {
                            ivLibraryImage.setScaleX((Float) animation.getAnimatedValue());
                            ivLibraryImage.setScaleY((Float) animation.getAnimatedValue());
                        });
                        anim.setRepeatCount(ValueAnimator.INFINITE);
                        anim.setRepeatMode(ValueAnimator.REVERSE);
                        anim.start();

                        rlNext.setOnClickListener(v -> fancyShowCaseView2.hide());
                    }).focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .enterAnimation(enterAnimation)
                    .exitAnimation(exitAnimation)
                    .closeOnTouch(false).build();

            fancyShowCaseView3 = new FancyShowCaseView.Builder(getActivity())
                    .customView(R.layout.layout_audio_categories, view -> {
                        RelativeLayout rlDone = view.findViewById(R.id.rlDone);
                        ImageView ivLibraryImage = view.findViewById(R.id.ivLibraryImage);
                        final ValueAnimator anim = ValueAnimator.ofFloat(0.9f, 1f);
                        anim.setDuration(1500);
                        anim.addUpdateListener(animation -> {
                            ivLibraryImage.setScaleX((Float) animation.getAnimatedValue());
                            ivLibraryImage.setScaleY((Float) animation.getAnimatedValue());
                        });
                        anim.setRepeatCount(ValueAnimator.INFINITE);
                        anim.setRepeatMode(ValueAnimator.REVERSE);
                        anim.start();
                        rlDone.setOnClickListener(v -> {
                            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString(CONSTANTS.PREF_KEY_AudioFirstLogin, "0");
                            editor.commit();
                            fancyShowCaseView3.hide();
                        });
                    })
                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .enterAnimation(enterAnimation).exitAnimation(exitAnimation)
                    .closeOnTouch(false).build();

            queue = new FancyShowCaseQueue()
                    .add(fancyShowCaseView1)
                    .add(fancyShowCaseView2)
                    .add(fancyShowCaseView3);
            queue.show();
        }

    }

    public class MainAudioListAdapter extends RecyclerView.Adapter<MainAudioListAdapter.MyViewHolder> {
        FragmentActivity activity;
        List<MainAudioModel.ResponseData> model;

        public MainAudioListAdapter(List<MainAudioModel.ResponseData> model, FragmentActivity activity) {
            this.model = model;
            this.activity = activity;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MainAudioLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.main_audio_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvViewAll.setOnClickListener(view -> {
                Fragment viewAllAudioFragment = new ViewAllAudioFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.flContainer, viewAllAudioFragment)
                        .commit();
                Bundle bundle = new Bundle();
                bundle.putString("ID", model.get(position).getHomeID());
                bundle.putString("Name", model.get(position).getView());
                bundle.putString("Category", "");
                viewAllAudioFragment.setArguments(bundle);
            });

            if (model.get(position).getDetails().size() == 0) {
                holder.binding.llMainLayout.setVisibility(View.GONE);
            } else {
                holder.binding.llMainLayout.setVisibility(View.VISIBLE);
                holder.binding.tvTitle.setText(model.get(position).getView());
                if (model.get(position).getView().equalsIgnoreCase("My Downloads")) {
                    RecyclerView.LayoutManager myDownloads = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(myDownloads);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    DownloadAdapter myDownloadsAdapter = new DownloadAdapter(model.get(position).getDetails(), getActivity(), activity);
                    holder.binding.rvMainAudio.setAdapter(myDownloadsAdapter);
                    if (model.get(position).getDetails() != null &&
                            model.get(position).getDetails().size() > 4) {
                        holder.binding.tvViewAll.setVisibility(View.VISIBLE);
                    } else {
                        holder.binding.tvViewAll.setVisibility(View.GONE);
                    }
                } else if (model.get(position).getView().equalsIgnoreCase(getString(R.string.Library))) {
                    LibraryAdapter recommendedAdapter = new LibraryAdapter(model.get(position).getDetails(), getActivity(), activity, model.get(position).getView());
                    RecyclerView.LayoutManager recommended = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recommended);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recommendedAdapter);
                    if (model.get(position).getDetails() != null &&
                            model.get(position).getDetails().size() > 4) {
                        holder.binding.tvViewAll.setVisibility(View.VISIBLE);
                    } else {
                        holder.binding.tvViewAll.setVisibility(View.GONE);
                    }
                } else if (model.get(position).getView().equalsIgnoreCase(getString(R.string.my_like))) {
                    holder.binding.llMainLayout.setVisibility(View.GONE);
                    /*RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(model.get(position).getDetails(), getActivity());
                    RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recentlyPlayed);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recentlyPlayedAdapter);*/
                } else if (model.get(position).getView().equalsIgnoreCase(getString(R.string.recently_played))) {
                    RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(model.get(position).getDetails(), getActivity(), activity, model.get(position).getView());
                    RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recentlyPlayed);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recentlyPlayedAdapter);
                    if (model.get(position).getDetails() != null &&
                            model.get(position).getDetails().size() > 6) {
                        holder.binding.tvViewAll.setVisibility(View.VISIBLE);
                    } else {
                        holder.binding.tvViewAll.setVisibility(View.GONE);
                    }
                } else if (model.get(position).getView().equalsIgnoreCase(getString(R.string.get_inspired))) {
                    RecommendedAdapter recommendedAdapter = new RecommendedAdapter(model.get(position).getDetails(), getActivity(), activity, model.get(position).getView());
                    RecyclerView.LayoutManager inspired = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(inspired);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recommendedAdapter);
                    if (model.get(position).getDetails() != null &&
                            model.get(position).getDetails().size() > 4) {
                        holder.binding.tvViewAll.setVisibility(View.VISIBLE);
                    } else {
                        holder.binding.tvViewAll.setVisibility(View.GONE);
                    }
                } else if (model.get(position).getView().equalsIgnoreCase(getString(R.string.popular))) {
                    PopularPlayedAdapter popularPlayedAdapter = new PopularPlayedAdapter(model.get(position).getDetails(), getActivity(), activity, model.get(position).getView());
                    RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recentlyPlayed);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(popularPlayedAdapter);
                    if (model.get(position).getDetails() != null &&
                            model.get(position).getDetails().size() > 6) {
                        holder.binding.tvViewAll.setVisibility(View.VISIBLE);
                    } else {
                        holder.binding.tvViewAll.setVisibility(View.GONE);
                    }
                } else if (model.get(position).getView().equalsIgnoreCase(getString(R.string.top_categories))) {
                    holder.binding.tvViewAll.setVisibility(View.GONE);
                    TopCategoriesAdapter topCategoriesAdapter = new TopCategoriesAdapter(model.get(position).getDetails(), getActivity(), activity,
                            model.get(position).getHomeID(), model.get(position).getView());
                    RecyclerView.LayoutManager topCategories = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(topCategories);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(topCategoriesAdapter);
                }
            }
        }

        @Override
        public int getItemCount() {
            return model.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            MainAudioLayoutBinding binding;

            public MyViewHolder(MainAudioLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}