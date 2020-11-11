package com.brainwellnessspa.DashboardModule.Playlist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Models.CardModel;
import com.brainwellnessspa.DashboardModule.Activities.AddAudioActivity;
import com.brainwellnessspa.DashboardModule.Activities.AddQueueActivity;
import com.brainwellnessspa.DashboardModule.Activities.MyPlaylistActivity;
import com.brainwellnessspa.DashboardModule.Models.AddToQueueModel;
import com.brainwellnessspa.DashboardModule.Models.ReminderStatusPlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.SucessModel;
import com.brainwellnessspa.DashboardModule.Search.SearchFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.DownloadModule.Activities.DownloadsActivity;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.LikeModule.Activities.LikeActivity;
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ReminderModule.Activities.ReminderActivity;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.RoomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.ItemMoveCallback;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.Utility.StartDragListener;
import com.brainwellnessspa.databinding.FragmentMyPlaylistsBinding;
import com.brainwellnessspa.databinding.MyPlaylistLayoutBinding;
import com.brainwellnessspa.databinding.MyPlaylistLayoutSorting1Binding;
import com.brainwellnessspa.databinding.MyPlaylistLayoutSorting2Binding;
import com.brainwellnessspa.databinding.MyPlaylistLayoutSortingBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.downloader.PRDownloader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenReminder;
import static com.brainwellnessspa.DashboardModule.Activities.AddAudioActivity.MyPlaylistIds;
import static com.brainwellnessspa.DashboardModule.Activities.AddAudioActivity.PlaylistIDMS;
import static com.brainwellnessspa.DashboardModule.Activities.AddAudioActivity.addToSearch;
import static com.brainwellnessspa.DashboardModule.Activities.AddPlaylistActivity.MyPlaylistId;
import static com.brainwellnessspa.DashboardModule.Activities.AddPlaylistActivity.addToPlayList;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.player;
import static com.brainwellnessspa.DashboardModule.Activities.MyPlaylistActivity.ComeFindAudio;
import static com.brainwellnessspa.DashboardModule.Activities.MyPlaylistActivity.comeRename;
import static com.brainwellnessspa.DashboardModule.Activities.MyPlaylistActivity.deleteFrg;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.DashboardModule.Playlist.ViewAllPlaylistFragment.GetPlaylistLibraryID;
import static com.brainwellnessspa.DashboardModule.Search.SearchFragment.comefrom_search;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment.myAudioId;
import static com.brainwellnessspa.DownloadModule.Activities.DownloadsActivity.ComeFrom_Playlist;
import static com.brainwellnessspa.LikeModule.Activities.LikeActivity.ComeFrom_LikePlaylist;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadIdOne;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadProgress;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.filename;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;
import static com.brainwellnessspa.Utility.MusicService.getProgressPercentage;
import static com.brainwellnessspa.Utility.MusicService.getStartTime;
import static com.brainwellnessspa.Utility.MusicService.isCompleteStop;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.isPrepare;
import static com.brainwellnessspa.Utility.MusicService.isStop;
import static com.brainwellnessspa.Utility.MusicService.mediaPlayer;
import static com.brainwellnessspa.Utility.MusicService.oTime;
import static com.brainwellnessspa.Utility.MusicService.pauseMedia;
import static com.brainwellnessspa.Utility.MusicService.releasePlayer;
import static com.brainwellnessspa.Utility.MusicService.resumeMedia;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;

public class MyPlaylistsFragment extends Fragment implements StartDragListener {
    public static int RefreshIconData = 0;
    public static String RefreshNew = "";
    public static int disclaimerPlayed = 0;
    public boolean RefreshPlaylist = false;
    public static int isPlayPlaylist = 0;
    FragmentMyPlaylistsBinding binding;
    String UserID, New, PlaylistID, PlaylistName = "", PlaylistImage, SearchFlag, MyDownloads = "", AudioFlag, PlaylistIDs = "";
    int RefreshIcon;
    PlayListsAdpater adpater;
    PlayListsAdpater1 adpater1;
    PlayListsAdpater2 adpater2;
    View view;
    EditText searchEditText;
    ArrayList<String> changedAudio;
    Activity activity;
    List<DownloadAudioDetails> downloadAudioDetailsList, downloadedSingleAudio;
    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongsList, playListSongListForDownload;
    List<DownloadAudioDetails> oneAudioDetailsList, playlistWiseAudioDetails;
    List<DownloadPlaylistDetails> downloadPlaylistDetailsList;
    DownloadPlaylistDetails downloadPlaylistDetails;
    Dialog dialog;
    List<String> fileNameList, playlistDownloadId, remainAudio;
    ItemTouchHelper touchHelper;
    Runnable UpdateSongTime2;
    int SongListSize = 0, count;
    List<DownloadAudioDetails> playlistWiseAudiosDetails;
    SubPlayListModel.ResponseData GlobalListModel;
    SubPlayListModel.ResponseData.PlaylistSong addDisclaimer = new SubPlayListModel.ResponseData.PlaylistSong();
    SubPlayListModel.ResponseData.PlaylistSong songListDownload = new SubPlayListModel.ResponseData.PlaylistSong();
    boolean isclose = false;
    private Handler handler1, handler2,handler3;
    private Runnable UpdateSongTime3;
//    private Runnable UpdateSongTime1 = new Runnable() {
//        @Override
//        public void run() {
//            getMediaByPer(PlaylistID,SongListSize);
//
///*            if (fileNameList.size() != 0) {
//                if (remainAudio.size() <= SongListSize) {
//                    int total = SongListSize;
//                    int remain = remainAudio.size();
//                    int complate = total - remain;
//                    long progressPercent = complate * 100 / total;
//                    int downloadProgress1 = (int) progressPercent;
//                    if (SongListSize == 1) {
//                        if (downloadProgress <= 100) {
//                            binding.pbProgress.setProgress(downloadProgress);
//                            binding.pbProgress.setVisibility(View.VISIBLE);
//                            binding.ivDownloads.setVisibility(View.GONE);
//                            if (downloadProgress == 100) {
//                                getDownloadData();
//                            }
//                        }
//                    } else if (downloadProgress1 <= 100) {
//                        if (downloadProgress1 == 100) {
//                            getDownloadData();
//                            binding.pbProgress.setVisibility(View.GONE);
//                            binding.ivDownloads.setVisibility(View.VISIBLE);
//                            handler1.removeCallbacks(UpdateSongTime1);
//                        } else {
//                            binding.pbProgress.setProgress(downloadProgress1);
//                            binding.pbProgress.setVisibility(View.VISIBLE);
//                            binding.ivDownloads.setVisibility(View.GONE);
//                        }
//                    } else {
//                        binding.pbProgress.setVisibility(View.GONE);
//                        binding.ivDownloads.setVisibility(View.VISIBLE);
//                        handler1.removeCallbacks(UpdateSongTime1);
//                    }
//                }
//                getDownloadData();
//                handler1.postDelayed(this, 500);
//            }else {
//                binding.pbProgress.setVisibility(View.GONE);
//                binding.ivDownloads.setVisibility(View.VISIBLE);
//                handler1.removeCallbacks(UpdateSongTime1);
//                getDownloadData();
//            }*/
//            handler1.postDelayed(this, 500);
//        }
//    };
    int position = 0, startTime, listSize, myCount;
    private long totalDuration, currentDuration = 0;
    long myProgress = 0, diff = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_playlists, container, false);
        view = binding.getRoot();
//        handler1 = new Handler();
        handler2 = new Handler();
        handler3 = new Handler();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        activity = getActivity();
        downloadAudioDetailsList = new ArrayList<>();
        oneAudioDetailsList = new ArrayList<>();
        fileNameList = new ArrayList<>();
        playlistDownloadId = new ArrayList<>();
        addDisclaimer();
