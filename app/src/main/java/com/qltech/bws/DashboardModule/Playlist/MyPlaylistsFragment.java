package com.qltech.bws.DashboardModule.Playlist;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.BillingOrderModule.Models.CardModel;
import com.qltech.bws.DashboardModule.Activities.AddAudioActivity;
import com.qltech.bws.DashboardModule.Activities.AddQueueActivity;
import com.qltech.bws.DashboardModule.Activities.MyPlaylistActivity;
import com.qltech.bws.DashboardModule.Models.ReminderStatusPlaylistModel;
import com.qltech.bws.DashboardModule.Models.SubPlayListModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.Search.SearchFragment;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.DownloadModule.Activities.DownloadsActivity;
import com.qltech.bws.EncryptDecryptUtils.DownloadMedia;
import com.qltech.bws.EncryptDecryptUtils.FileUtils;
import com.qltech.bws.R;
import com.qltech.bws.ReminderModule.Activities.ReminderActivity;
import com.qltech.bws.RoomDataBase.DatabaseClient;
import com.qltech.bws.RoomDataBase.DownloadAudioDetails;
import com.qltech.bws.RoomDataBase.DownloadPlaylistDetails;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.ItemMoveCallback;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.Utility.StartDragListener;
import com.qltech.bws.databinding.FragmentMyPlaylistsBinding;
import com.qltech.bws.databinding.MyPlaylistLayoutBinding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.qltech.bws.DashboardModule.Account.AccountFragment.ComeScreenReminder;
import static com.qltech.bws.DashboardModule.Activities.AddPlaylistActivity.MyPlaylistId;
import static com.qltech.bws.DashboardModule.Activities.AddPlaylistActivity.addToPlayList;
import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;
import static com.qltech.bws.DashboardModule.Activities.MyPlaylistActivity.ComeFindAudio;
import static com.qltech.bws.DashboardModule.Activities.MyPlaylistActivity.comeRename;
import static com.qltech.bws.DashboardModule.Activities.MyPlaylistActivity.deleteFrg;
import static com.qltech.bws.DashboardModule.Audio.AudioFragment.IsLock;
import static com.qltech.bws.DashboardModule.Playlist.ViewAllPlaylistFragment.GetPlaylistLibraryID;
import static com.qltech.bws.DashboardModule.Search.SearchFragment.comefrom_search;
import static com.qltech.bws.DownloadModule.Activities.DownloadsActivity.ComeFrom_Playlist;
import static com.qltech.bws.Utility.MusicService.isMediaStart;
import static com.qltech.bws.Utility.MusicService.isPause;
import static com.qltech.bws.Utility.MusicService.isPrepare;
import static com.qltech.bws.Utility.MusicService.stopMedia;

public class MyPlaylistsFragment extends Fragment implements StartDragListener {
    public static int RefreshIconData = 0;
    public static String RefreshNew = "";
    FragmentMyPlaylistsBinding binding;
    String UserID, New, PlaylistID, PlaylistName = "", PlaylistImage, SearchFlag, MyDownloads = "", AudioFlag;
    int RefreshIcon;
    PlayListsAdpater adpater;
    PlayListsAdpater2 adpater2;
    View view;
    EditText searchEditText;
    ArrayList<String> changedAudio;
    Activity activity;
    List<DownloadAudioDetails> downloadAudioDetailsList;
    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongsList;
    List<DownloadAudioDetails> oneAudioDetailsList, playlistWiseAudioDetails;
    List<DownloadPlaylistDetails> downloadPlaylistDetailsList;
    DownloadPlaylistDetails downloadPlaylistDetails;
    Dialog dialog;
    //    List<String> fileNameList, playlistDownloadId, remainAudio;
    ItemTouchHelper touchHelper;
   /* Runnable UpdateSongTime2;
    int SongListSize = 0;
    private Handler handler1, handler2;
    private Runnable UpdateSongTime1 = new Runnable() {
        @Override
        public void run() {
            if (fileNameList.size() != 0) {
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
                        }
                    } else if (downloadProgress1 <= 100) {
                        binding.pbProgress.setProgress(downloadProgress1);
                        binding.pbProgress.setVisibility(View.VISIBLE);
                    } else {
                        binding.pbProgress.setVisibility(View.GONE);
                        handler1.removeCallbacks(UpdateSongTime1);
                    }
                }
                getDownloadData();
                handler1.postDelayed(this, 10);
            }
        }
    };*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_playlists, container, false);
        view = binding.getRoot();
    /*    handler1 = new Handler();
        handler2 = new Handler();*/
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        activity = getActivity();
        downloadAudioDetailsList = new ArrayList<>();
        oneAudioDetailsList = new ArrayList<>();
    /*    fileNameList = new ArrayList<>();
        playlistDownloadId = new ArrayList<>();
        remainAudio = new ArrayList<>();*/
        playlistWiseAudioDetails = new ArrayList<>();
        downloadPlaylistDetailsList = new ArrayList<>();
        playlistSongsList = new ArrayList<>();
        changedAudio = new ArrayList<>();
        if (getArguments() != null) {
            New = getArguments().getString("New");
            PlaylistID = getArguments().getString("PlaylistID");
            PlaylistName = getArguments().getString("PlaylistName");
            PlaylistImage = getArguments().getString("PlaylistImage");
            MyDownloads = getArguments().getString("MyDownloads");
        }


