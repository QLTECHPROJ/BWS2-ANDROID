package com.qltech.bws.DashboardModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.DashboardModule.Models.AddPlaylistModel;
import com.qltech.bws.DashboardModule.Models.CreatePlaylistModel;
import com.qltech.bws.DashboardModule.Models.PlaylistingModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.Playlist.MyPlaylistsFragment;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityAddPlaylistBinding;
import com.qltech.bws.databinding.AddPlayListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPlaylistActivity extends AppCompatActivity {
    ActivityAddPlaylistBinding binding;
    List<AddPlaylistModel> addPlaylist = new ArrayList<>();
    String UserID;
    Context ctx;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_playlist);
        ctx = AddPlaylistActivity.this;
        activity = AddPlaylistActivity.this;
        Glide.with(AddPlaylistActivity.this).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

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
                final EditText edtCreate = dialog.findViewById(R.id.edtCreate);
                final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                final RelativeLayout rlCreate = dialog.findViewById(R.id.rlCreate);

                rlCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edtCreate.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(AddPlaylistActivity.this, "Please enter playlist name", Toast.LENGTH_SHORT).show();
                        } else {
                            if (BWSApplication.isNetworkConnected(AddPlaylistActivity.this)) {
                                Call<CreatePlaylistModel> listCall = APIClient.getClient().getCreatePlaylist(UserID, edtCreate.getText().toString());
                                listCall.enqueue(new Callback<CreatePlaylistModel>() {
                                    @Override
                                    public void onResponse(Call<CreatePlaylistModel> call, Response<CreatePlaylistModel> response) {
                                        if (response.isSuccessful()) {
                                            CreatePlaylistModel listModel = response.body();
                                            Toast.makeText(AddPlaylistActivity.this, listModel.getResponseMessage(), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CreatePlaylistModel> call, Throwable t) {
                                    }
                                });
                            } else {
                                Toast.makeText(AddPlaylistActivity.this, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                tvCancel.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
                dialog.setCancelable(false);
            }
        });

        RecyclerView.LayoutManager played = new LinearLayoutManager(AddPlaylistActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvPlayLists.setLayoutManager(played);
        binding.rvPlayLists.setItemAnimator(new DefaultItemAnimator());
        prepareData(ctx);
    }

    private void prepareData(Context ctx) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.ImgV,binding.progressBarHolder,activity);
            Call<PlaylistingModel> listCall = APIClient.getClient().getPlaylisting(UserID);
            listCall.enqueue(new Callback<PlaylistingModel>() {
                @Override
                public void onResponse(Call<PlaylistingModel> call, Response<PlaylistingModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV,binding.progressBarHolder,activity);
                        PlaylistingModel model = response.body();
                        AddPlaylistAdapter addPlaylistAdapter = new AddPlaylistAdapter(model.getResponseData(), ctx);
                        binding.rvPlayLists.setAdapter(addPlaylistAdapter);
                    }
                }

                @Override
                public void onFailure(Call<PlaylistingModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV,binding.progressBarHolder,activity);                }
            });

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }
    }

    private class AddPlaylistAdapter extends RecyclerView.Adapter<AddPlaylistAdapter.MyViewHolder> {
        private List<PlaylistingModel.ResponseData> listModel;
        Context ctx;

        public AddPlaylistAdapter(List<PlaylistingModel.ResponseData> listModel, Context ctx) {
            this.listModel = listModel;
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
            holder.binding.tvTitle.setText(listModel.get(position).getName());
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    3, 3, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            Glide.with(ctx).load(listModel.get(position).getImage()).thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
        }

        @Override
        public int getItemCount() {
            return listModel.size();
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