//        remainAudio = new ArrayList<>();
        playlistWiseAudioDetails = new ArrayList<>();
        downloadPlaylistDetailsList = new ArrayList<>();
        playlistSongsList = new ArrayList<>();
        playListSongListForDownload = new ArrayList<>();
        changedAudio = new ArrayList<>();
        if (getArguments() != null) {
            New = getArguments().getString("New");
            PlaylistID = getArguments().getString("PlaylistID");
            PlaylistName = getArguments().getString("PlaylistName");
            PlaylistImage = getArguments().getString("PlaylistImage");
            MyDownloads = getArguments().getString("MyDownloads");
        }
        binding.llBack.setOnClickListener(view1 -> {
            binding.searchView.clearFocus();
            callBack();
        });

        if (BWSApplication.isNetworkConnected(getActivity()) && !MyDownloads.equalsIgnoreCase("1")) {
            binding.llMore.setVisibility(View.VISIBLE);
            binding.llMore.setClickable(true);
            binding.llMore.setEnabled(true);
            binding.ivMore.setImageResource(R.drawable.ic_menu_icon);
            binding.ivMore.setColorFilter(activity.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        } else {
            binding.llMore.setVisibility(View.VISIBLE);
            binding.llMore.setClickable(false);
            binding.llMore.setEnabled(false);
            binding.ivMore.setImageResource(R.drawable.ic_menu_icon);
            binding.ivMore.setColorFilter(activity.getResources().getColor(R.color.light_gray), PorterDuff.Mode.SRC_IN);
        }

        binding.llMore.setOnClickListener(view13 -> {
//            handler1.removeCallbacks(UpdateSongTime1);
            handler2.removeCallbacks(UpdateSongTime2);
            Intent i = new Intent(getActivity(), MyPlaylistActivity.class);
            i.putExtra("PlaylistID", PlaylistID);
            i.putExtra("PlaylistIDImage", PlaylistImage);
            i.putExtra("Liked", "0");
            startActivity(i);

        });

        binding.tvSearch.setOnClickListener(view14 -> {
            Intent i = new Intent(getActivity(), AddAudioActivity.class);
            i.putExtra("PlaylistID", PlaylistID);
            startActivity(i);
        });

        binding.searchView.onActionViewExpanded();
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();
        searchClear(searchEditText);
        closeButton.setOnClickListener(v -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);

        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                binding.searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                try {
                    if (adpater2 != null) {
                        adpater2.getFilter().filter(search);
                        SearchFlag = search;
                        Log.e("searchsearch", "" + search);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager playList1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvPlayLists1.setLayoutManager(playList1);
        binding.rvPlayLists1.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager playList = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvPlayLists.setLayoutManager(playList);
        binding.rvPlayLists.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager playList3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvPlayLists2.setLayoutManager(playList3);
        binding.rvPlayLists2.setItemAnimator(new DefaultItemAnimator());
      /*  binding.rvPlayLists1.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                binding.rvPlayLists.setScrollX(dx);
                binding.rvPlayLists.setScrollY(dy);
//                int pos = linearLayoutManager1.findFirstCompletelyVisibleItemPosition();
//                linearLayoutManager.scrollToPositionWithOffset(pos, 20);
            }
        });*/
        binding.llDownloads.setOnClickListener(view1 -> {
            callDownload("", "", "", playlistSongsList, 0, binding.llDownloads, binding.ivDownloads);
        });

        RefreshNew = New;
        if (New.equalsIgnoreCase("1")) {
            binding.llAddAudio.setVisibility(View.VISIBLE);
            binding.llDownloads.setVisibility(View.VISIBLE);
            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            binding.llReminder.setVisibility(View.VISIBLE);
            binding.ivPlaylistStatus.setVisibility(View.INVISIBLE);
            binding.llListing.setVisibility(View.GONE);
            binding.btnAddAudio.setOnClickListener(view -> {
                Intent i = new Intent(getActivity(), AddAudioActivity.class);
                i.putExtra("PlaylistID", PlaylistID);
                startActivity(i);
            });
        } else if (New.equalsIgnoreCase("0")) {
            binding.llAddAudio.setVisibility(View.GONE);
            binding.llDownloads.setVisibility(View.VISIBLE);
            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            binding.llReminder.setVisibility(View.VISIBLE);
            binding.ivPlaylistStatus.setVisibility(View.VISIBLE);
            binding.llListing.setVisibility(View.VISIBLE);
            prepareData(UserID, PlaylistID);
        }
        return view;
    }

    private List<DownloadPlaylistDetails> GetPlaylistDetail(String download) {
        class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadPlaylistDetailsList = DatabaseClient
                        .getInstance(activity)
                        .getaudioDatabase()
                        .taskDao()
                        .getPlaylist(PlaylistID);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (downloadPlaylistDetailsList.size() != 0 /*New.equalsIgnoreCase("1") ||*/) {
                    enableDisableDownload(false, "orange");
                } else if (RefreshIcon == 0) {
                    enableDisableDownload(false, "gray");
                } else if (download.equalsIgnoreCase("1") /* New.equalsIgnoreCase("1") ||*/) {
                    enableDisableDownload(false, "orange");
                } else if (download.equalsIgnoreCase("0") || download.equalsIgnoreCase("") ||
                        New.equalsIgnoreCase("0") || RefreshIcon != 0) {
                    enableDisableDownload(true, "white");
                }
                super.onPostExecute(aVoid);
            }
        }

        GetTask st = new GetTask();
        st.execute();
        return downloadPlaylistDetailsList;
    }

    private List<DownloadPlaylistDetails> GetPlaylistDetail2() {
        class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadPlaylistDetailsList = DatabaseClient
                        .getInstance(activity)
                        .getaudioDatabase()
                        .taskDao()
                        .getPlaylist(PlaylistID);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
   /*
                    "PlaylistDesc": "When life gets tough and you struggle through the days, download the Ultimate Self-development Bundle to help you in finding a new appreciation for life. Everyone can use a little help in all areas of their lives at times. There are 12 programs aimed to help your self-development:",
                    "PlaylistMastercat": "Self-development",
                    "PlaylistSubcat": "Self-expression, Focus, Discipline, Self-love, Mindset, Passion, Enthusiasm, Gratitude, Self-doubt, Inner Strength ",
                    "PlaylistImage": "https://brainwellnessspa.com.au/wp-content/uploads/2018/06/Ultimate self development bundle.jpg",
                    "PlaylistSongs " */
                downloadAudioDetailsList = GetAllMedia();
                playlistWiseAudioDetails = GetMedia();

                super.onPostExecute(aVoid);
            }
        }

        GetTask st = new GetTask();
        st.execute();
        return downloadPlaylistDetailsList;
    }

    private void enableDisableDownload(boolean b, String color) {
        if (b) {
            binding.llDownloads.setClickable(true);
            binding.llDownloads.setEnabled(true);
            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        } else {
            binding.llDownloads.setClickable(false);
            binding.llDownloads.setEnabled(false);
            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            if (color.equalsIgnoreCase("gray")) {
                binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.light_gray), PorterDuff.Mode.SRC_IN);
            } else if (color.equalsIgnoreCase("orange")) {
                binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    public List<DownloadAudioDetails> GetAllMedia() {
        class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadAudioDetailsList = DatabaseClient
                        .getInstance(activity)
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
    }

    @Override
    public void onResume() {
        super.onResume();
        addDisclaimer();

        if (binding.searchView != null) {
            binding.searchView.clearFocus();
//            binding.searchView.onActionViewCollapsed();
            isclose = true;
            Toast.makeText(activity, "closeeeeeee", Toast.LENGTH_SHORT).show();
        }

        if (isclose) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                binding.searchView.setQuery("", false);
//                binding.rlMainLayouts.requestFocus();
                binding.searchView.clearFocus();
//                binding.searchView.setFocusable(false);
                callBack();
                return true;
            }
            return false;
        });
        }

        if (deleteFrg == 1) {
            binding.searchView.clearFocus();
            callBack();
            deleteFrg = 0;
        } else if (addToPlayList) {
            PlaylistID = MyPlaylistId;
            prepareData(UserID, MyPlaylistId);
            addToPlayList = false;
        } else if (addToSearch) {
            PlaylistIDs = PlaylistIDMS;
            prepareData(UserID, MyPlaylistIds);
            addToSearch = false;
        } else {
            prepareData(UserID, PlaylistID);
        }
        if (comeRename == 1) {
            prepareData(UserID, PlaylistID);
        }
    }

    private void callBack() {
        if (MyPlaylistIds.equalsIgnoreCase("")) {
            if (comefrom_search == 2) {
                Bundle bundle = new Bundle();
                Fragment playlistFragment = new ViewAllPlaylistFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.flContainer, playlistFragment)
                        .commit();
                bundle.putString("GetLibraryID", GetPlaylistLibraryID);
                bundle.putString("MyDownloads", MyDownloads);
                playlistFragment.setArguments(bundle);
//            comefrom_search = 0;
            } else if (comefrom_search == 1) {
                Fragment fragment = new SearchFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .commit();
                comefrom_search = 0;
            } else if (comefrom_search == 0) {
                Fragment fragment = new PlaylistFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .commit();
                comefrom_search = 0;
            } else if (comefrom_search == 4) {
                Intent i = new Intent(getActivity(), LikeActivity.class);
                ComeFrom_LikePlaylist = true;
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                getActivity().finish();
                //            comefrom_search = 0;
            } else if (comefrom_search == 3) {
                Intent i = new Intent(getActivity(), DownloadsActivity.class);
                ComeFrom_Playlist = true;
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                getActivity().finish();
                //            comefrom_search = 0;
            }
        } else {
            prepareData(UserID, PlaylistIDs);
            MyPlaylistIds = "";
        }
    }

    private void searchClear(EditText searchEditText) {
        if (ComeFindAudio == 1) {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);
            ComeFindAudio = 0;
        } else if (ComeFindAudio == 2) {
            binding.searchView.requestFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);
            ComeFindAudio = 0;
        }
    }

    private void prepareData(String UserId, String PlaylistId) {
        if (comefrom_search == 3) {
            binding.llExtra.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 8, 0, 260);
            binding.llSpace.setLayoutParams(params);
        }

        downloadedSingleAudio = getMyMedia();
        binding.tvPlaylist.setText("Playlist");
        searchClear(searchEditText);
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        try {
            SharedPreferences sharedx = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = sharedx.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            SharedPreferences shared2 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
            Gson gson1 = new Gson();
            Type type1 = new TypeToken<List<String>>() {
            }.getType();
            List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
            if (!IsLock.equalsIgnoreCase("0") && (AudioFlag.equalsIgnoreCase("MainAudioList")
                    || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
                String audioID = "";
                SharedPreferences sharedd = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                Gson gson = new Gson();
                String json = sharedd.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
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
                    if (isMediaStart) {
                        stopMedia();
                        releasePlayer();
                    }
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
                if (isMediaStart) {
                    stopMedia();
                    releasePlayer();
                }
            }
            SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                callAddTransFrag();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 8, 0, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 8, 0, 50);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (BWSApplication.isNetworkConnected(getActivity())) {
            if (!MyDownloads.equalsIgnoreCase("1")) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                Call<SubPlayListModel> listCall = APIClient.getClient().getSubPlayLists(UserId, PlaylistId);
                listCall.enqueue(new Callback<SubPlayListModel>() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void onResponse(Call<SubPlayListModel> call, Response<SubPlayListModel> response) {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                            SubPlayListModel listModel = response.body();
                            try {
                                if (listModel.getResponseData().getIsReminder().equalsIgnoreCase("0") ||
                                        listModel.getResponseData().getIsReminder().equalsIgnoreCase("")) {
                                    binding.ivReminder.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), PorterDuff.Mode.SRC_IN);

                                } else if (listModel.getResponseData().getIsReminder().equalsIgnoreCase("1")) {
                                    binding.ivReminder.setColorFilter(ContextCompat.getColor(getActivity(), R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            getDownloadData();
                            downloadAudioDetailsList = GetAllMedia();
                            SongListSize = listModel.getResponseData().getPlaylistSongs().size();
                            playlistWiseAudioDetails = GetMedia();
                            downloadedSingleAudio = getMyMedia();
                            getMediaByPer(PlaylistId, SongListSize);
                            binding.rlSearch.setVisibility(View.VISIBLE);
                            binding.llMore.setVisibility(View.VISIBLE);
                            binding.llReminder.setVisibility(View.VISIBLE);
                            binding.ivPlaylistStatus.setVisibility(View.VISIBLE);
                            binding.tvTag.setText(R.string.Audios_in_Playlist);
                            binding.llReminder.setOnClickListener(view -> {
                                if (listModel.getResponseData().getIsReminder().equalsIgnoreCase("0") ||
                                        listModel.getResponseData().getIsReminder().equalsIgnoreCase("")) {
                                    binding.ivReminder.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), PorterDuff.Mode.SRC_IN);
                                    Intent i = new Intent(getActivity(), ReminderActivity.class);
                                    ComeScreenReminder = 0;
                                    i.putExtra("ComeFrom", "1");
                                    i.putExtra("PlaylistID", PlaylistID);
                                    i.putExtra("PlaylistName", listModel.getResponseData().getPlaylistName());
                                    i.putExtra("Time", listModel.getResponseData().getReminderTime());
                                    i.putExtra("Day", listModel.getResponseData().getReminderDay());
                                    startActivity(i);
                                } else if (listModel.getResponseData().getIsReminder().equalsIgnoreCase("1")) {
                                    binding.ivReminder.setColorFilter(ContextCompat.getColor(getActivity(), R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                                    dialog = new Dialog(activity);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.delete_payment_card);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.dark_blue_gray)));
                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                    final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
                                    final TextView tvSubTitle = dialog.findViewById(R.id.tvSubTitle);
                                    final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                                    final Button Btn = dialog.findViewById(R.id.Btn);
                                    tvTitle.setText("Reminder off");
                                    tvSubTitle.setText("Are you sure you want to reminder off ?");
                                    dialog.setOnKeyListener((v, keyCode, event) -> {
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            dialog.dismiss();
                                            return true;
                                        }
                                        return false;
                                    });
                                    Btn.setOnTouchListener((view1, event) -> {
                                        if (BWSApplication.isNetworkConnected(getActivity())) {
                                            switch (event.getAction()) {
                                                case MotionEvent.ACTION_DOWN: {
                                                    Button views = (Button) view1;
                                                    views.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                                    view1.invalidate();
                                                    break;
                                                }
                                                case MotionEvent.ACTION_UP:
                                                    Call<ReminderStatusPlaylistModel> listCall1 = APIClient.getClient().getReminderStatusPlaylist(UserID, PlaylistID, "0");/*set 1 or not 0 */
                                                    listCall1.enqueue(new Callback<ReminderStatusPlaylistModel>() {
                                                        @Override
                                                        public void onResponse(Call<ReminderStatusPlaylistModel> call1, Response<ReminderStatusPlaylistModel> response1) {
                                                            if (response1.isSuccessful()) {
                                                                ReminderStatusPlaylistModel listModel1 = response1.body();
//                                                                prepareData(UserID, PlaylistID);
                                                                listModel.getResponseData().setIsReminder(listModel1.getResponseData().getIsCheck());
                                                                binding.ivReminder.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), PorterDuff.Mode.SRC_IN);
                                                                dialog.dismiss();
                                                                BWSApplication.showToast(listModel1.getResponseMessage(), activity);
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<ReminderStatusPlaylistModel> call1, Throwable t) {
                                                        }
                                                    });


                                                case MotionEvent.ACTION_CANCEL: {
                                                    Button views = (Button) view1;
                                                    views.getBackground().clearColorFilter();
                                                    views.invalidate();
                                                    break;
                                                }
                                            }
                                        } else {
                                            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                                        }

                                        return true;
                                    });

                                    tvGoBack.setOnClickListener(v -> {
                                        dialog.dismiss();
                                    });
                                    dialog.show();
                                    dialog.setCancelable(false);

                                }
                            });
                            playlistSongsList = new ArrayList<>();
                            playlistSongsList.addAll(listModel.getResponseData().getPlaylistSongs());
                            downloadPlaylistDetails = new DownloadPlaylistDetails();
                            downloadPlaylistDetails.setPlaylistID(listModel.getResponseData().getPlaylistID());
                            downloadPlaylistDetails.setPlaylistName(listModel.getResponseData().getPlaylistName());
                            downloadPlaylistDetails.setPlaylistDesc(listModel.getResponseData().getPlaylistDesc());
                            downloadPlaylistDetails.setIsReminder(listModel.getResponseData().getIsReminder());
                            downloadPlaylistDetails.setPlaylistMastercat(listModel.getResponseData().getPlaylistMastercat());
                            downloadPlaylistDetails.setPlaylistSubcat(listModel.getResponseData().getPlaylistSubcat());
                            downloadPlaylistDetails.setPlaylistImage(listModel.getResponseData().getPlaylistImage());
                            downloadPlaylistDetails.setPlaylistImageDetails(listModel.getResponseData().getPlaylistImageDetail());
                            downloadPlaylistDetails.setTotalAudio(listModel.getResponseData().getTotalAudio());
                            downloadPlaylistDetails.setTotalDuration(listModel.getResponseData().getTotalDuration());
                            downloadPlaylistDetails.setTotalhour(listModel.getResponseData().getTotalhour());
                            downloadPlaylistDetails.setTotalminute(listModel.getResponseData().getTotalminute());
                            downloadPlaylistDetails.setCreated(listModel.getResponseData().getCreated());
                            downloadPlaylistDetails.setDownload(listModel.getResponseData().getDownload());
                            downloadPlaylistDetails.setLike(listModel.getResponseData().getLike());

                            setData(listModel.getResponseData());
                            downloadPlaylistDetailsList = GetPlaylistDetail(listModel.getResponseData().getDownload());

                        }
                    }

                    @Override
                    public void onFailure(Call<SubPlayListModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                    }
                });
            } else {
                downloadPlaylistDetailsList = GetPlaylistDetail2();
            }
        } else {
            downloadPlaylistDetailsList = GetPlaylistDetail2();
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
        SharedPreferences sharedw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        boolean audioPlay = sharedw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        AudioFlag = sharedw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        String pID = sharedw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
        if (MyDownloads.equalsIgnoreCase("1")) {
            if (audioPlay && AudioFlag.equalsIgnoreCase("Downloadlist") && pID.equalsIgnoreCase(PlaylistName)) {
                if (isMediaStart) {
                    isPlayPlaylist = 1;
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
                    handler3.postDelayed(UpdateSongTime3,500);
                } else {
                    isPlayPlaylist = 0;
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                }
            } else {
                isPlayPlaylist = 0;
                binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
            }
        } else {
            if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                if (isMediaStart) {
                    isPlayPlaylist = 1;
                    handler3.postDelayed(UpdateSongTime3,500);
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
                } else {
                    isPlayPlaylist = 0;
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                }
            } else {
                isPlayPlaylist = 0;
                binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
            }
        }
    }

    private List<DownloadAudioDetails> getMyMedia() {
        downloadedSingleAudio = new ArrayList<>();
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadedSingleAudio = DatabaseClient
                        .getInstance(getActivity())
                        .getaudioDatabase()
                        .taskDao()
                        .getAllAudioByPlaylist("");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                /*playListSongListForDownload = new ArrayList<>();
                if (downloadedSingleAudio.size() != 0) {
                    for(int i =0;i<downloadedSingleAudio.size();i++) {
                        songListDownload = new SubPlayListModel.ResponseData.PlaylistSong();
                        songListDownload.setID(downloadedSingleAudio.get(i).getID());
                        songListDownload.setName(downloadedSingleAudio.get(i).getName());
                        songListDownload.setAudioFile(downloadedSingleAudio.get(i).getAudioFile());
                        songListDownload.setAudioDirection(downloadedSingleAudio.get(i).getAudioDirection());
                        songListDownload.setAudiomastercat(downloadedSingleAudio.get(i).getAudiomastercat());
                        songListDownload.setAudioSubCategory(downloadedSingleAudio.get(i).getAudioSubCategory());
                        songListDownload.setImageFile(downloadedSingleAudio.get(i).getImageFile());
                        songListDownload.setLike(downloadedSingleAudio.get(i).getLike());
                        songListDownload.setDownload("");
                        songListDownload.setAudioDuration(downloadedSingleAudio.get(i).getAudioDuration());
                        songListDownload.setPlaylistID(PlaylistID);
                        playListSongListForDownload.add(songListDownload);
                    }
                }*/
                super.onPostExecute(aVoid);
            }
        }

        GetMedia st = new GetMedia();
        st.execute();
        return downloadedSingleAudio;
    }

    private void callAddTransFrag() {
        Fragment fragment = new TransparentPlayerFragment();
        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .add(R.id.flContainer, fragment)
                .commit();
    }

    private void getMediaByPer(String playlistID, int totalAudio) {
        class getMediaByPer extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                count = DatabaseClient.getInstance(getActivity())
                        .getaudioDatabase()
                        .taskDao()
                        .getCountDownloadProgress("Complete", playlistID);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                downloadPlaylistDetailsList = GetPlaylistDetail(downloadPlaylistDetails.getDownload());
                if (downloadPlaylistDetailsList.size() != 0) {
                    if (count <= totalAudio) {
                        if (count == totalAudio) {
                            binding.pbProgress.setVisibility(View.GONE);
                            binding.ivDownloads.setVisibility(View.VISIBLE);
//                            handler1.removeCallbacks(UpdateSongTime1);
                        } else {
                            long progressPercent = count * 100 / totalAudio;
                            int downloadProgress1 = (int) progressPercent;
                            binding.pbProgress.setVisibility(View.VISIBLE);
                            binding.ivDownloads.setVisibility(View.GONE);
                            binding.pbProgress.setProgress(downloadProgress1);
                            getMediaByPer(playlistID, totalAudio);
//                             handler1.postDelayed(UpdateSongTime1, 500);
                        }
                    } else {
                        binding.pbProgress.setVisibility(View.GONE);
                        binding.ivDownloads.setVisibility(View.VISIBLE);
//                        handler1.removeCallbacks(UpdateSongTime1);
                    }
                }
                super.onPostExecute(aVoid);
            }
        }
        getMediaByPer st = new getMediaByPer();
        st.execute();
    }

    private void getDownloadData() {
        try {
            SharedPreferences sharedy = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
            Gson gson = new Gson();
            String jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
            String json1 = sharedy.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
            String jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
            if (!jsony.equalsIgnoreCase(String.valueOf(gson))) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                fileNameList = gson.fromJson(jsony, type);
                playlistDownloadId = gson.fromJson(jsonq, type);
               /* remainAudio = new ArrayList<>();
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
                } else {
                    fileNameList = new ArrayList<>();
                    playlistDownloadId = new ArrayList<>();
                    remainAudio = new ArrayList<>();
                }*/
            } else {
                fileNameList = new ArrayList<>();
                playlistDownloadId = new ArrayList<>();
//                remainAudio = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setData(SubPlayListModel.ResponseData listModel) {
        GlobalListModel = listModel;
        /*if (downloadAudioDetailsList.size() != 0) {
            for (int i = 0; i < downloadAudioDetailsList.size(); i++) {
                for (int f = 0; i < listModel.getPlaylistSongs().size(); i++) {
                    if (downloadAudioDetailsList.get(i).getAudioFile().equalsIgnoreCase(listModel.getPlaylistSongs().get(f).getAudioFile())) {


                    }
                }
            }
        }*/
        MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                5, 3, 1f, 0);
        binding.ivBanner.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivBanner.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivBanner.setScaleType(ImageView.ScaleType.FIT_XY);
        if (listModel.getPlaylistName().equalsIgnoreCase("") ||
                listModel.getPlaylistName() == null) {
            binding.tvLibraryName.setText(R.string.My_Playlist);
        } else {
            binding.tvLibraryName.setText(listModel.getPlaylistName());
        }
        binding.tvPlaylist.setText("Playlist");
        try {
            if (!listModel.getPlaylistImageDetail().equalsIgnoreCase("")) {
                Glide.with(getActivity()).load(listModel.getPlaylistImageDetail()).thumbnail(0.05f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivBanner);
            } else {
                binding.ivBanner.setImageResource(R.drawable.audio_bg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listModel.getTotalAudio().equalsIgnoreCase("") ||
                listModel.getTotalAudio().equalsIgnoreCase("0") &&
                        listModel.getTotalhour().equalsIgnoreCase("")
                        && listModel.getTotalminute().equalsIgnoreCase("")) {
            binding.tvLibraryDetail.setText("0 Audio | 0h 0m");
        } else {
            if (listModel.getTotalminute().equalsIgnoreCase("")) {
                binding.tvLibraryDetail.setText(listModel.getTotalAudio() + " Audio | "
                        + listModel.getTotalhour() + "h 0m");
            } else {
                binding.tvLibraryDetail.setText(listModel.getTotalAudio() + " Audio | "
                        + listModel.getTotalhour() + "h " + listModel.getTotalminute() + "m");
            }
        }
        RefreshIcon = listModel.getPlaylistSongs().size();
        RefreshIconData = listModel.getPlaylistSongs().size();
        binding.llReminder.setVisibility(View.INVISIBLE);
        binding.llDownloads.setVisibility(View.INVISIBLE);
        if (listModel.getPlaylistSongs().size() == 0) {
            binding.llAddAudio.setVisibility(View.VISIBLE);
            binding.llDownloads.setVisibility(View.VISIBLE);
            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            binding.llReminder.setVisibility(View.VISIBLE);
            binding.ivPlaylistStatus.setVisibility(View.INVISIBLE);
            binding.llListing.setVisibility(View.GONE);
            binding.btnAddAudio.setOnClickListener(view -> {
                Intent i = new Intent(getActivity(), AddAudioActivity.class);
                i.putExtra("PlaylistID", PlaylistID);
                startActivity(i);
            });
        } else {
            binding.llAddAudio.setVisibility(View.GONE);
            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            binding.ivPlaylistStatus.setVisibility(View.VISIBLE);
            binding.llListing.setVisibility(View.VISIBLE);
            try {
                if (MyDownloads.equalsIgnoreCase("1")) {
                    binding.llDelete.setVisibility(View.VISIBLE);
                    binding.llReminder.setVisibility(View.INVISIBLE);
                    binding.llDownloads.setVisibility(View.INVISIBLE);
                    binding.llMore.setVisibility(View.GONE);
                    binding.rlSearch.setVisibility(View.VISIBLE);
                    adpater2 = new PlayListsAdpater2(listModel.getPlaylistSongs(), getActivity(), UserID, "0");
                    binding.rvPlayLists2.setAdapter(adpater2);
                    binding.rvPlayLists.setVisibility(View.GONE);
                    binding.rvPlayLists1.setVisibility(View.GONE);
                    binding.rvPlayLists2.setVisibility(View.VISIBLE);
                    binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                    binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                    enableDisableDownload(false, "orange");
                    binding.ivReminder.setColorFilter(activity.getResources().getColor(R.color.gray), PorterDuff.Mode.SRC_IN);
                } else {
                    binding.llDownloads.setVisibility(View.VISIBLE);
                    binding.llReminder.setVisibility(View.VISIBLE);
                    if (listModel.getCreated().equalsIgnoreCase("1")) {
                        binding.rvPlayLists.setVisibility(View.VISIBLE);
                        binding.rvPlayLists1.setVisibility(View.VISIBLE);
                        binding.rvPlayLists2.setVisibility(View.GONE);
                        adpater1 = new PlayListsAdpater1(listModel.getPlaylistSongs(), getActivity(), UserID, listModel.getCreated(), this);
                        binding.rvPlayLists.setAdapter(adpater1);
                        adpater = new PlayListsAdpater(listModel.getPlaylistSongs(), getActivity(), UserID, listModel.getCreated(), this);
                        ItemTouchHelper.Callback callback = new ItemMoveCallback(adpater);
                        touchHelper = new ItemTouchHelper(callback);
                        touchHelper.attachToRecyclerView(binding.rvPlayLists1);
                        binding.rvPlayLists1.setAdapter(adpater);
                    } else {
                        adpater2 = new PlayListsAdpater2(listModel.getPlaylistSongs(), getActivity(), UserID, listModel.getCreated());
                        binding.rvPlayLists2.setAdapter(adpater2);
                        binding.rvPlayLists.setVisibility(View.GONE);
                        binding.rvPlayLists1.setVisibility(View.GONE);
                        binding.rvPlayLists2.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void callTransparentFrag(int position, Context ctx, ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList,
                                     String myPlaylist, String playlistID) {
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        boolean queuePlay = shared1.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        if (queuePlay) {
            int position1 = shared1.getInt(CONSTANTS.PREF_KEY_position, 0);
            ArrayList<AddToQueueModel> addToQueueModelList = new ArrayList<>();
            Gson gson = new Gson();
            String json1 = shared1.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
            if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
                Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
                }.getType();
                addToQueueModelList = gson.fromJson(json1, type1);
            }
            addToQueueModelList.remove(position1);
            SharedPreferences shared2 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared2.edit();
            String json = gson.toJson(addToQueueModelList);
            editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            editor.commit();

        }
        player = 1;
        if (isPrepare || isMediaStart || isPause) {
            stopMedia();
        }
        isPause = false;
        isMediaStart = false;
        isPrepare = false;
        isCompleteStop = false;
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listModelList);
        editor.putString(CONSTANTS.PREF_KEY_modelList, json);
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, myPlaylist);
        if (MyDownloads.equalsIgnoreCase("1")) {
            editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "Downloadlist");
            editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistName);
        } else {
            editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
            editor.putString(CONSTANTS.PREF_KEY_PlaylistId, playlistID);
        }
        editor.commit();
        try {
            callAddTransFrag();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callRemove(String id, String PlaylistAudioId, ArrayList<SubPlayListModel.ResponseData.PlaylistSong> mData, int position) {
        String AudioId = id;
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<SucessModel> listCall = APIClient.getClient().getRemoveAudioFromPlaylist(UserID, AudioId, PlaylistID);
            listCall.enqueue(new Callback<SucessModel>() {
                @Override
                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                    if (response.isSuccessful()) {
                        handler2.removeCallbacks(UpdateSongTime2);
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        SucessModel listModel = response.body();
                        mData.remove(position);
                        if (mData.size() == 0) {
                            enableDisableDownload(false, "gray");
                        }
                        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                        int pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
                        String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                        if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                            if (pos == position && position < mData.size() - 1) {
//                                            pos = pos + 1;
                                if (isDisclaimer == 1) {
//                                    BWSApplication.showToast("The audio shall remove after the disclaimer", getActivity());
                                } else {
                                    callTransparentFrag(pos, getActivity(), mData, "myPlaylist", PlaylistID);
                                }
                            } else if (pos == position && position == mData.size() - 1) {
                                pos = 0;
                                if (isDisclaimer == 1) {
//                                    BWSApplication.showToast("The audio shall remove after the disclaimer", getActivity());
                                } else {
                                    callTransparentFrag(pos, getActivity(), mData, "myPlaylist", PlaylistID);
                                }
                            } else if (pos < position && pos < mData.size() - 1) {
                                saveToPref(pos, mData);
                            } else if (pos > position && pos == mData.size()) {
                                pos = pos - 1;
                                saveToPref(pos, mData);
                            }
                        }
                        prepareData(UserID, PlaylistID);
                        BWSApplication.showToast(listModel.getResponseMessage(), getActivity());
                    }
                }

                private void saveToPref(int pos, ArrayList<SubPlayListModel.ResponseData.PlaylistSong> mData) {
                    SharedPreferences shareddd = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shareddd.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(mData);
                    editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                    editor.putInt(CONSTANTS.PREF_KEY_position, pos);
                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistID);
                    editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "myPlaylist");
                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
                    editor.commit();
                    callAddTransFrag();
                }

                @Override
                public void onFailure(Call<SucessModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    private void callDownload(String id, String audioFile, String Name, ArrayList<SubPlayListModel.ResponseData.PlaylistSong>
            playlistSongs, int position, RelativeLayout llDownload, ImageView ivDownloads) {
        if (id.isEmpty() && Name.isEmpty() && audioFile.isEmpty()) {
            enableDisableDownload(false, "orange");
            List<String> url = new ArrayList<>();
            List<String> name = new ArrayList<>();
            List<String> downloadPlaylistId = new ArrayList<>();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs2 = new ArrayList<>();
            playlistSongs2.addAll(playlistSongs);
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
            if (playlistSongs2.size() != 0) {
                for (int x = 0; x < playlistSongs2.size(); x++) {
                    name.add(playlistSongs2.get(x).getName());
                    url.add(playlistSongs2.get(x).getAudioFile());
                    downloadPlaylistId.add(playlistSongs2.get(x).getPlaylistID());
                }
            }
            byte[] encodedBytes = new byte[1024];
            SharedPreferences sharedx = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
            Gson gson1 = new Gson();
            String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson1));
            String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson1));
            String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson1));
            if (!json1.equalsIgnoreCase(String.valueOf(gson1))) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                List<String> fileNameList = gson1.fromJson(json, type);
                List<String> audioFile1 = gson1.fromJson(json1, type);
                List<String> playlistId1 = gson1.fromJson(json2, type);
                if (fileNameList.size() != 0) {
                    url.addAll(audioFile1);
                    name.addAll(fileNameList);
                    downloadPlaylistId.addAll(playlistId1);
                }
            }
            if (url.size() != 0) {
                DownloadMedia downloadMedia = new DownloadMedia(getActivity().getApplicationContext());
                downloadMedia.encrypt1(url, name, downloadPlaylistId/*, playlistSongs*/);
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
                /*remainAudio = new ArrayList<>();
                for (int i = 0; i < fileNameList.size(); i++) {
                    if (playlistDownloadId.get(i).equalsIgnoreCase(PlaylistID)) {
                        remainAudio.add(playlistDownloadId.get(i));
                    }
                }*/
                SongListSize = playlistSongs.size();
//                handler1.postDelayed(UpdateSongTime1, 500);
            }
            binding.pbProgress.setVisibility(View.VISIBLE);
            binding.ivDownloads.setVisibility(View.GONE);
