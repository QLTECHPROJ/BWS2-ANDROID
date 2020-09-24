package com.qltech.bws.DashboardModule.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Adapters.DirectionAdapter;
import com.qltech.bws.DashboardModule.Models.DownloadPlaylistModel;
import com.qltech.bws.DashboardModule.Models.RenamePlaylistModel;
import com.qltech.bws.DashboardModule.Models.SubPlayListModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.Playlist.PlaylistFragment;
import com.qltech.bws.R;
import com.qltech.bws.RoomDataBase.DownloadPlaylistDetails;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityMyPlaylistBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPlaylistActivity extends AppCompatActivity {
    ActivityMyPlaylistBinding binding;
    String UserID, PlaylistID, Download;
    Context ctx;
    public static int deleteFrg = 0;
    public static int ComeFindAudio = 0;
    DownloadPlaylistDetails downloadPlaylistDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_playlist);
        ctx = MyPlaylistActivity.this;
        Glide.with(MyPlaylistActivity.this).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        if (getIntent().getExtras() != null) {
            PlaylistID = getIntent().getStringExtra(CONSTANTS.PlaylistID);
        }

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComeFindAudio = 1;
                finish();
            }
        });

        getPrepareData();
    }

    @Override
    public void onBackPressed() {
        ComeFindAudio = 1;
        finish();
    }

    private void getPrepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            showProgressBar();
            Call<SubPlayListModel> listCall = APIClient.getClient().getSubPlayLists(UserID, PlaylistID);
            listCall.enqueue(new Callback<SubPlayListModel>() {
                @Override
                public void onResponse(Call<SubPlayListModel> call, Response<SubPlayListModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        SubPlayListModel model = response.body();
                        downloadPlaylistDetails = new DownloadPlaylistDetails();
                        downloadPlaylistDetails.setPlaylistID(model.getResponseData().getPlaylistID());
                        downloadPlaylistDetails.setPlaylistName( model.getResponseData().getPlaylistName());
                        downloadPlaylistDetails.setPlaylistDesc( model.getResponseData().getPlaylistDesc());
                        downloadPlaylistDetails.setIsReminder(model.getResponseData().getIsReminder());
                        downloadPlaylistDetails.setPlaylistMastercat( model.getResponseData().getPlaylistMastercat());
                        downloadPlaylistDetails.setPlaylistSubcat( model.getResponseData().getPlaylistSubcat());
                        downloadPlaylistDetails.setPlaylistImage( model.getResponseData().getPlaylistImage());
                        downloadPlaylistDetails.setTotalAudio( model.getResponseData().getTotalAudio());
                        downloadPlaylistDetails.setTotalDuration( model.getResponseData().getTotalDuration());
                        downloadPlaylistDetails.setTotalhour( model.getResponseData().getTotalhour());
                        downloadPlaylistDetails.setTotalminute( model.getResponseData().getTotalminute());
                        downloadPlaylistDetails.setCreated( model.getResponseData().getCreated());
                        downloadPlaylistDetails.setDownload( model.getResponseData().getDownload());
                        downloadPlaylistDetails.setLike( model.getResponseData().getLike());
                        binding.tvName.setText(model.getResponseData().getPlaylistName());
                        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 20,
                                1, 1, 0.54f, 20);
                        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
                        if (!model.getResponseData().getPlaylistImage().equalsIgnoreCase("")) {
                            Glide.with(ctx).load(model.getResponseData().getPlaylistImage())
                                    .thumbnail(0.05f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                        } else {
                            binding.ivRestaurantImage.setImageResource(R.drawable.ic_playlist_bg);
                        }

                        Download = model.getResponseData().getDownload();

                        if (model.getResponseData().getCreated().equalsIgnoreCase("1")) {
                            binding.llOptions.setVisibility(View.GONE);
                            binding.llRename.setVisibility(View.VISIBLE);
                            binding.llDelete.setVisibility(View.VISIBLE);
                            binding.llFind.setVisibility(View.GONE);
                        } else if (model.getResponseData().getCreated().equalsIgnoreCase("0")) {
                            binding.llOptions.setVisibility(View.VISIBLE);
                            binding.llRename.setVisibility(View.GONE);
                            binding.llDelete.setVisibility(View.GONE);
                            binding.llFind.setVisibility(View.VISIBLE);
                        }

                        binding.llFind.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ComeFindAudio = 1;
                                finish();
                            }
                        });
                        if (model.getResponseData().getPlaylistMastercat().equalsIgnoreCase("")) {
                            binding.tvDesc.setVisibility(View.GONE);
                        } else {
                            binding.tvDesc.setVisibility(View.VISIBLE);
                            binding.tvDesc.setText(model.getResponseData().getPlaylistMastercat());
                        }

                        if (model.getResponseData().getTotalAudio().equalsIgnoreCase("") ||
                                model.getResponseData().getTotalAudio().equalsIgnoreCase("0") &&
                                        model.getResponseData().getTotalhour().equalsIgnoreCase("")
                                        && model.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                            binding.tvTime.setText("0 Audio | 0h 0m");
                        } else {
                            if (model.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                binding.tvTime.setText(model.getResponseData().getTotalAudio() + " Audio | "
                                        + model.getResponseData().getTotalhour() + "h 0m");
                            } else {
                                binding.tvTime.setText(model.getResponseData().getTotalAudio() + " Audio | "
                                        + model.getResponseData().getTotalhour() + "h " + model.getResponseData().getTotalminute() + "m");
                            }
                        }

                        if (model.getResponseData().getPlaylistDesc().equalsIgnoreCase("")) {
                            binding.tvTitleDec.setVisibility(View.GONE);
                            binding.tvSubDec.setVisibility(View.GONE);
                        } else {
                            binding.tvTitleDec.setVisibility(View.VISIBLE);
                            binding.tvSubDec.setVisibility(View.VISIBLE);
                        }

                        binding.tvSubDec.setText(model.getResponseData().getPlaylistDesc());
                        int linecount = binding.tvSubDec.getLineCount();
                        if (linecount >= 4) {
                            binding.tvReadMore.setVisibility(View.VISIBLE);
                        } else {
                            binding.tvReadMore.setVisibility(View.GONE);
                        }

                        binding.tvReadMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Dialog dialog = new Dialog(ctx);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.full_desc_layout);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                final TextView tvDesc = dialog.findViewById(R.id.tvDesc);
                                final RelativeLayout tvClose = dialog.findViewById(R.id.tvClose);
                                tvDesc.setText(model.getResponseData().getPlaylistDesc());

                                dialog.setOnKeyListener((v, keyCode, event) -> {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        dialog.dismiss();
                                        return true;
                                    }
                                    return false;
                                });

                                tvClose.setOnClickListener(v -> dialog.dismiss());

                                dialog.show();
                                dialog.setCancelable(false);
                            }
                        });

                       /* if (model.getResponseData().get(0).getLike().equalsIgnoreCase("1")) {
                            binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                        } else if (!model.getResponseData().get(0).getLike().equalsIgnoreCase("0")) {
                            binding.ivLike.setImageResource(R.drawable.ic_like_white_icon);
                        }*/

                        if (model.getResponseData().getDownload().equalsIgnoreCase("1")) {
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                            binding.ivDownloads.setColorFilter(Color.argb(99, 99, 99, 99));
                            binding.ivDownloads.setAlpha(255);
                            binding.llDownload.setClickable(false);
                            binding.llDownload.setEnabled(false);
                        } else if (!model.getResponseData().getDownload().equalsIgnoreCase("")) {
                            binding.llDownload.setClickable(true);
                            binding.llDownload.setEnabled(true);
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                        }

                        binding.llDownload.setVisibility(View.VISIBLE);

                        binding.llDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (BWSApplication.isNetworkConnected(ctx)) {
                                    showProgressBar();
                                    Call<DownloadPlaylistModel> listCall = null;
                                    listCall = APIClient.getClient().getDownloadlistPlaylist(UserID, "", PlaylistID);
                                    listCall.enqueue(new Callback<DownloadPlaylistModel>() {
                                        @Override
                                        public void onResponse(Call<DownloadPlaylistModel> call, Response<DownloadPlaylistModel> response) {
                                            if (response.isSuccessful()) {
                                                hideProgressBar();
                                                DownloadPlaylistModel model = response.body();
                                                BWSApplication.showToast(model.getResponseMessage(), ctx);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<DownloadPlaylistModel> call, Throwable t) {
                                            hideProgressBar();
                                        }
                                    });

                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        String[] elements = model.getResponseData().getPlaylistSubcat().split(",");
                        List<String> direction = Arrays.asList(elements);

                        DirectionAdapter directionAdapter = new DirectionAdapter(direction, ctx);
                        RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                        binding.rvDirlist.setLayoutManager(recentlyPlayed);
                        binding.rvDirlist.setItemAnimator(new DefaultItemAnimator());
                        binding.rvDirlist.setAdapter(directionAdapter);
                        String PlaylistID = model.getResponseData().getPlaylistID();
                        binding.llRename.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Dialog dialog = new Dialog(ctx);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.create_palylist);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                final EditText edtCreate = dialog.findViewById(R.id.edtCreate);
                                final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                                final TextView tvAction = dialog.findViewById(R.id.tvAction);
                                final TextView tvHeading = dialog.findViewById(R.id.tvHeading);
                                final RelativeLayout rlCreate = dialog.findViewById(R.id.rlCreate);
                                tvHeading.setText(R.string.Rename_your_playlist);
                                tvAction.setText(R.string.Save);
                                edtCreate.requestFocus();
                                edtCreate.setText(model.getResponseData().getPlaylistName());
                                int position1 = edtCreate.getText().length();
                                Editable editObj = edtCreate.getText();
                                Selection.setSelection(editObj, position1);

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
                                            BWSApplication.showToast("Please provide the playlist's name", ctx);
                                        } else {
                                            if (BWSApplication.isNetworkConnected(ctx)) {
                                                showProgressBar();
                                                Call<RenamePlaylistModel> listCall = APIClient.getClient().getRenamePlaylist(UserID, PlaylistID, edtCreate.getText().toString());
                                                listCall.enqueue(new Callback<RenamePlaylistModel>() {
                                                    @Override
                                                    public void onResponse(Call<RenamePlaylistModel> call, Response<RenamePlaylistModel> response) {
                                                        if (response.isSuccessful()) {
                                                            hideProgressBar();
                                                            RenamePlaylistModel listModel = response.body();
                                                            BWSApplication.showToast(listModel.getResponseMessage(), ctx);
                                                            dialog.dismiss();
                                                            finish();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<RenamePlaylistModel> call, Throwable t) {
                                                        hideProgressBar();
                                                    }
                                                });
                                            } else {
                                                BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                                            }
                                        }
                                    }
                                });
                                tvCancel.setOnClickListener(v -> dialog.dismiss());
                                dialog.show();
                                dialog.setCancelable(false);
                            }
                        });

                        binding.llDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Dialog dialog = new Dialog(ctx);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.delete_playlist);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                                final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                                final TextView tvHeader = dialog.findViewById(R.id.tvHeader);
                                final RelativeLayout tvconfirm = dialog.findViewById(R.id.tvconfirm);

                                tvHeader.setText("Are you sure you want to delete " + model.getResponseData().getPlaylistName() + "  playlist?");
                                dialog.setOnKeyListener((v, keyCode, event) -> {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        dialog.dismiss();
                                        Fragment playlistFragment = new PlaylistFragment();
                                        FragmentManager fragmentManager1 = getSupportFragmentManager();
                                        fragmentManager1.beginTransaction()
                                                .add(R.id.rlPlaylist, playlistFragment)
                                                .commit();
                                        Bundle bundle = new Bundle();
                                        playlistFragment.setArguments(bundle);
                                        return true;
                                    }
                                    return false;
                                });

                                tvconfirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (BWSApplication.isNetworkConnected(ctx)) {
                                            showProgressBar();
                                            Call<SucessModel> listCall = APIClient.getClient().getDeletePlaylist(UserID, PlaylistID);
                                            listCall.enqueue(new Callback<SucessModel>() {
                                                @Override
                                                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                                    if (response.isSuccessful()) {
                                                        deleteFrg = 1;
                                                        hideProgressBar();
                                                        SucessModel listModel = response.body();
                                                        dialog.dismiss();
                                                        BWSApplication.showToast(listModel.getResponseMessage(), ctx);
                                                        finish();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<SucessModel> call, Throwable t) {
                                                    hideProgressBar();
                                                }
                                            });
                                        } else {
                                            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                                        }
                                    }
                                });

                                tvGoBack.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                                dialog.setCancelable(false);
                            }
                        });

                    } else {
                    }

                }

                @Override
                public void onFailure(Call<SubPlayListModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}