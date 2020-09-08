package com.qltech.bws.DashboardModule.Playlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.MainPlayListModel;
import com.qltech.bws.DashboardModule.Models.ViewAllPlayListModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentViewAllPlaylistBinding;
import com.qltech.bws.databinding.PlaylistCustomLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllPlaylistFragment extends Fragment {
    FragmentViewAllPlaylistBinding binding;
    String GetLibraryID, Name, UserID;
    ArrayList<MainPlayListModel.ResponseData.Detail> Audiolist;
    public static boolean viewallPlayList = false;
    String AudioFlag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_all_playlist, container, false);
        View view = binding.getRoot();
        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (getArguments() != null) {
            GetLibraryID = getArguments().getString("GetLibraryID");
            Name = getArguments().getString("Name");
        }
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if( keyCode == KeyEvent.KEYCODE_BACK )
            {
              callBack();
                return true;
            }
            return false;
        });
        binding.llBack.setOnClickListener(view1 -> {
          callBack();
        });
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
        binding.rvMainAudio.setLayoutManager(manager);
        prepareData();
        return view;
    }

    private void callBack() {
        FragmentManager fm = getActivity()
                .getSupportFragmentManager();
        fm.popBackStack("ViewAllPlaylistFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareData();
    }

    private void prepareData() {
        if (!AudioFlag.equalsIgnoreCase("0")) {
            Fragment fragment = new TransparentPlayerFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.rlPlaylist, fragment)
                    .addToBackStack("TransparentPlayerFragment")
                    .commit();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 6, 4, 224);
            binding.llSpace.setLayoutParams(params);
        }else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 6, 4, 120);
            binding.llSpace.setLayoutParams(params);
        }

        if (BWSApplication.isNetworkConnected(getActivity())) {
            showProgressBar();
            Call<ViewAllPlayListModel> listCall = APIClient.getClient().getViewAllPlayLists(UserID, GetLibraryID);
            listCall.enqueue(new Callback<ViewAllPlayListModel>() {
                @Override
                public void onResponse(Call<ViewAllPlayListModel> call, Response<ViewAllPlayListModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        ViewAllPlayListModel listModel = response.body();
                        binding.tvTitle.setText(listModel.getResponseData().getView());
                        PlaylistAdapter adapter = new PlaylistAdapter(listModel.getResponseData().getDetails());
                        binding.rvMainAudio.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<ViewAllPlayListModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
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

    public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {
        private List<ViewAllPlayListModel.ResponseData.Detail> listModelList;

        public PlaylistAdapter(List<ViewAllPlayListModel.ResponseData.Detail> listModelList) {
            this.listModelList = listModelList;
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
            MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                    1, 1, 0.46f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);

            holder.binding.tvPlaylistName.setText(listModelList.get(position).getPlaylistName());
            Glide.with(getActivity()).load(listModelList.get(position).getPlaylistImage()).thumbnail(0.05f)
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
                            .replace(R.id.rlPlaylist, myPlaylistsFragment).
                            addToBackStack("MyPlaylistsFragment")
                            .commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            PlaylistCustomLayoutBinding binding;

            public MyViewHolder(PlaylistCustomLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}