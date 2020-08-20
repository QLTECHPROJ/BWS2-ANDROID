package com.qltech.bws.BillingOrderModule.Activities;

import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.BillingOrderModule.Models.CancelPlanModel;
import com.qltech.bws.DownloadModule.Models.DownloadlistModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.AppUtils;
import com.qltech.bws.databinding.ActivityCancelMembershipBinding;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelMembershipActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {
    ActivityCancelMembershipBinding binding;
    Context ctx;
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cancel_membership);

        ctx = CancelMembershipActivity.this;

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.youtubeView.initialize(AppUtils.DEVELOPER_KEY, this);

    /*    MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 29,
                5, 3, 1.1f, 29);
        binding.youtubeView.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.youtubeView.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());*/

       /* getLifecycle().addObserver(binding.youtubeView);

        binding.youtubeView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "S0Q4gqBUs7c";
                youTubePlayer.loadVideo(videoId, 0);
            }
        });*/

        binding.btnCancelSubscrible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   if(responseReasonList.get(mSelectedItem).getCancelID().equalsIgnoreCase("5") &&
                        edtCancelBox.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(ctx, "Please enter reason", Toast.LENGTH_SHORT).show();
                }*/
                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.cancel_membership);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                final RelativeLayout tvconfirm = dialog.findViewById(R.id.tvconfirm);
                tvconfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                       /* if (BWSApplication.isNetworkConnected(ctx)) {
                            Call<CancelPlanModel> listCall = APIClient.getClient().getCancelPlan(UserID, CancelId, CancelReason);
                            listCall.enqueue(new Callback<CancelPlanModel>() {
                                @Override
                                public void onResponse(Call<CancelPlanModel> call, Response<CancelPlanModel> response) {
                                    showProgressBar();
                                }

                                @Override
                                public void onFailure(Call<CancelPlanModel> call, Throwable t) {

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
                        }*/
                    }
                });
            }
        });
    }

    private void hideProgressBar() {
        binding.progressBarHolder.setVisibility(View.GONE);
        binding.ImgV.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void showProgressBar() {
        binding.progressBarHolder.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        binding.ImgV.setVisibility(View.VISIBLE);
        binding.ImgV.invalidate();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer
            player,
                                        boolean wasRestored) {
        if (!wasRestored) {
            player.loadVideo(AppUtils.YOUTUBE_VIDEO_CODE);
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
      /*      PlayerUIController uiController = youTubePlayerView.getPlayerUIController();
            player.showVideoTitle(false);*/
            player.setShowFullscreenButton(true);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider
                                                provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            getYouTubePlayerProvider().initialize(AppUtils.DEVELOPER_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return binding.youtubeView;
    }
}