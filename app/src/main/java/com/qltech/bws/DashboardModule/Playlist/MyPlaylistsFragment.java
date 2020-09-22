package com.qltech.bws.DashboardModule.Playlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.BillingOrderModule.Models.CardModel;
import com.qltech.bws.DashboardModule.Activities.AddAudioActivity;
import com.qltech.bws.DashboardModule.Activities.AddQueueActivity;
import com.qltech.bws.DashboardModule.Activities.MyPlaylistActivity;
import com.qltech.bws.DashboardModule.Models.DownloadPlaylistModel;
import com.qltech.bws.DashboardModule.Models.SubPlayListModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.Search.SearchFragment;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.R;
import com.qltech.bws.ReminderModule.Activities.ReminderActivity;
import com.qltech.bws.ReminderModule.Activities.ReminderDetailsActivity;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.ItemMoveCallback;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentMyPlaylistsBinding;
import com.qltech.bws.databinding.MyPlaylistLayoutBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;
import static com.qltech.bws.DashboardModule.Activities.MyPlaylistActivity.ComeFindAudio;
import static com.qltech.bws.DashboardModule.Activities.MyPlaylistActivity.deleteFrg;
import static com.qltech.bws.DashboardModule.Search.SearchFragment.comefrom_search;
import static com.qltech.bws.Utility.MusicService.isMediaStart;
import static com.qltech.bws.Utility.MusicService.isPause;
import static com.qltech.bws.Utility.MusicService.isPrepare;
import static com.qltech.bws.Utility.MusicService.stopMedia;