        binding.llBack.setOnClickListener(view1 -> callBack());
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
  /*          handler1.removeCallbacks(UpdateSongTime1);
            handler2.removeCallbacks(UpdateSongTime2);*/
            Intent i = new Intent(getActivity(), MyPlaylistActivity.class);
            i.putExtra("PlaylistID", PlaylistID);
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
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

        RecyclerView.LayoutManager suggestedList = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvSuggestedList.setLayoutManager(suggestedList);
        binding.rvSuggestedList.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager playList = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvPlayLists.setLayoutManager(playList);
        binding.rvPlayLists.setItemAnimator(new DefaultItemAnimator());

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

    private List<DownloadPlaylistDetails> GetPlaylistDetail(String download, SubPlayListModel listModel) {
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
                if (downloadPlaylistDetailsList.size() != 0 || /*New.equalsIgnoreCase("1") ||*/ RefreshIcon == 0) {
                    enableDisableDownload(false);
                } else if (download.equalsIgnoreCase("1") ||/* New.equalsIgnoreCase("1") ||*/ RefreshIcon == 0) {
                    enableDisableDownload(false);
                } else if (download.equalsIgnoreCase("0") || download.equalsIgnoreCase("") ||
                        New.equalsIgnoreCase("0") || RefreshIcon != 0) {
                    enableDisableDownload(true);
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

    private void enableDisableDownload(boolean b) {
        if (b) {
            binding.llDownloads.setClickable(true);
            binding.llDownloads.setEnabled(true);
            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        } else {
            binding.llDownloads.setClickable(false);
            binding.llDownloads.setEnabled(false);
            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
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
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                callBack();
                return true;
            }
            return false;
        });
        super.onResume();
        if (deleteFrg == 1) {
            callBack();
            deleteFrg = 0;
        } else if (addToPlayList) {
            prepareData(UserID, MyPlaylistId);
            addToPlayList = false;
        } else {
            prepareData(UserID, PlaylistID);
        }
        if (comeRename == 1) {
            prepareData(UserID, PlaylistID);
        }
    }

