package com.qltech.bws.ReminderModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.qltech.bws.DashboardModule.Audio.Adapters.RecentlyPlayedAdapter;
import com.qltech.bws.R;
import com.qltech.bws.ReminderModule.Models.SelectPlaylistModel;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivitySelectPlaylistBinding;
import com.qltech.bws.databinding.SelectPlaylistLayoutBinding;
import com.qltech.bws.databinding.SmallBoxLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class SelectPlaylistActivity extends AppCompatActivity {
    private List<SelectPlaylistModel> model = new ArrayList<>();
    ActivitySelectPlaylistBinding binding;
    String UserId;
    Context ctx;
    SelectPlaylistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_playlist);
        ctx = SelectPlaylistActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        Glide.with(ctx).load(R.drawable.loading).asGif().into(binding.ImgV);

        adapter = new SelectPlaylistAdapter(model);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvSelectPlaylist.setLayoutManager(mLayoutManager);
        binding.rvSelectPlaylist.setItemAnimator(new DefaultItemAnimator());
        binding.rvSelectPlaylist.setAdapter(adapter);

        prepareData();
    }

    private void prepareData() {
        SelectPlaylistModel list = new SelectPlaylistModel("Ultimate Anger Relief Bundle");
        model.add(list);
        list = new SelectPlaylistModel("Ultimate Children’s Bundle");
        model.add(list);
        list = new SelectPlaylistModel("Ultimate Communication Bundle");
        model.add(list);
        list = new SelectPlaylistModel("Ultimate Executive Performance Bundle");
        model.add(list);
        list = new SelectPlaylistModel("Ultimate Fatherhood Stress Bundle -in");
        model.add(list);
        list = new SelectPlaylistModel("Ultimate FIFO Workers Survival Bundle");
        model.add(list);
        list = new SelectPlaylistModel("Ultimate Insomnia Bundle");
        model.add(list);
        list = new SelectPlaylistModel("Ultimate Mental Health Bundle");
        model.add(list);
        list = new SelectPlaylistModel("Ultimate Motherhood Stress Bundle");
        model.add(list);
        list = new SelectPlaylistModel("Ultimate Peaceful Sleeping Bundle");
        model.add(list);
        list = new SelectPlaylistModel("Ultimate Powerful Public Speaking Bundle");
        model.add(list);
        list = new SelectPlaylistModel("Ultimate Relationship Breakdown Bundle");
        model.add(list);
    }

    public class SelectPlaylistAdapter extends RecyclerView.Adapter<SelectPlaylistAdapter.MyViewHolder>{
        private List<SelectPlaylistModel> model;

        public SelectPlaylistAdapter(List<SelectPlaylistModel> model) {
            this.model = model;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SelectPlaylistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.select_playlist_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            SelectPlaylistModel listModel = model.get(position);
            holder.binding.tvTitle.setText(listModel.getTitle());
        }

        @Override
        public int getItemCount() {
            return model.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            SelectPlaylistLayoutBinding binding;

            public MyViewHolder(SelectPlaylistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}