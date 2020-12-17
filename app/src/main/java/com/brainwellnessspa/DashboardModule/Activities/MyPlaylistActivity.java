package com.brainwellnessspa.DashboardModule.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Adapters.DirectionAdapter;
import com.brainwellnessspa.DashboardModule.Models.PlaylistLikeModel;
import com.brainwellnessspa.DashboardModule.Models.RenamePlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.SucessModel;
import com.brainwellnessspa.DashboardModule.Playlist.PlaylistFragment;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.RoomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityMyPlaylistBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment.RefreshIconData;

public class MyPlaylistActivity extends AppCompatActivity {
    public static int deleteFrg = 0;
    public static int ComeFindAudio = 0;
    ActivityMyPlaylistBinding binding;
    String UserID, PlaylistID, Download = "", Liked = "", PlaylistDesc = "", PlaylistName = "";
    Context ctx;
    Activity activity;
    public static int comeAddPlaylist = 0;
    public static int comeRename = 0;
    private long mLastClickTime = 0;
    List<DownloadAudioDetails> downloadAudioDetailsList;
    List<DownloadAudioDetails> playlistWiseAudioDetails;
    DownloadPlaylistDetails downloadPlaylistDetails;
    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongsList;
    List<String> fileNameList, playlistDownloadId, remainAudio;
    int SongListSize = 0, count;
  /*  private Handler handler1;
    private Runnable UpdateSongTime1 = new Runnable() {
        @Override
        public void run() {
*/
  /*            if (fileNameList.size() != 0) {
                if (remainAudio.size() <= SongListSize) {
                    int total = SongListSize;
                    int remain = remainAudio.size();
                    int complate = total - remain;
                    long progressPercent = complate * 100 / total;
                    int downloadProgress1 = (int) progressPercent;
                    if (SongListSize == 1) {
                        if (downloadProgress <= 100) {
                            binding.pbProgress.setProgress(downloadProgress);
                            binding.pbProgress.setVisibility(View.VISIBLE);
                            binding.ivDownloads.setVisibility(View.GONE);
                            if(downloadProgress == 100){
                                getDownloadData();
                            }
                        }
                    } else if (downloadProgress1 <= 100) {
                        if(downloadProgress1 == 100){
                            getDownloadData();
                            binding.pbProgress.setVisibility(View.GONE);
                            binding.ivDownloads.setVisibility(View.VISIBLE);
                            handler1.removeCallbacks(UpdateSongTime1);
                        }else{
                            binding.pbProgress.setProgress(downloadProgress1);
                            binding.pbProgress.setVisibility(View.VISIBLE);
                            binding.ivDownloads.setVisibility(View.GONE);
                        }
                    } else {
                        binding.pbProgress.setVisibility(View.GONE);
                        binding.ivDownloads.setVisibility(View.VISIBLE);
                        handler1.removeCallbacks(UpdateSongTime1);
                    }
                }
                getDownloadData();
                handler1.postDelayed(this, 500);
            }else{
                binding.pbProgress.setVisibility(View.GONE);
                binding.ivDownloads.setVisibility(View.VISIBLE);
                handler1.removeCallbacks(UpdateSongTime1);
                getDownloadData();
            }*//*
//            getMediaByPer(PlaylistID,SongListSize);
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_playlist);
        ctx = MyPlaylistActivity.this;
        activity = MyPlaylistActivity.this;
//        handler1 = new Handler();
        fileNameList = new ArrayList<>();
        playlistDownloadId = new ArrayList<>();
        remainAudio = new ArrayList<>();
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        playlistSongsList = new ArrayList<>();
        downloadAudioDetailsList = new ArrayList<>();
        playlistWiseAudioDetails = new ArrayList<>();
//        downloadPlaylistDetailsList = new ArrayList<>();
        if (getIntent().getExtras() != null) {
            PlaylistID = getIntent().getStringExtra(CONSTANTS.PlaylistID);
        }

        if (getIntent().getExtras() != null) {
            Liked = getIntent().getStringExtra("Liked");
        }

//        downloadAudioDetailsList = GetAllMedia();
        CallObserverMethodGetAllMedia();
        GetPlaylistDetail();

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComeFindAudio = 1;
//                handler1.removeCallbacks(UpdateSongTime1);
                finish();
            }
        });

        binding.llAddPlaylist.setOnClickListener(view -> {
            comeAddPlaylist = 1;
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(ctx, AddPlaylistActivity.class);
            i.putExtra("AudioId", "");
            i.putExtra("PlaylistID", PlaylistID);
            startActivity(i);
        });

        binding.llLikes.setOnClickListener(v -> CallPlaylistLike(PlaylistID));

        binding.llFind.setOnClickListener(view -> {
            ComeFindAudio = 2;
            finish();
        });

        binding.tvReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.full_desc_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                final TextView tvDesc = dialog.findViewById(R.id.tvDesc);
                final RelativeLayout tvClose = dialog.findViewById(R.id.tvClose);
                tvDesc.setText(PlaylistDesc);

                dialog.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                });

                tvClose.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
                dialog.setCancelable(false);
            }
        });

        binding.llRename.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(ctx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.create_palylist);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final EditText edtCreate = dialog.findViewById(R.id.edtCreate);
            final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
            final TextView tvHeading = dialog.findViewById(R.id.tvHeading);
            final Button btnSendCode = dialog.findViewById(R.id.btnSendCode);
            tvHeading.setText(R.string.Rename_your_playlist);
            btnSendCode.setText(R.string.Save);
            edtCreate.requestFocus();
            edtCreate.setText(PlaylistName);
            int position1 = edtCreate.getText().length();
            Editable editObj = edtCreate.getText();
            Selection.setSelection(editObj, position1);
            btnSendCode.setEnabled(true);
            btnSendCode.setTextColor(getResources().getColor(R.color.white));
            btnSendCode.setBackgroundResource(R.drawable.extra_round_cornor);
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
                if (BWSApplication.isNetworkConnected(ctx)) {
                    BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    Call<RenamePlaylistModel> listCall1 = APIClient.getClient().getRenamePlaylist(UserID, PlaylistID, edtCreate.getText().toString());
                    listCall1.enqueue(new Callback<RenamePlaylistModel>() {
                        @Override
                        public void onResponse(Call<RenamePlaylistModel> call1, Response<RenamePlaylistModel> response1) {
                            try {
                                if (response1.isSuccessful()) {
                                    comeRename = 1;
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                    RenamePlaylistModel listModel = response1.body();
                                    BWSApplication.showToast(listModel.getResponseMessage(), ctx);
                                    dialog.dismiss();
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<RenamePlaylistModel> call1, Throwable t) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        }
                    });
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                }

            });
            tvCancel.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
            dialog.setCancelable(false);
        });

        binding.llDelete.setOnClickListener(view -> {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
            if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                BWSApplication.showToast("Currently this playlist is in player,so you can't delete this playlist as of now", ctx);
            } else {
                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.delete_playlist);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                final TextView tvHeader = dialog.findViewById(R.id.tvHeader);
                final RelativeLayout tvconfirm = dialog.findViewById(R.id.tvconfirm);
                tvHeader.setText("Are you sure you want to delete " + PlaylistName + "  playlist?");
                dialog.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        Fragment playlistFragment = new PlaylistFragment();
                        FragmentManager fragmentManager1 = getSupportFragmentManager();
                        fragmentManager1.beginTransaction()
                                .add(R.id.flContainer, playlistFragment)
                                .commit();
                        Bundle bundle = new Bundle();
                        playlistFragment.setArguments(bundle);
                        return true;
                    }
                    return false;
                });

                tvconfirm.setOnClickListener(v -> {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        Call<SucessModel> listCall12 = APIClient.getClient().getDeletePlaylist(UserID, PlaylistID);
                        listCall12.enqueue(new Callback<SucessModel>() {
                            @Override
                            public void onResponse(Call<SucessModel> call12, Response<SucessModel> response12) {
                                try {
                                    if (response12.isSuccessful()) {
                                        deleteFrg = 1;
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                        SucessModel listModel = response12.body();
                                        dialog.dismiss();
                                        BWSApplication.showToast(listModel.getResponseMessage(), ctx);
                                        finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<SucessModel> call12, Throwable t) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            }
                        });
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                    }
                });

                tvGoBack.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
                dialog.setCancelable(false);
            }
        });

        binding.llDownload.setOnClickListener(view -> callDownload());
    }

    private void CallObserverMethodGetAllMedia() {
       DatabaseClient
                .getInstance(ctx)
                .getaudioDatabase()
                .taskDao()
                .geAllData12().observe(this,audioList -> {
           this.downloadAudioDetailsList = audioList;

       });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPrepareData();
    }

    /*  private void getMediaByPer(String playlistID, int totalAudio) {
          class getMediaByPer extends AsyncTask<Void, Void, Void> {

              @Override
              protected Void doInBackground(Void... voids) {
                  count= DatabaseClient.getInstance(ctx)
                          .getaudioDatabase()
                          .taskDao()
                          .getCountDownloadProgress("Complete",playlistID);

                  return null;
              }

              @Override
              protected void onPostExecute(Void aVoid) {
                  downloadPlaylistDetailsList = GetPlaylistDetail();
                  if(downloadPlaylistDetailsList.size()!=0) {
                      if (count < totalAudio) {
                          long progressPercent = count * 100 / totalAudio;
                          int downloadProgress1 = (int) progressPercent;
                          binding.pbProgress.setVisibility(View.VISIBLE);
                          binding.ivDownloads.setVisibility(View.GONE);
                          binding.pbProgress.setProgress(downloadProgress1);
                          handler1.postDelayed(UpdateSongTime1, 300);
                      } else {
                          binding.pbProgress.setVisibility(View.GONE);
                          binding.ivDownloads.setVisibility(View.VISIBLE);
                          handler1.removeCallbacks(UpdateSongTime1);
                      }
                  }
                  super.onPostExecute(aVoid);
              }
          }

          getMediaByPer st = new getMediaByPer();
          st.execute();
      }*/

