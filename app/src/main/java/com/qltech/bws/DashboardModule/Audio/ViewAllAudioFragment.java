package com.qltech.bws.DashboardModule.Audio;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.ViewAllAudioListModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.qltech.bws.R;
import com.qltech.bws.RoomDataBase.DatabaseClient;
import com.qltech.bws.RoomDataBase.DownloadAudioDetails;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.AudiolistCustomLayoutBinding;
import com.qltech.bws.databinding.FragmentViewAllAudioBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;
import static com.qltech.bws.DashboardModule.Audio.AudioFragment.IsLock;
import static com.qltech.bws.Utility.MusicService.isMediaStart;
import static com.qltech.bws.Utility.MusicService.isPause;
import static com.qltech.bws.Utility.MusicService.isPrepare;
import static com.qltech.bws.Utility.MusicService.stopMedia;

public class ViewAllAudioFragment extends Fragment {
    public static boolean viewallAudio = false;
    public static int ComeFromAudioViewAll = 0;
    FragmentViewAllAudioBinding binding;
    String ID, Name, UserID, AudioFlag, Category;
    List<DownloadAudioDetails> audioList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_all_audio, container, false);
        View view = binding.getRoot();

        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (getArguments() != null) {
            ID = getArguments().getString("ID");
            Name = getArguments().getString("Name");
            Category = getArguments().getString("Category");
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                callBack();
                return true;
            }
            return false;
        });
        binding.llBack.setOnClickListener(view1 -> callBack());
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
        binding.rvMainAudio.setLayoutManager(manager);
        if (Name.equalsIgnoreCase("My Downloads")) {
            audioList = new ArrayList<>();
            GetAllMedia(getActivity());
        } else{
            prepareData();
        }
        return view;
    }

    private void callBack() {
        Fragment audioFragment = new AudioFragment();
        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .replace(R.id.flContainer, audioFragment)
                .commit();
        Bundle bundle = new Bundle();
        audioFragment.setArguments(bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
//        prepareData();
    }

    public List<DownloadAudioDetails> GetAllMedia(Context ctx) {
        class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                audioList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllData("");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                refreshData();
                binding.tvTitle.setText(Name);
                ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList = new ArrayList<>();
                for(int i = 0;i<audioList.size();i++){
                    ViewAllAudioListModel.ResponseData.Detail mainPlayModel = new ViewAllAudioListModel.ResponseData.Detail();

                    mainPlayModel.setID(audioList.get(i).getID());
                    mainPlayModel.setName(audioList.get(i).getName());
                    mainPlayModel.setAudioFile(audioList.get(i).getAudioFile());
                    mainPlayModel.setAudioDirection(audioList.get(i).getAudioDirection());
                    mainPlayModel.setAudiomastercat(audioList.get(i).getAudiomastercat());
                    mainPlayModel.setAudioSubCategory(audioList.get(i).getAudioSubCategory());
                    mainPlayModel.setImageFile(audioList.get(i).getImageFile());
                    mainPlayModel.setLike(audioList.get(i).getLike());
                    mainPlayModel.setDownload(audioList.get(i).getDownload());
                    mainPlayModel.setAudioDuration(audioList.get(i).getAudioDuration());
                    listModelList.add(mainPlayModel);
                }
                AudiolistAdapter adapter = new AudiolistAdapter(listModelList, IsLock);
                binding.rvMainAudio.setAdapter(adapter);
                super.onPostExecute(aVoid);
            }
        }
        GetTask st = new GetTask();
        st.execute();
        return audioList;
    }

    private void prepareData() {
        refreshData();
        if (BWSApplication.isNetworkConnected(getActivity())) {
            showProgressBar();
            Call<ViewAllAudioListModel> listCall = APIClient.getClient().getViewAllAudioLists(UserID, ID, Category);
            listCall.enqueue(new Callback<ViewAllAudioListModel>() {
                @Override
                public void onResponse(Call<ViewAllAudioListModel> call, Response<ViewAllAudioListModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        ViewAllAudioListModel listModel = response.body();
                        if (Category.equalsIgnoreCase("")) {
                            binding.tvTitle.setText(listModel.getResponseData().getView());
                        } else {
                            binding.tvTitle.setText(Category);
                        }
                        AudiolistAdapter adapter = new AudiolistAdapter(listModel.getResponseData().getDetails(), listModel.getResponseData().getIsLock());
                        binding.rvMainAudio.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<ViewAllAudioListModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    private void refreshData() {
        try {
            if(IsLock.equalsIgnoreCase("1") && !AudioFlag.equalsIgnoreCase("AppointmentDetailList")){
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
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                Fragment fragment = new TransparentPlayerFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .commit();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4, 6, 4, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4, 6, 4, 50);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.progressBar.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class AudiolistAdapter extends RecyclerView.Adapter<AudiolistAdapter.MyViewHolder> {
        String IsLock;
        private ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList;

        public AudiolistAdapter(ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList, String IsLock) {
            this.listModelList = listModelList;
            this.IsLock = IsLock;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AudiolistCustomLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.audiolist_custom_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                    1, 1, 0.46f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);

            holder.binding.tvPlaylistName.setText(listModelList.get(position).getName());
            Glide.with(getActivity()).load(listModelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            if (IsLock.equalsIgnoreCase("1")) {
                if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                        || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                }
            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            holder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (IsLock.equalsIgnoreCase("1")) {
                        if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                            holder.binding.ivLock.setVisibility(View.GONE);
                        } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                                || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                            holder.binding.ivLock.setVisibility(View.VISIBLE);
                            BWSApplication.showToast("Please re-activate your membership plan", getActivity());
                        }
                    } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        try {
                            player = 1;
                            if (isPrepare || isMediaStart || isPause) {
                                stopMedia();
                            }
                            isPause = false;
                            isMediaStart = false;
                            isPrepare = false;
//                            RefreshData();
                            Fragment fragment = new TransparentPlayerFragment();
                            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                            fragmentManager1.beginTransaction()
                                    .add(R.id.flContainer, fragment)
                                    .commit();
                            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(listModelList.get(position));
                            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                            editor.putInt(CONSTANTS.PREF_KEY_position, position);
                            editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                            editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                            editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                            editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                            editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "MainAudioList");
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            AudiolistCustomLayoutBinding binding;

            public MyViewHolder(AudiolistCustomLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class TopAudiolistAdapter extends RecyclerView.Adapter<TopAudiolistAdapter.MyViewHolder> {
        String IsLock;
        private ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList;

        public TopAudiolistAdapter(ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList, String IsLock) {
            this.listModelList = listModelList;
            this.IsLock = IsLock;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AudiolistCustomLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.audiolist_custom_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                    1, 1, 0.46f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);

            holder.binding.tvPlaylistName.setText(listModelList.get(position).getName());
            Glide.with(getActivity()).load(listModelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            if (IsLock.equalsIgnoreCase("1")) {
                if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                        || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                }
            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            holder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (IsLock.equalsIgnoreCase("1")) {
                        if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                            holder.binding.ivLock.setVisibility(View.GONE);
                        } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                                || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                            holder.binding.ivLock.setVisibility(View.VISIBLE);
                            BWSApplication.showToast("Please re-activate your membership plan", getActivity());
                        }
                    } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        try {
                            player = 1;
                            if (isPrepare || isMediaStart || isPause) {
                                stopMedia();
                            }
                            isPause = false;
                            isMediaStart = false;
                            isPrepare = false;
                            Fragment fragment = new TransparentPlayerFragment();
                            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                            fragmentManager1.beginTransaction()
                                    .add(R.id.flContainer, fragment)
                                    .commit();
                            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(listModelList.get(position));
                            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                            editor.putInt(CONSTANTS.PREF_KEY_position, position);
                            editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                            editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                            editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                            editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                            editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "MainAudioList");
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            AudiolistCustomLayoutBinding binding;

            public MyViewHolder(AudiolistCustomLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}