    private void callBack() {
        binding.searchView.requestFocus();
   /*     handler1.removeCallbacks(UpdateSongTime1);
        handler2.removeCallbacks(UpdateSongTime2);*/
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
        } else if (comefrom_search == 3) {
            Intent i = new Intent(getActivity(), DownloadsActivity.class);
            ComeFrom_Playlist = true;
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            getActivity().finish();
//            comefrom_search = 0;
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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 8, 10, 260);
            binding.llSpace.setLayoutParams(params);
        }

        searchClear(searchEditText);
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        try {
            if (IsLock.equalsIgnoreCase("1") && !AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = sharedm.edit();
                editorr.remove(CONSTANTS.PREF_KEY_modelList);
                editorr.remove(CONSTANTS.PREF_KEY_position);
                editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                editorr.remove(CONSTANTS.PREF_KEY_AudioFlag);
                editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                editorr.clear();
                editorr.commit();
            }
            SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);

            AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                if (comefrom_search == 1) {
                    Fragment fragment = new TransparentPlayerFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.flContainer, fragment)
                            .commit();
                } else {
                    Fragment fragment = new TransparentPlayerFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.flContainer, fragment)
                            .commit();
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 8, 10, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 8, 10, 50);
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

                            if (listModel.getResponseData().getCreated().equalsIgnoreCase("1")) {
                                searchEditText.setHint("Add or search for audio, playlist");
                                binding.tvSearch.setHint("Add or search for audio, playlist");
//                                binding.tvSearch.setTextSize(14);
                            } else {
                                searchEditText.setHint("Search for audios");
                                binding.tvSearch.setHint("Search for audios");
                            }
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


                            playlistSongsList = listModel.getResponseData().getPlaylistSongs();
                            downloadPlaylistDetails = new DownloadPlaylistDetails();
                            downloadPlaylistDetails.setPlaylistID(listModel.getResponseData().getPlaylistID());
                            downloadPlaylistDetails.setPlaylistName(listModel.getResponseData().getPlaylistName());
                            downloadPlaylistDetails.setPlaylistDesc(listModel.getResponseData().getPlaylistDesc());
                            downloadPlaylistDetails.setIsReminder(listModel.getResponseData().getIsReminder());
                            downloadPlaylistDetails.setPlaylistMastercat(listModel.getResponseData().getPlaylistMastercat());
                            downloadPlaylistDetails.setPlaylistSubcat(listModel.getResponseData().getPlaylistSubcat());
                            downloadPlaylistDetails.setPlaylistImage(listModel.getResponseData().getPlaylistImage());
                            downloadPlaylistDetails.setTotalAudio(listModel.getResponseData().getTotalAudio());
                            downloadPlaylistDetails.setTotalDuration(listModel.getResponseData().getTotalDuration());
                            downloadPlaylistDetails.setTotalhour(listModel.getResponseData().getTotalhour());
                            downloadPlaylistDetails.setTotalminute(listModel.getResponseData().getTotalminute());
                            downloadPlaylistDetails.setCreated(listModel.getResponseData().getCreated());
                            downloadPlaylistDetails.setDownload(listModel.getResponseData().getDownload());
                            downloadPlaylistDetails.setLike(listModel.getResponseData().getLike());

                            setData(listModel.getResponseData());
                            downloadAudioDetailsList = GetAllMedia();
                            downloadPlaylistDetailsList = GetPlaylistDetail(listModel.getResponseData().getDownload(), listModel);
                            playlistWiseAudioDetails = GetMedia();
//                            getDownloadData();
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
    }

/*
    private void getDownloadData() {
     */
