package com.qltech.bws.DashboardModule.Playlist.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qltech.bws.DashboardModule.Models.AddPlaylistModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityAddPlaylistBinding;
import com.qltech.bws.databinding.AddPlayListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class AddPlaylistActivity extends AppCompatActivity {
    ActivityAddPlaylistBinding binding;
    List<AddPlaylistModel> addPlaylist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_playlist);

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnAddPlatLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(AddPlaylistActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.create_palylist);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                final RelativeLayout tvconfirm = dialog.findViewById(R.id.tvconfirm);

                tvconfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       /* Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                        FragmentManager fragmentManager1 = getSupportFragmentManager();
                        fragmentManager1.beginTransaction()
                                .replace(R.id.rlPlaylist, myPlaylistsFragment).
                                addToBackStack("MyPlaylistsFragment")
                                .commit();*/
                        dialog.dismiss();
                    }
                });

                tvGoBack.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
                dialog.setCancelable(false);
            }
        });

        AddPlaylistAdapter addPlaylistAdapter = new AddPlaylistAdapter(addPlaylist, AddPlaylistActivity.this);
        RecyclerView.LayoutManager played = new LinearLayoutManager(AddPlaylistActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvPlayLists.setLayoutManager(played);
        binding.rvPlayLists.setItemAnimator(new DefaultItemAnimator());
        binding.rvPlayLists.setAdapter(addPlaylistAdapter);
        preparePlayLists();
    }

    private void preparePlayLists() {
        AddPlaylistModel list = new AddPlaylistModel("Playlist 1");
        addPlaylist.add(list);
        list = new AddPlaylistModel("Playlist 2");
        addPlaylist.add(list);
        list = new AddPlaylistModel("Playlist 3");
        addPlaylist.add(list);
        list = new AddPlaylistModel("Night audio");
        addPlaylist.add(list);
        list = new AddPlaylistModel("Playlist 3");
        addPlaylist.add(list);
        list = new AddPlaylistModel("Night audio");
        addPlaylist.add(list);
    }

    private class AddPlaylistAdapter extends RecyclerView.Adapter<AddPlaylistAdapter.MyViewHolder> {
        private List<AddPlaylistModel> listModelList;
        Context ctx;

        public AddPlaylistAdapter(List<AddPlaylistModel> listModelList, Context ctx) {
            this.listModelList = listModelList;
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AddPlayListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.add_play_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            AddPlaylistModel listModel = listModelList.get(position);
            holder.binding.tvTitle.setText(listModel.getTitle());

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivRestaurantImage.setImageResource(R.drawable.square_logo);
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            AddPlayListLayoutBinding binding;

            public MyViewHolder(AddPlayListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}