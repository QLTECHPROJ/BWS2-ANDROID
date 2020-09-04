package com.qltech.bws.DashboardModule.Playlist;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;

import com.qltech.bws.DashboardModule.Models.CreatePlaylistModel;
import com.qltech.bws.DashboardModule.Models.MainPlayListModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentPlaylistBinding;
import com.qltech.bws.databinding.MainAudioLayoutBinding;
import com.qltech.bws.databinding.PlaylistCustomLayoutBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistFragment extends Fragment {
    FragmentPlaylistBinding binding;
    private PlaylistViewModel playlistViewModel;
    String UserID, Check = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        playlistViewModel =
                ViewModelProviders.of(this).get(PlaylistViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false);
        View view = binding.getRoot();
        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        if (getArguments() != null) {
            Check = getArguments().getString("Check");
        }
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

        if (!AudioFlag.equalsIgnoreCase("0")) {
            Fragment fragment = new TransparentPlayerFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.rlPlaylist, fragment)
                    .addToBackStack("TransparentPlayerFragment")
                    .commit();
        }
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvMainPlayList.setLayoutManager(manager);
        binding.rvMainPlayList.setItemAnimator(new DefaultItemAnimator());

        prepareData();

        binding.rlCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.create_palylist);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                final EditText edtCreate = dialog.findViewById(R.id.edtCreate);
                final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                final RelativeLayout rlCreate = dialog.findViewById(R.id.rlCreate);
                edtCreate.requestFocus();
                dialog.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                });

                rlCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edtCreate.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(getActivity(), "Please enter playlist name", Toast.LENGTH_SHORT).show();
                        } else {
                            if (BWSApplication.isNetworkConnected(getActivity())) {
                                Call<CreatePlaylistModel> listCall = APIClient.getClient().getCreatePlaylist(UserID, edtCreate.getText().toString());
                                listCall.enqueue(new Callback<CreatePlaylistModel>() {
                                    @Override
                                    public void onResponse(Call<CreatePlaylistModel> call, Response<CreatePlaylistModel> response) {
                                        if (response.isSuccessful()) {
                                            CreatePlaylistModel listModel = response.body();
                                            Bundle bundle = new Bundle();
                                            Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                                            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                                            bundle.putString("New", "1");
                                            bundle.putString("PlaylistID", listModel.getResponseData().getId());
                                            bundle.putString("PlaylistName", listModel.getResponseData().getName());
                                            myPlaylistsFragment.setArguments(bundle);
                                            fragmentManager1.beginTransaction()
                                                    .replace(R.id.rlPlaylist, myPlaylistsFragment).
                                                    addToBackStack("MyPlaylistsFragment")
                                                    .commit();
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CreatePlaylistModel> call, Throwable t) {
                                        hideProgressBar();
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                tvCancel.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
                dialog.setCancelable(false);
            }
        });

        playlistViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareData();
    }

    private void prepareData() {
        showProgressBar();
        if (BWSApplication.isNetworkConnected(getActivity())) {
            Call<MainPlayListModel> listCall = APIClient.getClient().getMainPlayLists(UserID);
            listCall.enqueue(new Callback<MainPlayListModel>() {
                @Override
                public void onResponse(Call<MainPlayListModel> call, Response<MainPlayListModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        MainPlayListModel listModel = response.body();
                        MainPlayListAdapter adapter = new MainPlayListAdapter(listModel.getResponseData(), getActivity());
                        binding.rvMainPlayList.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<MainPlayListModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }
    }

    public class MainPlayListAdapter extends RecyclerView.Adapter<MainPlayListAdapter.MyViewHolder> {
        private ArrayList<MainPlayListModel.ResponseData> listModelList;
        Context ctx;

        public MainPlayListAdapter(ArrayList<MainPlayListModel.ResponseData> listModelList, Context ctx) {
            this.listModelList = listModelList;
            this.ctx = ctx;
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
            if (listModelList.get(position).getDetails() != null &&
                    listModelList.get(position).getDetails().size() > 2) {
                holder.binding.tvViewAll.setVisibility(View.VISIBLE);
            } else {
                holder.binding.tvViewAll.setVisibility(View.GONE);
            }

            holder.binding.tvViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment viewAllPlaylistFragment = new ViewAllPlaylistFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.rlPlaylist, viewAllPlaylistFragment)
                            .addToBackStack("ViewAllPlaylistFragment")
                            .commit();
                    Bundle bundle = new Bundle();
                    bundle.putString("GetLibraryID", listModelList.get(position).getGetLibraryID());
                    bundle.putString("Name", listModelList.get(position).getView());
                    viewAllPlaylistFragment.setArguments(bundle);
                }
            });

            GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
            holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
            holder.binding.rvMainAudio.setLayoutManager(manager);
            if (listModelList.get(position).getDetails().size() == 0) {
                holder.binding.llMainLayout.setVisibility(View.GONE);
            } else {
                holder.binding.llMainLayout.setVisibility(View.VISIBLE);
                holder.binding.tvTitle.setText(listModelList.get(position).getView());
                if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.your_created))) {
                    PlaylistAdapter adapter1 = new PlaylistAdapter(listModelList.get(position).getDetails(), getActivity());
                    holder.binding.rvMainAudio.setAdapter(adapter1);
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.MyDownloads))) {
                    PlaylistAdapter adapter2 = new PlaylistAdapter(listModelList.get(position).getDetails(), getActivity());
                    holder.binding.rvMainAudio.setAdapter(adapter2);
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.Recommended))) {
                    PlaylistAdapter adapter3 = new PlaylistAdapter(listModelList.get(position).getDetails(), getActivity());
                    holder.binding.rvMainAudio.setAdapter(adapter3);
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.populars))) {
                    PlaylistAdapter adapter4 = new PlaylistAdapter(listModelList.get(position).getDetails(), getActivity());
                    holder.binding.rvMainAudio.setAdapter(adapter4);
                }
            }

            if (!listModelList.get(position).getDetails().equals("")) {

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

    public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {
        private ArrayList<MainPlayListModel.ResponseData.Detail> listModelList;
        Context ctx;

        public PlaylistAdapter(ArrayList<MainPlayListModel.ResponseData.Detail> listModelList, Context ctx) {
            this.listModelList = listModelList;
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            PlaylistCustomLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.playlist_custom_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.44f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);

            holder.binding.tvPlaylistName.setText(listModelList.get(position).getPlaylistName());
            Glide.with(ctx).load(listModelList.get(position).getPlaylistImage()).thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            holder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    bundle.putString("New", "0");
                    bundle.putString("PlaylistID", listModelList.get(position).getPlaylistID());
                    bundle.putString("PlaylistName", listModelList.get(position).getPlaylistName());
                    bundle.putString("PlaylistImage", listModelList.get(position).getPlaylistImage());
                    myPlaylistsFragment.setArguments(bundle);
                    fragmentManager1.beginTransaction()
                            .add(R.id.rlPlaylist, myPlaylistsFragment).
                            addToBackStack("MyPlaylistsFragment")
                            .commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            if (2 > listModelList.size()) {
                return listModelList.size();
            } else {
                return 2;
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            PlaylistCustomLayoutBinding binding;

            public MyViewHolder(PlaylistCustomLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}