/*   SharedPreferences sharedx = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_removedDownloadPlaylist, MODE_PRIVATE);
        Gson gson1 = new Gson();
        String json = sharedx.getString(CONSTANTS.PREF_KEY_removedDownloadName, String.valueOf(gson1));
        String json2 = sharedx.getString(CONSTANTS.PREF_KEY_removedDownloadPlaylistId, String.valueOf(gson1));
        if (!json.equalsIgnoreCase(String.valueOf(gson1))) {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            removedFileNameList = gson1.fromJson(json, type);
            removedPlaylistDownloadId = gson1.fromJson(json2, type);
        }*//*

        try {
            SharedPreferences sharedy = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
            Gson gson = new Gson();
            String jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
            String json1 = sharedy.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
            String jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
            if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
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

    private void setData(SubPlayListModel.ResponseData listModel) {
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
        if (!listModel.getPlaylistImage().equalsIgnoreCase("")) {
            try {
                Glide.with(getActivity()).load(listModel.getPlaylistImage()).thumbnail(0.05f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivBanner);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            binding.ivBanner.setImageResource(R.drawable.audio_bg);
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
            binding.llDownloads.setVisibility(View.VISIBLE);
            binding.llReminder.setVisibility(View.VISIBLE);
            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            binding.ivPlaylistStatus.setVisibility(View.VISIBLE);
            binding.llListing.setVisibility(View.VISIBLE);
            try {
                if (MyDownloads.equalsIgnoreCase("1")) {
                    adpater2 = new PlayListsAdpater2(listModel.getPlaylistSongs(), getActivity(), UserID, listModel.getCreated());
                    binding.rvPlayLists.setAdapter(adpater2);
                    binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                    binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                    enableDisableDownload(false);
                    binding.llReminder.setClickable(false);
                    binding.llReminder.setEnabled(false);
                    binding.ivReminder.setColorFilter(activity.getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                    binding.rlSearch.setVisibility(View.GONE);
                } else {
                    if (listModel.getCreated().equalsIgnoreCase("1")) {
                        adpater = new PlayListsAdpater(listModel.getPlaylistSongs(), getActivity(), UserID, listModel.getCreated(), this);
//                        SongListSize = listModel.getPlaylistSongs().size();
                        ItemTouchHelper.Callback callback = new ItemMoveCallback(adpater);
                        touchHelper = new ItemTouchHelper(callback);
                        touchHelper.attachToRecyclerView(binding.rvPlayLists);
                        binding.rvPlayLists.setAdapter(adpater);
                        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                        boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                        int pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
                        if (audioPlay) {
                            if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
                                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                                if (pID.equalsIgnoreCase(PlaylistID)) {
                                    SharedPreferences shareddd = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = shareddd.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(listModel.getPlaylistSongs());
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                                    editor.putInt(CONSTANTS.PREF_KEY_position, pos);
                                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                    editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistID);
                                    editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "myPlaylist");
                                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
                                    editor.commit();
                                    Fragment fragment = new TransparentPlayerFragment();
                                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                                    fragmentManager1.beginTransaction()
                                            .add(R.id.flContainer, fragment)
                                            .commit();
                                }
                            }
                        }
                    } else {
                        adpater2 = new PlayListsAdpater2(listModel.getPlaylistSongs(), getActivity(), UserID, listModel.getCreated());
                        binding.rvPlayLists.setAdapter(adpater2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void callTransparentFrag(int position, Context ctx, ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList,
                                     String myPlaylist) {
        player = 1;
        if (isPrepare || isMediaStart || isPause) {
            stopMedia();
        }
        isPause = false;
        isMediaStart = false;
        isPrepare = false;
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listModelList);
        editor.putString(CONSTANTS.PREF_KEY_modelList, json);
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistID);
        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, myPlaylist);
        editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
        editor.commit();
        try {
            if (comefrom_search == 1) {
                Fragment fragment = new TransparentPlayerFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .commit();
            } else {
                Fragment fragment = new TransparentPlayerFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .commit();
            }
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
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        SucessModel listModel = response.body();
                        mData.remove(position);
                        if (mData.size() == 0) {
                            enableDisableDownload(false);
                        }
                        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                        boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                        int pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
                        if (audioPlay) {
                            if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
                                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                                if (pID.equalsIgnoreCase(PlaylistID)) {
                                    if (mData.size() != 0) {
                                        if (pos == position && position < mData.size() - 1) {
//                                            pos = pos + 1;
                                            callTransparentFrag(pos, getActivity(), mData, "myPlaylist");
                                        } else if (pos == position && position == mData.size() - 1) {
                                            pos = 0;
                                            callTransparentFrag(pos, getActivity(), mData, "myPlaylist");
                                        } else if (pos < position && pos < mData.size() - 1) {
                                            saveToPref(pos, mData);
                                        } else if (pos > position && pos == mData.size()) {
                                            pos = pos - 1;
                                            saveToPref(pos, mData);
                                        }/*else if(pos != position || pos > mData.size()){}*/

//                                        callTransparentFrag(pos, getActivity(), mData, "myPlaylist");
                                    } else {

                                    }
                                }
                            }
                        }
                        //      adpater.notifyItemRemoved(position);
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
                    Fragment fragment = new TransparentPlayerFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.flContainer, fragment)
                            .commit();
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
            enableDisableDownload(false);
            List<String> url = new ArrayList<>();
            List<String> name = new ArrayList<>();
            List<String> downloadPlaylistId = new ArrayList<>();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs2 = new ArrayList<>();
            playlistSongs2 = playlistSongs;
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
                downloadMedia.encrypt1(url, name/*, playlistSongs*/);
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();
                String urlJson = gson.toJson(url);
                String nameJson = gson.toJson(name);
                String playlistIdJson = gson.toJson(downloadPlaylistId);
               /* fileNameList = name;
                playlistDownloadId = downloadPlaylistId;*/
                editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                editor.commit();
              /*  remainAudio = new ArrayList<>();
                for (int i = 0; i < fileNameList.size(); i++) {
                    if (playlistDownloadId.get(i).equalsIgnoreCase(PlaylistID)) {
                        remainAudio.add(playlistDownloadId.get(i));
                    }
                }
                SongListSize = playlistSongs.size();
                handler1.postDelayed(UpdateSongTime1, 500);*/
            }
