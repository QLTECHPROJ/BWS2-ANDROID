package com.brainwellnessspa.DashboardModule.Audio;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Audio.Adapters.DownloadAdapter;
import com.brainwellnessspa.DashboardModule.Audio.Adapters.RecentlyPlayedAdapter;
import com.brainwellnessspa.DashboardModule.Audio.Adapters.RecommendedAdapter;
import com.brainwellnessspa.DashboardModule.Audio.Adapters.TopCategoriesAdapter;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentAudioBinding;
import com.brainwellnessspa.databinding.MainAudioLayoutBinding;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.brainwellnessspa.DashboardModule.Audio.ViewAllAudioFragment.viewallAudio;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;

public class AudioFragment extends Fragment {
    public static boolean exit = false;
    public static String IsLock = "";
    FragmentAudioBinding binding;
    String UserID, AudioFlag, expDate;
    List<String> fileNameList;
    List<String> audioFile, playlistDownloadId;
    List<DownloadAudioDetails> downloadAudioDetailsList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio, container, false);
        View view = binding.getRoot();
        viewallAudio = false;
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (!isDownloading) {
            SharedPreferences sharedx = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
            String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
            String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
            if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                fileNameList = gson.fromJson(json, type);
                audioFile = gson.fromJson(json1, type);
            /*if(json2.equalsIgnoreCase(String.valueOf(gson))){
                playlistDownloadId = new ArrayList<>();
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                String playlistIdJson = gson.toJson(playlistDownloadId);
                editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                editor.commit();
                SharedPreferences sharedy = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
                json2 = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
            }*/
                playlistDownloadId = gson.fromJson(json2, type);
                if (fileNameList.size() != 0) {
                    DownloadMedia downloadMedia = new DownloadMedia(getActivity().getApplicationContext());
                    downloadMedia.encrypt1(audioFile, fileNameList,playlistDownloadId/*, playlistSongs*/);
                }
            }
        }
        prepareData();
        return view;
    }

    public void GetAllMedia(FragmentActivity ctx, List<MainAudioModel.ResponseData> listModel) {
        ArrayList<MainAudioModel.ResponseData.Detail> details = new ArrayList<>();
        class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadAudioDetailsList = new ArrayList<>();
                downloadAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllData("");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (downloadAudioDetailsList.size() != 0) {
                    for (int i = 0; i < downloadAudioDetailsList.size(); i++) {
                        MainAudioModel.ResponseData.Detail detail = new MainAudioModel.ResponseData.Detail();
                        detail.setID(downloadAudioDetailsList.get(i).getID());
                        detail.setName(downloadAudioDetailsList.get(i).getName());
                        detail.setAudioFile(downloadAudioDetailsList.get(i).getAudioFile());
                        detail.setAudioDirection(downloadAudioDetailsList.get(i).getAudioDirection());
                        detail.setAudiomastercat(downloadAudioDetailsList.get(i).getAudiomastercat());
                        detail.setAudioSubCategory(downloadAudioDetailsList.get(i).getAudioSubCategory());
                        detail.setImageFile(downloadAudioDetailsList.get(i).getImageFile());
                        detail.setLike(downloadAudioDetailsList.get(i).getLike());
                        detail.setDownload(downloadAudioDetailsList.get(i).getDownload());
                        detail.setAudioDuration(downloadAudioDetailsList.get(i).getAudioDuration());
                        details.add(detail);
                    }
                    for (int i = 0; i < listModel.size(); i++) {
                        if (listModel.get(i).getView().equalsIgnoreCase("My Downloads")) {
                            listModel.get(i).setDetails(details);
                        }
                    }
                    MainAudioListAdapter adapter = new MainAudioListAdapter(listModel, getActivity());
                    RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    binding.rvMainAudioList.setLayoutManager(manager);
                    binding.rvMainAudioList.setItemAnimator(new DefaultItemAnimator());
                    binding.rvMainAudioList.setAdapter(adapter);
                } else {
                    MainAudioListAdapter adapter = new MainAudioListAdapter(listModel, getActivity());
                    RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    binding.rvMainAudioList.setLayoutManager(manager);
                    binding.rvMainAudioList.setItemAnimator(new DefaultItemAnimator());
                    binding.rvMainAudioList.setAdapter(adapter);
                }
                /*if (downloadAudioDetailsList.size() != 0) {
                    MainAudioListAdapter1 adapter1 = new MainAudioListAdapter1(getActivity(),listModel);
                    RecyclerView.LayoutManager manager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    binding.rvMainAudioList.setLayoutManager(manager1);
                    binding.rvMainAudioList.setItemAnimator(new DefaultItemAnimator());
                    binding.rvMainAudioList.setAdapter(adapter1);
                } else {
                    binding.rvMainAudioList.setVisibility(View.GONE);
                }*/
                super.onPostExecute(aVoid);
            }
        }

        GetTask st = new GetTask();
        st.execute();
    }

    private void prepareData() {
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<MainAudioModel> listCall = APIClient.getClient().getMainAudioLists(UserID);
            listCall.enqueue(new Callback<MainAudioModel>() {
                @Override
                public void onResponse(Call<MainAudioModel> call, Response<MainAudioModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        MainAudioModel listModel = response.body();
                        try {
                            IsLock = listModel.getResponseData().get(0).getIsLock();
                            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString(CONSTANTS.PREF_KEY_ExpDate, listModel.getResponseData().get(0).getExpireDate());
                            editor.putString(CONSTANTS.PREF_KEY_IsLock, listModel.getResponseData().get(0).getIsLock());
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        GetAllMedia(getActivity(), listModel.getResponseData());
                    } else {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                    }
                }

                @Override
                public void onFailure(Call<MainAudioModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            expDate = (shared1.getString(CONSTANTS.PREF_KEY_ExpDate, ""));
//            expDate = "2020-09-29 06:34:10";
            Log.e("Exp Date !!!!", expDate);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date Expdate = new Date();
            try {
                Expdate = format.parse(expDate);
                Log.e("Exp Date Expdate!!!!", String.valueOf(Expdate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date currdate = Calendar.getInstance().getTime();
            Date currdate1 = new Date();
            String currantDateTime = simpleDateFormat1.format(currdate);
            try {
                currdate1 = format.parse(currantDateTime);
                Log.e("currant currdate !!!!", String.valueOf(currdate1));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.e("currant Date !!!!", currantDateTime);
            if (Expdate.before(currdate1)) {
                Log.e("app", "Date1 is before Date2");
                IsLock = "1";
            } else if (Expdate.after(currdate1)) {
                Log.e("app", "Date1 is after Date2");
                IsLock = "0";
            } else if (Expdate == currdate1) {
                Log.e("app", "Date1 is equal Date2");
                IsLock = "1";
            }
            ArrayList<MainAudioModel.ResponseData> responseData = new ArrayList<>();
            ArrayList<MainAudioModel.ResponseData.Detail> details = new ArrayList<>();
            MainAudioModel.ResponseData listModel = new MainAudioModel.ResponseData();
            listModel.setHomeID("1");
            listModel.setDetails(details);/*
            "UserID": "2",
            "IsLock": "0",*/
            listModel.setView("My Downloads");
            listModel.setHomeID("1");
            listModel.setUserID(UserID);
            listModel.setIsLock(IsLock);
            responseData.add(listModel);
            GetAllMedia(getActivity(), responseData);
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
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
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

            if (!AudioFlag.equalsIgnoreCase("0")) {
                Fragment fragment = new TransparentPlayerFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .commit();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(13, 6, 13, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(13, 6, 13, 50);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareData();
    }

    public class MainAudioListAdapter extends RecyclerView.Adapter<MainAudioListAdapter.MyViewHolder> {
        FragmentActivity activity;
        private List<MainAudioModel.ResponseData> listModelList;

        public MainAudioListAdapter(List<MainAudioModel.ResponseData> listModelList, FragmentActivity activity) {
            this.listModelList = listModelList;
            this.activity = activity;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MainAudioLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.main_audio_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvViewAll.setOnClickListener(view -> {
                Fragment viewAllAudioFragment = new ViewAllAudioFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.flContainer, viewAllAudioFragment)
                        .commit();
                Bundle bundle = new Bundle();
                bundle.putString("ID", listModelList.get(position).getHomeID());
                bundle.putString("Name", listModelList.get(position).getView());
                bundle.putString("Category", "");
                viewAllAudioFragment.setArguments(bundle);
            });

            if (listModelList.get(position).getDetails().size() == 0) {
                holder.binding.llMainLayout.setVisibility(View.GONE);
            } else {
                holder.binding.llMainLayout.setVisibility(View.VISIBLE);
                holder.binding.tvTitle.setText(listModelList.get(position).getView());
                if (listModelList.get(position).getView().equalsIgnoreCase("My Downloads")) {
                    DownloadAdapter myDownloadsAdapter = new DownloadAdapter(listModelList.get(position).getDetails(), getActivity(), activity,
                            listModelList.get(position).getIsLock());
                    IsLock = listModelList.get(position).getIsLock();
                    RecyclerView.LayoutManager myDownloads = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(myDownloads);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(myDownloadsAdapter);
                    if (listModelList.get(position).getDetails() != null &&
                            listModelList.get(position).getDetails().size() > 4) {
                        holder.binding.tvViewAll.setVisibility(View.VISIBLE);
                    } else {
                        holder.binding.tvViewAll.setVisibility(View.GONE);
                    }
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.Library))) {
                    RecommendedAdapter recommendedAdapter = new RecommendedAdapter(listModelList.get(position).getDetails(), getActivity(), activity,
                            listModelList.get(position).getIsLock());
                    IsLock = listModelList.get(position).getIsLock();
                    RecyclerView.LayoutManager recommended = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recommended);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recommendedAdapter);
                    if (listModelList.get(position).getDetails() != null &&
                            listModelList.get(position).getDetails().size() > 4) {
                        holder.binding.tvViewAll.setVisibility(View.VISIBLE);
                    } else {
                        holder.binding.tvViewAll.setVisibility(View.GONE);
                    }
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.my_like))) {
                    holder.binding.llMainLayout.setVisibility(View.GONE);
                    /*RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(listModelList.get(position).getDetails(), getActivity());
                    RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recentlyPlayed);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recentlyPlayedAdapter);*/
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.recently_played))) {
                    RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(listModelList.get(position).getDetails(), getActivity(), activity,
                            listModelList.get(position).getIsLock());
                    RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recentlyPlayed);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recentlyPlayedAdapter);
                    if (listModelList.get(position).getDetails() != null &&
                            listModelList.get(position).getDetails().size() > 6) {
                        holder.binding.tvViewAll.setVisibility(View.VISIBLE);
                    } else {
                        holder.binding.tvViewAll.setVisibility(View.GONE);
                    }
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.get_inspired))) {
                    RecommendedAdapter inspiredAdapter = new RecommendedAdapter(listModelList.get(position).getDetails(), getActivity(), activity,
                            listModelList.get(position).getIsLock());
                    RecyclerView.LayoutManager inspired = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(inspired);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(inspiredAdapter);
                    if (listModelList.get(position).getDetails() != null &&
                            listModelList.get(position).getDetails().size() > 4) {
                        holder.binding.tvViewAll.setVisibility(View.VISIBLE);
                    } else {
                        holder.binding.tvViewAll.setVisibility(View.GONE);
                    }
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.popular))) {
                    RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(listModelList.get(position).getDetails(), getActivity(), activity,
                            listModelList.get(position).getIsLock());
                    RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recentlyPlayed);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recentlyPlayedAdapter);
                    if (listModelList.get(position).getDetails() != null &&
                            listModelList.get(position).getDetails().size() > 6) {
                        holder.binding.tvViewAll.setVisibility(View.VISIBLE);
                    } else {
                        holder.binding.tvViewAll.setVisibility(View.GONE);
                    }
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.top_categories))) {
                    holder.binding.tvViewAll.setVisibility(View.GONE);
                    TopCategoriesAdapter topCategoriesAdapter = new TopCategoriesAdapter(listModelList.get(position).getDetails(), getActivity(), activity,
                            listModelList.get(position).getHomeID(), listModelList.get(position).getView());
                    RecyclerView.LayoutManager topCategories = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(topCategories);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(topCategoriesAdapter);
                }
            }
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            MainAudioLayoutBinding binding;

            public MyViewHolder(MainAudioLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}