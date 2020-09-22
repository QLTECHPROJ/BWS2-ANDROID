package com.qltech.bws.BillingOrderModule.Activities;

import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.BillingOrderModule.Models.CancelPlanModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.AppUtils;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityCancelMembershipBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.qltech.bws.Utility.MusicService.isMediaStart;
import static com.qltech.bws.Utility.MusicService.isPause;
import static com.qltech.bws.Utility.MusicService.pauseMedia;
import static com.qltech.bws.Utility.MusicService.resumeMedia;

public class CancelMembershipActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {
    ActivityCancelMembershipBinding binding;
    Context ctx;
    String UserID, CancelId = "";
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cancel_membership);

        ctx = CancelMembershipActivity.this;
        Glide.with(ctx).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                resumeMedia();
                isPause = false;
            }
        });

        if (isMediaStart) {
            pauseMedia();
        } else {

        }
        binding.youtubeView.initialize(AppUtils.DEVELOPER_KEY, this);

        binding.cbOne.setOnClickListener(view -> {
            binding.cbOne.setChecked(true);
            binding.cbTwo.setChecked(false);
            binding.cbThree.setChecked(false);
            binding.cbFour.setChecked(false);
            CancelId = "1";
            binding.edtCancelBox.setVisibility(View.GONE);
            binding.edtCancelBox.setText("");
        });

        binding.cbTwo.setOnClickListener(view -> {
            binding.cbOne.setChecked(false);
            binding.cbTwo.setChecked(true);
            binding.cbThree.setChecked(false);
            binding.cbFour.setChecked(false);
            CancelId = "2";
            binding.edtCancelBox.setVisibility(View.GONE);
            binding.edtCancelBox.setText("");
        });

        binding.cbThree.setOnClickListener(view -> {
            binding.cbOne.setChecked(false);
            binding.cbTwo.setChecked(false);
            binding.cbThree.setChecked(true);
            binding.cbFour.setChecked(false);
            CancelId = "3";
            binding.edtCancelBox.setVisibility(View.GONE);
            binding.edtCancelBox.setText("");
        });

        binding.cbFour.setOnClickListener(view -> {
            binding.cbOne.setChecked(false);
            binding.cbTwo.setChecked(false);
            binding.cbThree.setChecked(false);
            binding.cbFour.setChecked(true);
            CancelId = "4";
            binding.edtCancelBox.setVisibility(View.VISIBLE);
        });

        binding.btnCancelSubscrible.setOnClickListener(view -> {
            if (isMediaStart) {
                pauseMedia();
            } else {

            }
            if (CancelId.equalsIgnoreCase("4") &&
                    binding.edtCancelBox.getText().toString().equalsIgnoreCase("")) {
                BWSApplication.showToast("Please enter reason", ctx);
            } else {
                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.cancel_membership);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                final RelativeLayout tvconfirm = dialog.findViewById(R.id.tvconfirm);

                dialog.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        if (isMediaStart) {
                            pauseMedia();
                        } else {

                        }
                        return true;
                    }
                    return false;
                });

                tvconfirm.setOnClickListener(v -> {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        showProgressBar();
                        Call<CancelPlanModel> listCall = APIClient.getClient().getCancelPlan(UserID, CancelId, binding.edtCancelBox.getText().toString());
                        listCall.enqueue(new Callback<CancelPlanModel>() {
                            @Override
                            public void onResponse(Call<CancelPlanModel> call, Response<CancelPlanModel> response) {
                                if (response.isSuccessful()) {
                                    hideProgressBar();
                                    CancelPlanModel model = response.body();
                                    BWSApplication.showToast(model.getResponseMessage(), ctx);
                                    dialog.dismiss();
                                    resumeMedia();
                                    isPause = false;
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<CancelPlanModel> call, Throwable t) {
                                hideProgressBar();
                            }
                        });
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                    }
                });

                tvGoBack.setOnClickListener(v -> {
                    dialog.dismiss();
                    if (isMediaStart) {
                        pauseMedia();
                    } else {

                    }
                });
                dialog.show();
                dialog.setCancelable(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        resumeMedia();
        isPause = false;
        finish();
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

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer
            player, boolean wasRestored) {
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
            BWSApplication.showToast(errorMessage, this);
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