//            String dirPath = FileUtils.getFilePath(getActivity().getApplicationContext(), Name);
//            SaveMedia(EncodeBytes, dirPath, playlistSongs, i, llDownload);
            savePlaylist();
            saveAllMedia(playlistSongsList, encodedBytes);
        } else {
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
                downloadMedia.encrypt1(url, name/*, playlistSongs*/);
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
               /* fileNameList = name;
                playlistDownloadId = downloadPlaylistId;*/
            }
            String dirPath = FileUtils.getFilePath(getActivity().getApplicationContext(), Name);
            SaveMedia(new byte[1024], dirPath, playlistSongs, position, llDownload, ivDownloads);
//            handler2.postDelayed(UpdateSongTime2, 500);
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
                    downloadAudioDetails.setEncodedBytes(encodedBytes);
                    downloadAudioDetails.setDirPath(FileUtils.getFilePath(getActivity().getApplicationContext(), playlistSongs.get(i).getName()));
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

                enableDisableDownload(false);
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
                downloadAudioDetails.setEncodedBytes(encodeBytes);
                downloadAudioDetails.setDirPath(dirPath);

                DatabaseClient.getInstance(activity)
                        .getaudioDatabase()
                        .taskDao()
                        .insertMedia(downloadAudioDetails);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
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
                        listModel.setPlaylistImage(downloadPlaylistDetailsList.get(0).getPlaylistImage());
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
            MyPlaylistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.my_playlist_layout, parent, false);
            return new MyViewHolder(v);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final ArrayList<SubPlayListModel.ResponseData.PlaylistSong> mData = listFilterData;
         /*   UpdateSongTime2 = new Runnable() {
                @Override
                public void run() {
                    for (int f = 0; f < listModelList.size(); f++) {
                        if (fileNameList.size() != 0) {
                            for (int i = 0; i < fileNameList.size(); i++) {
                                if (fileNameList.get(i).equalsIgnoreCase(mData.get(f).getName())) {
                                    if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(mData.get(f).getName())) {
                                        if (downloadProgress <= 100) {
                                            holder.binding.pbProgress.setProgress(downloadProgress);
                                            holder.binding.pbProgress.setVisibility(View.VISIBLE);
                                        } else {
                                            holder.binding.pbProgress.setVisibility(View.GONE);
                                            handler2.removeCallbacks(UpdateSongTime2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    handler2.postDelayed(this, 500);
                }
            };
            if (fileNameList.size() != 0) {
//                for (int i = 0; i < fileNameList.size(); i++) {
                    if (fileNameList.contains(mData.get(position).getName())) {
                        holder.binding.pbProgress.setVisibility(View.VISIBLE);
                        handler2.postDelayed(UpdateSongTime2, 500);
                    }
//                }
                *//*else {
                    holder.binding.pbProgress.setVisibility(View.GONE);
                }*//*
            }*/
            holder.binding.tvTitleA.setText(mData.get(position).getName());
            holder.binding.tvTitleB.setText(mData.get(position).getName());
            holder.binding.tvTimeA.setText(mData.get(position).getAudioDuration());
            holder.binding.tvTimeB.setText(mData.get(position).getAudioDuration());
            holder.binding.llSort.setOnTouchListener((v, event) -> {
                if (event.getAction() ==
                        MotionEvent.ACTION_DOWN) {
                    startDragListener.requestDrag(holder);
                }
                if (event.getAction() ==
                        MotionEvent.ACTION_UP) {
                    startDragListener.requestDrag(holder);
                }
                return false;
            });
            String id = mData.get(position).getID();
