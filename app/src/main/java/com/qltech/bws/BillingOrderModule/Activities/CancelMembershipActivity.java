package com.qltech.bws.BillingOrderModule.Activities;

import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.qltech.bws.R;
import com.qltech.bws.Utility.AppUtils;
import com.qltech.bws.databinding.ActivityCancelMembershipBinding;

public class CancelMembershipActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {
    ActivityCancelMembershipBinding binding;
    MediaController mediaControls;
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cancel_membership);

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.youtubeView.initialize(AppUtils.DEVELOPER_KEY, this);

    /*    MeasureRatio measureRatio = BWSApplication.measureRatio(CancelMembershipActivity.this, 29,
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

/*       binding.rlPlay.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               binding.rlPlay.setVisibility(View.GONE);
               binding.videoView.setVisibility(View.VISIBLE);
               MediaController mediaController= new MediaController(CancelMembershipActivity.this);
               mediaController.setAnchorView(binding.videoView);

               if (mediaControls == null) {
                   mediaControls = new MediaController(CancelMembershipActivity.this);
                   mediaControls.setAnchorView(binding.videoView);
               }
               binding.videoView.setMediaController(null);
               binding.videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video));
               binding.videoView.start();

               binding.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                   @Override
                   public void onCompletion(MediaPlayer mp) {
                       binding.rlPlay.setVisibility(View.VISIBLE);
                       binding.videoView.setVisibility(View.GONE);
                   }
               });

               binding.videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                   @Override
                   public boolean onError(MediaPlayer mp, int what, int extra) {
                       Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show();
                       return false;
                   }
               });
           }
       });*/
/* binding.rlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/watch?v=inpok4MKVLM"));
                try {
                    CancelMembershipActivity.this.startActivity(webIntent);
                } catch (ActivityNotFoundException ex) {
                }
            }
        });*/
        binding.btnCancelSubscrible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(CancelMembershipActivity.this);
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

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.loadVideo(AppUtils.YOUTUBE_VIDEO_CODE);
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
      /*      PlayerUIController uiController = youTubePlayerView.getPlayerUIController();
            player.showVideoTitle(false);*/
            player.setShowFullscreenButton(true);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
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