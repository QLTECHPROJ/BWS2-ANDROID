package com.brainwellnessspa.DashboardModule.Audio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DashboardModule.Activities.AddPlaylistActivity;
import com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity;
import com.brainwellnessspa.DashboardModule.Audio.Adapters.DownloadAdapter;
import com.brainwellnessspa.DashboardModule.Audio.Adapters.LibraryAdapter;
import com.brainwellnessspa.DashboardModule.Audio.Adapters.PopularPlayedAdapter;
import com.brainwellnessspa.DashboardModule.Audio.Adapters.RecentlyPlayedAdapter;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.UnlockAudioList;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.BigBoxLayoutBinding;
import com.brainwellnessspa.databinding.FragmentAudioBinding;
import com.brainwellnessspa.databinding.MainAudioLayoutBinding;
import com.brainwellnessspa.databinding.RoundBoxLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.DashboardModule.Audio.ViewAllAudioFragment.viewallAudio;
import static com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment.disclaimerPlayed;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;

import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease;

public class AudioFragment extends Fragment {
    public static boolean exit = false;
    public static String IsLock = "0";
    FragmentAudioBinding binding;
    String UserID, AudioFlag, expDate;
    List<String> fileNameList;
    FancyShowCaseView fancyShowCaseView11, fancyShowCaseView21, fancyShowCaseView31;
    FancyShowCaseQueue queue;
    List<String> audioFile, playlistDownloadId;
    List<DownloadAudioDetails> downloadAudioDetailsList;
    MainAudioListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio, container, false);
        View view = binding.getRoot();
        viewallAudio = false;
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        ComeScreenAccount = 0;
        comefromDownload = "0";
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvMainAudioList.setLayoutManager(manager);
        binding.rvMainAudioList.setItemAnimator(new DefaultItemAnimator());
        prepareDisplayData();
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

            /*if(json2.equalsIgnoreCase(String.valueOf(gson))){
                playlistDownloadId = new ArrayList<>();
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                String playlistIdJson = gson.toJson(playlistDownloadId);
                editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                editor.commit();
                SharedPreferences sharedy = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
                json2 = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
            }*/
                    playlistDownloadId = gson.fromJson(json2, type);
                    if (fileNameList.size() != 0) {
                        DownloadMedia downloadMedia = new DownloadMedia(getActivity().getApplicationContext());
                        downloadMedia.encrypt1(audioFile, fileNameList, playlistDownloadId/*, playlistSongs*/);
                    }
                }
            }
        } else {
//            getRemainDownloads();
        }
