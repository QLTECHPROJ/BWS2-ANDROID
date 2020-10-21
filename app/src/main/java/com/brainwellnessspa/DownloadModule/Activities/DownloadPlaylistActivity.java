package com.brainwellnessspa.DownloadModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Activities.AddQueueActivity;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityDownloadPlaylistBinding;
import com.brainwellnessspa.databinding.DownloadPlaylistLayoutBinding;
import com.brainwellnessspa.databinding.MyPlaylistLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.player;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.DownloadModule.Adapters.AudioDownlaodsAdapter.comefromDownload;
import static com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment.disclaimerPlayed;
import static com.brainwellnessspa.Utility.MusicService.isCompleteStop;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.Utility.MusicService.isPrepare;
import static com.brainwellnessspa.Utility.MusicService.releasePlayer;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;

public class DownloadPlaylistActivity extends AppCompatActivity {
    ActivityDownloadPlaylistBinding binding;
    PlayListsAdpater adpater;
    String UserID, SearchFlag, AudioFlag, PlaylistID, PlaylistName, PlaylistImage, TotalAudio, Totalhour, Totalminute;
    EditText searchEditText;
    Context ctx;
    List<DownloadAudioDetails> playlistWiseAudioDetails = new ArrayList<>();
    DownloadAudioDetails addDisclaimer = new DownloadAudioDetails();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_download_playlist);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        ctx = DownloadPlaylistActivity.this;
        addDisclaimer();
        if (getIntent() != null) {
            PlaylistID = getIntent().getStringExtra("PlaylistID");
            PlaylistName = getIntent().getStringExtra("PlaylistName");
            PlaylistImage = getIntent().getStringExtra("PlaylistImage");
            TotalAudio = getIntent().getStringExtra("TotalAudio");
            Totalhour = getIntent().getStringExtra("Totalhour");
            Totalminute = getIntent().getStringExtra("Totalminute");
        }

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        PrepareData();
    }

    public void PrepareData() {

        try {
            if (!IsLock.equalsIgnoreCase("0") && (AudioFlag.equalsIgnoreCase("MainAudioList")
                    || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
                String audioFile = "";
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                Gson gson = new Gson();
                String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
                Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                }.getType();
                ArrayList<MainPlayModel> arrayList = gson.fromJson(json, type);

                if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                    arrayList.remove(0);
                }
                audioFile = arrayList.get(0).getName();

                if (audioFile.equalsIgnoreCase("Hope") || audioFile.equalsIgnoreCase("Mindfulness")) {

                } else {
                    SharedPreferences sharedm =  getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
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
                    if(isMediaStart){
                        stopMedia();
                        releasePlayer();
                    }
                }

            } else if (!IsLock.equalsIgnoreCase("0") && !AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                SharedPreferences sharedm = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
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
                if(isMediaStart){
                    stopMedia();
                    releasePlayer();
                }
            }
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

            if (!AudioFlag.equalsIgnoreCase("0")) {
            comefromDownload = "1";
            Fragment fragment = new TransparentPlayerFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flContainer, fragment)
                    .commit();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
        binding.ivPlaylistStatus.setVisibility(View.VISIBLE);
        binding.tvLibraryName.setText(PlaylistName);
        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                5, 3, 1f, 0);
        binding.ivBanner.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivBanner.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivBanner.setScaleType(ImageView.ScaleType.FIT_XY);
        if (!PlaylistImage.equalsIgnoreCase("")) {
            try {
                Glide.with(ctx).load(PlaylistImage).thumbnail(0.05f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivBanner);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            binding.ivBanner.setImageResource(R.drawable.audio_bg);
        }
        binding.searchView.onActionViewExpanded();
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();
        searchEditText.setHint("Search for audios");
        closeButton.setOnClickListener(v -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);

        });
        if (TotalAudio.equalsIgnoreCase("") ||
                TotalAudio.equalsIgnoreCase("0") &&
                        Totalhour.equalsIgnoreCase("")
                        && Totalminute.equalsIgnoreCase("")) {
            binding.tvLibraryDetail.setText("0 Audio | 0h 0m");
        } else {
            if (Totalminute.equalsIgnoreCase("")) {
                binding.tvLibraryDetail.setText(TotalAudio + " Audio | "
                        + Totalhour + "h 0m");
            } else {
                binding.tvLibraryDetail.setText(TotalAudio + " Audio | "
                        + Totalhour + "h " + Totalminute + "m");
            }
        }
        RecyclerView.LayoutManager playList = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvPlayLists.setLayoutManager(playList);
        binding.rvPlayLists.setItemAnimator(new DefaultItemAnimator());
        getMedia(PlaylistID);
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                binding.searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                try {
                    if (adpater != null) {
                        adpater.getFilter().filter(search);
                        SearchFlag = search;
                        Log.e("searchsearch", "" + search);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        binding.tvTag.setVisibility(View.VISIBLE);
        binding.tvTag.setText("Audios in Playlist");
        binding.tvPlaylist.setText("Playlist");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void getMedia(String playlistID) {
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                playlistWiseAudioDetails = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getAllAudioByPlaylist(playlistID);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                adpater = new PlayListsAdpater(playlistWiseAudioDetails , ctx, UserID);
              binding.rvPlayLists.setAdapter(adpater);

                super.onPostExecute(aVoid);
            }
        }
        GetMedia st = new GetMedia();
        st.execute();
    }

    public class PlayListsAdpater extends RecyclerView.Adapter<PlayListsAdpater.MyViewHolders> implements Filterable {
        Context ctx;
        String UserID;
        private List<DownloadAudioDetails> listModelList;
        private List<DownloadAudioDetails> listFilterData;

        public PlayListsAdpater(List<DownloadAudioDetails> listModelList, Context ctx, String UserID) {
            this.listModelList = listModelList;
            this.listFilterData = listModelList;
            this.ctx = ctx;
            this.UserID = UserID;
        }

        @NonNull
        @Override
        public MyViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            DownloadPlaylistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.download_playlist_layout, parent, false);
            return new MyViewHolders(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolders holder, int position) {
            final  List<DownloadAudioDetails> mData = listFilterData;
            holder.binding.tvTitleA.setText(mData.get(position).getName());
//            holder.binding.tvTitleB.setText(mData.get(position).getName());
            holder.binding.tvTimeA.setText(mData.get(position).getAudioDuration());
//            holder.binding.tvTimeB.setText(mData.get(position).getAudioDuration());
            String id = mData.get(position).getID();
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(mData.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
//            GetMedia(id, activity, mData.get(position).getDownload(), holder.binding.llDownload, holder.binding.ivDownloads);
            binding.ivPlaylistStatus.setOnClickListener(view -> {
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                    } else {
                        callTransparentFrag(0, ctx, listModelList, "", PlaylistID);
                    }
                } else {
                    isDisclaimer = 0;
                    disclaimerPlayed = 0;
                    List<DownloadAudioDetails> listModelList2 = new ArrayList<>();
                    listModelList2.addAll(listModelList);
                    callTransparentFrag(0, ctx, listModelList2, "", PlaylistID);
                }
            });
            holder.binding.llMainLayout.setOnClickListener(view -> {
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                    } else {
                        callTransparentFrag(position, ctx, listModelList, "", PlaylistID);
                    }
                } else {
                    isDisclaimer = 0;
                    disclaimerPlayed = 0;
                     List<DownloadAudioDetails> listModelList2 = new ArrayList<>();
                    if (position != 0) {
                        listModelList2.addAll(listModelList);
                        listModelList2.add(position, addDisclaimer);
                    } else {
                        listModelList2.add(addDisclaimer);
                        listModelList2.addAll(listModelList);
                    }
                    callTransparentFrag(position, ctx, listModelList2, "", PlaylistID);
                }
            });

          /*  if (Created.equalsIgnoreCase("1")) {
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
            }*/

            if (BWSApplication.isNetworkConnected(ctx)) {
                holder.binding.llMore.setClickable(true);
                holder.binding.llMore.setEnabled(true);
                holder.binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

            } else {
                holder.binding.llMore.setClickable(false);
                holder.binding.llMore.setEnabled(false);
                holder.binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            holder.binding.llMore.setOnClickListener(view -> {
                SharedPreferences shared =  getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
              /*  if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
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
                }*/
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
                        List<DownloadAudioDetails> filteredList = new ArrayList<>();
                        for (DownloadAudioDetails row : listModelList) {
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
//                        binding.llError.setVisibility(View.VISIBLE);
//                        binding.rvPlayLists.setVisibility(View.GONE);
//                        binding.tvFound.setText("Couldn't find '" + SearchFlag + "'. Try searching again");
//                        Log.e("search", SearchFlag);
                    } else {
//                        binding.llError.setVisibility(View.GONE);
//                        binding.rvPlayLists.setVisibility(View.VISIBLE);
//                        listFilterData = (ArrayList<SubPlayListModel.ResponseData.PlaylistSong>) filterResults.values;
//                        notifyDataSetChanged();
                    }
                }
            };
        }

        public class MyViewHolders extends RecyclerView.ViewHolder {
            DownloadPlaylistLayoutBinding binding;

            public MyViewHolders(DownloadPlaylistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
    private void addDisclaimer() {
        addDisclaimer = new DownloadAudioDetails();
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

    private void callTransparentFrag(int i, Context ctx, List<DownloadAudioDetails> listModelList, String s, String playlistID) {
        player = 1;
        if (isPrepare || isMediaStart || isPause) {
            stopMedia();
        }
        isPause = false;
        isMediaStart = false;
        isPrepare = false;

        isCompleteStop = false;

        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        ArrayList<DownloadAudioDetails> listModelList2 = new ArrayList<>();
        DownloadAudioDetails mainPlayModel = new DownloadAudioDetails();
        mainPlayModel.setID("0");
        mainPlayModel.setName("Disclaimer");
        mainPlayModel.setAudioFile("");
        mainPlayModel.setAudioDirection("The audio shall start playing after the disclaimer");
        mainPlayModel.setAudiomastercat("");
        mainPlayModel.setAudioSubCategory("");
        mainPlayModel.setImageFile("");
        mainPlayModel.setLike("");
        mainPlayModel.setDownload("");
        mainPlayModel.setAudioDuration("0:48");
        listModelList2.add(mainPlayModel);
        listModelList2.addAll(playlistWiseAudioDetails);

        String json = gson.toJson(listModelList2);
        editor.putString(CONSTANTS.PREF_KEY_modelList, json);
        editor.putInt(CONSTANTS.PREF_KEY_position, 0);
        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
        editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "Downloadlist");
        editor.commit();
        try {
            Fragment fragment = new TransparentPlayerFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flContainer, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}