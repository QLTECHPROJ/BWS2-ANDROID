package com.brainwellnessspa.LikeModule.Fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DashboardModule.Activities.AudioDetailActivity;
import com.brainwellnessspa.DashboardModule.Models.AddToQueueModel;
import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardModule.Models.AudioLikeModel;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.Models.SearchBothModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.SuggestedModel;
import com.brainwellnessspa.DashboardModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.FragmentLikesBinding;
import com.brainwellnessspa.databinding.LikeListLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;


import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.myAudioId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;

public class LikeAudiosFragment extends Fragment {
    FragmentLikesBinding binding;
    String UserID, AudioFlag, IsPlayDisclimer;
    Handler handler3;
    int startTime;
    long myProgress = 0;
    LikeAudiosAdapter adapter;
    private long currentDuration = 0;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("MyData")) {
                String data = intent.getStringExtra("MyData");
                Log.d("play_pause_Action", data);
                try {
                    SharedPreferences sharedzw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                    if (!AudioFlag.equalsIgnoreCase("Downloadlist") && !AudioFlag.equalsIgnoreCase("SubPlayList") && !AudioFlag.equalsIgnoreCase("TopCategories")) {
                        if (player != null) {
                            if (data.equalsIgnoreCase("play")) {
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_likes, container, false);
        View view = binding.getRoot();
//        handler3 = new Handler();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

     /*   Properties p = new Properties();
        p.putValue("userId", UserID);
        BWSApplication.addToSegment("Liked Audios Viewed", p, CONSTANTS.screen);*/

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvLikesList.setLayoutManager(mLayoutManager);
        binding.rvLikesList.setItemAnimator(new DefaultItemAnimator());
        prepareData();
        binding.llError.setVisibility(View.GONE);
        binding.tvFound.setText("Your like audios will appear here");
        return view;
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

    @Override
    public void onResume() {
        prepareData();
        super.onResume();
    }

    public void prepareData() {
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (!AudioFlag.equalsIgnoreCase("0")) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 9, 0, 190);
            binding.llSpace.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 9, 0, 28);
            binding.llSpace.setLayoutParams(params);
        }
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<LikesHistoryModel> listCall = APIClient.getClient().getLikeAudioPlaylistListing(UserID);
            listCall.enqueue(new Callback<LikesHistoryModel>() {
                @Override
                public void onResponse(Call<LikesHistoryModel> call, Response<LikesHistoryModel> response) {
                    try {
                        LikesHistoryModel listModel = response.body();
                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                            List<LikesHistoryModel.ResponseData.Audio> listDataModel = listModel.getResponseData().getAudio();
                            if (listDataModel.size() == 0) {
                                binding.tvFound.setVisibility(View.VISIBLE);
                                binding.llError.setVisibility(View.VISIBLE);
                                binding.rvLikesList.setVisibility(View.GONE);
                            } else {
                                binding.llError.setVisibility(View.GONE);
                                binding.rvLikesList.setVisibility(View.VISIBLE);
                                adapter = new LikeAudiosAdapter(listModel.getResponseData().getAudio(), getActivity());
                                binding.rvLikesList.setAdapter(adapter);
                                LocalBroadcastManager.getInstance(getActivity())
                                        .registerReceiver(listener, new IntentFilter("play_pause_Action"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<LikesHistoryModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    private void callTransFrag(int position, List<LikesHistoryModel.ResponseData.Audio> listModelList, boolean audioc) {
        try {
            miniPlayer = 1;
            audioClick = audioc;
            if (audioc) {
                callNewPlayerRelease();
            }
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(listModelList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
            editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
            editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
            editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "LikeAudioList");
            editor.commit();
            callAddTransFrag();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callAddTransFrag() {


        callNewPlayerRelease();
        Fragment fragment = new MiniPlayerFragment();
        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .add(R.id.flContainer, fragment)
                .commit();

    }

    private void callRemoveLike(String id, int position, List<LikesHistoryModel.ResponseData.Audio> listModelList2) {
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<AudioLikeModel> listCall = APIClient.getClient().getAudioLike(id, UserID);
            listCall.enqueue(new Callback<AudioLikeModel>() {
                @Override
                public void onResponse(Call<AudioLikeModel> call, Response<AudioLikeModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                            AudioLikeModel model = response.body();
                            BWSApplication.showToast(model.getResponseMessage(), getActivity());
                            prepareData();
                                /*listModelList2.remove(position);
                                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                                int pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
                                if (audioPlay && AudioFlag.equalsIgnoreCase("LikeAudioList")) {
                                    if (pos == position && position < listModelList2.size() - 1) {
//                                            pos = pos + 1;
                                        if (isDisclaimer == 1) {
//                                    BWSApplication.showToast("The audio shall remove after the disclaimer", getActivity());
                                        } else {
                                            callTransFrag(position, listModelList2);
                                        }
                                    } else if (pos == position && position == listModelList2.size() - 1) {
                                        pos = 0;
                                        if (isDisclaimer == 1) {
//                                    BWSApplication.showToast("The audio shall remove after the disclaimer", getActivity());
                                        } else {
                                            callTransFrag(position, listModelList2);
                                        }
                                    } else if (pos < position && pos < listModelList2.size() - 1) {
                                        saveToPref(pos, listModelList2);
                                    } else if (pos > position && pos == listModelList2.size()) {
                                        pos = pos - 1;
                                        saveToPref(pos, listModelList2);
                                    }
                                }*/

                        /*if (audioPlay && AudioFlag.equalsIgnoreCase("LikeAudioList")) {
                            if (model.getResponseData().getFlag().equalsIgnoreCase("0")) {
                                SharedPreferences sharedx = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                 AudioFlag = sharedx.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                                Gson gsonx = new Gson();
                                String json = sharedx.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gsonx));
                                Type type1 = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
                                }.getType();
                                Gson gson1 = new Gson();
                                ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gson1.fromJson(json, type1);

                                mainPlayModelList.add(mainPlayModelList.get(position));

                                SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedd.edit();
                                Gson gson = new Gson();
                                String jsonx = gson.toJson(mainPlayModelList);
                                String json1 = gson.toJson(arrayList);
                                editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
                                editor.putString(CONSTANTS.PREF_KEY_audioList, jsonx);
                                editor.putInt(CONSTANTS.PREF_KEY_position, pos);
                                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                                editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "LikeAudioList");
                                editor.commit();

                            } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                                SharedPreferences sharedx = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                AudioFlag = sharedx.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                                Gson gsonx = new Gson();
                                String json = sharedx.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gsonx));
                                Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                                }.getType();
                                ArrayList<MainPlayModel> mainPlayModelListold = new ArrayList<>();
                                mainPlayModelListold = gsonx.fromJson(json, type);
                                String id = mainPlayModelListold.get(pos).getID();
                                Type type1 = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
                                }.getType();
                                Gson gson1 = new Gson();
                                ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gson1.fromJson(json, type1);
                                int x = 0;
                                for (int i = 0; i < mainPlayModelList.size(); i++) {
                                    if (mainPlayModelList.get(i).getID().equalsIgnoreCase(id)) {
                                        x++;
                                    }if(x== 0) {
                                        if (audioPlay) {
                                            LikesHistoryModel.ResponseData.Audio mainPlayModel = new LikesHistoryModel.ResponseData.Audio();
                                            mainPlayModel.setID(mainPlayModelList.get(position).getID());
                                            mainPlayModel.setName(mainPlayModelList.get(position).getName());
                                            mainPlayModel.setAudioFile(mainPlayModelList.get(position).getAudioFile());
                                            mainPlayModel.setAudioDirection(mainPlayModelList.get(position).getAudioDirection());
                                            mainPlayModel.setAudiomastercat(mainPlayModelList.get(position).getAudiomastercat());
                                            mainPlayModel.setAudioSubCategory(mainPlayModelList.get(position).getAudioSubCategory());
                                            mainPlayModel.setImageFile(mainPlayModelList.get(position).getImageFile());
                                            mainPlayModel.setLike(mainPlayModelList.get(position).getLike());
                                            mainPlayModel.setDownload(mainPlayModelList.get(position).getDownload());
                                            mainPlayModel.setAudioDuration(mainPlayModelList.get(position).getAudioDuration());
                                            arrayList.add(mainPlayModel);
                                        } else if (queuePlay) {
                                            LikesHistoryModel.ResponseData.Audio mainPlayModel = new LikesHistoryModel.ResponseData.Audio();
                                            mainPlayModel.setID(addToQueueModelList.get(position).getID());
                                            mainPlayModel.setName(addToQueueModelList.get(position).getName());
                                            mainPlayModel.setAudioFile(addToQueueModelList.get(position).getAudioFile());
                                            mainPlayModel.setAudioDirection(addToQueueModelList.get(position).getAudioDirection());
                                            mainPlayModel.setAudiomastercat(addToQueueModelList.get(position).getAudiomastercat());
                                            mainPlayModel.setAudioSubCategory(addToQueueModelList.get(position).getAudioSubCategory());
                                            mainPlayModel.setImageFile(addToQueueModelList.get(position).getImageFile());
                                            mainPlayModel.setLike(addToQueueModelList.get(position).getLike());
                                            mainPlayModel.setDownload(addToQueueModelList.get(position).getDownload());
                                            mainPlayModel.setAudioDuration(addToQueueModelList.get(position).getAudioDuration());
                                            arrayList.add(mainPlayModel);
                                        }
                                        mainPlayModelList.add(mainPlayModelList.get(position));
                                    }
                                }
                                SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedd.edit();
                                Gson gson = new Gson();
                                String jsonx = gson.toJson(mainPlayModelList);
                                String json1 = gson.toJson(arrayList);
                                editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
                                editor.putString(CONSTANTS.PREF_KEY_audioList, jsonx);
                                editor.putInt(CONSTANTS.PREF_KEY_position, pos);
                                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                                editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "LikeAudioList");
                                editor.commit();
                            }
                        }*/
                            SharedPreferences sharedq = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                            AudioFlag = sharedq.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                            boolean audioPlay = sharedq.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                            boolean queuePlay = sharedq.getBoolean(CONSTANTS.PREF_KEY_queuePlay, true);
                            Gson gsonq = new Gson();
                            String jsonq = sharedq.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gsonq));
                            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            Gson gson = new Gson();
                            ArrayList<MainPlayModel> mainPlayModelList = new ArrayList<>();
                            ArrayList<AddToQueueModel> addToQueueModelList = new ArrayList<>();
                            String json23 = sharedq.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
                            if (!json23.equalsIgnoreCase(String.valueOf(gson))) {
                                Type type2 = new TypeToken<ArrayList<AddToQueueModel>>() {
                                }.getType();
                                addToQueueModelList = gson.fromJson(json23, type2);
                            }
                            String json33 = sharedq.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
                            if (!json33.equalsIgnoreCase(String.valueOf(gson))) {
                                Type type23 = new TypeToken<ArrayList<MainPlayModel>>() {
                                }.getType();
                                mainPlayModelList = gson.fromJson(json33, type23);
                            }
                            if (audioPlay) {
                                if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
                                    Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
                                    }.getType();
                                    ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
                                    Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
                                    }.getType();
                                    ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("SearchModelAudio")) {
                                    Type type = new TypeToken<ArrayList<SearchBothModel.ResponseData>>() {
                                    }.getType();
                                    ArrayList<SearchBothModel.ResponseData> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("SearchAudio")) {
                                    Type type = new TypeToken<ArrayList<SuggestedModel.ResponseData>>() {
                                    }.getType();
                                    ArrayList<SuggestedModel.ResponseData> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                                    Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
                                    }.getType();
                                    ArrayList<AppointmentDetailModel.Audio> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("LikeAudioList")) {
                                    Type type = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
                                    }.getType();
                                    ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                                    Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
                                    }.getType();
                                    ArrayList<DownloadAudioDetails> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
                                    Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
                                    }.getType();
                                    ArrayList<DownloadAudioDetails> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("TopCategories")) {
                                    Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
                                    }.getType();
                                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
                                    Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
                                    }.getType();
                                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                }
                            }
                            if (queuePlay) {
                                addToQueueModelList.get(position).setLike(model.getResponseData().getFlag());
                            } else
                                mainPlayModelList.get(position).setLike(model.getResponseData().getFlag());

                            String json = gson.toJson(mainPlayModelList);
                            editor.putString(CONSTANTS.PREF_KEY_audioList, json);
                            if (queuePlay) {
                                String json1 = gson.toJson(addToQueueModelList);
                                editor.putString(CONSTANTS.PREF_KEY_queueList, json1);
                            }
                            editor.putInt(CONSTANTS.PREF_KEY_position, position);
                            editor.commit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<AudioLikeModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    public class LikeAudiosAdapter extends RecyclerView.Adapter<LikeAudiosAdapter.MyViewHolder> {
        Context ctx;
        String songId;
        int ps = 0, nps = 0;
        private List<LikesHistoryModel.ResponseData.Audio> modelList;

        public LikeAudiosAdapter(List<LikesHistoryModel.ResponseData.Audio> modelList, Context ctx) {
            this.modelList = modelList;
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LikeListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.like_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(modelList.get(position).getName());
            holder.binding.tvTime.setText(modelList.get(position).getAudioDuration());
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(modelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            SharedPreferences sharedzw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
            if (!AudioFlag.equalsIgnoreCase("Downloadlist") && !AudioFlag.equalsIgnoreCase("SubPlayList") && !AudioFlag.equalsIgnoreCase("TopCategories")) {
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
            } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0") || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            holder.binding.llMenu.setOnClickListener(v -> {
                if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    startActivity(i);
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0") || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                    Intent i = new Intent(ctx, AudioDetailActivity.class);
                    if (AudioFlag.equalsIgnoreCase("TopCategories")) {
                        i.putExtra("play", "TopCategories");
                    } else
                        i.putExtra("play", "play");
                    i.putExtra("ID", modelList.get(position).getID());
                    i.putExtra("position", position);
                    i.putExtra("PlaylistAudioId", "");
                    Gson gson = new Gson();
                    String json = gson.toJson(modelList);
                    i.putExtra("data", json);
                    i.putExtra("comeFrom", "myLikeAudioList");
                    startActivity(i);
                }
            });


            holder.binding.llLikes.setOnClickListener(v -> callAlert(position));

            holder.binding.llMainLayout.setOnClickListener(view -> {
                if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                    if (modelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        callTransFragss(position);
                        notifyDataSetChanged();
                    } else if (modelList.get(position).getIsPlay().equalsIgnoreCase("0")
                            || modelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        startActivity(i);
                    }
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                    if (modelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        callTransFragss(position);
                        notifyDataSetChanged();
                    } else if (modelList.get(position).getIsPlay().equalsIgnoreCase("0")
                            || modelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
                    }
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0") || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                    callTransFragss(position);
                    notifyDataSetChanged();
                }
            });
        }

        private void callTransFragss(int adapterPosition) {
            int pos = adapterPosition;
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
            Log.e("position of play", String.valueOf(pos));
            if (audioPlay && AudioFlag.equalsIgnoreCase("LikeAudioList")) {
                if (isDisclaimer == 1) {
                    BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                } else {
                   /* if (player != null) {
                        player.seekTo(pos,0);
                        player.setPlayWhenReady(true);
                        miniPlayer = 1;
                        SharedPreferences sharedxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedxx.edit();
                        editor.putInt(CONSTANTS.PREF_KEY_position, pos);
                        editor.commit();
                        try {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {*/
                    List<LikesHistoryModel.ResponseData.Audio> listModelList2 = new ArrayList<>();
                    if (!IsLock.equalsIgnoreCase("0")) {
                        SharedPreferences shared2 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                        String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
                        Gson gson1 = new Gson();
                        Type type1 = new TypeToken<List<String>>() {
                        }.getType();
                        List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
                        int size = modelList.size();
                        for (int i = 0; i < size; i++) {
                            if (UnlockAudioList.contains(modelList.get(i).getID())) {
                                listModelList2.add(modelList.get(i));
                            }
                        }
                        pos = 0;
                    } else {
                        listModelList2.addAll(modelList);
                    }
                    callTransFrag(pos, listModelList2, true);
//                    }
                }
            } else {
                List<LikesHistoryModel.ResponseData.Audio> listModelList2 = new ArrayList<>();
                if (!IsLock.equalsIgnoreCase("0")) {
                    SharedPreferences shared2 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                    String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
                    Gson gson1 = new Gson();
                    Type type1 = new TypeToken<List<String>>() {
                    }.getType();
                    List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
                    int size = modelList.size();
                    for (int i = 0; i < size; i++) {
                        if (UnlockAudioList.contains(modelList.get(i).getID())) {
                            listModelList2.add(modelList.get(i));
                        }
                    }
                    pos = 0;
                } else {
                    listModelList2.addAll(modelList);
                }
                LikesHistoryModel.ResponseData.Audio mainPlayModel = new LikesHistoryModel.ResponseData.Audio();
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
                        listModelList2.add(pos, mainPlayModel);
                    } else {
                        isDisclaimer = 0;
                        if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                            audioc = true;
                            listModelList2.add(pos, mainPlayModel);
                        }
                    }
                } else {
                    isDisclaimer = 0;
                    if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                        audioc = true;
                        listModelList2.add(pos, mainPlayModel);
                    }
                }
                callTransFrag(pos, listModelList2, audioc);
            }
        }

        private void callAlert(int position) {
            final Dialog dialog = new Dialog(ctx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.logout_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.dark_blue_gray)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
            final TextView tvHeader = dialog.findViewById(R.id.tvHeader);
            final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
            final Button Btn = dialog.findViewById(R.id.Btn);
            tvTitle.setText("Remove from Liked Audios?");
            tvHeader.setText(modelList.get(position).getName());
            Btn.setText("Remove");
            tvGoBack.setText("Cancel");
            dialog.setOnKeyListener((v1, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return false;
            });

            Btn.setOnClickListener(v4 -> {
                callRemoveLike(modelList.get(position).getID(), position, modelList);
                dialog.dismiss();
            });
            tvGoBack.setOnClickListener(v3 -> dialog.dismiss());
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            LikeListLayoutBinding binding;

            public MyViewHolder(LikeListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}