package com.qltech.bws.DashboardModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qltech.bws.DashboardModule.Models.RenamePlaylistModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.Playlist.MyPlaylistsFragment;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityMyPlaylistBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPlaylistActivity extends AppCompatActivity {
    ActivityMyPlaylistBinding binding;
    String UserID, PlaylistID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_playlist);

        Glide.with(MyPlaylistActivity.this).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        MeasureRatio measureRatio = BWSApplication.measureRatio(MyPlaylistActivity.this, 20,
                1, 1, 0.54f, 20);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.ivRestaurantImage.setImageResource(R.drawable.ic_playlist_bg);

        binding.llRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MyPlaylistActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.create_palylist);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                final EditText edtCreate = dialog.findViewById(R.id.edtCreate);
                final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                final TextView tvAction = dialog.findViewById(R.id.tvAction);
                final RelativeLayout rlCreate = dialog.findViewById(R.id.rlCreate);
                tvAction.setText(R.string.Rename);

                rlCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edtCreate.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(MyPlaylistActivity.this, "Please enter playlist name", Toast.LENGTH_SHORT).show();
                        } else {
                            if (BWSApplication.isNetworkConnected(MyPlaylistActivity.this)) {
                                Call<RenamePlaylistModel> listCall = APIClient.getClient().getRenamePlaylist(UserID, PlaylistID, edtCreate.getText().toString());
                                listCall.enqueue(new Callback<RenamePlaylistModel>() {
                                    @Override
                                    public void onResponse(Call<RenamePlaylistModel> call, Response<RenamePlaylistModel> response) {
                                        if (response.isSuccessful()) {
                                            RenamePlaylistModel listModel = response.body();
                                            Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                                            FragmentManager fragmentManager1 = getSupportFragmentManager();
                                            fragmentManager1.beginTransaction()
                                                    .replace(R.id.rlPlaylist, myPlaylistsFragment).
                                                    addToBackStack("MyPlaylistsFragment")
                                                    .commit();
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<RenamePlaylistModel> call, Throwable t) {
                                    }
                                });
                            } else {
                                Toast.makeText(MyPlaylistActivity.this, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
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
                final Dialog dialog = new Dialog(MyPlaylistActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.delete_playlist);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                final RelativeLayout tvconfirm = dialog.findViewById(R.id.tvconfirm);
                tvconfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (BWSApplication.isNetworkConnected(MyPlaylistActivity.this)) {
                            Call<SucessModel> listCall = APIClient.getClient().getDeletePlaylist(UserID, PlaylistID);
                            listCall.enqueue(new Callback<SucessModel>() {
                                @Override
                                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                    if (response.isSuccessful()) {
                                        SucessModel listModel = response.body();
                                        Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                                        FragmentManager fragmentManager1 = getSupportFragmentManager();
                                        fragmentManager1.beginTransaction()
                                                .replace(R.id.rlPlaylist, myPlaylistsFragment).
                                                addToBackStack("MyPlaylistsFragment")
                                                .commit();
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onFailure(Call<SucessModel> call, Throwable t) {
                                }
                            });
                        } else {
                            Toast.makeText(MyPlaylistActivity.this, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
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
    }
}