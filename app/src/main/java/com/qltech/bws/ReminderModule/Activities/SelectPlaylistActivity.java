package com.qltech.bws.ReminderModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.R;
import com.qltech.bws.ReminderModule.Models.SelectPlaylistModel;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivitySelectPlaylistBinding;
import com.qltech.bws.databinding.SelectPlaylistLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectPlaylistActivity extends AppCompatActivity {
    ActivitySelectPlaylistBinding binding;
    String UserId, ComeFrom = "", PlaylistID = "", PlaylistName = "";
    Context ctx;
    Activity activity;
    SelectPlaylistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_playlist);
        ctx = SelectPlaylistActivity.this;
        activity = SelectPlaylistActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        Glide.with(ctx).load(R.drawable.loading).asGif().into(binding.ImgV);

        if (getIntent().getExtras() != null) {
            ComeFrom = getIntent().getStringExtra("ComeFrom");
            PlaylistID = getIntent().getStringExtra(CONSTANTS.PlaylistID);
            PlaylistName = getIntent().getStringExtra("PlaylistName");
        }

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, ReminderActivity.class);
                i.putExtra("ComeFrom", "0");
                i.putExtra("PlaylistID", PlaylistID);
                i.putExtra("PlaylistName", PlaylistName);
                startActivity(i);
                finish();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvSelectPlaylist.setLayoutManager(mLayoutManager);
        binding.rvSelectPlaylist.setItemAnimator(new DefaultItemAnimator());

        prepareData();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ctx, ReminderActivity.class);
        i.putExtra("ComeFrom", "0");
        i.putExtra("PlaylistID", PlaylistID);
        i.putExtra("PlaylistName", PlaylistName);
        startActivity(i);
        finish();
    }

    private void prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
            Call<SelectPlaylistModel> listCall = APIClient.getClient().getAllPlayListing(UserId);
            listCall.enqueue(new Callback<SelectPlaylistModel>() {
                @Override
                public void onResponse(Call<SelectPlaylistModel> call, Response<SelectPlaylistModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        SelectPlaylistModel listModel = response.body();
                        adapter = new SelectPlaylistAdapter(listModel.getResponseData());
                        binding.rvSelectPlaylist.setAdapter(adapter);

                        if (listModel.getResponseData().size() == 0) {
                            binding.llError.setVisibility(View.GONE);
                            binding.rvSelectPlaylist.setVisibility(View.GONE);
                        } else {
                            binding.llError.setVisibility(View.GONE);
                            binding.rvSelectPlaylist.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<SelectPlaylistModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    public class SelectPlaylistAdapter extends RecyclerView.Adapter<SelectPlaylistAdapter.MyViewHolder> {
        private List<SelectPlaylistModel.ResponseData> model;
        public int mSelectedItem = -1;

        public SelectPlaylistAdapter(List<SelectPlaylistModel.ResponseData> model) {
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
            holder.binding.cbChecked.setTag(model.get(position));
            holder.binding.cbChecked.setChecked(position == mSelectedItem);
            holder.binding.cbChecked.setText(model.get(position).getName());
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

                binding.cbChecked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSelectedItem = getAdapterPosition();
                        notifyDataSetChanged();

                        Intent i = new Intent(ctx, ReminderActivity.class);
                        i.putExtra("PlaylistID", model.get(mSelectedItem).getID());
                        i.putExtra("PlaylistName", model.get(mSelectedItem).getName());
                        startActivity(i);
                        finish();
                    }
                });
            }
        }
    }
}