    /*
        private void getDownloadData() {
            try {
                SharedPreferences sharedy = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
                Gson gson = new Gson();
                String jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
                String json1 = sharedy.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
                String jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
                if (!jsony.equalsIgnoreCase(String.valueOf(gson))) {
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    fileNameList = gson.fromJson(jsony, type);
                    playlistDownloadId = gson.fromJson(jsonq, type);
                    remainAudio = new ArrayList<>();
                    if (playlistDownloadId.size() != 0) {
                        playlistDownloadId.contains(PlaylistID);
                        for (int i = 0; i < fileNameList.size(); i++) {
                            if (playlistDownloadId.get(i).equalsIgnoreCase(PlaylistID)) {
                                remainAudio.add(playlistDownloadId.get(i));
                            }
                        }
                        if (downloadPlaylistDetailsList.size() != 0) {
                            if (remainAudio.size() < SongListSize) {
                                handler1.postDelayed(UpdateSongTime1, 500);
                            }
                        }
                        //
                    }else {
                        fileNameList = new ArrayList<>();
                        playlistDownloadId = new ArrayList<>();
                        remainAudio = new ArrayList<>();
                    }
                } else {
                    fileNameList = new ArrayList<>();
                    playlistDownloadId = new ArrayList<>();
                    remainAudio = new ArrayList<>();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    */
  /*       public List<DownloadAudioDetails> GetAllMedia() {
   class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllData1();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }
        GetTask st = new GetTask();
        st.execute();
        return downloadAudioDetailsList;
    }*/

