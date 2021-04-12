package com.brainwellnessspa.DashboardModule.Search;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DashboardTwoModule.AddPlaylistActivity;
import com.brainwellnessspa.DashboardModule.Models.SearchBothModel;
import com.brainwellnessspa.DashboardModule.Models.SearchPlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.SuggestedModel;
import com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.FragmentSearchBinding;
import com.brainwellnessspa.databinding.GlobalSearchLayoutBinding;
import com.brainwellnessspa.databinding.PlaylistCustomLayoutBinding;
import com.brainwellnessspa.databinding.SearchAudioLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;


import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.tutorial;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.myAudioId;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;

public class SearchFragment extends Fragment {
    public static int comefrom_search = 0;
    FragmentSearchBinding binding;
    String UserID, AudioFlag, IsPlayDisclimer, SearchFirstLogin = "0";
    EditText searchEditText;
    SerachListAdpater serachListAdpater;
    int listSize = 0;
    FancyShowCaseView fancyShowCaseView11, fancyShowCaseView21;
    FancyShowCaseQueue queue;
    SuggestionAudiosAdpater suggestionAudiosAdpater;
    Properties p;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.hasExtra("MyData")) {
                    String data = intent.getStringExtra("MyData");
                    Log.d("play_pause_Action", data);
                    SharedPreferences sharedzw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                    if (!AudioFlag.equalsIgnoreCase("Downloadlist") && !AudioFlag.equalsIgnoreCase("SubPlayList") && !AudioFlag.equalsIgnoreCase("TopCategories")) {
                        if (player != null) {
                            if (listSize != 0) {
                                serachListAdpater.notifyDataSetChanged();
                            }
                            suggestionAudiosAdpater.notifyDataSetChanged();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        ComeScreenAccount = 0;
        comefromDownload = "0";
        ArrayList<String> section = new ArrayList<>();
        section.add("Recommended Audios");
        section.add("Recommended Playlists");
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        p = new Properties();
        p.putValue("userId", UserID);
        p.putValue("source", "Search Screen");
        p.putValue("sections", gson.toJson(section));
        BWSApplication.addToSegment("Search Screen Viewed", p, CONSTANTS.screen);
        binding.searchView.onActionViewExpanded();
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.dark_blue_gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();
        closeButton.setOnClickListener(v -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.rvSerachList.setAdapter(null);
            binding.rvSerachList.setVisibility(View.GONE);
            binding.llError.setVisibility(View.GONE);
            binding.searchView.setQuery("", false);
        });

        showTooltips();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                if (searchEditText.getText().toString().equalsIgnoreCase("")) {

                } else {
                    prepareSearchData(search, searchEditText);
                }
                p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("source", "Search Screen");
                p.putValue("searchKeyword", search);
                BWSApplication.addToSegment("Audio/Playlist Searched", p, CONSTANTS.track);
                return false;
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvSerachList.setLayoutManager(layoutManager);
        binding.rvSerachList.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutDownloads = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvDownloadsList.setLayoutManager(layoutDownloads);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvPlayList.setLayoutManager(manager);
        binding.rvPlayList.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    private void showTooltips() {
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        SearchFirstLogin = (shared1.getString(CONSTANTS.PREF_KEY_SearchFirstLogin, "0"));

        if (SearchFirstLogin.equalsIgnoreCase("1")) {
            Animation enterAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_top);
            Animation exitAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_bottom);

            fancyShowCaseView11 = new FancyShowCaseView.Builder(getActivity())
                    .customView(R.layout.layout_search_audioplay, view -> {
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
                        rlNext.setOnClickListener(v -> fancyShowCaseView11.hide());
                    }).focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .enterAnimation(enterAnimation).exitAnimation(exitAnimation)
                    .closeOnTouch(false)
                    .build();

            fancyShowCaseView21 = new FancyShowCaseView.Builder(getActivity())
                    .customView(R.layout.layout_search_addplaylist, view -> {
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
                            editor.putString(CONSTANTS.PREF_KEY_SearchFirstLogin, "0");
                            editor.commit();
                            fancyShowCaseView21.hide();
                            tutorial = true;
                        });
                    })
                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .enterAnimation(enterAnimation).exitAnimation(exitAnimation)
                    .closeOnTouch(false).build();

            queue = new FancyShowCaseQueue()
                    .add(fancyShowCaseView11)
                    .add(fancyShowCaseView21);
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        super.onDestroy();
    }

    private void prepareSearchData(String search, EditText searchEditText) {
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<SearchBothModel> listCall = APIClient.getClient().getSearchBoth(UserID, search);
            listCall.enqueue(new Callback<SearchBothModel>() {
                @Override
                public void onResponse(Call<SearchBothModel> call, Response<SearchBothModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                            SearchBothModel listModel = response.body();
                            if (!searchEditText.getText().toString().equalsIgnoreCase("")) {
                                if (listModel.getResponseData().size() == 0) {
                                    binding.rvSerachList.setVisibility(View.GONE);
                                    binding.llError.setVisibility(View.VISIBLE);
                                    binding.tvFound.setText("Couldn't find '" + search + "'. Try searching again");
                                } else {
                                    binding.llError.setVisibility(View.GONE);
                                    binding.rvSerachList.setVisibility(View.VISIBLE);
                                    serachListAdpater = new SerachListAdpater(listModel.getResponseData(), getActivity(), binding.rvSerachList, UserID);
                                    binding.rvSerachList.setAdapter(serachListAdpater);
                                }
                            } else if (searchEditText.getText().toString().equalsIgnoreCase("")) {
                                binding.rvSerachList.setAdapter(null);
                                binding.rvSerachList.setVisibility(View.GONE);
                                binding.llError.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SearchBothModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }


    @Override
    public void onResume() {
        ComeScreenAccount = 0;
        comefromDownload = "0";
        prepareSuggestedData();
        super.onResume();
    }

    private void prepareSuggestedData() {
        try {
            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
            globalInitExoPlayer.UpdateMiniPlayer(getActivity());
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                callAddFrag();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 6, 0, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 6, 0, 50);
                binding.llSpace.setLayoutParams(params);
            }
            /*
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
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");*/
        } catch (Exception e) {
            e.printStackTrace();
        }
      /*  try {
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
                SharedPreferences shared11 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = shared11.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
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
            SharedPreferences shareda = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shareda.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {


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
        }*/

        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<SuggestedModel> listCall = APIClient.getClient().getSuggestedLists(UserID);
            listCall.enqueue(new Callback<SuggestedModel>() {
                @Override
                public void onResponse(Call<SuggestedModel> call, Response<SuggestedModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                            SuggestedModel listModel = response.body();
                            binding.tvSuggestedAudios.setText(R.string.Recommended_Audios);
                            /*p = new Properties();
                            p.putValue("userId", UserID);
                            p.putValue("source", "Search Screen");
                            BWSApplication.addToSegment("Recommended Audios List Viewed", p, CONSTANTS.screen);*/
                            binding.tvSAViewAll.setVisibility(View.VISIBLE);
                            suggestionAudiosAdpater = new SuggestionAudiosAdpater(listModel.getResponseData(), getActivity());
                            LocalBroadcastManager.getInstance(getActivity())
                                    .registerReceiver(listener, new IntentFilter("play_pause_Action"));
                            binding.rvDownloadsList.setAdapter(suggestionAudiosAdpater);

                            binding.tvSAViewAll.setOnClickListener(view -> {
                                Fragment fragment = new ViewAllSearchFragment();
                                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                                fragmentManager1.beginTransaction()
                                        .replace(R.id.flContainer, fragment)
                                        .commit();
                                Bundle bundle = new Bundle();
                                bundle.putString("Name", "Recommended  Audios");
                                bundle.putParcelableArrayList("AudiolistModel", listModel.getResponseData());
                                fragment.setArguments(bundle);
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SuggestedModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }

        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<SearchPlaylistModel> listCall = APIClient.getClient().getSuggestedPlayLists(UserID);
            listCall.enqueue(new Callback<SearchPlaylistModel>() {
                @Override
                public void onResponse(Call<SearchPlaylistModel> call, Response<SearchPlaylistModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                            SearchPlaylistModel listModel = response.body();
                            binding.tvSuggestedPlaylist.setText(R.string.Recommendeds_Playlist);
                            /*p = new Properties();
                            p.putValue("userId", UserID);
                            p.putValue("source", "Search Screen");
                            BWSApplication.addToSegment("Recommended Playlists List Viewed", p, CONSTANTS.screen);*/
                            binding.tvSPViewAll.setVisibility(View.VISIBLE);
                            SearchPlaylistAdapter suggestedAdpater = new SearchPlaylistAdapter(listModel.getResponseData());
                            binding.rvPlayList.setAdapter(suggestedAdpater);
                            binding.tvSPViewAll.setOnClickListener(view -> {
                                Fragment fragment = new ViewAllSearchFragment();
                                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                                fragmentManager1.beginTransaction()
                                        .replace(R.id.flContainer, fragment).commit();
                                Bundle bundle = new Bundle();
                                bundle.putString("Name", "Recommended Playlist");
                                bundle.putParcelableArrayList("PlaylistModel", listModel.getResponseData());
                                fragment.setArguments(bundle);
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SearchPlaylistModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    private void callAddFrag() {

        Fragment fragment = new MiniPlayerFragment();
        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .add(R.id.flContainer, fragment)
                .commit();
    }

    public class SerachListAdpater extends RecyclerView.Adapter<SerachListAdpater.MyViewHolder> {
        Context ctx;
        String UserID, songId;
        RecyclerView rvSerachList;
        private List<SearchBothModel.ResponseData> modelList;

        public SerachListAdpater(List<SearchBothModel.ResponseData> modelList, Context ctx,
                                 RecyclerView rvSerachList, String UserID) {
            this.modelList = modelList;
            this.ctx = ctx;
            this.rvSerachList = rvSerachList;
            this.UserID = UserID;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            GlobalSearchLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.global_search_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(modelList.get(position).getName());
            holder.binding.equalizerview.setVisibility(View.GONE);
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            Glide.with(getActivity()).load(modelList.get(position).getImageFile()).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.ivIcon.setImageResource(R.drawable.ic_add_two_icon);
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);

            if (modelList.get(position).getIscategory().equalsIgnoreCase("1")) {
                holder.binding.tvPart.setText(R.string.Audio);
                holder.binding.llRemoveAudio.setVisibility(View.VISIBLE);
                holder.binding.equalizerview.setVisibility(View.GONE);

                if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                    if (modelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                    } else if (modelList.get(position).getIsPlay().equalsIgnoreCase("0")
                            || modelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                    }
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                    if (modelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                    } else if (modelList.get(position).getIsPlay().equalsIgnoreCase("0")
                            || modelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                    }
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0")
                        || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                }

                SharedPreferences sharedzw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (!AudioFlag.equalsIgnoreCase("Downloadlist") &&
                        !AudioFlag.equalsIgnoreCase("SubPlayList")
                        && !AudioFlag.equalsIgnoreCase("TopCategories")) {
                    if (myAudioId.equalsIgnoreCase(modelList.get(position).getID())) {
                        songId = myAudioId;
                        if (player != null) {
                            if (!player.getPlayWhenReady()) {
                                holder.binding.equalizerview.pause();
                            } else
                                holder.binding.equalizerview.resume(true);
                        } else
                            holder.binding.equalizerview.stop(true);
                        holder.binding.equalizerview.setVisibility(View.VISIBLE);
                        holder.binding.llMainLayout.setBackgroundResource(R.color.highlight_background);
                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                        holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
                    } else {
                        holder.binding.equalizerview.setVisibility(View.GONE);
                        holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                        holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    }
                } else {
                    holder.binding.equalizerview.setVisibility(View.GONE);
                    holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                }

                holder.binding.llMainLayoutForPlayer.setOnClickListener(view -> {
                    if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                        if (modelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                            callMainTransFrag(position);
                        } else if (modelList.get(position).getIsPlay().equalsIgnoreCase("0")
                                || modelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                            Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                            i.putExtra("ComeFrom", "Plan");
                            startActivity(i);
                        }
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                        if (modelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                            callMainTransFrag(position);
                        } else if (modelList.get(position).getIsPlay().equalsIgnoreCase("0")
                                || modelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                            BWSApplication.showToast(getString(R.string.reactive_plan), getActivity());
                        }
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0")
                            || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                        callMainTransFrag(position);
                    }
                });

                holder.binding.llRemoveAudio.setOnClickListener(view -> {
                    if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                        Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        startActivity(i);
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                        BWSApplication.showToast(getString(R.string.reactive_plan), getActivity());
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0") || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                        Intent i = new Intent(ctx, AddPlaylistActivity.class);
                        i.putExtra("AudioId", modelList.get(position).getID());
                        i.putExtra("ScreenView", "Search Audio");
                        i.putExtra("PlaylistID", "");
                        i.putExtra("PlaylistName", "");
                        i.putExtra("PlaylistImage", "");
                        i.putExtra("PlaylistType", "");
                        i.putExtra("Liked", "0");
                        startActivity(i);
                    }
                });
            } else if (modelList.get(position).getIscategory().equalsIgnoreCase("0")) {
                holder.binding.tvPart.setText(R.string.Playlist);
                if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0")
                        || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                }
                holder.binding.equalizerview.setVisibility(View.GONE);
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                holder.binding.llRemoveAudio.setVisibility(View.VISIBLE);

                holder.binding.llMainLayout.setOnClickListener(view -> {
                    if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                        Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        startActivity(i);
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                        BWSApplication.showToast(getString(R.string.reactive_plan), getActivity());
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0") || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                        comefrom_search = 1;
                        Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("New", "0");
                        bundle.putString("PlaylistID", modelList.get(position).getID());
                        bundle.putString("PlaylistName", modelList.get(position).getName());
                        bundle.putString("MyDownloads", "0");
                        myPlaylistsFragment.setArguments(bundle);
                        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                        fragmentManager1.beginTransaction()
                                .replace(R.id.flContainer, myPlaylistsFragment)
                                .commit();
                    }
                });

                holder.binding.llRemoveAudio.setOnClickListener(view -> {
                    if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                        Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        startActivity(i);
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                        BWSApplication.showToast(getString(R.string.reactive_plan), getActivity());
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0") || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                        Intent i = new Intent(ctx, AddPlaylistActivity.class);
                        i.putExtra("AudioId", "");
                        i.putExtra("ScreenView", "Search Playlist");
                        i.putExtra("PlaylistID", modelList.get(position).getID());
                        i.putExtra("PlaylistName", "");
                        i.putExtra("PlaylistImage", "");
                        i.putExtra("PlaylistType", "");
                        i.putExtra("Liked", "0");
                        startActivity(i);
                    }
                });
            }
        }

        public void callMainTransFrag(int position) {
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String MyPlaylist = shared.getString(CONSTANTS.PREF_KEY_myPlaylist, "");
            SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
            if (audioPlay && (AudioFlag.equalsIgnoreCase("SearchModelAudio")
                    && MyPlaylist.equalsIgnoreCase("Search Audio"))) {
                if (isDisclaimer == 1) {
                    if (player != null) {
                        if (!player.getPlayWhenReady()) {
                            player.setPlayWhenReady(true);
                        }
                    } else {
                        audioClick = true;
                        miniPlayer = 1;
                    }
                    callAddFrag();
                    BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                } else {
                    ArrayList<SearchBothModel.ResponseData> listModelList2 = new ArrayList<>();
                    listModelList2.add(modelList.get(position));
                    callTransFrag(0, listModelList2, true);
                }
            } else {
                ArrayList<SearchBothModel.ResponseData> listModelList2 = new ArrayList<>();
                listModelList2.add(modelList.get(position));
                SearchBothModel.ResponseData mainPlayModel = new SearchBothModel.ResponseData();
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
                boolean audioc = true;
                if (isDisclaimer == 1) {
                    if (player != null) {
                        player.setPlayWhenReady(true);
                        audioc = false;
                        listModelList2.add(mainPlayModel);
                    } else {
                        isDisclaimer = 0;
                        if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                            audioc = true;
                            listModelList2.add(mainPlayModel);
                        }
                    }
                } else {
                    isDisclaimer = 0;
                    if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                        audioc = true;
                        listModelList2.add(mainPlayModel);
                    }
                }
                callTransFrag(0, listModelList2, audioc);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 6, 0, 260);
            binding.llSpace.setLayoutParams(params);
            notifyDataSetChanged();
        }

        private void callTransFrag(int position, ArrayList<SearchBothModel.ResponseData> listModelList, boolean audioc) {
            try {
                miniPlayer = 1;
                audioClick = audioc;
                if (audioc) {
                    callNewPlayerRelease();
                }
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();

                String json = gson.toJson(listModelList);
                editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                editor.putInt(CONSTANTS.PREF_KEY_position, 0);
                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "Search Audio");
                editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SearchModelAudio");
                editor.commit();
                callAddFrag();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public int getItemCount() {
            listSize = modelList.size();
            return modelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            GlobalSearchLayoutBinding binding;

            public MyViewHolder(GlobalSearchLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class SuggestionAudiosAdpater extends RecyclerView.Adapter<SuggestionAudiosAdpater.MyViewHolder> {
        Context ctx;
        String songId;
        int ps = 0, nps = 0;
        private List<SuggestedModel.ResponseData> modelList;

        public SuggestionAudiosAdpater(List<SuggestedModel.ResponseData> modelList, Context ctx) {
            this.modelList = modelList;
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SearchAudioLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.search_audio_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(modelList.get(position).getName());
            holder.binding.tvTime.setText(modelList.get(position).getAudioDuration());
            holder.binding.pbProgress.setVisibility(View.GONE);
            holder.binding.equalizerview.setVisibility(View.GONE);
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(getActivity()).load(modelList.get(position).getImageFile()).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.ivIcon.setImageResource(R.drawable.ic_add_two_icon);
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);

            if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                if (modelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                } else if (modelList.get(position).getIsPlay().equalsIgnoreCase("0")
                        || modelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                }
            } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                if (modelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                } else if (modelList.get(position).getIsPlay().equalsIgnoreCase("0")
                        || modelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                }
            } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0")
                    || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            SharedPreferences sharedzw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
            if (!AudioFlag.equalsIgnoreCase("Downloadlist") &&
                    !AudioFlag.equalsIgnoreCase("SubPlayList") && !AudioFlag.equalsIgnoreCase("TopCategories")) {
                if (myAudioId.equalsIgnoreCase(modelList.get(position).getID())) {
                    songId = myAudioId;
                    if (player != null) {
                        if (!player.getPlayWhenReady()) {
                            holder.binding.equalizerview.pause();
                        } else
                            holder.binding.equalizerview.resume(true);
                    } else
                        holder.binding.equalizerview.stop(true);
                    holder.binding.equalizerview.setVisibility(View.VISIBLE);
                    holder.binding.llMainLayout.setBackgroundResource(R.color.highlight_background);
                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                } else {
                    holder.binding.equalizerview.setVisibility(View.GONE);
                    holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                }
            } else {
                holder.binding.equalizerview.setVisibility(View.GONE);
                holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
            }

            holder.binding.llMainLayoutForPlayer.setOnClickListener(view -> {
                if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                    if (modelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        callMainTransFrag(position);
                    } else if (modelList.get(position).getIsPlay().equalsIgnoreCase("0")
                            || modelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                        Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        startActivity(i);
                    }
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                    if (modelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        callMainTransFrag(position);
                    } else if (modelList.get(position).getIsPlay().equalsIgnoreCase("0")
                            || modelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                        BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
                    }
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0")
                        || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                    callMainTransFrag(position);
                }
            });

            holder.binding.llRemoveAudio.setOnClickListener(view -> {
                if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                    Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    startActivity(i);
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                    BWSApplication.showToast(getString(R.string.reactive_plan), getActivity());
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0") || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                    Intent i = new Intent(ctx, AddPlaylistActivity.class);
                    i.putExtra("AudioId", modelList.get(position).getID());
                    i.putExtra("ScreenView", "Recommended Search Audio");
                    i.putExtra("PlaylistID", "");
                    i.putExtra("PlaylistName", "");
                    i.putExtra("PlaylistImage", "");
                    i.putExtra("PlaylistType", "");
                    i.putExtra("Liked", "0");
                    startActivity(i);
                }
            });
        }

        public void callMainTransFrag(int position) {
            try {
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String MyPlaylist = shared.getString(CONSTANTS.PREF_KEY_myPlaylist, "");
                SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
                if (audioPlay && (AudioFlag.equalsIgnoreCase("SearchAudio")
                        && MyPlaylist.equalsIgnoreCase("Recommended Search Audio"))) {
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            if (!player.getPlayWhenReady()) {
                                player.setPlayWhenReady(true);
                            }
                        } else {
                            audioClick = true;
                            miniPlayer = 1;
                        }
                        callAddFrag();
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                    } else {
                        ArrayList<SuggestedModel.ResponseData> listModelList2 = new ArrayList<>();
                        listModelList2.add(modelList.get(position));
                        callTransFrag(0, listModelList2, true);
                    }
                } else {
                    ArrayList<SuggestedModel.ResponseData> listModelList2 = new ArrayList<>();
                    listModelList2.add(modelList.get(position));
                    SuggestedModel.ResponseData mainPlayModel = new SuggestedModel.ResponseData();
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
                    boolean audioc = true;
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            player.setPlayWhenReady(true);
                            audioc = false;
                            listModelList2.add(mainPlayModel);
                        } else {
                            isDisclaimer = 0;
                            if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                audioc = true;
                                listModelList2.add(mainPlayModel);
                            }
                        }
                    } else {
                        isDisclaimer = 0;
                        if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                            audioc = true;
                            listModelList2.add(mainPlayModel);
                        }
                    }
                    callTransFrag(0, listModelList2, audioc);
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 6, 0, 260);
                binding.llSpace.setLayoutParams(params);
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void callTransFrag(int position, ArrayList<SuggestedModel.ResponseData> listModelList, boolean audioc) {
            try {
                miniPlayer = 1;
                audioClick = audioc;
                if (audioc) {
                    callNewPlayerRelease();
                }
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();

                String json = gson.toJson(listModelList);
                editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                editor.putInt(CONSTANTS.PREF_KEY_position, 0);
                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "Recommended Search Audio");
                editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SearchAudio");
                editor.commit();
                callAddFrag();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (10 > modelList.size()) {
                return modelList.size();
            } else {
                return 10;
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            SearchAudioLayoutBinding binding;

            public MyViewHolder(SearchAudioLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class SearchPlaylistAdapter extends RecyclerView.Adapter<SearchPlaylistAdapter.MyViewHolder> {
        int index = -1;
        private List<SearchPlaylistModel.ResponseData> modelList;

        public SearchPlaylistAdapter(List<SearchPlaylistModel.ResponseData> listModelList) {
            this.modelList = listModelList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            PlaylistCustomLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.playlist_custom_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                    1, 1, 0.38f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.tvAddToPlaylist.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.tvAddToPlaylist.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            MeasureRatio measureRatio1 = BWSApplication.measureRatio(getActivity(), 0,
                    1, 1, 0.38f, 0);
            holder.binding.rlMainLayout.getLayoutParams().height = (int) (measureRatio1.getHeight() * measureRatio1.getRatio());
            holder.binding.rlMainLayout.getLayoutParams().width = (int) (measureRatio1.getWidthImg() * measureRatio1.getRatio());

            holder.binding.tvPlaylistName.setText(modelList.get(position).getName());
            Glide.with(getActivity()).load(modelList.get(position).getImage()).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0")
                    || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            if (index == position) {
                holder.binding.tvAddToPlaylist.setVisibility(View.VISIBLE);
            } else
                holder.binding.tvAddToPlaylist.setVisibility(View.GONE);
            holder.binding.tvAddToPlaylist.setText("Add To Playlist");
            holder.binding.rlMainLayout.setOnLongClickListener(new View.OnLongClickListener() {
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
                    if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        startActivity(i);
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        startActivity(i);
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0")
                            || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        Intent i = new Intent(getActivity(), AddPlaylistActivity.class);
                        i.putExtra("AudioId", "");
                        i.putExtra("ScreenView", "Recommended Search Playlist");
                        i.putExtra("PlaylistID", modelList.get(position).getID());
                        i.putExtra("PlaylistName", "");
                        i.putExtra("PlaylistImage", "");
                        i.putExtra("PlaylistType", "");
                        i.putExtra("Liked", "0");
                        startActivity(i);
                    }

                }
            });
            holder.binding.rlMainLayout.setOnClickListener(view -> {
                if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    startActivity(i);
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    BWSApplication.showToast(getString(R.string.reactive_plan), getActivity());
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0")
                        || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                    comefrom_search = 1;
                    Bundle bundle = new Bundle();
                    Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    bundle.putString("New", "0");
                    bundle.putString("ComeFrom", "Search");
                    bundle.putString("PlaylistID", modelList.get(position).getID());
                    bundle.putString("PlaylistName", modelList.get(position).getName());
                    bundle.putString("MyDownloads", "0");
                    myPlaylistsFragment.setArguments(bundle);
                    fragmentManager1.beginTransaction()
                            .replace(R.id.flContainer, myPlaylistsFragment)
                            .commit();
                    /*Intent intent = new Intent(getActivity(), DashboardActivity.class);
                    intent.putExtra("Goplaylist","1");
                    intent.putExtra("PlaylistID", modelList.get(position).getID());
                    intent.putExtra("PlaylistName", modelList.get(position).getName());
                    intent.putExtra("PlaylistImage","");
                    startActivity(intent);
                    getActivity().finish();*/
                }
            });
        }

        @Override
        public int getItemCount() {
            if (6 > modelList.size()) {
                return modelList.size();
            } else {
                return 6;
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            PlaylistCustomLayoutBinding binding;

            public MyViewHolder(PlaylistCustomLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}