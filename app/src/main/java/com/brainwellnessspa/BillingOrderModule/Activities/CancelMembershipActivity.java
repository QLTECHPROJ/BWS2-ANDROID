package com.brainwellnessspa.BillingOrderModule.Activities;

import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.brainwellnessspa.LikeModule.Activities.LikeActivity;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Models.CancelPlanModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;

import static com.brainwellnessspa.Services.GlobalInitExoPlayer.APP_SERVICE_STATUS;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityCancelMembershipBinding;
import com.segment.analytics.Properties;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelMembershipActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {
    ActivityCancelMembershipBinding binding;
    Context ctx;
    String UserID, CancelId = "";
    Activity activity;
    boolean audioPause = false;
    public static final String API_KEY = "AIzaSyCzqUwQUD58tA8wrINDc1OnL0RgcU52jzQ", VIDEO_ID = "y1rfRW6WX08";
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private int numStarted = 0;
    int stackStatus = 0;
    boolean myBackPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cancel_membership);
        ctx = CancelMembershipActivity.this;
        activity = CancelMembershipActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        binding.llBack.setOnClickListener(view -> {
            myBackPress = true;
            if (audioPause) {
                player.setPlayWhenReady(true);
            } else {
            }
            finish();
//            resumeMedia();
//            isPause = false;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }
        binding.youtubeView.initialize(API_KEY, this);
        Properties p = new Properties();
        p.putValue("userId", UserID);
        p.putValue("plan", "");
        p.putValue("planStatus", "");
        p.putValue("planStartDt", "");
        p.putValue("planExpiryDt", "");
        p.putValue("planAmount", "");
        BWSApplication.addToSegment("Cancel Subscription Viewed", p, CONSTANTS.screen);
        if (player != null) {
            if (player.getPlayWhenReady()) {
                player.setPlayWhenReady(false);
                audioPause = true;
            }
        }

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
            if (player != null) {
                if (player.getPlayWhenReady()) {
                    player.setPlayWhenReady(false);
                    audioPause = true;
                }
            }
            if (CancelId.equalsIgnoreCase("4") &&
                    binding.edtCancelBox.getText().toString().equalsIgnoreCase("")) {
                BWSApplication.showToast("Cancellation reason is required", ctx);
            } else {
                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.cancel_membership);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                final Button Btn = dialog.findViewById(R.id.Btn);

                dialog.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        if (player != null) {
                            if (player.getPlayWhenReady()) {
                                player.setPlayWhenReady(false);
                                audioPause = true;
                            }
                        }
                        return true;
                    }
                    return false;
                });

                Btn.setOnTouchListener((view1, event) -> {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            Button views = (Button) view1;
                            views.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                            view1.invalidate();
                            break;
                        }
                        case MotionEvent.ACTION_UP:
                            if (BWSApplication.isNetworkConnected(ctx)) {
                                Call<CancelPlanModel> listCall = APIClient.getClient().getCancelPlan(UserID, CancelId, binding.edtCancelBox.getText().toString());
                                listCall.enqueue(new Callback<CancelPlanModel>() {
                                    @Override
                                    public void onResponse(Call<CancelPlanModel> call, Response<CancelPlanModel> response) {
                                        try {
                                            CancelPlanModel model = response.body();
                                            BWSApplication.showToast(model.getResponseMessage(), ctx);
                                            dialog.dismiss();
                                            String CancelReason = binding.edtCancelBox.getText().toString();
                                            /*Properties p = new Properties();
                                            p.putValue("userId", UserID);
                                            p.putValue("cancelId", CancelId);
                                            p.putValue("cancelReason", CancelReason);
                                            BWSApplication.addToSegment("Cancel Subscription Clicked", p, CONSTANTS.track);*/
                                            if (player != null) {
                                                if (player.getPlayWhenReady()) {
                                                    player.setPlayWhenReady(false);
                                                    audioPause = true;
                                                }
                                            }
                                            finish();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CancelPlanModel> call, Throwable t) {
                                    }
                                });
                            } else {
                                BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                            }
                        case MotionEvent.ACTION_CANCEL: {
                            Button views = (Button) view1;
                            views.getBackground().clearColorFilter();
                            views.invalidate();
                            break;
                        }
                    }
                    return true;
                });

                tvGoBack.setOnClickListener(v -> {
                    dialog.dismiss();
                    if (player != null) {
                        if (player.getPlayWhenReady()) {
                            player.setPlayWhenReady(false);
                            audioPause = true;
                        }
                    }
                });
                dialog.show();
                dialog.setCancelable(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        myBackPress = true;
        if (audioPause) {
            player.setPlayWhenReady(true);
        } else {
        }
        finish();
//        resumeMedia();
//        isPause = false;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer
            youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            youTubePlayer.loadVideo(VIDEO_ID);
            youTubePlayer.setShowFullscreenButton(true);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
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
            getYouTubePlayerProvider().initialize(API_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return binding.youtubeView;
    }

    class AppLifecycleCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                stackStatus = 1;
                APP_SERVICE_STATUS = getString(R.string.Foreground);
                Log.e("APPLICATION", "APP IN FOREGROUND");
                //app went to foreground
            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            numStarted--;
            if (numStarted == 0) {
                if (!myBackPress) {
                    Log.e("APPLICATION", "Back press false");
                    stackStatus = 2;
                } else {
                    myBackPress = true;
                    stackStatus = 1;
                    Log.e("APPLICATION", "back press true ");
                }
                APP_SERVICE_STATUS = getString(R.string.Background);
                Log.e("APPLICATION", "App is in BACKGROUND");
                // app went to background
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationId);
                relesePlayer(getApplicationContext());
            } else {
                Log.e("Destroy", "Activity go in main activity");
            }
        }
    }

}