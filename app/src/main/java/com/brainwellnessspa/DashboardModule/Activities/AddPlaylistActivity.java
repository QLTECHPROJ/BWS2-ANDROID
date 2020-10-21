package com.brainwellnessspa.DashboardModule.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityAddPlaylistBinding;
import com.brainwellnessspa.databinding.AddPlayListLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Activities.MyPlaylistActivity.comeAddPlaylist;
import static com.brainwellnessspa.DashboardModule.Search.SearchFragment.comefrom_search;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment.isDisclaimer;

public class AddPlaylistActivity extends AppCompatActivity {
    public static boolean addToPlayList = false;
    public static String MyPlaylistId = "";
    ActivityAddPlaylistBinding binding;
    String UserID, AudioId, FromPlaylistID;
    Context ctx;
    Activity activity;

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

        binding.llBack.setOnClickListener(view -> {
            comefrom_search = 0;
            finish();
        });

        RecyclerView.LayoutManager played = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvPlayLists.setLayoutManager(played);
        binding.rvPlayLists.setItemAnimator(new DefaultItemAnimator());
        prepareData(ctx);
    }

    @Override
    public void onBackPressed() {
        comefrom_search = 0;
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareData(AddPlaylistActivity.this);
    }

    private void prepareData(Context ctx) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<PlaylistingModel> listCall = APIClient.getClient().getPlaylisting(UserID);
            listCall.enqueue(new Callback<PlaylistingModel>() {
                @Override
                public void onResponse(Call<PlaylistingModel> call, Response<PlaylistingModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        PlaylistingModel model = response.body();
                        AddPlaylistAdapter addPlaylistAdapter = new AddPlaylistAdapter(model.getResponseData(), ctx);
                        binding.rvPlayLists.setAdapter(addPlaylistAdapter);
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

    private void callAddPlaylistFromPlaylist(String PlaylistID, Dialog dialog, String d) {
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
                                BWSApplication.showToast(listModels.getResponseMessage(), ctx);
                                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
                                                                /*Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                                                                Bundle bundle = new Bundle();
                                                                bundle.putString("New", "0");
                                                                bundle.putString("PlaylistID", listsModel.getResponseData().getId());
                                                                bundle.putString("PlaylistName", listsModel.getResponseData().getName());
                                                                bundle.putString("MyDownloads", "0");
                                                                myPlaylistsFragment.setArguments(bundle);
                                                                FragmentManager fragmentManager1 = getSupportFragmentManager();
                                                                fragmentManager1.beginTransaction()
                                                                        .replace(R.id.flContainer, myPlaylistsFragment)
                                                                        .commit();*/
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
                                    finish();
                                                            /*final Dialog dialog = new Dialog(ctx);
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
                                                                MyPlaylistId = listsModel.getResponseData().getId();
                                                                dialog.dismiss();
                                                                Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                                                                Bundle bundle = new Bundle();
                                                                bundle.putString("New", "0");
                                                                bundle.putString("PlaylistID", listsModel.getResponseData().getId());
                                                                bundle.putString("PlaylistName", listsModel.getResponseData().getName());
                                                                bundle.putString("MyDownloads", "0");
                                                                myPlaylistsFragment.setArguments(bundle);
                                                                FragmentManager fragmentManager1 = getSupportFragmentManager();
                                                                fragmentManager1.beginTransaction()
                                                                        .replace(R.id.flContainer, myPlaylistsFragment)
                                                                        .commit();
                                                            });

                                                            tvCancel.setOnClickListener(v -> {
                                                                dialog.dismiss();
                                                                finish();
                                                            });
                                                            dialog.show();
                                                            dialog.setCancelable(false);*/
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

            binding.btnAddPlatLists.setOnClickListener(view -> {

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
                                    if (response.isSuccessful()) {
                                        CreatePlaylistModel listsModel = response.body();
                                        dialog.dismiss();
                                        prepareData(ctx);
                                        String PlaylistID = listsModel.getResponseData().getId();
                                        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                                        boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                                        String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                                        if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                                            if (isDisclaimer == 1) {
                                                BWSApplication.showToast("The audio shall add after playing the disclaimer", ctx);
                                            } else {
                                                callAddPlaylistFromPlaylist(PlaylistID, dialog, "0");
                                            }
                                        } else {
                                            callAddPlaylistFromPlaylist(PlaylistID, dialog, "0");

                                        }
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

            holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String PlaylistID = listModel.get(position).getID();
                    SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                    if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                        if (isDisclaimer == 1) {
                            BWSApplication.showToast("The audio shall add after playing the disclaimer", ctx);
                        } else {
                            final Dialog dialogx = new Dialog(ctx);
                            callAddPlaylistFromPlaylist(PlaylistID, dialogx, "1");
                        }
                    } else {
                        final Dialog dialogx = new Dialog(ctx);
                        callAddPlaylistFromPlaylist(PlaylistID, dialogx, "1");
                    }
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