public class MyPlaylistsFragment extends Fragment {
    FragmentMyPlaylistsBinding binding;
    String UserID, New, PlaylistID, PlaylistName = "", PlaylistImage;
    PlayListsAdpater adpater;
    PlayListsAdpater2 adpater2;
    String SearchFlag;
    View view;
    EditText searchEditText;
    ArrayList<String> changedAudio;
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_playlists, container, false);
        view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        activity = getActivity();

        changedAudio = new ArrayList<>();
        if (getArguments() != null) {
            New = getArguments().getString("New");
            PlaylistID = getArguments().getString("PlaylistID");
            PlaylistName = getArguments().getString("PlaylistName");
            PlaylistImage = getArguments().getString("PlaylistImage");
        }

        binding.llBack.setOnClickListener(view1 -> callBack());

        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);

        binding.llMore.setOnClickListener(view13 -> {
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
        searchEditText.setHint("Search for audio");

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
            callDownload("");
        });

        if (New.equalsIgnoreCase("1")) {
            binding.llAddAudio.setVisibility(View.VISIBLE);
            binding.llDownloads.setVisibility(View.INVISIBLE);
            binding.llReminder.setVisibility(View.INVISIBLE);
            binding.ivPlaylistStatus.setVisibility(View.INVISIBLE);
            binding.llListing.setVisibility(View.GONE);
            binding.btnAddAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), AddAudioActivity.class);
                    i.putExtra("PlaylistID", PlaylistID);
                    startActivity(i);
                }
            });
        } else if (New.equalsIgnoreCase("0")) {
            binding.llAddAudio.setVisibility(View.GONE);
            binding.llDownloads.setVisibility(View.VISIBLE);
            binding.llReminder.setVisibility(View.VISIBLE);
            binding.ivPlaylistStatus.setVisibility(View.VISIBLE);
            binding.llListing.setVisibility(View.VISIBLE);
            prepareData(UserID, PlaylistID);
        }
        return view;
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
        } else {
            prepareData(UserID, PlaylistID);
        }
    }

    private void callBack() {
        if (comefrom_search == 1) {
            Fragment fragment = new SearchFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.rlSearchList, fragment)
                    .commit();
            comefrom_search = 0;
        } else {
            Fragment playlistFragment = new PlaylistFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.rlPlaylist, playlistFragment)
                    .commit();
        }
    }

    private void searchClear(EditText searchEditText) {
        if (ComeFindAudio == 1) {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);
            ComeFindAudio = 0;
        }
    }

    private void prepareData(String UserID, String PlaylistID) {
        searchClear(searchEditText);
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        try {
            String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                if (comefrom_search == 1) {
                    Fragment fragment = new TransparentPlayerFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.rlSearchList, fragment)
                            .commit();
                } else {
                    Fragment fragment = new TransparentPlayerFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.rlPlaylist, fragment)
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
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, getActivity());
            Call<SubPlayListModel> listCall = APIClient.getClient().getSubPlayLists(UserID, PlaylistID);
            listCall.enqueue(new Callback<SubPlayListModel>() {
                @Override
                public void onResponse(Call<SubPlayListModel> call, Response<SubPlayListModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, getActivity());
                        SubPlayListModel listModel = response.body();

                        binding.llReminder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(getActivity(), ReminderActivity.class);
                                i.putExtra("ComeFrom","1");
                                i.putExtra("PlaylistID", PlaylistID);
                                i.putExtra("PlaylistName", listModel.getResponseData().getPlaylistName());
                                startActivity(i);
                            }
                        });

                        if (listModel.getResponseData().getPlaylistName().equalsIgnoreCase("") ||
                                listModel.getResponseData().getPlaylistName() == null) {
                            binding.tvLibraryName.setText(R.string.My_Playlist);
                        } else {
                            binding.tvLibraryName.setText(listModel.getResponseData().getPlaylistName());
                        }
                        MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                                4, 2, 1.2f, 0);
                        binding.ivBanner.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                        binding.ivBanner.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                        binding.ivBanner.setScaleType(ImageView.ScaleType.FIT_XY);
                        if (!listModel.getResponseData().getPlaylistImage().equalsIgnoreCase("")) {
                            try {
                                Glide.with(getActivity()).load(listModel.getResponseData().getPlaylistImage()).thumbnail(0.05f)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivBanner);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            binding.ivBanner.setImageResource(R.drawable.audio_bg);
                        }

                        if (listModel.getResponseData().getDownload().equalsIgnoreCase("0")
                                || listModel.getResponseData().getDownload().equalsIgnoreCase("")) {
                            binding.llDownloads.setClickable(true);
                            binding.llDownloads.setEnabled(true);
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                            binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                        } else if (listModel.getResponseData().getDownload().equalsIgnoreCase("1")) {
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                            binding.ivDownloads.setColorFilter(Color.argb(99, 99, 99, 99));
                            binding.ivDownloads.setAlpha(255);
                            binding.llDownloads.setClickable(false);
                            binding.llDownloads.setEnabled(false);
                            binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                        }

                        if (listModel.getResponseData().getTotalAudio().equalsIgnoreCase("") ||
                                listModel.getResponseData().getTotalAudio().equalsIgnoreCase("0") &&
                                        listModel.getResponseData().getTotalhour().equalsIgnoreCase("")
                                        && listModel.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                            binding.tvLibraryDetail.setText("0 Audio | 0h 0m");
                        } else {
                            if (listModel.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                binding.tvLibraryDetail.setText(listModel.getResponseData().getTotalAudio() + " Audio | "
                                        + listModel.getResponseData().getTotalhour() + "h 0m");
                            } else {
                                binding.tvLibraryDetail.setText(listModel.getResponseData().getTotalAudio() + " Audio | "
                                        + listModel.getResponseData().getTotalhour() + "h " + listModel.getResponseData().getTotalminute() + "m");
                            }
                        }

                        if (listModel.getResponseData().getPlaylistSongs().size() == 0) {
                            binding.llAddAudio.setVisibility(View.VISIBLE);
                            binding.llDownloads.setVisibility(View.INVISIBLE);
                            binding.llReminder.setVisibility(View.INVISIBLE);
                            binding.ivPlaylistStatus.setVisibility(View.INVISIBLE);
                            binding.llListing.setVisibility(View.GONE);
                            binding.btnAddAudio.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(getActivity(), AddAudioActivity.class);
                                    i.putExtra("PlaylistID", PlaylistID);
                                    startActivity(i);
                                }
                            });
                        } else {
                            binding.llAddAudio.setVisibility(View.GONE);
                            binding.llDownloads.setVisibility(View.VISIBLE);
                            binding.llReminder.setVisibility(View.VISIBLE);
                            binding.ivPlaylistStatus.setVisibility(View.VISIBLE);
                            binding.llListing.setVisibility(View.VISIBLE);
                            if (listModel.getResponseData().getCreated().equalsIgnoreCase("1")) {
                                adpater = new PlayListsAdpater(listModel.getResponseData().getPlaylistSongs(), getActivity(), UserID, listModel.getResponseData().getCreated());
                                ItemTouchHelper.Callback callback = new ItemMoveCallback(adpater);
                                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                                touchHelper.attachToRecyclerView(binding.rvPlayLists);
                                binding.rvPlayLists.setAdapter(adpater);
                            } else {
                                adpater2 = new PlayListsAdpater2(listModel.getResponseData().getPlaylistSongs(), getActivity(), UserID, listModel.getResponseData().getCreated());
                                binding.rvPlayLists.setAdapter(adpater2);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<SubPlayListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
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
                        .add(R.id.rlSearchList, fragment)
                        .commit();
            } else {
                Fragment fragment = new TransparentPlayerFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.rlPlaylist, fragment)
                        .commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callRemove(String id) {
        String AudioId = id;
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, getActivity());
            Call<SucessModel> listCall = APIClient.getClient().getRemoveAudioFromPlaylist(UserID, AudioId, PlaylistID);
            listCall.enqueue(new Callback<SucessModel>() {
                @Override
                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, getActivity());
                        SucessModel listModel = response.body();
                        prepareData(UserID, PlaylistID);
                        BWSApplication.showToast(listModel.getResponseMessage(), getActivity());
                    }
                }

                @Override
                public void onFailure(Call<SucessModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    private void callDownload(String id) {
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, getActivity());
            String AudioId = id;
            Call<DownloadPlaylistModel> listCall = APIClient.getClient().getDownloadlistPlaylist(UserID, AudioId, PlaylistID);
            listCall.enqueue(new Callback<DownloadPlaylistModel>() {
                @Override
                public void onResponse(Call<DownloadPlaylistModel> call, Response<DownloadPlaylistModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, getActivity());
                        DownloadPlaylistModel model = response.body();
                        if (model.getResponseData().getFlag().equalsIgnoreCase("0")
                                || model.getResponseData().getFlag().equalsIgnoreCase("")) {
                            binding.llDownloads.setClickable(true);
                            binding.llDownloads.setEnabled(true);
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                        } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                            binding.ivDownloads.setColorFilter(Color.argb(99, 99, 99, 99));
                            binding.ivDownloads.setAlpha(255);
                            binding.llDownloads.setClickable(false);
                            binding.llDownloads.setEnabled(false);
                        }
                        BWSApplication.showToast(model.getResponseMessage(), getActivity());
                    }
                }

                @Override
                public void onFailure(Call<DownloadPlaylistModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, getActivity());
                }
            });

        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    public class PlayListsAdpater extends RecyclerView.Adapter<PlayListsAdpater.MyViewHolder> implements Filterable, ItemMoveCallback.ItemTouchHelperContract {
        Context ctx;
        String UserID, Created;
        private ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList;
        private ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listFilterData;

        public PlayListsAdpater(ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList, Context ctx, String UserID,
                                String Created) {
            this.listModelList = listModelList;
            this.listFilterData = listModelList;
            this.ctx = ctx;
            this.UserID = UserID;
            this.Created = Created;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MyPlaylistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.my_playlist_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final List<SubPlayListModel.ResponseData.PlaylistSong> mData = listFilterData;
            holder.binding.tvTitleA.setText(mData.get(position).getName());
            holder.binding.tvTitleB.setText(mData.get(position).getName());
            holder.binding.tvTimeA.setText(mData.get(position).getAudioDuration());
            holder.binding.tvTimeB.setText(mData.get(position).getAudioDuration());

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

            if (mData.get(position).getDownload().equalsIgnoreCase("1")) {
                holder.binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                holder.binding.ivDownloads.setColorFilter(Color.argb(99, 99, 99, 99));
                holder.binding.ivDownloads.setAlpha(255);
                holder.binding.llDownload.setClickable(false);
                holder.binding.llDownload.setEnabled(false);
            } else if (!mData.get(position).getDownload().equalsIgnoreCase("")) {
                holder.binding.llDownload.setClickable(true);
                holder.binding.llDownload.setEnabled(true);
                holder.binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            }

            holder.binding.llMore.setOnClickListener(view -> {
                Intent i = new Intent(ctx, AddQueueActivity.class);
                i.putExtra("play", "");
                i.putExtra("ID", mData.get(position).getID());
                i.putExtra("comeFrom", "myPlayList");
                startActivity(i);
            });

            holder.binding.llDownload.setOnClickListener(view -> callDownload(mData.get(position).getID()));

            holder.binding.llRemove.setOnClickListener(view -> callRemove(mData.get(position).getID()));
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

            notifyItemMoved(fromPosition, toPosition);
            changedAudio.clear();
            for (int i = 0; i < listModelList.size(); i++) {
                changedAudio.add(listModelList.get(i).getID());
            }
            callDragApi();
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

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(mData.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            binding.ivPlaylistStatus.setOnClickListener(view -> callTransparentFrag(0, ctx, listModelList, ""));
            holder.binding.llMainLayout.setOnClickListener(view -> callTransparentFrag(position, ctx, listModelList, ""));

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

            if (mData.get(position).getDownload().equalsIgnoreCase("1")) {
                holder.binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                holder.binding.ivDownloads.setColorFilter(Color.argb(99, 99, 99, 99));
                holder.binding.ivDownloads.setAlpha(255);
                holder.binding.llDownload.setClickable(false);
                holder.binding.llDownload.setEnabled(false);
            } else if (!mData.get(position).getDownload().equalsIgnoreCase("")) {
                holder.binding.llDownload.setClickable(true);
                holder.binding.llDownload.setEnabled(true);
                holder.binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            }

            holder.binding.llMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ctx, AddQueueActivity.class);
                    i.putExtra("play", "");
                    i.putExtra("ID", mData.get(position).getID());
                    i.putExtra("position", position);
                    i.putParcelableArrayListExtra("data", mData);
                    i.putExtra("comeFrom", "myPlayList");
                    startActivity(i);
                }
            });

            holder.binding.llDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callDownload(mData.get(position).getID());

                }
            });

            holder.binding.llRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callRemove(mData.get(position).getID());
                }
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