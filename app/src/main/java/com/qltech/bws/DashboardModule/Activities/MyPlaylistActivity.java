package com.qltech.bws.DashboardModule.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Adapters.DirectionAdapter;
import com.qltech.bws.DashboardModule.Models.DirectionModel;
import com.qltech.bws.DashboardModule.Models.DownloadPlaylistModel;
import com.qltech.bws.DashboardModule.Models.RenamePlaylistModel;
import com.qltech.bws.DashboardModule.Models.SubPlayListModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityMyPlaylistBinding;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPlaylistActivity extends AppCompatActivity {
    ActivityMyPlaylistBinding binding;
    String UserID, PlaylistID, Download, Like;
    Context ctx;
    public static int deleteFrg = 0;

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
                finish();
            }
        });

        getPrepareData();
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
                                finish();
                            }
                        });

                        binding.tvName.setText(model.getResponseData().getPlaylistName());
                        if (model.getResponseData().getTotalAudio().equalsIgnoreCase("") &&
                                model.getResponseData().getTotalhour().equalsIgnoreCase("")
                                && model.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                            binding.tvDesc.setText("0 Audio | 0h 0m");
                        } else {
                            binding.tvDesc.setText(model.getResponseData().getTotalAudio() + " Audio | "
                                    + model.getResponseData().getTotalhour() + "h " + model.getResponseData().getTotalminute() + "m");
                        }

                        if (model.getResponseData().getPlaylistDesc().equalsIgnoreCase("")){
                            binding.tvTitleDec.setVisibility(View.GONE);
                            binding.tvSubDec.setVisibility(View.GONE);
                        }else {
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

                        String[] elements = model.getResponseData().getPlaylistMastercat().split(",");
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
                                            Toast.makeText(ctx, "Please enter playlist name", Toast.LENGTH_SHORT).show();
                                        } else {
                                            showProgressBar();
                                            if (BWSApplication.isNetworkConnected(ctx)) {
                                                Call<RenamePlaylistModel> listCall = APIClient.getClient().getRenamePlaylist(UserID, PlaylistID, edtCreate.getText().toString());
                                                listCall.enqueue(new Callback<RenamePlaylistModel>() {
                                                    @Override
                                                    public void onResponse(Call<RenamePlaylistModel> call, Response<RenamePlaylistModel> response) {
                                                        if (response.isSuccessful()) {
                                                            hideProgressBar();
                                                            RenamePlaylistModel listModel = response.body();
                                                            Toast.makeText(MyPlaylistActivity.this, listModel.getResponseMessage(), Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(ctx, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
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
                                final RelativeLayout tvconfirm = dialog.findViewById(R.id.tvconfirm);

                                dialog.setOnKeyListener((v, keyCode, event) -> {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        dialog.dismiss();
                                        FragmentManager fm = getSupportFragmentManager();
                                        fm.popBackStack("MyPlaylistsFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                        return true;
                                    }
                                    return false;
                                });

                                tvconfirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showProgressBar();
                                        if (BWSApplication.isNetworkConnected(ctx)) {
                                            Call<SucessModel> listCall = APIClient.getClient().getDeletePlaylist(UserID, PlaylistID);
                                            listCall.enqueue(new Callback<SucessModel>() {
                                                @Override
                                                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                                    if (response.isSuccessful()) {
                                                        deleteFrg = 1;
                                                        hideProgressBar();
                                                        SucessModel listModel = response.body();
                                                        dialog.dismiss();
                                                        Toast.makeText(MyPlaylistActivity.this, listModel.getResponseMessage(), Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<SucessModel> call, Throwable t) {
                                                    hideProgressBar();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(ctx, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ctx, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
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