//            String dirPath = FileUtils.getFilePath(getActivity().getApplicationContext(), Name);
//            SaveMedia(EncodeBytes, dirPath, playlistSongs, i, llDownload);
            getMediaByPer(PlaylistID, SongListSize);
            savePlaylist();
            saveAllMedia(playlistSongs, playlistSongs2, encodedBytes);
        } else {
            boolean downloadOrNot = false;
            if (downloadAudioDetailsList.size() != 0) {
                for (int i = 0; i < downloadAudioDetailsList.size(); i++) {
                    if (downloadAudioDetailsList.get(i).equals(audioFile)) {
                        downloadOrNot = false;
                        break;
                    } else {
                        downloadOrNot = true;
                    }
                }
            } else {
                downloadOrNot = true;
            }
            if (downloadOrNot) {
                disableDownload(llDownload, ivDownloads);
                List<String> url = new ArrayList<>();
                List<String> name = new ArrayList<>();
                List<String> downloadPlaylistId = new ArrayList<>();
                SharedPreferences sharedx = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
                Gson gson1 = new Gson();
                String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson1));
                String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson1));
                String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson1));
                if (!json1.equalsIgnoreCase(String.valueOf(gson1))) {
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    List<String> fileNameList = gson1.fromJson(json, type);
                    List<String> audioFile1 = gson1.fromJson(json1, type);
                    List<String> playlistId1 = gson1.fromJson(json2, type);
                    if (fileNameList.size() != 0) {
                        url.addAll(audioFile1);
                        name.addAll(fileNameList);
                        downloadPlaylistId.addAll(playlistId1);
                    }
                }
                url.add(audioFile);
                name.add(Name);
                downloadPlaylistId.add("");
                if (url.size() != 0) {
                    DownloadMedia downloadMedia = new DownloadMedia(getActivity().getApplicationContext());
                    downloadMedia.encrypt1(url, name, downloadPlaylistId/*, playlistSongs*/);
                    SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    Gson gson = new Gson();
                    String urlJson = gson.toJson(url);
                    String nameJson = gson.toJson(name);
                    String playlistIdJson = gson.toJson(downloadPlaylistId);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                    editor.commit();
                    fileNameList = name;
                    playlistDownloadId = downloadPlaylistId;
                }
            }
            String dirPath = FileUtils.getFilePath(getActivity().getApplicationContext(), Name);
            SaveMedia(new byte[1024], dirPath, playlistSongs, position, llDownload, ivDownloads);
            SharedPreferences sharedx = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            AudioFlag = sharedx.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            int position1 = sharedx.getInt(CONSTANTS.PREF_KEY_position, 0);
            boolean audioPlay = sharedx.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            Gson gsonx = new Gson();
            String json = sharedx.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gsonx));
            String jsonw = sharedx.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gsonx));
            Type type1 = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
            }.getType();
            Gson gson1 = new Gson();
            ArrayList<DownloadAudioDetails> arrayList = gson1.fromJson(jsonw, type1);
            ArrayList<MainPlayModel> arrayList2 = gson1.fromJson(json, type1);

            if (audioPlay && AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                DownloadAudioDetails mainPlayModel = new DownloadAudioDetails();
                mainPlayModel.setID(playlistSongs.get(position).getID());
                mainPlayModel.setName(playlistSongs.get(position).getName());
                mainPlayModel.setAudioFile(playlistSongs.get(position).getAudioFile());
                mainPlayModel.setAudioDirection(playlistSongs.get(position).getAudioDirection());
                mainPlayModel.setAudiomastercat(playlistSongs.get(position).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(playlistSongs.get(position).getAudioSubCategory());
                mainPlayModel.setImageFile(playlistSongs.get(position).getImageFile());
                mainPlayModel.setLike(playlistSongs.get(position).getLike());
                mainPlayModel.setDownload(playlistSongs.get(position).getDownload());
                mainPlayModel.setAudioDuration(playlistSongs.get(position).getAudioDuration());
                arrayList.add(mainPlayModel);
                MainPlayModel mainPlayModel1 = new MainPlayModel();
                mainPlayModel1.setID(playlistSongs.get(position).getID());
                mainPlayModel1.setName(playlistSongs.get(position).getName());
                mainPlayModel1.setAudioFile(playlistSongs.get(position).getAudioFile());
                mainPlayModel1.setAudioDirection(playlistSongs.get(position).getAudioDirection());
                mainPlayModel1.setAudiomastercat(playlistSongs.get(position).getAudiomastercat());
                mainPlayModel1.setAudioSubCategory(playlistSongs.get(position).getAudioSubCategory());
                mainPlayModel1.setImageFile(playlistSongs.get(position).getImageFile());
                mainPlayModel1.setLike(playlistSongs.get(position).getLike());
                mainPlayModel1.setDownload(playlistSongs.get(position).getDownload());
                mainPlayModel1.setAudioDuration(playlistSongs.get(position).getAudioDuration());
                arrayList2.add(mainPlayModel1);
            }
            SharedPreferences sharedd = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedd.edit();
            Gson gson = new Gson();
            String jsonx = gson.toJson(arrayList2);
            String json1 = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonx);
            editor.putInt(CONSTANTS.PREF_KEY_position, position1);
            editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
            editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
            editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
            editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "DownloadListAudio");
            editor.commit();
            callAddTransFrag();
            handler2.postDelayed(UpdateSongTime2, 2000);
        }
    }

    private void savePlaylist() {
        class SaveMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getActivity())
                        .getaudioDatabase()
                        .taskDao()
                        .insertPlaylist(downloadPlaylistDetails);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
