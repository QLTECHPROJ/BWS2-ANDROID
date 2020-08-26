package com.qltech.bws.DashboardModule.Playlist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Appointment.AppointmentFragment;
import com.qltech.bws.DashboardModule.Models.MainPlayListModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentViewAllPlaylistBinding;
import com.qltech.bws.databinding.PlaylistCustomLayoutBinding;

import java.util.ArrayList;

public class ViewAllPlaylistFragment extends Fragment {
    FragmentViewAllPlaylistBinding binding;
    String Name;
    ArrayList<MainPlayListModel.ResponseData.Detail> Audiolist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_all_playlist, container, false);
        View view = binding.getRoot();


        if (getArguments() != null) {
            Name = getArguments().getString("Name");
            Audiolist = getArguments().getParcelableArrayList("Audiolist");
        }
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                PlaylistFragment playlistFragment = new PlaylistFragment();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                fm.popBackStack ("ViewAllPlaylistFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                fragmentManager.beginTransaction()
//                        .replace(R.id.rlPlaylist, playlistFragment)
//                        .commit();
//                Bundle bundle = new Bundle();
//                bundle.putString("Check", "1");
//                playlistFragment.setArguments(bundle);

            }
        });
        binding.tvTitle.setText(Name);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
        binding.rvMainAudio.setLayoutManager(manager);
        PlaylistAdapter adapter = new PlaylistAdapter(Audiolist, getActivity());
        binding.rvMainAudio.setAdapter(adapter);
        return view;
    }

    public class PlaylistAdapter  extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder>  {
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