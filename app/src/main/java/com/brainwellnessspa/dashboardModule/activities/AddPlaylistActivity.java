package com.brainwellnessspa.dashboardModule.activities;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardOldModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.APINewClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.dashboardModule.models.AddToPlaylistModel;
import com.brainwellnessspa.dashboardModule.models.CreateNewPlaylistModel;
import com.brainwellnessspa.dashboardModule.models.CreatePlaylistingModel;
import com.brainwellnessspa.databinding.ActivityAddPlaylistBinding;
import com.brainwellnessspa.databinding.AddPlayListLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class AddPlaylistActivity extends AppCompatActivity {
    public static boolean addToPlayList = false;
    public static String MyPlaylistId = "";
    ActivityAddPlaylistBinding binding;
    String UserName, CoUSERID, USERID, AudioId = "", FromPlaylistID = "", PlaylistName = "", ScreenView = "", PlaylistImage = "", PlaylistType = "";
    Context ctx;
    Activity activity;
    Properties p;
    int stackStatus = 0;
    boolean myBackPress = false;
    private int numStarted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_playlist);
        ctx = AddPlaylistActivity.this;
        activity = AddPlaylistActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        CoUSERID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        UserName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "");

        if (getIntent().getExtras() != null) {
            AudioId = getIntent().getStringExtra("AudioId");
            FromPlaylistID = getIntent().getStringExtra("PlaylistID");
            PlaylistName = getIntent().getStringExtra("PlaylistName");
            PlaylistImage = getIntent().getStringExtra("PlaylistImage");
            PlaylistType = getIntent().getStringExtra("PlaylistType");
            ScreenView = getIntent().getStringExtra("ScreenView");
        }

        binding.llBack.setOnClickListener(view -> {
     /*       comefrom_search = 0;
            myBackPress = true;
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
            }*/
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }
        RecyclerView.LayoutManager played = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvPlayLists.setLayoutManager(played);
        binding.rvPlayLists.setItemAnimator(new DefaultItemAnimator());
        binding.llError.setVisibility(View.GONE);
        binding.tvFound.setText("No result found");

        binding.btnAddPlatLists.setOnClickListener(view -> {
            myBackPress = true;
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
                        btnSendCode.setTextColor(getResources().getColor(R.color.light_black));
                        btnSendCode.setBackgroundResource(R.drawable.white_round_cornor);
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
                    BWSApplication.showToast("Please provide the playlist's name", activity);
                } else {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        Call<CreateNewPlaylistModel> listCall = APINewClient.getClient().getCreatePlaylist(CoUSERID, edtCreate.getText().toString());
                        listCall.enqueue(new Callback<CreateNewPlaylistModel>() {
                            @Override
                            public void onResponse(Call<CreateNewPlaylistModel> call, Response<CreateNewPlaylistModel> response) {
                                try {
                                    if (response.isSuccessful()) {
                                        CreateNewPlaylistModel listsModel = response.body();
                                        if (listsModel.getResponseData().getIscreate().equalsIgnoreCase("1")) {
                                            dialog.dismiss();
                                            prepareData(ctx);
                                            String PlaylistID = listsModel.getResponseData().getPlaylistID();
                                            String Created = listsModel.getResponseData().getIscreate();
                                            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                            boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                            String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                                            String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                                            if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                                                if (isDisclaimer == 1) {
                                                    BWSApplication.showToast("The audio shall add after playing the disclaimer", activity);
                                                } else {
                                                    callAddPlaylistFromPlaylist(PlaylistID, listsModel.getResponseData().getPlaylistName(), dialog, "0", Created, "1");
                                                }
                                            } else {
                                                callAddPlaylistFromPlaylist(PlaylistID, listsModel.getResponseData().getPlaylistName(), dialog, "0", Created, "1");
                                            }
//                                            Properties p = new Properties();
//                                            p.putValue("userId", UserID);
//                                            p.putValue("playlistId", PlaylistID);
//                                            p.putValue("playlistName", listsModel.getResponseData().getName());
//                                            p.putValue("source", "Add To Playlist Screen");
//                                            BWSApplication.addToSegment("Playlist Created", p, CONSTANTS.track);
                                        } else {
                                            BWSApplication.showToast(listsModel.getResponseMessage(), activity);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<CreateNewPlaylistModel> call, Throwable t) {
                            }
                        });
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), activity);
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
     /*   comefrom_search = 0;
        myBackPress = true;
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
        }*/
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        prepareData(AddPlaylistActivity.this);
        super.onResume();
    }

    private void prepareData(Context ctx) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<CreatePlaylistingModel> listCall = APINewClient.getClient().getPlaylisting(CoUSERID);
            listCall.enqueue(new Callback<CreatePlaylistingModel>() {
                @Override
                public void onResponse(Call<CreatePlaylistingModel> call, Response<CreatePlaylistingModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            CreatePlaylistingModel model = response.body();
                            if (model.getResponseData().size() == 0) {
                                binding.llError.setVisibility(View.GONE);
                                binding.rvPlayLists.setVisibility(View.GONE);
                            } else {
                                binding.rvPlayLists.setVisibility(View.VISIBLE);

//                            p = new Properties();
//                            p.putValue("userId", UserID);
//                            p.putValue("source", ScreenView);
//                            ArrayList<SegmentPlaylist> section = new ArrayList<>();
//                            for (int i = 0; i < model.getResponseData().size(); i++) {
//                                SegmentPlaylist e = new SegmentPlaylist();
//                                e.setPlaylistId(model.getResponseData().get(i).getID());
//                                e.setPlaylistName(model.getResponseData().get(i).getName());
//                                e.setPlaylistType(model.getResponseData().get(i).getCreated());
//                                e.setPlaylistDuration(model.getResponseData().get(i).getTotalhour() + "h " + model.getResponseData().get(i).getTotalminute() + "m");
//                                e.setAudioCount(model.getResponseData().get(i).getTotalAudio());
//                                section.add(e);
//                            }
//                            Gson gson = new Gson();
//                            p.putValue("playlists", gson.toJson(section));
//                            BWSApplication.addToSegment("Playlist List Viewed", p, CONSTANTS.screen);
                                AddPlaylistAdapter addPlaylistAdapter = new AddPlaylistAdapter(model.getResponseData(), ctx);
                                binding.rvPlayLists.setAdapter(addPlaylistAdapter);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<CreatePlaylistingModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity);
        }
    }

    private void callAddPlaylistFromPlaylist(String PlaylistID, String name, Dialog dialog, String d, String Created, String New) {

        myBackPress = true;
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<AddToPlaylistModel> listCall = APINewClient.getClient().getAddSearchAudioFromPlaylist(CoUSERID, AudioId, PlaylistID, FromPlaylistID);
            listCall.enqueue(new Callback<AddToPlaylistModel>() {
                @Override
                public void onResponse(Call<AddToPlaylistModel> call, Response<AddToPlaylistModel> response) {
                    try {
                        AddToPlaylistModel listModels = response.body();
                        if (listModels.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
//                                BWSApplication.showToast(listModels.getResponseMessage(), activity);
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
                                        List<String> downloadAudioDetailsList = new ArrayList<>();
                                        GlobalInitExoPlayer ge = new GlobalInitExoPlayer();
                                        ge.AddAudioToPlayer(size, mainPlayModelList, downloadAudioDetailsList, ctx);
                                    }
                                }
                            }
                          /*  if (comeAddPlaylist == 1) {
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

                                    addToPlayList = true;
                                    MyPlaylistId = PlaylistID;
                                    Intent intent = new Intent(ctx, DashboardActivity.class);
                                    intent.putExtra("Goplaylist", "1");
                                    intent.putExtra("New", New);
                                    intent.putExtra("PlaylistID", PlaylistID);
                                    intent.putExtra("PlaylistName", name);
                                    intent.putExtra("PlaylistImage", "");
                                    intent.putExtra("PlaylistType", Created);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(0, 0);
                                    dialog.dismiss();
                                });

                                tvCancel.setOnClickListener(v -> {
                                    dialog.dismiss();
                                    finish();
                                });
                                dialog.show();
                                dialog.setCancelable(false);
                            }*/
                        } else if (listModels.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))) {

                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            BWSApplication.showToast(listModels.getResponseMessage(), activity);
                        }
                    } catch (Exception e) {

                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AddToPlaylistModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity);
        }
    }

    private class AddPlaylistAdapter extends RecyclerView.Adapter<AddPlaylistAdapter.MyViewHolder> {
        Context ctx;
        private List<CreatePlaylistingModel.ResponseData> listModel;

        public AddPlaylistAdapter(List<CreatePlaylistingModel.ResponseData> listModel, Context ctx) {
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
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(listModel.get(position).getImage()).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(32))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            holder.binding.llMainLayout.setOnClickListener(view -> {
                String PlaylistID = listModel.get(position).getId();
                String Created = listModel.get(position).getCreated();
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall add after playing the disclaimer", activity);
                    } else {
                        final Dialog dialogx = new Dialog(ctx);
                        callAddPlaylistFromPlaylist(PlaylistID, listModel.get(position).getName(), dialogx, "1", Created, "0");
                    }
                } else {
                    final Dialog dialogx = new Dialog(ctx);
                    callAddPlaylistFromPlaylist(PlaylistID, listModel.get(position).getName(), dialogx, "1", Created, "0");
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

    class AppLifecycleCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                stackStatus = 1;
                Log.e("APPLICATION", "APP IN FOREGROUND");
                //app went to foreground
            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            numStarted--;
            if (numStarted == 0) {
                if (!myBackPress) {
                    Log.e("APPLICATION", "Back press false");
                    stackStatus = 2;
                } else {
                    myBackPress = true;
                    stackStatus = 1;
                    Log.e("APPLICATION", "back press true ");
                }
                Log.e("APPLICATION", "App is in BACKGROUND");
                // app went to background
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationId);
                relesePlayer(getApplicationContext());
            } else {
                Log.e("Destroy", "Activity go in main activity");
            }
        }
    }

}