//                llDownload.setClickable(false);
//                llDownload.setEnabled(false);
                getMediaByPer(PlaylistID, SongListSize);
                super.onPostExecute(aVoid);
            }
        }
        SaveMedia st = new SaveMedia();
        st.execute();
    }

    private void saveAllMedia(ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs, ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs2, byte[] encodedBytes) {
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
                    downloadAudioDetails.setPlaylistId(PlaylistID);
                    downloadAudioDetails.setDownload("1");
                    downloadAudioDetails.setAudioDuration(playlistSongs.get(i).getAudioDuration());
                    downloadAudioDetails.setIsSingle("0");
                    if (downloadAudioDetailsList.size() != 0) {
                        for (int y = 0; y < downloadAudioDetailsList.size(); y++) {
                            if (playlistSongs.get(i).getAudioFile().equalsIgnoreCase(downloadAudioDetailsList.get(y).getAudioFile())) {
                                downloadAudioDetails.setIsDownload("Complete");
                                downloadAudioDetails.setDownloadProgress(100);
                                break;
                            } else {
                                downloadAudioDetails.setIsDownload("pending");
                                downloadAudioDetails.setDownloadProgress(0);
                            }

                        }
                    }

                    DatabaseClient.getInstance(getActivity())
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

                getMediaByPer(PlaylistID, SongListSize);
                enableDisableDownload(false, "orange");
                downloadAudioDetailsList = GetAllMedia();
                playlistWiseAudioDetails = GetMedia();
                super.onPostExecute(aVoid);
            }
        }

        SaveMedia st = new SaveMedia();
        st.execute();
    }

    private void SaveMedia(byte[] encodeBytes, String dirPath, ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs, int i, RelativeLayout llDownload, ImageView ivDownloads) {
        class SaveMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
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
                downloadAudioDetails.setIsSingle("1");
                downloadAudioDetails.setPlaylistId("");
                downloadAudioDetails.setIsDownload("pending");
                downloadAudioDetails.setDownloadProgress(0);
                DatabaseClient.getInstance(activity)
                        .getaudioDatabase()
                        .taskDao()
                        .insertMedia(downloadAudioDetails);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                downloadAudioDetailsList = GetAllMedia();
                playlistWiseAudioDetails = GetMedia();
                downloadedSingleAudio = getMyMedia();
                disableDownload(llDownload, ivDownloads);
                super.onPostExecute(aVoid);
            }
        }

        SaveMedia st = new SaveMedia();
        st.execute();
    }

    public void GetMedia(String url, Context ctx, String download, RelativeLayout llDownload, ImageView ivDownloads) {
        oneAudioDetailsList = new ArrayList<>();
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {

                oneAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getLastIdByuId(url);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (oneAudioDetailsList.size() != 0) {
                    if (oneAudioDetailsList.get(0).getDownload().equalsIgnoreCase("1")) {
                        disableDownload(llDownload, ivDownloads);
                    }
                } else if (download.equalsIgnoreCase("1")) {
                    disableDownload(llDownload, ivDownloads);
                } else {
                    enableDownload(llDownload, ivDownloads);
                }
                super.onPostExecute(aVoid);
            }
        }

        GetMedia st = new GetMedia();
        st.execute();
    }

    public List<DownloadAudioDetails> GetMedia() {
        playlistWiseAudioDetails = new ArrayList<>();
        class GetMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                playlistWiseAudioDetails = DatabaseClient
                        .getInstance(getActivity())
                        .getaudioDatabase()
                        .taskDao()
                        .getAllAudioByPlaylist(PlaylistID);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (MyDownloads.equalsIgnoreCase("1")) {
                    if (downloadPlaylistDetailsList.size() != 0) {
                        SubPlayListModel responseData = new SubPlayListModel();
                        ArrayList<SubPlayListModel.ResponseData.PlaylistSong> details = new ArrayList<>();
                        SubPlayListModel.ResponseData listModel = new SubPlayListModel.ResponseData();
                        listModel.setPlaylistID(downloadPlaylistDetailsList.get(0).getPlaylistID());
                        listModel.setPlaylistName(downloadPlaylistDetailsList.get(0).getPlaylistName());
                        listModel.setPlaylistDesc(downloadPlaylistDetailsList.get(0).getPlaylistDesc());
                        listModel.setPlaylistMastercat(downloadPlaylistDetailsList.get(0).getPlaylistMastercat());
                        listModel.setPlaylistSubcat(downloadPlaylistDetailsList.get(0).getPlaylistSubcat());
                        listModel.setPlaylistImageDetail(downloadPlaylistDetailsList.get(0).getPlaylistImage());
                        listModel.setTotalAudio(downloadPlaylistDetailsList.get(0).getTotalAudio());
                        listModel.setTotalDuration(downloadPlaylistDetailsList.get(0).getTotalDuration());
                        listModel.setTotalhour(downloadPlaylistDetailsList.get(0).getTotalhour());
                        listModel.setTotalminute(downloadPlaylistDetailsList.get(0).getTotalminute());
                        listModel.setCreated(downloadPlaylistDetailsList.get(0).getCreated());
                        listModel.setLike(downloadPlaylistDetailsList.get(0).getLike());
                        listModel.setIsReminder(downloadPlaylistDetailsList.get(0).getIsReminder());
                        if (playlistWiseAudioDetails.size() != 0) {
                            for (int i = 0; i < playlistWiseAudioDetails.size(); i++) {
                                SubPlayListModel.ResponseData.PlaylistSong detail = new SubPlayListModel.ResponseData.PlaylistSong();
                                detail.setID(playlistWiseAudioDetails.get(i).getID());
                                detail.setName(playlistWiseAudioDetails.get(i).getName());
                                detail.setAudioFile(playlistWiseAudioDetails.get(i).getAudioFile());
                                detail.setAudioDirection(playlistWiseAudioDetails.get(i).getAudioDirection());
                                detail.setAudiomastercat(playlistWiseAudioDetails.get(i).getAudiomastercat());
                                detail.setAudioSubCategory(playlistWiseAudioDetails.get(i).getAudioSubCategory());
                                detail.setImageFile(playlistWiseAudioDetails.get(i).getImageFile());
                                detail.setLike(playlistWiseAudioDetails.get(i).getLike());
                                detail.setDownload(playlistWiseAudioDetails.get(i).getDownload());
                                detail.setAudioDuration(playlistWiseAudioDetails.get(i).getAudioDuration());
                                details.add(detail);
                            }
                            listModel.setPlaylistSongs(details);
                        }
                        setData(listModel);
                    }
                }
                super.onPostExecute(aVoid);
            }
        }

        GetMedia st = new GetMedia();
        st.execute();
        return playlistWiseAudioDetails;
    }

    private void enableDownload(RelativeLayout llDownload, ImageView ivDownloads) {
        llDownload.setClickable(true);
        llDownload.setEnabled(true);
        ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
        ivDownloads.setColorFilter(activity.getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
    }

    private void disableDownload(RelativeLayout llDownload, ImageView ivDownloads) {
        ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
        ivDownloads.setColorFilter(activity.getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
        llDownload.setClickable(false);
        llDownload.setEnabled(false);
    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }

    private void addDisclaimer() {
        addDisclaimer = new SubPlayListModel.ResponseData.PlaylistSong();
        addDisclaimer.setID("0");
        addDisclaimer.setName("Disclaimer");
        addDisclaimer.setAudioFile("");
        addDisclaimer.setAudioDirection("The audio shall start playing after the disclaimer");
        addDisclaimer.setAudiomastercat("");
        addDisclaimer.setAudioSubCategory("");
        addDisclaimer.setImageFile("");
        addDisclaimer.setLike("");
        addDisclaimer.setDownload("");
        addDisclaimer.setAudioDuration("0:48");
    }

    public class PlayListsAdpater extends RecyclerView.Adapter<PlayListsAdpater.MyViewHolder> implements Filterable/*, StartDragListener*/, ItemMoveCallback.ItemTouchHelperContract {
        Context ctx;
        String UserID, Created, name;
        StartDragListener startDragListener;
        private ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList;
        private ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listFilterData;

        public PlayListsAdpater(ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList, Context ctx, String UserID,
                                String Created, StartDragListener startDragListener) {
            this.listModelList = listModelList;
            this.listFilterData = listModelList;
            this.ctx = ctx;
            this.UserID = UserID;
            this.Created = Created;
            this.startDragListener = startDragListener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MyPlaylistLayoutSorting2Binding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.my_playlist_layout_sorting2, parent, false);
            return new MyViewHolder(v);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final ArrayList<SubPlayListModel.ResponseData.PlaylistSong> mData = listFilterData;

            /*UpdateSongTime2 = new Runnable() {
                @Override
                public void run() {
                    try {
//                        for (int f = 0; f < GlobalListModel.getPlaylistSongs().size(); f++) {
                        if (fileNameList.size() != 0) {
                            for (int i = 0; i < fileNameList.size(); i++) {
                                if (fileNameList.get(i).equalsIgnoreCase(GlobalListModel.getPlaylistSongs().get(position).getName())) {
                                    if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(GlobalListModel.getPlaylistSongs().get(position).getName())) {
                                        if (downloadProgress <= 100) {
                                            if (BWSApplication.isNetworkConnected(ctx)) {
                                                notifyItemChanged(position);
                                            }
                                        } else {
                                            holder.binding.pbProgress.setVisibility(View.GONE);
                                            holder.binding.ivDownloads.setVisibility(View.VISIBLE);
                                            getDownloadData();
                                        }
                                    } else {
//                                        notifyItemChanged(position);
                                    }
                                }
                            }
//                            }
                        }
                        if (downloadProgress == 0) {
                            notifyDataSetChanged();
                            getDownloadData();
                        }
                        handler2.postDelayed(this, 3000);
                    } catch (Exception e) {

                    }
                }
            };
            searchEditText.setHint(R.string.playlist_or_audio_search);
            binding.tvSearch.setHint(R.string.playlist_or_audio_search);
            binding.tvSearch.setVisibility(View.VISIBLE);
            binding.searchView.setVisibility(View.GONE);
            holder.binding.tvTitle.setText(mData.get(position).getName());
            holder.binding.tvTime.setText(mData.get(position).getAudioDuration());*/

            SharedPreferences sharedzw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
            if (MyDownloads.equalsIgnoreCase("1")) {
                if (audioPlayz && AudioFlag.equalsIgnoreCase("Downloadlist") && pIDz.equalsIgnoreCase(PlaylistName)) {
                    if(myAudioId.equalsIgnoreCase(mData.get(position).getID())){
                        holder.binding.llHighLight.setBackgroundResource(R.color.highlight_background);
                    }else{
                        holder.binding.llHighLight.setBackgroundResource(R.color.white);
                    }
                }else{
                    holder.binding.llHighLight.setBackgroundResource(R.color.white);
                    handler3.removeCallbacks(UpdateSongTime3);
                }
            } else {
                if (audioPlayz && AudioFlag.equalsIgnoreCase("SubPlayList") && pIDz.equalsIgnoreCase(PlaylistID)) {
                    if(myAudioId.equalsIgnoreCase(mData.get(position).getID())){
                        holder.binding.llHighLight.setBackgroundResource(R.color.highlight_background);
//            holder.binding.equalizerview.stopBars();
                    }else{
                        holder.binding.llHighLight.setBackgroundResource(R.color.white);
                    }
                }else{
                    holder.binding.llHighLight.setBackgroundResource(R.color.white);
                    handler3.removeCallbacks(UpdateSongTime3);
                }
            }

          /*  holder.binding.llMainLayout.setVisibility(View.GONE);
            holder.binding.llDownload.setVisibility(View.GONE);
            holder.binding.llRemove.setVisibility(View.GONE);*/
//            holder.binding.llHighLight.setBackgroundResource(R.color.highlight_background);
//          holder.binding.llThirdLayout.setWeightSum(0.13f);
            holder.binding.llSort.setOnTouchListener((v, event) -> {
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall sort after the disclaimer", ctx);
                    } else {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            startDragListener.requestDrag(holder);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            startDragListener.requestDrag(holder);
                        }
                    }
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        startDragListener.requestDrag(holder);
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        startDragListener.requestDrag(holder);
                    }
                }
                return false;
            });
           /* String id = mData.get(position).getID();

            if (fileNameList.size() != 0) {
                for (int i = 0; i < fileNameList.size(); i++) {
                    if (fileNameList.get(i).equalsIgnoreCase(mData.get(position).getName()) && playlistDownloadId.get(i).equalsIgnoreCase("")) {
                        if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(mData.get(position).getName())) {
                            if (downloadProgress <= 100) {
                                if (downloadProgress == 100) {
                                    holder.binding.pbProgress.setVisibility(View.GONE);
                                    holder.binding.ivDownloads.setVisibility(View.VISIBLE);
                                } else {
                                    holder.binding.pbProgress.setProgress(downloadProgress);
                                    holder.binding.pbProgress.setVisibility(View.VISIBLE);
                                    holder.binding.ivDownloads.setVisibility(View.GONE);
                                }
                            } else {
                                holder.binding.pbProgress.setVisibility(View.GONE);
                                holder.binding.ivDownloads.setVisibility(View.VISIBLE);
//                                handler2.removeCallbacks(UpdateSongTime2);
                            }
                            handler2.postDelayed(UpdateSongTime2, 3000);
                        } else {
                            holder.binding.pbProgress.setVisibility(View.VISIBLE);
                            holder.binding.ivDownloads.setVisibility(View.GONE);
                            handler2.postDelayed(UpdateSongTime2, 3000);
                        }
                    }
                }
            } else {
                holder.binding.pbProgress.setVisibility(View.GONE);
                holder.binding.ivDownloads.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < downloadAudioDetailsList.size(); i++) {
                if (downloadAudioDetailsList.get(i).getAudioFile().equalsIgnoreCase(mData.get(position).getAudioFile())
                        && downloadAudioDetailsList.get(i).getPlaylistId().equalsIgnoreCase("")) {
                    disableDownload(holder.binding.llDownload, holder.binding.ivDownloads);
                    break;
                } else {
                    enableDownload(holder.binding.llDownload, holder.binding.ivDownloads);
                }
            }

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(mData.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            binding.ivPlaylistStatus.setOnClickListener(view -> {
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(listModelList.get(0).getPlaylistID())) {

                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                    } else {
                        callTransparentFrag(0, ctx, listModelList, "myPlaylist", PlaylistID);
                    }
                } else {
                    isDisclaimer = 0;
                    disclaimerPlayed = 0;
                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList2 = new ArrayList<>();
                    listModelList2.add(addDisclaimer);
                    listModelList2.addAll(listModelList);
                    callTransparentFrag(0, ctx, listModelList2, "myPlaylist", PlaylistID);
                }
            });

            holder.binding.llMainLayout.setOnClickListener(view -> {
                int pos = holder.getAdapterPosition();
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                Log.e("postion of paly", String.valueOf(position));
                if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                    } else {
                        callTransparentFrag(pos, ctx, listModelList, "myPlaylist", PlaylistID);
                    }
                } else {
                    isDisclaimer = 0;
                    disclaimerPlayed = 0;
                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList2 = new ArrayList<>();
                    if (position != 0) {
                        listModelList2.addAll(listModelList);
                        listModelList2.add(pos, addDisclaimer);
                    } else {
                        listModelList2.add(addDisclaimer);
                        listModelList2.addAll(listModelList);
                    }
                    callTransparentFrag(pos, ctx, listModelList2, "myPlaylist", PlaylistID);
                }
            });

            holder.binding.llDownload.setOnClickListener(view -> {
                name = mData.get(position).getName();
                holder.binding.pbProgress.setVisibility(View.VISIBLE);
                holder.binding.ivDownloads.setVisibility(View.GONE);
                callDownload(mData.get(position).getID(), mData.get(position).getAudioFile(), mData.get(position).getName(), listFilterData, position, holder.binding.llDownload, holder.binding.ivDownloads);
            });
            try {
                holder.binding.llRemove.setOnClickListener(view -> {
                    handler2.removeCallbacks(UpdateSongTime2);
                    SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                    if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                        if (isDisclaimer == 1) {
                            BWSApplication.showToast("The audio shall remove after the disclaimer", ctx);
                        } else {
                            if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID) && mData.size() == 1) {
                                BWSApplication.showToast("Currently you play this playlist, you can't remove last audio", ctx);
                            } else {
                                callRemove(mData.get(position).getID(), mData.get(position).getPlaylistAudioId(), mData, position);
                            }
                        }
                    } else {
                        if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID) && mData.size() == 1) {
                            BWSApplication.showToast("Currently you play this playlist, you can't remove last audio", ctx);
                        } else {
                            callRemove(mData.get(position).getID(), mData.get(position).getPlaylistAudioId(), mData, position);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }

        @Override
        public int getItemCount() {
            if (listFilterData != null) {
                return listFilterData.size();
            }
            return 0;
        }

        @Override
        public void onRowMoved(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(listModelList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(listModelList, i, i - 1);
                }
            }
            changedAudio.clear();
            for (int i = 0; i < listModelList.size(); i++) {
                changedAudio.add(listModelList.get(i).getID());
            }
            callDragApi();
            notifyItemMoved(fromPosition, toPosition);
            adpater1.notifyItemMoved(fromPosition, toPosition);
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            int pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
            if (audioPlay) {
                if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
                    String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                    if (pID.equalsIgnoreCase(PlaylistID)) {
                        if (fromPosition == pos) {
                            pos = toPosition;
                            String one = "1";
                            Log.e("one", one);
                        }/* else if (toPosition == pos) {
                            if (action == 0) {
                                pos = pos + 1;
                            } else if (action == 1) {
                                pos = pos - 1;
                            }
                        }*/ else if (fromPosition < pos && toPosition > pos) {
                            pos = pos - 1;
                            String one = "2";
                            Log.e("one", one);
                        } else if ((fromPosition > pos && toPosition > pos) || (fromPosition < pos && toPosition < pos)) {
                            pos = pos;
                            String one = "3";
                            Log.e("one", one);
                        } else if (fromPosition > pos && toPosition < pos) {
                            pos = pos + 1;
                            String one = "4";
                            Log.e("one", one);
                        } else if (fromPosition > pos && toPosition == pos) {
                            pos = pos + 1;
                            String one = "5";
                            Log.e("one", one);
                        } else if (fromPosition < pos && toPosition == pos) {
                            pos = pos - 1;
                            String one = "6";
                            Log.e("one", one);
                        }
                        SharedPreferences shareddd = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shareddd.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(listModelList);
                        editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                        editor.putInt(CONSTANTS.PREF_KEY_position, pos);
                        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                        editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistID);
                        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "myPlaylist");
                        editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
                        editor.commit();
                        callAddTransFrag();
                    }
                }
            }

         /* SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(listModelList);
            editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            editor.commit();*/

        }

        private void callDragApi() {
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Call<CardModel> listCall = APIClient.getClient().setShortedAudio(UserID, PlaylistID, TextUtils.join(",", changedAudio));
                listCall.enqueue(new Callback<CardModel>() {
                    @Override
                    public void onResponse(Call<CardModel> call, Response<CardModel> response) {
                        if (response.isSuccessful()) {
                            CardModel listModel = response.body();
                        }
                    }

                    @Override
                    public void onFailure(Call<CardModel> call, Throwable t) {
                    }
                });
            }
        }

        @Override
        public void onRowSelected(RecyclerView.ViewHolder myViewHolder) {

        }

        @Override
        public void onRowClear(RecyclerView.ViewHolder myViewHolder) {

        }

       /* @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }*/

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    final FilterResults filterResults = new FilterResults();
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        listFilterData = listModelList;
                    } else {
                        ArrayList<SubPlayListModel.ResponseData.PlaylistSong> filteredList = new ArrayList<>();
                        for (SubPlayListModel.ResponseData.PlaylistSong row : listModelList) {
                            if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                        listFilterData = filteredList;
                    }
                    filterResults.values = listFilterData;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    if (listFilterData.size() == 0) {
                        binding.llError.setVisibility(View.VISIBLE);
                        binding.tvFound.setText("Couldn't find '" + SearchFlag + "'. Try searching again");
                        binding.rvPlayLists2.setVisibility(View.GONE);
                    } else {
                        binding.llError.setVisibility(View.GONE);
                        binding.rvPlayLists2.setVisibility(View.VISIBLE);
                        listFilterData = (ArrayList<SubPlayListModel.ResponseData.PlaylistSong>) filterResults.values;
                        notifyDataSetChanged();
                    }
                }
            };
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            MyPlaylistLayoutSorting2Binding binding;

            public MyViewHolder(MyPlaylistLayoutSorting2Binding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class PlayListsAdpater1 extends RecyclerView.Adapter<PlayListsAdpater1.MyViewHolder> implements Filterable/*, ItemMoveCallback.ItemTouchHelperContract */ {
        Context ctx;
        String UserID, Created, name,songId;
        StartDragListener startDragListener;
        private ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList;
        private ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listFilterData;

        public PlayListsAdpater1(ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList, Context ctx, String UserID,
                                 String Created, StartDragListener startDragListener) {
            this.listModelList = listModelList;
            this.listFilterData = listModelList;
            this.ctx = ctx;
            this.UserID = UserID;
            this.Created = Created;
            this.startDragListener = startDragListener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MyPlaylistLayoutSorting1Binding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.my_playlist_layout_sorting1, parent, false);
            return new MyViewHolder(v);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final ArrayList<SubPlayListModel.ResponseData.PlaylistSong> mData = listFilterData;
            UpdateSongTime3 = new Runnable() {
                @Override
                public void run() {
                    try {
                        startTime = getStartTime();
                        myProgress = currentDuration;
                        currentDuration = getStartTime();
                        diff = totalDuration - myProgress;
                        if (currentDuration == 0 && isCompleteStop) {
                            binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                        }  else if (currentDuration >= 1 && !isPause) {
                            binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
                        } else if (currentDuration >= 1 && isPause) {
                            binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                        }
                        if(!songId.equalsIgnoreCase(myAudioId)){
                            notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler3.postDelayed(this, 500);
                }
            };
            UpdateSongTime2 = new Runnable() {
                @Override
                public void run() {
                    try {
//                        for (int f = 0; f < GlobalListModel.getPlaylistSongs().size(); f++) {
                        if (fileNameList.size() != 0) {
                            for (int i = 0; i < fileNameList.size(); i++) {
                                if (fileNameList.get(i).equalsIgnoreCase(GlobalListModel.getPlaylistSongs().get(position).getName())) {
                                    if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(GlobalListModel.getPlaylistSongs().get(position).getName())) {
                                        if (downloadProgress <= 100) {
                                            if (BWSApplication.isNetworkConnected(ctx)) {
                                                notifyItemChanged(position);
                                            }
                                        } else {
                                            holder.binding.pbProgress.setVisibility(View.GONE);
                                            holder.binding.ivDownloads.setVisibility(View.VISIBLE);
                                            getDownloadData();
                                        }
                                    } else {
//                                        notifyItemChanged(position);
                                    }
                                }
                            }
//                            }
                        }
                        if (downloadProgress == 0) {
                            notifyDataSetChanged();
                            getDownloadData();
                        }
                        handler2.postDelayed(this, 3000);
                    } catch (Exception e) {

                    }
                }
            };
            holder.binding.llMainLayout.setVisibility(View.VISIBLE);
            holder.binding.llDownload.setVisibility(View.VISIBLE);
            holder.binding.llRemove.setVisibility(View.VISIBLE);
            holder.binding.llSort.setVisibility(View.GONE);
            searchEditText.setHint(R.string.playlist_or_audio_search);
            binding.tvSearch.setHint(R.string.playlist_or_audio_search);
            binding.tvSearch.setVisibility(View.VISIBLE);
            binding.searchView.setVisibility(View.GONE);
            holder.binding.tvTitle.setText(mData.get(position).getName());
            holder.binding.tvTime.setText(mData.get(position).getAudioDuration());
//            holder.binding.llThirdLayout.setWeightSum(0.26f);
           /* holder.binding.llSort.setOnTouchListener((v, event) -> {
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall sort after the disclaimer", ctx);
                    } else {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            startDragListener.requestDrag(holder);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            startDragListener.requestDrag(holder);
                        }
                    }
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        startDragListener.requestDrag(holder);
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        startDragListener.requestDrag(holder);
                    }
                }
                return false;
            });*/
            String id = mData.get(position).getID();
            SharedPreferences sharedzw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
            if (MyDownloads.equalsIgnoreCase("1")) {
                if (audioPlayz && AudioFlag.equalsIgnoreCase("Downloadlist") && pIDz.equalsIgnoreCase(PlaylistName)) {
                    if(myAudioId.equalsIgnoreCase(mData.get(position).getID())){
                        songId = myAudioId;
                        holder.binding.equalizerview.animateBars();
                        holder.binding.equalizerview.setVisibility(View.VISIBLE);
                        holder.binding.llHighLight.setBackgroundResource(R.color.highlight_background);
                        adpater.notifyItemChanged(position);
                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                        holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
//            holder.binding.equalizerview.stopBars();
                    }else{
                        holder.binding.equalizerview.setVisibility(View.GONE);
                        holder.binding.llHighLight.setBackgroundResource(R.color.white);
                        holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    }
                }else{
                    holder.binding.equalizerview.setVisibility(View.GONE);
                    holder.binding.llHighLight.setBackgroundResource(R.color.white);
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    handler3.removeCallbacks(UpdateSongTime3);
                }
                handler3.postDelayed(UpdateSongTime3,500);
            } else {
                if (audioPlayz && AudioFlag.equalsIgnoreCase("SubPlayList") && pIDz.equalsIgnoreCase(PlaylistID)) {
                    if(myAudioId.equalsIgnoreCase(mData.get(position).getID())){
                        songId = myAudioId;
                        holder.binding.equalizerview.animateBars();
                        holder.binding.equalizerview.setVisibility(View.VISIBLE);
                        holder.binding.llHighLight.setBackgroundResource(R.color.highlight_background);
                        adpater.notifyItemChanged(position);
                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                        holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
//            holder.binding.equalizerview.stopBars();
                    }else{
                        holder.binding.equalizerview.setVisibility(View.GONE);
                        holder.binding.llHighLight.setBackgroundResource(R.color.white);
                        holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    }
                    handler3.postDelayed(UpdateSongTime3,500);
                }else{
                    holder.binding.equalizerview.setVisibility(View.GONE);
                    holder.binding.llHighLight.setBackgroundResource(R.color.white);
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    handler3.removeCallbacks(UpdateSongTime3);
                }
            }
            if (fileNameList.size() != 0) {
                for (int i = 0; i < fileNameList.size(); i++) {
                    if (fileNameList.get(i).equalsIgnoreCase(mData.get(position).getName()) && playlistDownloadId.get(i).equalsIgnoreCase("")) {
                        if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(mData.get(position).getName())) {
                            if (downloadProgress <= 100) {
                                if (downloadProgress == 100) {
                                    holder.binding.pbProgress.setVisibility(View.GONE);
                                    holder.binding.ivDownloads.setVisibility(View.VISIBLE);
                                } else {
                                    holder.binding.pbProgress.setProgress(downloadProgress);
                                    holder.binding.pbProgress.setVisibility(View.VISIBLE);
                                    holder.binding.ivDownloads.setVisibility(View.GONE);
                                }
                            } else {
                                holder.binding.pbProgress.setVisibility(View.GONE);
                                holder.binding.ivDownloads.setVisibility(View.VISIBLE);
//                                handler2.removeCallbacks(UpdateSongTime2);
                            }
                            handler2.postDelayed(UpdateSongTime2, 3000);
                        } else {
                            holder.binding.pbProgress.setVisibility(View.VISIBLE);
                            holder.binding.ivDownloads.setVisibility(View.GONE);
                            handler2.postDelayed(UpdateSongTime2, 3000);
                        }
                    }
                }
            } else {
                holder.binding.pbProgress.setVisibility(View.GONE);
                holder.binding.ivDownloads.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < downloadAudioDetailsList.size(); i++) {
                if (downloadAudioDetailsList.get(i).getAudioFile().equalsIgnoreCase(mData.get(position).getAudioFile())
                        && downloadAudioDetailsList.get(i).getPlaylistId().equalsIgnoreCase("")) {
                    disableDownload(holder.binding.llDownload, holder.binding.ivDownloads);
                    break;
                } else {
                    enableDownload(holder.binding.llDownload, holder.binding.ivDownloads);
                }
            }

           /* MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);*/
//            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
//            holder.binding.llHighLight.setBackgroundResource(R.color.highlight_background);
//            holder.binding.equalizerview.animateBars();
//            holder.binding.equalizerview.stopBars();
            Glide.with(ctx).load(mData.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            binding.ivPlaylistStatus.setOnClickListener(view -> {
                if (isPlayPlaylist == 1) {
                    pauseMedia();
                    isPlayPlaylist = 2;
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                } else if (isPlayPlaylist == 2) {
                    resumeMedia();
                    isPlayPlaylist = 1;
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
                } else {
                    SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                    if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(listModelList.get(0).getPlaylistID())) {
                        if (isDisclaimer == 1) {
                            if (isPause) {
                                resumeMedia();
                            } else
                                BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                        } else {
                            callTransparentFrag(0, ctx, listModelList, "myPlaylist", PlaylistID);
                        }
                    } else {
                        isDisclaimer = 0;
                        disclaimerPlayed = 0;
                        ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList2 = new ArrayList<>();
                        listModelList2.add(addDisclaimer);
                        listModelList2.addAll(listModelList);
                        callTransparentFrag(0, ctx, listModelList2, "myPlaylist", PlaylistID);
                    }
                    isPlayPlaylist = 1;
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
                }
                songId = mData.get(position).getID();
                handler3.postDelayed(UpdateSongTime3,500);
            });

            holder.binding.llMainLayout.setOnClickListener(view -> {
                int pos = holder.getAdapterPosition();
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                Log.e("postion of paly", String.valueOf(position));
                if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                    } else {
                        callTransparentFrag(pos, ctx, listModelList, "myPlaylist", PlaylistID);
                    }
                } else {
                    isDisclaimer = 0;
                    disclaimerPlayed = 0;
                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList2 = new ArrayList<>();
                    if (position != 0) {
                        listModelList2.addAll(listModelList);
                        listModelList2.add(pos, addDisclaimer);
                    } else {
                        listModelList2.add(addDisclaimer);
                        listModelList2.addAll(listModelList);
                    }
                    callTransparentFrag(pos, ctx, listModelList2, "myPlaylist", PlaylistID);
                }
                isPlayPlaylist = 1;
                songId = mData.get(position).getID();
                handler3.postDelayed(UpdateSongTime3,500);
                binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
            });
            holder.binding.llDownload.setOnClickListener(view -> {
                name = mData.get(position).getName();
                holder.binding.pbProgress.setVisibility(View.VISIBLE);
                holder.binding.ivDownloads.setVisibility(View.GONE);
                callDownload(mData.get(position).getID(), mData.get(position).getAudioFile(), mData.get(position).getName(), listFilterData, position, holder.binding.llDownload, holder.binding.ivDownloads);
            });
            try {
                holder.binding.llRemove.setOnClickListener(view -> {
                    handler2.removeCallbacks(UpdateSongTime2);
                    SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                    if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                        if (isDisclaimer == 1) {
                            BWSApplication.showToast("The audio shall remove after the disclaimer", ctx);
                        } else {
                            if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID) && mData.size() == 1) {
                                BWSApplication.showToast("Currently you play this playlist, you can't remove last audio", ctx);
                            } else {
                                callRemove(mData.get(position).getID(), mData.get(position).getPlaylistAudioId(), mData, position);
                            }
                        }
                    } else {
                        if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID) && mData.size() == 1) {
                            BWSApplication.showToast("Currently you play this playlist, you can't remove last audio", ctx);
                        } else {
                            callRemove(mData.get(position).getID(), mData.get(position).getPlaylistAudioId(), mData, position);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (listFilterData != null) {
                return listFilterData.size();
            }
            return 0;
        }

      /*  @Override
        public void onRowMoved(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(listModelList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(listModelList, i, i - 1);
                }
            }
            changedAudio.clear();
            for (int i = 0; i < listModelList.size(); i++) {
                changedAudio.add(listModelList.get(i).getID());
            }

            callDragApi();

            notifyItemMoved(fromPosition, toPosition);
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            int pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
            if (audioPlay) {
                if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
                    String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                    if (pID.equalsIgnoreCase(PlaylistID)) {
                        if (fromPosition == pos) {
                            pos = toPosition;
                            String one = "1";
                            Log.e("one", one);
                        }*//* else if (toPosition == pos) {
                            if (action == 0) {
                                pos = pos + 1;
                            } else if (action == 1) {
                                pos = pos - 1;
                            }
                        }*//* else if (fromPosition < pos && toPosition > pos) {
                            pos = pos - 1;
                            String one = "2";
                            Log.e("one", one);
                        } else if ((fromPosition > pos && toPosition > pos) || (fromPosition < pos && toPosition < pos)) {
                            pos = pos;
                            String one = "3";
                            Log.e("one", one);
                        } else if (fromPosition > pos && toPosition < pos) {
                            pos = pos + 1;
                            String one = "4";
                            Log.e("one", one);
                        } else if (fromPosition > pos && toPosition == pos) {
                            pos = pos + 1;
                            String one = "5";
                            Log.e("one", one);
                        } else if (fromPosition < pos && toPosition == pos) {
                            pos = pos - 1;
                            String one = "6";
                            Log.e("one", one);
                        }
                        SharedPreferences shareddd = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shareddd.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(listModelList);
                        editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                        editor.putInt(CONSTANTS.PREF_KEY_position, pos);
                        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                        editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistID);
                        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "myPlaylist");
                        editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
                        editor.commit();
                        callAddTransFrag();
                    }
                }
            }

         *//* SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(listModelList);
            editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            editor.commit();*//*

        }

        private void callDragApi() {
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Call<CardModel> listCall = APIClient.getClient().setShortedAudio(UserID, PlaylistID, TextUtils.join(",", changedAudio));
                listCall.enqueue(new Callback<CardModel>() {
                    @Override
                    public void onResponse(Call<CardModel> call, Response<CardModel> response) {
                        if (response.isSuccessful()) {
                            CardModel listModel = response.body();
                        }
                    }

                    @Override
                    public void onFailure(Call<CardModel> call, Throwable t) {
                    }
                });
            }
        }

        @Override
        public void onRowSelected(RecyclerView.ViewHolder myViewHolder) {

        }

        @Override
        public void onRowClear(RecyclerView.ViewHolder myViewHolder) {

        }*/

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    final FilterResults filterResults = new FilterResults();
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        listFilterData = listModelList;
                    } else {
                        ArrayList<SubPlayListModel.ResponseData.PlaylistSong> filteredList = new ArrayList<>();
                        for (SubPlayListModel.ResponseData.PlaylistSong row : listModelList) {
                            if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                        listFilterData = filteredList;
                    }
                    filterResults.values = listFilterData;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    if (listFilterData.size() == 0) {
                        binding.llError.setVisibility(View.VISIBLE);
                        binding.tvFound.setText("Couldn't find '" + SearchFlag + "'. Try searching again");
                        binding.rvPlayLists2.setVisibility(View.GONE);
                    } else {
                        binding.llError.setVisibility(View.GONE);
                        binding.rvPlayLists2.setVisibility(View.VISIBLE);
                        listFilterData = (ArrayList<SubPlayListModel.ResponseData.PlaylistSong>) filterResults.values;
                        notifyDataSetChanged();
                    }
                }
            };
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            MyPlaylistLayoutSorting1Binding binding;

            public MyViewHolder(MyPlaylistLayoutSorting1Binding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class PlayListsAdpater2 extends RecyclerView.Adapter<PlayListsAdpater2.MyViewHolder2> implements Filterable {
        Context ctx;
        String UserID, Created,songId;
        private ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList;
        private ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listFilterData;

        public PlayListsAdpater2(ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList, Context ctx, String UserID,
                                 String Created) {
            this.listModelList = listModelList;
            this.listFilterData = listModelList;
            this.ctx = ctx;
            this.UserID = UserID;
            this.Created = Created;
        }

        @NonNull
        @Override
        public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MyPlaylistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.my_playlist_layout, parent, false);
            return new MyViewHolder2(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder2 holder, int position) {
            UpdateSongTime3 = new Runnable() {
                @Override
                public void run() {
                    try {
                        startTime = getStartTime();
                        myProgress = currentDuration;
                        currentDuration = getStartTime();
                        diff = totalDuration - myProgress;
                        if (currentDuration == 0 && isCompleteStop) {
                            binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                        }  else if (currentDuration >= 1 && !isPause) {
                            binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
                        } else if (currentDuration >= 1 && isPause) {
                            binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                        }

                        if(!songId.equalsIgnoreCase(myAudioId)){
                            notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler3.postDelayed(this, 500);
                }
            };
            searchEditText.setHint("Search for audios");
            binding.tvSearch.setHint("Search for audios");

            final ArrayList<SubPlayListModel.ResponseData.PlaylistSong> mData = listFilterData;
            holder.binding.tvTitleA.setText(mData.get(position).getName());
            holder.binding.tvTimeA.setText(mData.get(position).getAudioDuration());
            binding.tvSearch.setVisibility(View.GONE);
            binding.searchView.setVisibility(View.VISIBLE);
            String id = mData.get(position).getID();
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.rlImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.rlImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with(ctx).load(mData.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
//            GetMedia(id, activity, mData.get(position).getDownload(), holder.binding.llDownload, holder.binding.ivDownloads);
//            holder.binding.equalizerview.animateBars();
//            holder.binding.equalizerview.stopBars();
            SharedPreferences sharedzw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
            if (MyDownloads.equalsIgnoreCase("1")) {
                if (audioPlayz && AudioFlag.equalsIgnoreCase("Downloadlist") && pIDz.equalsIgnoreCase(PlaylistName)) {
                    if(myAudioId.equalsIgnoreCase(mData.get(position).getID())){
                        holder.binding.equalizerview.animateBars();
                        holder.binding.equalizerview.setVisibility(View.VISIBLE);
                        holder.binding.llMainLayout.setBackgroundResource(R.color.highlight_background);
                        songId = myAudioId;
                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                        holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
//            holder.binding.equalizerview.stopBars();
                    }else{
                        holder.binding.equalizerview.setVisibility(View.GONE);
                        holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                        holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    }
                    handler3.postDelayed(UpdateSongTime3,500);
                }else{
                    holder.binding.equalizerview.setVisibility(View.GONE);
                    holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    handler3.removeCallbacks(UpdateSongTime3);
                }
            } else {
                if (audioPlayz && AudioFlag.equalsIgnoreCase("SubPlayList") && pIDz.equalsIgnoreCase(PlaylistID)) {
                    if(myAudioId.equalsIgnoreCase(mData.get(position).getID())){
                        holder.binding.equalizerview.animateBars();
                        holder.binding.equalizerview.setVisibility(View.VISIBLE);
                        holder.binding.llMainLayout.setBackgroundResource(R.color.highlight_background);
                        songId = myAudioId;
                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                        holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
//            holder.binding.equalizerview.stopBars();
                    }else{
                        holder.binding.equalizerview.setVisibility(View.GONE);
                        holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                        holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    }
                    handler3.postDelayed(UpdateSongTime3,500);
                }else{
                    holder.binding.equalizerview.setVisibility(View.GONE);
                    holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    handler3.removeCallbacks(UpdateSongTime3);
                }
            }

            binding.ivPlaylistStatus.setOnClickListener(view -> {
                if (isPlayPlaylist == 1) {
                    pauseMedia();
                    isPlayPlaylist = 2;
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                } else if (isPlayPlaylist == 2) {
                    resumeMedia();
                    isPlayPlaylist = 1;
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
                } else {
                    SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                    if (MyDownloads.equalsIgnoreCase("1")) {
                        if (audioPlay && AudioFlag.equalsIgnoreCase("Downloadlist") && pID.equalsIgnoreCase(PlaylistName)) {
                            if (isDisclaimer == 1) {
                                if (isPause) {
                                    resumeMedia();
                                } else
                                    BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                            } else {
                                callTransparentFrag(0, ctx, listModelList, "", PlaylistID);
                            }
                        } else {
                            isDisclaimer = 0;
                            disclaimerPlayed = 0;
                            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList2 = new ArrayList<>();
                            listModelList2.add(addDisclaimer);
                            listModelList2.addAll(listModelList);
                            callTransparentFrag(0, ctx, listModelList2, "", PlaylistID);
                        }
                    } else {
                        if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                            if (isDisclaimer == 1) {
                                if (isPause) {
                                    resumeMedia();
                                } else
                                    BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                            } else {
                                callTransparentFrag(0, ctx, listModelList, "", PlaylistID);
                            }
                        } else {
                            isDisclaimer = 0;
                            disclaimerPlayed = 0;
                            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList2 = new ArrayList<>();
                            listModelList2.add(addDisclaimer);
                            listModelList2.addAll(listModelList);
                            callTransparentFrag(0, ctx, listModelList2, "", PlaylistID);
                        }
                    }
                    isPlayPlaylist = 1;
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
                }
                songId = mData.get(position).getID();
                handler3.postDelayed(UpdateSongTime3,500);
            });
            holder.binding.llMainLayout.setOnClickListener(view -> {
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (MyDownloads.equalsIgnoreCase("1")) {
                    if (audioPlay && AudioFlag.equalsIgnoreCase("Downloadlist") && pID.equalsIgnoreCase(PlaylistName)) {
                        if (isDisclaimer == 1) {
                            BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                        } else {
                            callTransparentFrag(position, ctx, listModelList, "", PlaylistID);
                        }
                    } else {
                        isDisclaimer = 0;
                        disclaimerPlayed = 0;
                        ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList2 = new ArrayList<>();
                        if (position != 0) {
                            listModelList2.addAll(listModelList);
                            listModelList2.add(position, addDisclaimer);
                        } else {
                            listModelList2.add(addDisclaimer);
                            listModelList2.addAll(listModelList);
                        }
                        callTransparentFrag(position, ctx, listModelList2, "", PlaylistID);
                    }
                } else {
                    if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                        if (isDisclaimer == 1) {
                            BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                        } else {
                            callTransparentFrag(position, ctx, listModelList, "", PlaylistID);
                        }
                    } else {
                        isDisclaimer = 0;
                        disclaimerPlayed = 0;
                        ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList2 = new ArrayList<>();
                        if (position != 0) {
                            listModelList2.addAll(listModelList);
                            listModelList2.add(position, addDisclaimer);
                        } else {
                            listModelList2.add(addDisclaimer);
                            listModelList2.addAll(listModelList);
                        }
                        callTransparentFrag(position, ctx, listModelList2, "", PlaylistID);
                    }
                }
                isPlayPlaylist = 1;
                binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
                songId = mData.get(position).getID();
                handler3.postDelayed(UpdateSongTime3,500);
            });

            binding.llDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                    if (audioPlay && AudioFlag.equalsIgnoreCase("Downloadlist") && pID.equalsIgnoreCase(PlaylistName)) {
                        BWSApplication.showToast("Currently this playlist is in player,so you can't delete this playlist as of now", ctx);
                    } else {
                        final Dialog dialog = new Dialog(ctx);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.logout_layout);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.dark_blue_gray)));
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                        final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                        final TextView tvHeader = dialog.findViewById(R.id.tvHeader);
                        final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
                        final Button Btn = dialog.findViewById(R.id.Btn);
                        tvTitle.setText("Remove playlist");
                        tvHeader.setText("Are you sure you want to remove the " + PlaylistName + " from downloads??");
                        Btn.setText("Confirm");
                        dialog.setOnKeyListener((vi, keyCode, event) -> {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                dialog.dismiss();
                            }
                            return false;
                        });

                        Btn.setOnClickListener(views -> {
                            getDeleteDownloadData();
                            playlistWiseAudiosDetails = GetPlaylistMedia(PlaylistID);
                            dialog.dismiss();
                            Fragment fragment = new PlaylistFragment();
                            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                            fragmentManager1.beginTransaction()
                                    .replace(R.id.flContainer, fragment)
                                    .commit();
                        });

                        tvGoBack.setOnClickListener(viewd -> dialog.dismiss());
                        dialog.show();
                        dialog.setCancelable(false);

                    }
                }
            });

            if (BWSApplication.isNetworkConnected(ctx)) {
                holder.binding.llMore.setClickable(true);
                holder.binding.llMore.setEnabled(true);
                holder.binding.ivMore.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                holder.binding.llMore.setClickable(false);
                holder.binding.llMore.setEnabled(false);
                holder.binding.ivMore.setColorFilter(ContextCompat.getColor(getActivity(), R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            holder.binding.llMore.setOnClickListener(view -> {
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("You can see details after the disclaimer", ctx);
                    } else {
                        Intent i = new Intent(ctx, AddQueueActivity.class);
                        i.putExtra("play", "playlist");
                        i.putExtra("ID", mData.get(position).getID());
                        i.putExtra("PlaylistAudioId", mData.get(position).getPlaylistAudioId());
                        i.putExtra("position", position);
                        i.putParcelableArrayListExtra("data", mData);
                        i.putExtra("comeFrom", "myPlayList");
                        startActivity(i);
                    }
                } else {
                    Intent i = new Intent(ctx, AddQueueActivity.class);
                    i.putExtra("play", "playlist");
                    i.putExtra("ID", mData.get(position).getID());
                    i.putExtra("PlaylistAudioId", mData.get(position).getPlaylistAudioId());
                    i.putExtra("position", position);
                    i.putParcelableArrayListExtra("data", mData);
                    i.putExtra("comeFrom", "myPlayList");
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listFilterData.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    final FilterResults filterResults = new FilterResults();
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        listFilterData = listModelList;
                    } else {
                        ArrayList<SubPlayListModel.ResponseData.PlaylistSong> filteredList = new ArrayList<>();
                        for (SubPlayListModel.ResponseData.PlaylistSong row : listModelList) {
                            if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                        listFilterData = filteredList;
                    }
                    filterResults.values = listFilterData;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    if (listFilterData.size() == 0) {
                        binding.llError.setVisibility(View.VISIBLE);
                        binding.rvPlayLists2.setVisibility(View.GONE);
                        binding.tvFound.setText("Couldn't find '" + SearchFlag + "'. Try searching again");
                        Log.e("search", SearchFlag);
                    } else {
                        binding.llError.setVisibility(View.GONE);
                        binding.rvPlayLists2.setVisibility(View.VISIBLE);
                        listFilterData = (ArrayList<SubPlayListModel.ResponseData.PlaylistSong>) filterResults.values;
                        notifyDataSetChanged();
                    }
                }
            };
        }

        public class MyViewHolder2 extends RecyclerView.ViewHolder {
            MyPlaylistLayoutBinding binding;

            public MyViewHolder2(MyPlaylistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public List<DownloadAudioDetails> GetPlaylistMedia(String playlistID) {
        playlistWiseAudioDetails = new ArrayList<>();
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                playlistWiseAudioDetails = DatabaseClient
                        .getInstance(getActivity())
                        .getaudioDatabase()
                        .taskDao()
                        .getAllAudioByPlaylist(playlistID);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                deleteDownloadFile(getActivity(), playlistID);
                for (int i = 0; i < playlistWiseAudioDetails.size(); i++) {
                    GetSingleMedia(playlistWiseAudioDetails.get(i).getAudioFile(), getActivity(), playlistID);
                }
                super.onPostExecute(aVoid);
            }
        }
        GetMedia st = new GetMedia();
        st.execute();
        return playlistWiseAudioDetails;
    }

    private void deleteDownloadFile(Context applicationContext, String PlaylistId) {
        class DeleteMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(applicationContext)
                        .getaudioDatabase()
                        .taskDao()
                        .deleteByPlaylistId(PlaylistId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
//                notifyItemRemoved(position);
                deletePlaylist(PlaylistID);
                super.onPostExecute(aVoid);
            }
        }
        DeleteMedia st = new DeleteMedia();
        st.execute();
    }

    public void GetSingleMedia(String AudioFile, Context ctx, String playlistID) {
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                oneAudioDetailsList = DatabaseClient
                        .getInstance(getActivity())
                        .getaudioDatabase()
                        .taskDao()
                        .getLastIdByuId(AudioFile);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    if (oneAudioDetailsList.size() != 0) {
                        if (oneAudioDetailsList.size() == 1) {
                            FileUtils.deleteDownloadedFile(ctx, oneAudioDetailsList.get(0).getName());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onPostExecute(aVoid);
            }
        }
        GetMedia sts = new GetMedia();
        sts.execute();
    }

    public void getDeleteDownloadData() {
        List<String> fileNameList, fileNameList1, audioFile, playlistDownloadId;
        try {
            SharedPreferences sharedy = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
            Gson gson = new Gson();
            String jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
            String json1 = sharedy.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
            String jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
            if (!jsony.equalsIgnoreCase(String.valueOf(gson))) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                fileNameList = gson.fromJson(jsony, type);
                fileNameList1 = gson.fromJson(jsony, type);
                audioFile = gson.fromJson(json1, type);
                playlistDownloadId = gson.fromJson(jsonq, type);

                if (playlistDownloadId.size() != 0) {
                    playlistDownloadId.contains(PlaylistID);
                    for (int i = 1; i < fileNameList1.size(); i++) {
                        if (playlistDownloadId.get(i).equalsIgnoreCase(PlaylistID)) {
                            fileNameList.remove(i);
                            audioFile.remove(i);
                            playlistDownloadId.remove(i);
                        }
                    }
                }
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                String nameJson = gson.toJson(fileNameList);
                String urlJson = gson.toJson(audioFile);
                String playlistIdJson = gson.toJson(playlistDownloadId);
                editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                editor.commit();
                if (fileNameList.get(0).equalsIgnoreCase(filename) && playlistDownloadId.get(0).equalsIgnoreCase(PlaylistID)) {
                    PRDownloader.cancel(downloadIdOne);
                    filename = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deletePlaylist(String playlistId) {
        class DeleteMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getActivity())
                        .getaudioDatabase()
                        .taskDao()
                        .deletePlaylist(playlistId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }
        DeleteMedia st = new DeleteMedia();
        st.execute();
    }
}