//        showTooltiop();
        return view;
    }

    /* private void getRemainDownloads() {
         DatabaseClient.getInstance(getActivity())
                 .getaudioDatabase()
                 .taskDao()
                 .getDownloadProgressRemain(100).observe(getActivity(), audiolist -> {
             List<String> url = new ArrayList<>();
             List<String> name = new ArrayList<>();
             List<String> downloadPlaylistId = new ArrayList<>();

             for (int x = 0; x < audiolist.size(); x++) {
                 name.add(audiolist.get(x).getName());
                 url.add(audiolist.get(x).getAudioFile());
                 downloadPlaylistId.add(audiolist.get(x).getPlaylistId());
             }
             SharedPreferences sharedx = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
             Gson gson1 = new Gson();
             String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson1));
             String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson1));
             String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson1));
             if (!json1.equalsIgnoreCase(String.valueOf(gson1))) {
                 Type type = new TypeToken<List<String>>() {
                 }.getType();
                 List<String> fileNameList = gson1.fromJson(json, type);
                 List<String> audioFile = gson1.fromJson(json1, type);
                 List<String> playlistId1 = gson1.fromJson(json2, type);
                 if (fileNameList.size() != 0) {
                     url.addAll(audioFile);
                     name.addAll(fileNameList);
                     downloadPlaylistId.addAll(playlistId1);
                 }
             }

             if (url.size() != 0) {
                 DownloadMedia downloadMedia = new DownloadMedia(getActivity().getApplicationContext());
                 downloadMedia.encrypt1(url, name, downloadPlaylistId*//*, playlistSongs*//*);
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();
                String urlJson = gson.toJson(url);
                String nameJson = gson.toJson(name);
                String playlistIdJson = gson.toJson(downloadPlaylistId);
                fileNameList = name;
                playlistDownloadId = downloadPlaylistId;
                editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                editor.commit();
            }
        });
    }
*/
    @Override
    public void onResume() {
        super.onResume();
        prepareDisplayData();
        prepareData();
        ComeScreenAccount = 0;
        comefromDownload = "0";
    }

 /*   public void GetAllMedia(FragmentActivity ctx, List<MainAudioModel.ResponseData> listModel) {
        ArrayList<MainAudioModel.ResponseData.Detail> details = new ArrayList<>();
        downloadAudioDetailsList = new ArrayList<>();
        class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllData("");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (downloadAudioDetailsList.size() != 0) {
                    for (int i = 0; i < downloadAudioDetailsList.size(); i++) {
                        MainAudioModel.ResponseData.Detail detail = new MainAudioModel.ResponseData.Detail();
                        detail.setID(downloadAudioDetailsList.get(i).getID());
                        detail.setName(downloadAudioDetailsList.get(i).getName());
                        detail.setAudioFile(downloadAudioDetailsList.get(i).getAudioFile());
                        detail.setAudioDirection(downloadAudioDetailsList.get(i).getAudioDirection());
                        detail.setAudiomastercat(downloadAudioDetailsList.get(i).getAudiomastercat());
                        detail.setAudioSubCategory(downloadAudioDetailsList.get(i).getAudioSubCategory());
                        detail.setImageFile(downloadAudioDetailsList.get(i).getImageFile());
                        detail.setLike(downloadAudioDetailsList.get(i).getLike());
                        detail.setDownload(downloadAudioDetailsList.get(i).getDownload());
                        detail.setAudioDuration(downloadAudioDetailsList.get(i).getAudioDuration());
                        details.add(detail);
                    }
                    for (int i = 0; i < listModel.size(); i++) {
                        if (listModel.get(i).getView().equalsIgnoreCase("My Downloads")) {
                            listModel.get(i).setDetails(details);
                        }
                    }
                    adapter = new MainAudioListAdapter(listModel,getActivity());
                    binding.rvMainAudioList.setAdapter(adapter);
                }*//* else {
                    adapter = new MainAudioListAdapter(getActivity());
                    binding.rvMainAudioList.setAdapter(adapter);
                }*//*
     *//*if (downloadAudioDetailsList.size() != 0) {
                    MainAudioListAdapter1 adapter1 = new MainAudioListAdapter1(getActivity(),listModel);
                    RecyclerView.LayoutManager manager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    binding.rvMainAudioList.setLayoutManager(manager1);
                    binding.rvMainAudioList.setItemAnimator(new DefaultItemAnimator());
                    binding.rvMainAudioList.setAdapter(adapter1);
                } else {
                    binding.rvMainAudioList.setVisibility(View.GONE);
                }*//*
                super.onPostExecute(aVoid);
            }
        }

        GetTask st = new GetTask();
        st.execute();
    }*/

    private void prepareDisplayData() {
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
                .geAllData1("").observe(getActivity(), downloadAudioDetails -> {
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
            Call<UnlockAudioList> listCall1 = APIClient.getClient().getUnLockAudioList(UserID);
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
                            Gson gson = new Gson();
                            editor.putString(CONSTANTS.PREF_KEY_UnLockAudiList, gson.toJson(listModel.getResponseData().getID()));
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
            listModel.setDetails(details);/*
            "UserID": "2",
            "IsLock": "0",*/
            listModel.setView("My Downloads");
            listModel.setHomeID("1");
            listModel.setUserID(UserID);
            listModel.setIsLock(IsLock);
            responseData.add(listModel);
//            GetAllMedia(getActivity(), responseData);
            callObserverMethod(responseData);
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
        try {
            SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

            SharedPreferences shared2 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
            Gson gson1 = new Gson();
            Type type1 = new TypeToken<List<String>>() {
            }.getType();
            List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
            if (!IsLock.equalsIgnoreCase("0") && (AudioFlag.equalsIgnoreCase("MainAudioList")
                    || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
                String audioID = "";
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
                Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                }.getType();
                ArrayList<MainPlayModel> arrayList = gson.fromJson(json, type);

                if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                    arrayList.remove(0);
                }
                audioID = arrayList.get(0).getID();

                if (UnlockAudioList.contains(audioID)) {

                } else {
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
                    callNewPlayerRelease();

                }

            } else if (!IsLock.equalsIgnoreCase("0") && !AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
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
                callNewPlayerRelease();

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTooltiop() {
        Animation enterAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_top);
        Animation exitAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_bottom);

        fancyShowCaseView11 = new FancyShowCaseView.Builder(getActivity())
                .customView(R.layout.layout_audio_librarys, view -> {
                    RelativeLayout rlNext = view.findViewById(R.id.rlNext);
                    rlNext.setOnClickListener(v -> fancyShowCaseView11.hide());
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

                }).closeOnTouch(false)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .enterAnimation(enterAnimation).exitAnimation(exitAnimation)
                /*.focusOn(binding.llDownloads)*/.closeOnTouch(false)
                .build();

        fancyShowCaseView21 = new FancyShowCaseView.Builder(getActivity())
                .customView(R.layout.layout_audio_addplaylist, (OnViewInflateListener) view -> {
                    RelativeLayout rlNext = view.findViewById(R.id.rlNext);
                    rlNext.setOnClickListener(v -> fancyShowCaseView21.hide());
                }).focusShape(FocusShape.ROUNDED_RECTANGLE)
                .enterAnimation(enterAnimation)
                .exitAnimation(exitAnimation)/*.focusOn(binding.llBillingOrder)*/
                .closeOnTouch(false).build();

        fancyShowCaseView31 = new FancyShowCaseView.Builder(getActivity())
                .customView(R.layout.layout_audio_categories, view -> {
                    view.findViewById(R.id.rlSearch);
                    RelativeLayout rlDone = view.findViewById(R.id.rlDone);
                    rlDone.setOnClickListener(v -> fancyShowCaseView31.hide());
                })
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .enterAnimation(enterAnimation).exitAnimation(exitAnimation)
                /*.focusOn(binding.llResource)*/.closeOnTouch(false).build();


        queue = new FancyShowCaseQueue()
                .add(fancyShowCaseView11)
                .add(fancyShowCaseView21)
                .add(fancyShowCaseView31);
        queue.show();
       /* IsRegisters = "false";
        IsRegisters1 = "false";*/
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
                    DownloadAdapter myDownloadsAdapter = new DownloadAdapter(model.get(position).getDetails(), getActivity(), activity,
                            model.get(position).getIsLock());
                    IsLock = model.get(position).getIsLock();
                    RecyclerView.LayoutManager myDownloads = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(myDownloads);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(myDownloadsAdapter);
                    if (model.get(position).getDetails() != null &&
                            model.get(position).getDetails().size() > 4) {
                        holder.binding.tvViewAll.setVisibility(View.VISIBLE);
                    } else {
                        holder.binding.tvViewAll.setVisibility(View.GONE);
                    }
                } else if (model.get(position).getView().equalsIgnoreCase(getString(R.string.Library))) {
                    LibraryAdapter recommendedAdapter = new LibraryAdapter(model.get(position).getDetails(), getActivity(), activity,
                            model.get(position).getIsLock(), model.get(position).getView());
                    IsLock = model.get(position).getIsLock();
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
                    RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(model.get(position).getDetails(), getActivity(), activity,
                            model.get(position).getIsLock(), model.get(position).getView());
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
                    RecommendedAdapter recommendedAdapter = new RecommendedAdapter(model.get(position).getDetails(), getActivity(), activity,
                            model.get(position).getIsLock(), model.get(position).getView());
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
                    PopularPlayedAdapter popularPlayedAdapter = new PopularPlayedAdapter(model.get(position).getDetails(), getActivity(), activity,
                            model.get(position).getIsLock(), model.get(position).getView());
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

    public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.MyViewHolder> {
        Context ctx;
        FragmentActivity activity;
        String IsLock, HomeView;
        int index = -1;
        private ArrayList<MainAudioModel.ResponseData.Detail> listModelList;

        public RecommendedAdapter(ArrayList<MainAudioModel.ResponseData.Detail> listModelList, Context ctx, FragmentActivity activity,
                                  String IsLock, String HomeView) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.activity = activity;
            this.IsLock = IsLock;
            this.HomeView = HomeView;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            BigBoxLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.big_box_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(listModelList.get(position).getName());
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 16,
                    1, 1, 0.44f, 10);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.tvAddToPlaylist.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.tvAddToPlaylist.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            Glide.with(ctx).load(listModelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            if (IsLock.equalsIgnoreCase("1")) {
                if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                        || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                }
            } else if (IsLock.equalsIgnoreCase("2")) {
                if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                        || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                }
            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            if (index == position) {
                holder.binding.tvAddToPlaylist.setVisibility(View.VISIBLE);
            } else
                holder.binding.tvAddToPlaylist.setVisibility(View.GONE);
            holder.binding.tvAddToPlaylist.setText("Add To Playlist");
            holder.binding.llMainLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    holder.binding.tvAddToPlaylist.setVisibility(View.VISIBLE);
                    index = position;
                    notifyDataSetChanged();
                    return true;
                }
            });
            holder.binding.tvAddToPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (IsLock.equalsIgnoreCase("1")) {
                        if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                            holder.binding.ivLock.setVisibility(View.GONE);
                            Intent i = new Intent(ctx, AddPlaylistActivity.class);
                            i.putExtra("AudioId", listModelList.get(position).getID());
                            i.putExtra("PlaylistID", "");
                            ctx.startActivity(i);
                        } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                                || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                            holder.binding.ivLock.setVisibility(View.VISIBLE);
                            Intent i = new Intent(ctx, MembershipChangeActivity.class);
                            i.putExtra("ComeFrom", "Plan");
                            ctx.startActivity(i);
                        }
                    } else if (IsLock.equalsIgnoreCase("2")) {
                        if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                            holder.binding.ivLock.setVisibility(View.GONE);
                            Intent i = new Intent(ctx, AddPlaylistActivity.class);
                            i.putExtra("AudioId", listModelList.get(position).getID());
                            i.putExtra("PlaylistID", "");
                            ctx.startActivity(i);
                        } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                                || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                            holder.binding.ivLock.setVisibility(View.VISIBLE);
                            Intent i = new Intent(ctx, MembershipChangeActivity.class);
                            i.putExtra("ComeFrom", "Plan");
                            ctx.startActivity(i);
                        }
                    } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        Intent i = new Intent(ctx, AddPlaylistActivity.class);
                        i.putExtra("AudioId", listModelList.get(position).getID());
                        i.putExtra("PlaylistID", "");
                        ctx.startActivity(i);
                    }
                }
            });

            holder.binding.llMainLayout.setOnClickListener(view -> {
//       TODO                 Active and cancelled = 0, InActive = 1, Suspeded = 2

                if (IsLock.equalsIgnoreCase("1")) {
                    if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        callnewTrans(position);
                    } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                            || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        ctx.startActivity(i);
                    }
                } else if (IsLock.equalsIgnoreCase("2")) {
                    if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        callnewTrans(position);
                    } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                            || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        BWSApplication.showToast("Please re-activate your membership plan", ctx);
                    }
                } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                    callnewTrans(position);
                }
            });
        }

        private void callnewTrans(int position) {
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String MyPlaylist = shared.getString(CONSTANTS.PREF_KEY_myPlaylist, "");
            if (audioPlay && (AudioFlag.equalsIgnoreCase("MainAudioList") ||
                    AudioFlag.equalsIgnoreCase("ViewAllAudioList")) && MyPlaylist.equalsIgnoreCase(HomeView)) {
                if (isDisclaimer == 1) {
                    BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                } else {
                    callTransFrag(position, listModelList);
                }
            } else {
                isDisclaimer = 0;
                disclaimerPlayed = 0;
                ArrayList<MainAudioModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                MainAudioModel.ResponseData.Detail mainPlayModel = new MainAudioModel.ResponseData.Detail();
                mainPlayModel.setID("0");
                mainPlayModel.setName("Disclaimer");
                mainPlayModel.setAudioFile("");
                mainPlayModel.setAudioDirection("The audio shall start playing after the disclaimer");
                mainPlayModel.setAudiomastercat("");
                mainPlayModel.setAudioSubCategory("");
                mainPlayModel.setImageFile("");
                mainPlayModel.setLike("");
                mainPlayModel.setDownload("");
                mainPlayModel.setAudioDuration("00:48");
                listModelList2.addAll(listModelList);
                listModelList2.add(position, mainPlayModel);
                callTransFrag(position, listModelList2);
            }
        }

        private void callTransFrag(int position, ArrayList<MainAudioModel.ResponseData.Detail> listModelList) {
            try {
                miniPlayer = 1;
                audioClick = true;
                callNewPlayerRelease();

            /*Fragment fragment = new MiniPlayerFragment();
            FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flContainer, fragment)
                    .commit();*/

                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();

                String json = gson.toJson(listModelList);
                editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                editor.putInt(CONSTANTS.PREF_KEY_position, position);
                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, HomeView);
                editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "MainAudioList");
                editor.commit();
                Intent i = new Intent(ctx, AudioPlayerActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                ctx.startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (4 > listModelList.size()) {
                return listModelList.size();
            } else {
                return 4;
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            BigBoxLayoutBinding binding;

            public MyViewHolder(BigBoxLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class TopCategoriesAdapter extends RecyclerView.Adapter<TopCategoriesAdapter.MyViewHolder> {
        private ArrayList<MainAudioModel.ResponseData.Detail> listModelList;
        Context ctx;
        FragmentActivity activity;
        String HomeID, Views;

        public TopCategoriesAdapter(ArrayList<MainAudioModel.ResponseData.Detail> listModelList, Context ctx, FragmentActivity activity,
                                    String HomeID, String Views) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.activity = activity;
            this.HomeID = HomeID;
            this.Views = Views;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RoundBoxLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.round_box_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(listModelList.get(position).getCategoryName());
            Glide.with(ctx).load(listModelList.get(position).getCatImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
        /*MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 16,
                1, 1, 0.52f, 10);
        holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());*/
//        holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.llMainLayout.setOnClickListener(view -> {
                Fragment viewAllAudioFragment = new ViewAllAudioFragment();
                FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.flContainer, viewAllAudioFragment)
                        .commit();
                Bundle bundle = new Bundle();
                bundle.putString("ID", HomeID);
                bundle.putString("Name", Views);
                bundle.putString("Category", listModelList.get(position).getCategoryName());
                viewAllAudioFragment.setArguments(bundle);
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            RoundBoxLayoutBinding binding;

            public MyViewHolder(RoundBoxLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}