//            GetMedia(id, activity, mData.get(position).getDownload(), holder.binding.llDownload, holder.binding.ivDownloads);
            for (int i = 0; i < downloadAudioDetailsList.size(); i++) {
                if (downloadAudioDetailsList.get(i).getAudioFile().equalsIgnoreCase(mData.get(position).getAudioFile())
                        && downloadAudioDetailsList.get(i).getPlaylistId().equalsIgnoreCase("")) {
                    //disableName.add(mData.get(position).getName());
                    disableDownload(holder.binding.llDownload, holder.binding.ivDownloads);
                    break;
                }/* else {
                    enableDownload(holder.binding.llDownload, holder.binding.ivDownloads);
                }*/
            }
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(mData.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            binding.ivPlaylistStatus.setOnClickListener(view ->
                    callTransparentFrag(0, ctx, listModelList, "myPlaylist"));
            holder.binding.llMainLayout.setOnClickListener(view ->
                    callTransparentFrag(position, ctx, listModelList, "myPlaylist"));

            if (Created.equalsIgnoreCase("1")) {
                holder.binding.llMore.setVisibility(View.GONE);
                holder.binding.llCenterLayoutA.setVisibility(View.GONE);
                holder.binding.llCenterLayoutB.setVisibility(View.VISIBLE);
                holder.binding.llDownload.setVisibility(View.VISIBLE);
                holder.binding.llRemove.setVisibility(View.VISIBLE);
                holder.binding.llSort.setVisibility(View.VISIBLE);
                binding.tvSearch.setVisibility(View.VISIBLE);
                binding.searchView.setVisibility(View.GONE);
            } else if (Created.equalsIgnoreCase("0")) {
                holder.binding.llMore.setVisibility(View.VISIBLE);
                holder.binding.llCenterLayoutA.setVisibility(View.VISIBLE);
                holder.binding.llCenterLayoutB.setVisibility(View.GONE);
                holder.binding.llDownload.setVisibility(View.GONE);
                holder.binding.llRemove.setVisibility(View.GONE);
                holder.binding.llSort.setVisibility(View.GONE);
                binding.tvSearch.setVisibility(View.GONE);
                binding.searchView.setVisibility(View.VISIBLE);
            }
            if (BWSApplication.isNetworkConnected(ctx)) {
                holder.binding.llMore.setClickable(true);
                holder.binding.llMore.setEnabled(true);
                holder.binding.ivMore.setColorFilter(activity.getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);

            } else {
                holder.binding.llMore.setClickable(false);
                holder.binding.llMore.setEnabled(false);
                holder.binding.ivMore.setColorFilter(activity.getResources().getColor(R.color.light_gray), PorterDuff.Mode.SRC_IN);
            }
            holder.binding.llMore.setOnClickListener(view -> {
                Intent i = new Intent(ctx, AddQueueActivity.class);
                i.putExtra("play", "myPlayList");
                i.putExtra("ID", mData.get(position).getID());
                i.putExtra("PlaylistAudioId", mData.get(position).getPlaylistAudioId());
                i.putExtra("position", position);
                i.putParcelableArrayListExtra("data", mData);
                i.putExtra("comeFrom", "myPlayList");
                startActivity(i);
                getActivity().finish();
            });

//            if (changedAudio != null) {
//                callDragApi();
//            }
            holder.binding.llDownload.setOnClickListener(view -> {
                name = mData.get(position).getName();
//                holder.binding.pbProgress.setVisibility(View.VISIBLE);
                callDownload(mData.get(position).getID(), mData.get(position).getAudioFile(), mData.get(position).getName(), listFilterData, position, holder.binding.llDownload, holder.binding.ivDownloads);
            });
            try {
                holder.binding.llRemove.setOnClickListener(view -> callRemove(mData.get(position).getID(), mData.get(position).getPlaylistAudioId(), mData, position));

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
                        }/* else if (toPosition == pos) {
                            if (action == 0) {
                                pos = pos + 1;
                            } else if (action == 1) {
                                pos = pos - 1;
                            }
                        }*/ else if (fromPosition < pos && toPosition > pos) {
                            pos = pos - 1;
                        } else if ((fromPosition > pos && toPosition > pos) || (fromPosition < pos && toPosition < pos)) {
                            pos = pos;
                        } else if (fromPosition > pos && toPosition < pos) {
                            pos = pos + 1;
                        } else if (fromPosition > pos && toPosition == pos) {
                            pos = pos + 1;
                        } else if (fromPosition < pos && toPosition == pos) {
                            pos = pos - 1;
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
                        Fragment fragment = new TransparentPlayerFragment();
                        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                        fragmentManager1.beginTransaction()
                                .add(R.id.flContainer, fragment)
                                .commit();
                    }
                }
            }
            callDragApi();

            notifyItemMoved(fromPosition, toPosition);

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
                        binding.rvPlayLists.setVisibility(View.GONE);
                    } else {
                        binding.llError.setVisibility(View.GONE);
                        binding.rvPlayLists.setVisibility(View.VISIBLE);
                        listFilterData = (ArrayList<SubPlayListModel.ResponseData.PlaylistSong>) filterResults.values;
                        notifyDataSetChanged();
                    }
                }
            };
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            MyPlaylistLayoutBinding binding;

            public MyViewHolder(MyPlaylistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class PlayListsAdpater2 extends RecyclerView.Adapter<PlayListsAdpater2.MyViewHolder2> implements Filterable {
        Context ctx;
        String UserID, Created;
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
            final ArrayList<SubPlayListModel.ResponseData.PlaylistSong> mData = listFilterData;
            holder.binding.tvTitleA.setText(mData.get(position).getName());
            holder.binding.tvTitleB.setText(mData.get(position).getName());
            holder.binding.tvTimeA.setText(mData.get(position).getAudioDuration());
            holder.binding.tvTimeB.setText(mData.get(position).getAudioDuration());
            String id = mData.get(position).getID();
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(mData.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
//            GetMedia(id, activity, mData.get(position).getDownload(), holder.binding.llDownload, holder.binding.ivDownloads);
            binding.ivPlaylistStatus.setOnClickListener(view -> callTransparentFrag(0, ctx, listModelList, ""));
            holder.binding.llMainLayout.setOnClickListener(view -> callTransparentFrag(position, ctx, listModelList, ""));

//            if (Created.equalsIgnoreCase("1")) {
//                holder.binding.llMore.setVisibility(View.GONE);
//                holder.binding.llCenterLayoutA.setVisibility(View.GONE);
//                holder.binding.llCenterLayoutB.setVisibility(View.VISIBLE);
//                holder.binding.llDownload.setVisibility(View.VISIBLE);
//                holder.binding.llRemove.setVisibility(View.VISIBLE);
//                holder.binding.llSort.setVisibility(View.VISIBLE);
//                binding.tvSearch.setVisibility(View.VISIBLE);
//                binding.searchView.setVisibility(View.GONE);
//            } else if (Created.equalsIgnoreCase("0")) {
            holder.binding.llMore.setVisibility(View.VISIBLE);
            holder.binding.llCenterLayoutA.setVisibility(View.VISIBLE);
            holder.binding.llCenterLayoutB.setVisibility(View.GONE);
            holder.binding.llDownload.setVisibility(View.GONE);
            holder.binding.llRemove.setVisibility(View.GONE);
            holder.binding.llSort.setVisibility(View.GONE);
            binding.tvSearch.setVisibility(View.GONE);
            binding.searchView.setVisibility(View.VISIBLE);
//            }
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
                Intent i = new Intent(ctx, AddQueueActivity.class);
                i.putExtra("play", "playlist");
                i.putExtra("ID", mData.get(position).getID());
                i.putExtra("PlaylistAudioId", mData.get(position).getPlaylistAudioId());
                i.putExtra("position", position);
                i.putParcelableArrayListExtra("data", mData);
                i.putExtra("comeFrom", "myPlayList");
                startActivity(i);
            });
        }

        @Override
        public int getItemCount() {
            if (listFilterData != null) {
                return listFilterData.size();
            }
            return 0;
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
                        binding.tvFound.setText("Search term not found please use another one");
                        binding.rvPlayLists.setVisibility(View.GONE);
                    } else {
                        binding.llError.setVisibility(View.GONE);
                        binding.rvPlayLists.setVisibility(View.VISIBLE);
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
}