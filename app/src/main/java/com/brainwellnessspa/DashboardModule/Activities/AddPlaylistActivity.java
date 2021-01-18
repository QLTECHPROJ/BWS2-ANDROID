package com.brainwellnessspa.DashboardModule.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Models.AddToPlaylist;
import com.brainwellnessspa.DashboardModule.Models.CreatePlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.PlaylistingModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityAddPlaylistBinding;
import com.brainwellnessspa.databinding.AddPlayListLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Activities.MyPlaylistActivity.comeAddPlaylist;
import static com.brainwellnessspa.DashboardModule.Search.SearchFragment.comefrom_search;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;

public class AddPlaylistActivity extends AppCompatActivity {
    public static boolean addToPlayList = false;
    public static String MyPlaylistId = "";
    ActivityAddPlaylistBinding binding;
    String UserID, AudioId = "", FromPlaylistID = "", PlaylistName = "", ScreenView = "", PlaylistImage = "", PlaylistType = "";
    Context ctx;
    Activity activity;
    Properties p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_playlist);
        ctx = AddPlaylistActivity.this;
        activity = AddPlaylistActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        if (getIntent().getExtras() != null) {
            AudioId = getIntent().getStringExtra("AudioId");
            FromPlaylistID = getIntent().getStringExtra("PlaylistID");
        }
        if (getIntent().getExtras() != null) {
            PlaylistName = getIntent().getStringExtra("PlaylistName");
        }
        if (getIntent().getExtras() != null) {
            PlaylistImage = getIntent().getStringExtra("PlaylistImage");
        }
        if (getIntent().getExtras() != null) {
            PlaylistType = getIntent().getStringExtra("PlaylistType");
        }
        if (getIntent().getExtras() != null) {
            ScreenView = getIntent().getStringExtra("ScreenView");
        }

        p = new Properties();
        p.putValue("userId", UserID);
        p.putValue("source", ScreenView);
        BWSApplication.addToSegment("Playlist List Viewed", p, CONSTANTS.screen);

        binding.llBack.setOnClickListener(view -> {
            comefrom_search = 0;
            if (comeAddPlaylist == 1) {
                Intent i = new Intent(ctx, MyPlaylistActivity.class);
                i.putExtra("PlaylistID", FromPlaylistID);
                i.putExtra("PlaylistName", PlaylistName);
                i.putExtra("PlaylistIDImage", PlaylistImage);
                i.putExtra("ScreenView", ScreenView);
                i.putExtra("PlaylistType", PlaylistType);
                i.putExtra("Liked", "0");
                startActivity(i);
                finish();
            } else {
                finish();
            }
        });

        RecyclerView.LayoutManager played = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvPlayLists.setLayoutManager(played);
        binding.rvPlayLists.setItemAnimator(new DefaultItemAnimator());
        binding.llError.setVisibility(View.GONE);
        binding.tvFound.setText("No result found");

        binding.btnAddPlatLists.setOnClickListener(view -> {
            Properties p = new Properties();
            p.putValue("userId", UserID);
            p.putValue("source", "Add To Playlist Screen");
            BWSApplication.addToSegment("Create Playlist Clicked", p, CONSTANTS.track);
            final Dialog dialog = new Dialog(ctx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.create_palylist);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final EditText edtCreate = dialog.findViewById(R.id.edtCreate);
            final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
            final Button btnSendCode = dialog.findViewById(R.id.btnSendCode);

            dialog.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            });

            TextWatcher popupTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String number = edtCreate.getText().toString().trim();
                    if (!number.isEmpty()) {
                        btnSendCode.setEnabled(true);
                        btnSendCode.setTextColor(getResources().getColor(R.color.white));
                        btnSendCode.setBackgroundResource(R.drawable.extra_round_cornor);
                    } else {
                        btnSendCode.setEnabled(false);
                        btnSendCode.setTextColor(getResources().getColor(R.color.white));
                        btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };

            edtCreate.addTextChangedListener(popupTextWatcher);
            btnSendCode.setOnClickListener(view1 -> {
                if (edtCreate.getText().toString().equalsIgnoreCase("")) {
                    BWSApplication.showToast("Please provide the playlist's name", ctx);
                } else {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        Call<CreatePlaylistModel> listCall = APIClient.getClient().getCreatePlaylist(UserID, edtCreate.getText().toString());
                        listCall.enqueue(new Callback<CreatePlaylistModel>() {
                            @Override
                            public void onResponse(Call<CreatePlaylistModel> call, Response<CreatePlaylistModel> response) {
                                try {
                                    if (response.isSuccessful()) {
                                        CreatePlaylistModel listsModel = response.body();
                                        if (listsModel.getResponseData().getIscreated().equalsIgnoreCase("1")) {
                                            dialog.dismiss();
                                            prepareData(ctx);
                                            String PlaylistID = listsModel.getResponseData().getId();
                                            String Created = listsModel.getResponseData().getIscreated();
                                            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                            boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                            String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                                            String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                                            if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                                                if (isDisclaimer == 1) {
                                                    BWSApplication.showToast("The audio shall add after playing the disclaimer", ctx);
                                                } else {
                                                    callAddPlaylistFromPlaylist(PlaylistID, listsModel.getResponseData().getName(), dialog, "0", Created,"1");
                                                }
                                            } else {
                                                callAddPlaylistFromPlaylist(PlaylistID, listsModel.getResponseData().getName(), dialog, "0", Created,"1");
                                            }
                                           /* Properties p = new Properties();
                                            p.putValue("userId", UserID);
                                            p.putValue("playlistId", PlaylistID);
                                            p.putValue("playlistName", listsModel.getResponseData().getName());
                                            p.putValue("source", "Add To Playlist Screen");
                                            BWSApplication.addToSegment("Playlist Created", p, CONSTANTS.track);*/
                                        } else {
                                            BWSApplication.showToast(listsModel.getResponseMessage(), ctx);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<CreatePlaylistModel> call, Throwable t) {
                            }
                        });
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                    }
                }
            });
            tvCancel.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
            dialog.setCancelable(false);
        });
    }

    @Override
    public void onBackPressed() {
        comefrom_search = 0;
        if (comeAddPlaylist == 1) {
            Intent i = new Intent(ctx, MyPlaylistActivity.class);
            i.putExtra("PlaylistID", FromPlaylistID);
            i.putExtra("PlaylistName", PlaylistName);
            i.putExtra("PlaylistIDImage", PlaylistImage);
            i.putExtra("ScreenView", ScreenView);
            i.putExtra("PlaylistType", PlaylistType);
            i.putExtra("Liked", "0");
            startActivity(i);
            finish();
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        prepareData(AddPlaylistActivity.this);
        super.onResume();
    }

    private void prepareData(Context ctx) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<PlaylistingModel> listCall = APIClient.getClient().getPlaylisting(UserID);
            listCall.enqueue(new Callback<PlaylistingModel>() {
                @Override
                public void onResponse(Call<PlaylistingModel> call, Response<PlaylistingModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            PlaylistingModel model = response.body();
//                            if (model.getResponseData().size() == 0) {
//                                binding.llError.setVisibility(View.GONE);
//                                binding.rvPlayLists.setVisibility(View.GONE);
//                            } else {
                            binding.rvPlayLists.setVisibility(View.VISIBLE);
                            AddPlaylistAdapter addPlaylistAdapter = new AddPlaylistAdapter(model.getResponseData(), ctx);
                            binding.rvPlayLists.setAdapter(addPlaylistAdapter);
//                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<PlaylistingModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private void callAddPlaylistFromPlaylist(String PlaylistID, String name, Dialog dialog, String d, String Created,String New) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<AddToPlaylist> listCall = APIClient.getClient().getAddSearchAudioFromPlaylist(UserID, AudioId, PlaylistID, FromPlaylistID);
            listCall.enqueue(new Callback<AddToPlaylist>() {
                @Override
                public void onResponse(Call<AddToPlaylist> call, Response<AddToPlaylist> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            AddToPlaylist listModels = response.body();
                            if (listModels.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
//                                BWSApplication.showToast(listModels.getResponseMessage(), ctx);
                                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                                int pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
                                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                                if (audioPlay) {
                                    if (AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                                        Gson gsonx = new Gson();
                                        String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gsonx));
                                        Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                                        }.getType();
                                        ArrayList<MainPlayModel> mainPlayModelListold = new ArrayList<>();
                                        mainPlayModelListold = gsonx.fromJson(json, type);
                                        String id = mainPlayModelListold.get(pos).getID();
                                        int size = mainPlayModelListold.size();
                                        ArrayList<MainPlayModel> mainPlayModelList = new ArrayList<>();
                                        ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs = new ArrayList<>();
                                        for (int i = 0; i < listModels.getResponseData().size(); i++) {
                                            MainPlayModel mainPlayModel = new MainPlayModel();
                                            mainPlayModel.setID(listModels.getResponseData().get(i).getID());
                                            mainPlayModel.setName(listModels.getResponseData().get(i).getName());
                                            mainPlayModel.setAudioFile(listModels.getResponseData().get(i).getAudioFile());
                                            mainPlayModel.setPlaylistID(listModels.getResponseData().get(i).getPlaylistID());
                                            mainPlayModel.setAudioDirection(listModels.getResponseData().get(i).getAudioDirection());
                                            mainPlayModel.setAudiomastercat(listModels.getResponseData().get(i).getAudiomastercat());
                                            mainPlayModel.setAudioSubCategory(listModels.getResponseData().get(i).getAudioSubCategory());
                                            mainPlayModel.setImageFile(listModels.getResponseData().get(i).getImageFile());
                                            mainPlayModel.setLike(listModels.getResponseData().get(i).getLike());
                                            mainPlayModel.setDownload(listModels.getResponseData().get(i).getDownload());
                                            mainPlayModel.setAudioDuration(listModels.getResponseData().get(i).getAudioDuration());
                                            mainPlayModelList.add(mainPlayModel);
                                        }
                                        for (int i = 0; i < listModels.getResponseData().size(); i++) {
                                            SubPlayListModel.ResponseData.PlaylistSong mainPlayModel = new SubPlayListModel.ResponseData.PlaylistSong();
                                            mainPlayModel.setID(listModels.getResponseData().get(i).getID());
                                            mainPlayModel.setName(listModels.getResponseData().get(i).getName());
                                            mainPlayModel.setAudioFile(listModels.getResponseData().get(i).getAudioFile());
                                            mainPlayModel.setPlaylistID(listModels.getResponseData().get(i).getPlaylistID());
                                            mainPlayModel.setAudioDirection(listModels.getResponseData().get(i).getAudioDirection());
                                            mainPlayModel.setAudiomastercat(listModels.getResponseData().get(i).getAudiomastercat());
                                            mainPlayModel.setAudioSubCategory(listModels.getResponseData().get(i).getAudioSubCategory());
                                            mainPlayModel.setImageFile(listModels.getResponseData().get(i).getImageFile());
                                            mainPlayModel.setLike(listModels.getResponseData().get(i).getLike());
                                            mainPlayModel.setDownload(listModels.getResponseData().get(i).getDownload());
                                            mainPlayModel.setAudioDuration(listModels.getResponseData().get(i).getAudioDuration());
                                            playlistSongs.add(mainPlayModel);
                                        }
                                        for (int i = 0; i < mainPlayModelList.size(); i++) {
                                            if (mainPlayModelList.get(i).getID().equalsIgnoreCase(id)) {
                                                pos = i;
                                                break;
                                            }
                                        }
                                        SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedd.edit();
                                        Gson gson = new Gson();
                                        String jsonx = gson.toJson(mainPlayModelList);
                                        String json1 = gson.toJson(playlistSongs);
                                        editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
                                        editor.putString(CONSTANTS.PREF_KEY_audioList, jsonx);
                                        editor.putInt(CONSTANTS.PREF_KEY_position, pos);
                                        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                                        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                        editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistID);
                                        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "myPlaylist");
                                        editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
                                        editor.commit();

                                        if (!mainPlayModelList.get(pos).getAudioFile().equals("")) {
                                            List<File> filesDownloaded = new ArrayList<>();
                                            List<String> downloadAudioDetailsList = new ArrayList<>();
                                            GlobalInitExoPlayer ge = new GlobalInitExoPlayer();
                                            ge.AddAudioToPlayer(size, mainPlayModelList, downloadAudioDetailsList, ctx);
                                        }
                                    }
                                }
                                if (comeAddPlaylist == 1) {
                                    final Dialog dialog = new Dialog(ctx);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.go_to_playlist);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                    final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                                    final RelativeLayout rlCreate = dialog.findViewById(R.id.rlCreate);
                                    dialog.setOnKeyListener((v, keyCode, event) -> {
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            dialog.dismiss();
                                            return true;
                                        }
                                        return false;
                                    });

                                    rlCreate.setOnClickListener(view2 -> {
                                        addToPlayList = true;
                                        MyPlaylistId = PlaylistID;
                                        dialog.dismiss();
                                        finish();
                                    });

                                    tvCancel.setOnClickListener(v -> {
                                        dialog.dismiss();
                                        finish();
                                    });
                                    dialog.show();
                                    dialog.setCancelable(false);
                                } else {
                                    if (d.equalsIgnoreCase("0")) {
                                        dialog.dismiss();
                                    }
                                    final Dialog dialog = new Dialog(ctx);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.go_to_playlist);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                    final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                                    final RelativeLayout rlCreate = dialog.findViewById(R.id.rlCreate);
                                    dialog.setOnKeyListener((v, keyCode, event) -> {
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            dialog.dismiss();
                                            return true;
                                        }
                                        return false;
                                    });

                                    rlCreate.setOnClickListener(view2 -> {
                                        comefrom_search = 0;
                                        addToPlayList = true;
                                        MyPlaylistId = PlaylistID;
                                        dialog.dismiss();
                                        Intent intent = new Intent(ctx, DashboardActivity.class);
                                        intent.putExtra("New", New);
                                        intent.putExtra("Goplaylist", "1");
                                        intent.putExtra("PlaylistID", PlaylistID);
                                        intent.putExtra("PlaylistName", name);
                                        intent.putExtra("PlaylistImage", "");
                                        intent.putExtra("PlaylistType", Created);
                                        startActivity(intent);
                                        finish();
                                    });

                                    tvCancel.setOnClickListener(v -> {
                                        dialog.dismiss();
                                        finish();
                                    });
                                    dialog.show();
                                    dialog.setCancelable(false);
                                }
                            } else if (listModels.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))) {
                                BWSApplication.showToast(listModels.getResponseMessage(), ctx);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AddToPlaylist> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private class AddPlaylistAdapter extends RecyclerView.Adapter<AddPlaylistAdapter.MyViewHolder> {
        Context ctx;
        private List<PlaylistingModel.ResponseData> listModel;

        public AddPlaylistAdapter(List<PlaylistingModel.ResponseData> listModel, Context ctx) {
            this.listModel = listModel;
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AddPlayListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.add_play_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(listModel.get(position).getName());
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.16f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            Glide.with(ctx).load(listModel.get(position).getImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            holder.binding.llMainLayout.setOnClickListener(view -> {
                String PlaylistID = listModel.get(position).getID();
                String Created = listModel.get(position).getCreated();
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall add after playing the disclaimer", ctx);
                    } else {
                        final Dialog dialogx = new Dialog(ctx);
                        callAddPlaylistFromPlaylist(PlaylistID, listModel.get(position).getName(), dialogx, "1", Created,"0");
                    }
                } else {
                    final Dialog dialogx = new Dialog(ctx);
                    callAddPlaylistFromPlaylist(PlaylistID, listModel.get(position).getName(), dialogx, "1", Created,"0");
                }
            });
        }

        @Override
        public int getItemCount() {
            return listModel.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            AddPlayListLayoutBinding binding;

            public MyViewHolder(AddPlayListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}