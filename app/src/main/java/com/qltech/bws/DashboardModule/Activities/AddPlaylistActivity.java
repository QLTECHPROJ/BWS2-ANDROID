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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.DashboardModule.Models.CreatePlaylistModel;
import com.qltech.bws.DashboardModule.Models.PlaylistingModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityAddPlaylistBinding;
import com.qltech.bws.databinding.AddPlayListLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.qltech.bws.DashboardModule.Activities.MyPlaylistActivity.comeAddPlaylist;
import static com.qltech.bws.DashboardModule.Search.SearchFragment.comefrom_search;

public class AddPlaylistActivity extends AppCompatActivity {
    ActivityAddPlaylistBinding binding;
    String UserID, AudioId, FromPlaylistID;
    Context ctx;
    Activity activity;
    public static boolean addToPlayList = false;
    public static String MyPlaylistId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_playlist);
        ctx = AddPlaylistActivity.this;
        activity = AddPlaylistActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        if (getIntent().getExtras() != null) {
            AudioId = getIntent().getStringExtra("AudioId");
            FromPlaylistID = getIntent().getStringExtra("PlaylistID");
        }

        binding.llBack.setOnClickListener(view -> {
            comefrom_search = 0;
            finish();
        });

        RecyclerView.LayoutManager played = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvPlayLists.setLayoutManager(played);
        binding.rvPlayLists.setItemAnimator(new DefaultItemAnimator());
        prepareData(ctx);
    }

    @Override
    public void onBackPressed() {
        comefrom_search = 0;
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareData(AddPlaylistActivity.this);
    }

    private void prepareData(Context ctx) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<PlaylistingModel> listCall = APIClient.getClient().getPlaylisting(UserID);
            listCall.enqueue(new Callback<PlaylistingModel>() {
                @Override
                public void onResponse(Call<PlaylistingModel> call, Response<PlaylistingModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        PlaylistingModel model = response.body();
                        AddPlaylistAdapter addPlaylistAdapter = new AddPlaylistAdapter(model.getResponseData(), ctx);
                        binding.rvPlayLists.setAdapter(addPlaylistAdapter);
                    }
                }

                @Override
                public void onFailure(Call<PlaylistingModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });

        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
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
            AddPlayListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.add_play_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(listModel.get(position).getName());
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.16f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            Glide.with(ctx).load(listModel.get(position).getImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            binding.btnAddPlatLists.setOnClickListener(view -> {
                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.create_palylist);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                final EditText edtCreate = dialog.findViewById(R.id.edtCreate);
                final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                final RelativeLayout rlCreate = dialog.findViewById(R.id.rlCreate);

                dialog.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                });

                rlCreate.setOnClickListener(view1 -> {
                    if (edtCreate.getText().toString().equalsIgnoreCase("")) {
                        BWSApplication.showToast("Please provide the playlist's name", ctx);
                    } else {
                        if (BWSApplication.isNetworkConnected(ctx)) {
                            Call<CreatePlaylistModel> listCall = APIClient.getClient().getCreatePlaylist(UserID, edtCreate.getText().toString());
                            listCall.enqueue(new Callback<CreatePlaylistModel>() {
                                @Override
                                public void onResponse(Call<CreatePlaylistModel> call, Response<CreatePlaylistModel> response) {
                                    if (response.isSuccessful()) {
                                        CreatePlaylistModel listsModel = response.body();
                                        dialog.dismiss();
                                        prepareData(ctx);
                                        String PlaylistID = listsModel.getResponseData().getId();
                                        if (BWSApplication.isNetworkConnected(ctx)) {
                                            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                            Call<SucessModel> listCall = APIClient.getClient().getAddSearchAudioFromPlaylist(UserID, AudioId, PlaylistID, FromPlaylistID);
                                            listCall.enqueue(new Callback<SucessModel>() {
                                                @Override
                                                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                                    if (response.isSuccessful()) {
                                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                                        SucessModel listModels = response.body();
                                                        if (comeAddPlaylist == 1) {
                                                            final Dialog dialog = new Dialog(ctx);
                                                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                            dialog.setContentView(R.layout.go_to_playlist);
                                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                                                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                                            final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                                                            final RelativeLayout rlCreate = dialog.findViewById(R.id.rlCreate);

                                                            dialog.setOnKeyListener((v, keyCode, event) -> {
                                                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                                                    dialog.dismiss();
                                                                    return true;
                                                                }
                                                                return false;
                                                            });

                                                            rlCreate.setOnClickListener(view2 -> {
                                                                addToPlayList = true;
                                                                MyPlaylistId = listsModel.getResponseData().getId();
                                                                dialog.dismiss();
                                                                finish();
                                                                /*Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                                                                Bundle bundle = new Bundle();
                                                                bundle.putString("New", "0");
                                                                bundle.putString("PlaylistID", listsModel.getResponseData().getId());
                                                                bundle.putString("PlaylistName", listsModel.getResponseData().getName());
                                                                bundle.putString("MyDownloads", "0");
                                                                myPlaylistsFragment.setArguments(bundle);
                                                                FragmentManager fragmentManager1 = getSupportFragmentManager();
                                                                fragmentManager1.beginTransaction()
                                                                        .replace(R.id.flContainer, myPlaylistsFragment)
                                                                        .commit();*/
                                                            });

                                                            tvCancel.setOnClickListener(v -> {
                                                                dialog.dismiss();
                                                                finish();
                                                            });
                                                            dialog.show();
                                                            dialog.setCancelable(false);
                                                        } else {
                                                            dialog.dismiss();
                                                            finish();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<SucessModel> call, Throwable t) {
                                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                                }
                                            });
                                        } else {
                                            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<CreatePlaylistModel> call, Throwable t) {
                                }
                            });
                        } else {
                            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                        }
                    }
                });
                tvCancel.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
                dialog.setCancelable(false);
            });

            holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String PlaylistID = listModel.get(position).getID();
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        Call<SucessModel> listCall = APIClient.getClient().getAddSearchAudioFromPlaylist(UserID, AudioId, PlaylistID, FromPlaylistID);
                        listCall.enqueue(new Callback<SucessModel>() {
                            @Override
                            public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                if (response.isSuccessful()) {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                    SucessModel listModels = response.body();
                                    if (comeAddPlaylist == 1) {
                                        final Dialog dialog = new Dialog(ctx);
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.go_to_playlist);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                        final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                                        final RelativeLayout rlCreate = dialog.findViewById(R.id.rlCreate);

                                        dialog.setOnKeyListener((v, keyCode, event) -> {
                                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                                dialog.dismiss();
                                                return true;
                                            }
                                            return false;
                                        });

                                        rlCreate.setOnClickListener(view2 -> {
                                            addToPlayList = true;
                                            MyPlaylistId = listModel.get(position).getID();
                                            dialog.dismiss();
                                            finish();
                                        });

                                        tvCancel.setOnClickListener(v -> {
                                            dialog.dismiss();
                                            finish();
                                        });
                                        dialog.show();
                                        dialog.setCancelable(false);
                                    } else {
                                        finish();
                                    }
                                    BWSApplication.showToast(listModels.getResponseMessage(), ctx);
                                }
                            }

                            @Override
                            public void onFailure(Call<SucessModel> call, Throwable t) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            }
                        });
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                    }
                }
            });
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