    private void callDownload() {
        List<String> url = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<String> downloadPlaylistId = new ArrayList<>();
        ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs2 = new ArrayList<>();
        playlistSongs2 = playlistSongsList;
        if (downloadAudioDetailsList.size() != 0) {
            for (int y = 0; y < downloadAudioDetailsList.size(); y++) {
                if (playlistSongs2.size() == 0) {
                    break;
                } else {
                    for (int x = 0; x < playlistSongs2.size(); x++) {
                        if (playlistSongs2.size() != 0) {
                            if (playlistSongs2.get(x).getAudioFile().equalsIgnoreCase(downloadAudioDetailsList.get(y).getAudioFile())) {
                                playlistSongs2.remove(x);
                            }
                            if (playlistSongs2.size() == 0) {
                                break;
                            }
                        } else break;
                    }
                }
            }
        }
        for (int x = 0; x < playlistSongs2.size(); x++) {
            name.add(playlistSongs2.get(x).getName());
            url.add(playlistSongs2.get(x).getAudioFile());
            downloadPlaylistId.add(playlistSongs2.get(x).getPlaylistID());
        }
        enableDisableDownload(false, "orange");
        byte[] encodedBytes = new byte[1024];

        SharedPreferences sharedx = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
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
            DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());
            downloadMedia.encrypt1(url, name, downloadPlaylistId/*, playlistSongs*/);
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
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
        SongListSize = playlistSongsList.size();
        savePlaylist();
        saveAllMedia(playlistSongsList, encodedBytes);

    }

    private void savePlaylist() {
        class SaveMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .insertPlaylist(downloadPlaylistDetails);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
//                llDownload.setClickable(false);
//                llDownload.setEnabled(false);
//                getMediaByPer(PlaylistID,SongListSize);
                super.onPostExecute(aVoid);
            }
        }
        SaveMedia st = new SaveMedia();
        st.execute();
    }

    private void saveAllMedia(ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs, byte[] encodedBytes) {
        class SaveMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
                for (int i = 0; i < playlistSongs.size(); i++) {
                    downloadAudioDetails.setID(playlistSongs.get(i).getID());
                    downloadAudioDetails.setName(playlistSongs.get(i).getName());
                    downloadAudioDetails.setAudioFile(playlistSongs.get(i).getAudioFile());
                    downloadAudioDetails.setAudioDirection(playlistSongs.get(i).getAudioDirection());
                    downloadAudioDetails.setAudiomastercat(playlistSongs.get(i).getAudiomastercat());
                    downloadAudioDetails.setAudioSubCategory(playlistSongs.get(i).getAudioSubCategory());
                    downloadAudioDetails.setImageFile(playlistSongs.get(i).getImageFile());
                    downloadAudioDetails.setLike(playlistSongs.get(i).getLike());
                    downloadAudioDetails.setDownload("1");
                    downloadAudioDetails.setAudioDuration(playlistSongs.get(i).getAudioDuration());
                    downloadAudioDetails.setIsSingle("0");
                    downloadAudioDetails.setPlaylistId(playlistSongs.get(i).getPlaylistID());
                    downloadAudioDetails.setIsDownload("pending");
                    downloadAudioDetails.setDownloadProgress(0);
                    DatabaseClient.getInstance(ctx)
                            .getaudioDatabase()
                            .taskDao()
                            .insertMedia(downloadAudioDetails);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
//                llDownload.setClickable(false);
//                llDownload.setEnabled(false);
                enableDisableDownload(false, "orange");
                super.onPostExecute(aVoid);
            }
        }

        SaveMedia st = new SaveMedia();
        st.execute();
    }

    @Override
    public void onBackPressed() {
        ComeFindAudio = 1;
//        handler1.removeCallbacks(UpdateSongTime1);
        finish();
    }

    private void getPrepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SubPlayListModel> listCall = APIClient.getClient().getSubPlayLists(UserID, PlaylistID);
            listCall.enqueue(new Callback<SubPlayListModel>() {
                @Override
                public void onResponse(Call<SubPlayListModel> call, Response<SubPlayListModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            SubPlayListModel model = response.body();
                            binding.llDownload.setVisibility(View.VISIBLE);
                            playlistSongsList = model.getResponseData().getPlaylistSongs();
                            downloadPlaylistDetails = new DownloadPlaylistDetails();
                            downloadPlaylistDetails.setPlaylistID(model.getResponseData().getPlaylistID());
                            downloadPlaylistDetails.setPlaylistName(model.getResponseData().getPlaylistName());
                            downloadPlaylistDetails.setPlaylistDesc(model.getResponseData().getPlaylistDesc());
                            downloadPlaylistDetails.setIsReminder(model.getResponseData().getIsReminder());
                            downloadPlaylistDetails.setPlaylistMastercat(model.getResponseData().getPlaylistMastercat());
                            downloadPlaylistDetails.setPlaylistSubcat(model.getResponseData().getPlaylistSubcat());
                            downloadPlaylistDetails.setPlaylistImage(model.getResponseData().getPlaylistImage());
                            downloadPlaylistDetails.setPlaylistImageDetails(model.getResponseData().getPlaylistImageDetail());
                            downloadPlaylistDetails.setTotalAudio(model.getResponseData().getTotalAudio());
                            downloadPlaylistDetails.setTotalDuration(model.getResponseData().getTotalDuration());
                            downloadPlaylistDetails.setTotalhour(model.getResponseData().getTotalhour());
                            downloadPlaylistDetails.setTotalminute(model.getResponseData().getTotalminute());
                            downloadPlaylistDetails.setCreated(model.getResponseData().getCreated());
                            downloadPlaylistDetails.setDownload(model.getResponseData().getDownload());
                            downloadPlaylistDetails.setLike(model.getResponseData().getLike());
                            binding.tvName.setText(model.getResponseData().getPlaylistName());

                            PlaylistDesc = model.getResponseData().getPlaylistDesc();
                            PlaylistName = model.getResponseData().getPlaylistName();
                            PlaylistID = model.getResponseData().getPlaylistID();
                            if (model.getResponseData().getPlaylistMastercat().equalsIgnoreCase("")) {
                                binding.tvDesc.setVisibility(View.GONE);
                            } else {
                                binding.tvDesc.setVisibility(View.VISIBLE);
                                binding.tvDesc.setText(model.getResponseData().getPlaylistMastercat());
                            }

                            if (model.getResponseData().getTotalAudio().equalsIgnoreCase("") ||
                                    model.getResponseData().getTotalAudio().equalsIgnoreCase("0") &&
                                            model.getResponseData().getTotalhour().equalsIgnoreCase("")
                                            && model.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                binding.tvTime.setText("0 Audio | 0h 0m");
                            } else {
                                if (model.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                    binding.tvTime.setText(model.getResponseData().getTotalAudio() + " Audio | "
                                            + model.getResponseData().getTotalhour() + "h 0m");
                                } else {
                                    binding.tvTime.setText(model.getResponseData().getTotalAudio() + " Audio | "
                                            + model.getResponseData().getTotalhour() + "h " + model.getResponseData().getTotalminute() + "m");
                                }
                            }

                            if (model.getResponseData().getCreated().equalsIgnoreCase("1")) {
                                binding.llOptions.setVisibility(View.GONE);
                                binding.llRename.setVisibility(View.VISIBLE);
                                binding.llDelete.setVisibility(View.VISIBLE);
                                binding.llFind.setVisibility(View.GONE);
                                binding.llLikes.setVisibility(View.VISIBLE);
                            } else if (model.getResponseData().getCreated().equalsIgnoreCase("0")) {
                                binding.llOptions.setVisibility(View.VISIBLE);
                                binding.llRename.setVisibility(View.GONE);
                                binding.llDelete.setVisibility(View.GONE);
                                binding.llLikes.setVisibility(View.VISIBLE);

                                if (Liked.equalsIgnoreCase("1")) {
                                    binding.llFind.setVisibility(View.GONE);
                                } else if (Liked.equalsIgnoreCase("0") || Liked.equalsIgnoreCase("")) {
                                    binding.llFind.setVisibility(View.VISIBLE);
                                }
                            }

                            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 20,
                                    1, 1, 0.54f, 20);
                            binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                            binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                            binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
                            if (!model.getResponseData().getPlaylistImage().equalsIgnoreCase("")) {
                                Glide.with(ctx).load(model.getResponseData().getPlaylistImage()).thumbnail(0.05f)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                            } else {
                                binding.ivRestaurantImage.setImageResource(R.drawable.ic_playlist_bg);
                            }

//                            getDownloadData();
                            SongListSize = model.getResponseData().getPlaylistSongs().size();
//                            getMediaByPer(PlaylistID,SongListSize);
//                            SongListSize = model.getResponseData().getPlaylistSongs().size();
                            Download = model.getResponseData().getDownload();
                            binding.llAddPlaylist.setVisibility(View.VISIBLE);
//                            getDownloadData();
                            if (model.getResponseData().getLike().equalsIgnoreCase("1")) {
                                binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                            } else if (model.getResponseData().getLike().equalsIgnoreCase("0") ||
                                    model.getResponseData().getLike().equalsIgnoreCase("")) {
                                binding.ivLike.setImageResource(R.drawable.ic_like_white_icon);
                            }

                            if (model.getResponseData().getPlaylistDesc().equalsIgnoreCase("")) {
                                binding.tvTitleDec.setVisibility(View.GONE);
                                binding.tvSubDec.setVisibility(View.GONE);
                            } else {
                                binding.tvTitleDec.setVisibility(View.VISIBLE);
                                binding.tvSubDec.setVisibility(View.VISIBLE);
                            }

                            binding.tvSubDec.setText(model.getResponseData().

                                    getPlaylistDesc());
                            int linecount = binding.tvSubDec.getLineCount();
                            if (linecount >= 4) {
                                binding.tvReadMore.setVisibility(View.VISIBLE);
                            } else {
                                binding.tvReadMore.setVisibility(View.GONE);
                            }
                            /*    if (model.getResponseData().getDownload().equalsIgnoreCase("1")) {
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                            binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                            binding.tvDownload.setTextColor(getResources().getColor(R.color.light_gray));
                            binding.llDownload.setClickable(false);
                            binding.llDownload.setEnabled(false);
                        } else if (!model.getResponseData().getDownload().equalsIgnoreCase("")) {
                            binding.llDownload.setClickable(true);
                            binding.llDownload.setEnabled(true);
                            binding.ivDownloads.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                            binding.tvDownload.setTextColor(getResources().getColor(R.color.white));
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                        }*/
                            /*   binding.llDownload.setOnClickListener(view -> {
                                if (BWSApplication.isNetworkConnected(ctx)) {
                                    BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                    Call<DownloadPlaylistModel> listCall13 = null;
                                    listCall13 = APIClient.getClient().getDownloadlistPlaylist(UserID, "", PlaylistID);
                                    listCall13.enqueue(new Callback<DownloadPlaylistModel>() {
                                        @Override
                                        public void onResponse(Call<DownloadPlaylistModel> call13, Response<DownloadPlaylistModel> response13) {
                                            if (response13.isSuccessful()) {
                                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                                DownloadPlaylistModel model1 = response13.body();
                                                BWSApplication.showToast(model1.getResponseMessage(), ctx);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<DownloadPlaylistModel> call13, Throwable t) {
                                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                        }
                                    });

                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                                }
                            });
*/
                            String[] elements = model.getResponseData().getPlaylistSubcat().split(",");
                            List<String> direction = Arrays.asList(elements);
                            DirectionAdapter directionAdapter = new DirectionAdapter(direction, ctx);
                            RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                            binding.rvDirlist.setLayoutManager(recentlyPlayed);
                            binding.rvDirlist.setItemAnimator(new
                                    DefaultItemAnimator());
                            binding.rvDirlist.setAdapter(directionAdapter);
                            String PlaylistID = model.getResponseData().getPlaylistID();


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SubPlayListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    public void CallPlaylistLike(String PlaylistID) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<PlaylistLikeModel> listCall = APIClient.getClient().getPlaylistLike(PlaylistID, UserID);
            listCall.enqueue(new Callback<PlaylistLikeModel>() {
                @Override
                public void onResponse(Call<PlaylistLikeModel> call, Response<PlaylistLikeModel> response) {
                    if (response.isSuccessful()) {
                        try {
                            binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            PlaylistLikeModel model = response.body();
                            if (model.getResponseData().getFlag().equalsIgnoreCase("0")) {
                                binding.ivLike.setImageResource(R.drawable.ic_like_white_icon);
                            } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                                binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                            }
                            BWSApplication.showToast(model.getResponseMessage(), ctx);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<PlaylistLikeModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private void GetPlaylistDetail() {
        DatabaseClient
                .getInstance(ctx)
                .getaudioDatabase()
                .taskDao()
                .getPlaylist1(PlaylistID).observe(this, audioList -> {

            if (audioList.size() != 0) {
                enableDisableDownload(false, "orange");
            } else if (RefreshIconData == 0) {
                enableDisableDownload(false, "gray");
            } else if (Download.equalsIgnoreCase("1")) {
                enableDisableDownload(false, "orange");
            } else if (Download.equalsIgnoreCase("0") || Download.equalsIgnoreCase("") ||
                    RefreshIconData != 0) {
                enableDisableDownload(true, "white");
            } else if (audioList.size() == 0 && RefreshIconData != 0) {
                enableDisableDownload(true, "white");
            }
        });
    }

    private void enableDisableDownload(boolean b, String color) {
        if (b) {
            binding.llDownload.setClickable(true);
            binding.llDownload.setEnabled(true);
            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            binding.ivDownloads.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            binding.tvDownload.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            binding.llDownload.setClickable(false);
            binding.llDownload.setEnabled(false);
            if (color.equalsIgnoreCase("gray")) {
                binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.light_gray), PorterDuff.Mode.SRC_IN);
            } else if (color.equalsIgnoreCase("orange")) {
                binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
            }
            binding.tvDownload.setTextColor(getResources().getColor(R.color.white));
        }
    }
}