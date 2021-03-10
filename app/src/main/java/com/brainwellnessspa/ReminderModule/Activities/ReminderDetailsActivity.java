package com.brainwellnessspa.ReminderModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.BillingOrderModule.Models.SegmentPayment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ReferralModule.Activities.ReferFriendActivity;
import com.brainwellnessspa.ReminderModule.Models.DeleteRemiderModel;
import com.brainwellnessspa.ReminderModule.Models.RemiderDetailsModel;
import com.brainwellnessspa.ReminderModule.Models.ReminderStatusModel;
import com.brainwellnessspa.ReminderModule.Models.SegmentReminder;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityReminderDetailsBinding;
import com.brainwellnessspa.databinding.RemiderDetailsLayoutBinding;
import com.google.gson.Gson;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class ReminderDetailsActivity extends AppCompatActivity {
    ActivityReminderDetailsBinding binding;
    String UserID, ReminderFirstLogin = "0";
    Context ctx;
    Activity activity;
    ArrayList<String> remiderIds = new ArrayList<>();
    RemiderDetailsAdapter adapter;
    FancyShowCaseView fancyShowCaseView1, fancyShowCaseView2;
    FancyShowCaseQueue queue;
    RemiderDetailsModel listReminderModel;
    Properties p;
    private int numStarted = 0;
    int stackStatus = 0;
    boolean myBackPress = false;
    boolean notificationStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder_details);
        ctx = ReminderDetailsActivity.this;
        activity = ReminderDetailsActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvReminderDetails.setLayoutManager(mLayoutManager);
        binding.rvReminderDetails.setItemAnimator(new DefaultItemAnimator());
        notificationStatus = false;
        /*ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.rvReminderDetails);*/

        binding.llBack.setOnClickListener(view -> {
            myBackPress = true;
            finish();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }
        binding.btnAddReminder.setOnClickListener(view -> {
            notificationStatus = true;
            myBackPress = false;
            if (BWSApplication.isNetworkConnected(ctx)) {
                Intent i = new Intent(ctx, ReminderActivity.class);
                i.putExtra("ComeFrom", "");
                i.putExtra("ReminderId", "");
                i.putExtra("PlaylistID", "");
                i.putExtra("PlaylistName", "");
                i.putExtra("Time", "");
                i.putExtra("Day", "");
                i.putExtra("ReminderDay", "");
                i.putExtra("IsCheck", "");
                startActivity(i);
                finish();
            } else {
                BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
            }
        });
    }

    @Override
    protected void onResume() {
        prepareData();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        myBackPress = true;
        finish();
    }

    private void prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<RemiderDetailsModel> listCall = APIClient.getClient().getGetReminderStatus(UserID);
            listCall.enqueue(new Callback<RemiderDetailsModel>() {
                @Override
                public void onResponse(Call<RemiderDetailsModel> call, Response<RemiderDetailsModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            RemiderDetailsModel listModel = response.body();
                            listReminderModel = listModel;
                            adapter = new RemiderDetailsAdapter(listModel.getResponseData());
                            binding.rvReminderDetails.setAdapter(adapter);
                            binding.btnAddReminder.setVisibility(View.VISIBLE);
                            showTooltips();
                            p = new Properties();
                            p.putValue("userId", UserID);
                            if (listModel.getResponseData().size() == 0) {
                                binding.llError.setVisibility(View.VISIBLE);
                                binding.rvReminderDetails.setVisibility(View.GONE);
                                p.putValue("reminders ", "");
                            } else {
                                binding.llError.setVisibility(View.GONE);
                                binding.rvReminderDetails.setVisibility(View.VISIBLE);
                                ArrayList<SegmentReminder> section1 = new ArrayList<>();
                                SegmentReminder e = new SegmentReminder();
                                Gson gson = new Gson();
                                for (int i = 0; i < listModel.getResponseData().size(); i++) {
                                    e.setReminderId(listModel.getResponseData().get(i).getReminderId());
                                    e.setPlaylistId(listModel.getResponseData().get(i).getPlaylistId());
                                    e.setPlaylistName(listModel.getResponseData().get(i).getPlaylistName());
                                    e.setPlaylistType("");
                                    if (listModel.getResponseData().get(i).getIsCheck().equalsIgnoreCase("1")) {
                                        e.setReminderStatus("on");
                                    } else {
                                        e.setReminderStatus("off");
                                    }
                                    e.setReminderTime(listModel.getResponseData().get(i).getReminderTime());
                                    e.setReminderDay(listModel.getResponseData().get(i).getReminderDay());
                                    section1.add(e);
                                }
                                p.putValue("reminders ", gson.toJson(section1));
                            }
                            BWSApplication.addToSegment("Reminder Screen Viewed", p, CONSTANTS.screen);
                            if (remiderIds.size() == 0) {
                                binding.llSelectAll.setVisibility(View.GONE);
                                binding.btnAddReminder.setVisibility(View.VISIBLE);
                                binding.btnDeleteReminder.setVisibility(View.GONE);
                            } else {
                                binding.llSelectAll.setVisibility(View.VISIBLE);
                                binding.btnAddReminder.setVisibility(View.GONE);
                                binding.btnDeleteReminder.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RemiderDetailsModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }

        binding.btnDeleteReminder.setOnClickListener(view -> {
            notificationStatus = true;
            myBackPress = false;
            final Dialog dialog = new Dialog(ctx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.delete_reminder);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
            final RelativeLayout tvconfirm = dialog.findViewById(R.id.tvconfirm);

            dialog.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            });

            tvconfirm.setOnClickListener(v -> {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    Call<DeleteRemiderModel> listCall = APIClient.getClient().getDeleteRemiderStatus(UserID,
                            TextUtils.join(",", remiderIds));
                    listCall.enqueue(new Callback<DeleteRemiderModel>() {
                        @Override
                        public void onResponse(Call<DeleteRemiderModel> call, Response<DeleteRemiderModel> response) {
                            try {
                                DeleteRemiderModel model = response.body();
                                if (model.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                    remiderIds.clear();
                                    BWSApplication.showToast(model.getResponseMessage(), ctx);
                                    dialog.dismiss();
                                    prepareData();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<DeleteRemiderModel> call, Throwable t) {
                        }
                    });
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                }
            });

            tvGoBack.setOnClickListener(v -> {
                dialog.dismiss();
            });
            dialog.show();
            dialog.setCancelable(false);
        });
    }

    private void showTooltips() {
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        ReminderFirstLogin = (shared1.getString(CONSTANTS.PREF_KEY_ReminderFirstLogin, "0"));

        if (ReminderFirstLogin.equalsIgnoreCase("1")) {
            Animation enterAnimation = AnimationUtils.loadAnimation(ctx, R.anim.slide_in_top);
            Animation exitAnimation = AnimationUtils.loadAnimation(ctx, R.anim.slide_out_bottom);
            fancyShowCaseView1 = new FancyShowCaseView.Builder(activity)
                    .customView(R.layout.layout_reminder_status, view -> {
                        RelativeLayout rlNext = view.findViewById(R.id.rlNext);
                        ImageView ivLibraryImage = view.findViewById(R.id.ivLibraryImage);
                        final ValueAnimator anim = ValueAnimator.ofFloat(0.9f, 1f);
                        anim.setDuration(1500);
                        anim.addUpdateListener(animation -> {
                            ivLibraryImage.setScaleX((Float) animation.getAnimatedValue());
                            ivLibraryImage.setScaleY((Float) animation.getAnimatedValue());
                        });
                        anim.setRepeatCount(ValueAnimator.INFINITE);
                        anim.setRepeatMode(ValueAnimator.REVERSE);
                        anim.start();
                        rlNext.setOnClickListener(v -> fancyShowCaseView1.hide());
                    }).focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .enterAnimation(enterAnimation).exitAnimation(exitAnimation).closeOnTouch(false).build();

            fancyShowCaseView2 = new FancyShowCaseView.Builder(activity)
                    .customView(R.layout.layout_reminder_remove, view -> {
                        RelativeLayout rlDone = view.findViewById(R.id.rlDone);
                        ImageView ivLibraryImage = view.findViewById(R.id.ivLibraryImage);
                        final ValueAnimator anim = ValueAnimator.ofFloat(0.9f, 1f);
                        anim.setDuration(1500);
                        anim.addUpdateListener(animation -> {
                            ivLibraryImage.setScaleX((Float) animation.getAnimatedValue());
                            ivLibraryImage.setScaleY((Float) animation.getAnimatedValue());
                        });
                        anim.setRepeatCount(ValueAnimator.INFINITE);
                        anim.setRepeatMode(ValueAnimator.REVERSE);
                        anim.start();
                        rlDone.setOnClickListener(v -> {
                            fancyShowCaseView2.hide();
                        });
                    })
                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .enterAnimation(enterAnimation).exitAnimation(exitAnimation).closeOnTouch(false).build();

            queue = new FancyShowCaseQueue().add(fancyShowCaseView1).add(fancyShowCaseView2);
            queue.show();
        }
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(CONSTANTS.PREF_KEY_ReminderFirstLogin, "0");
        editor.commit();
    }

    public class RemiderDetailsAdapter extends RecyclerView.Adapter<RemiderDetailsAdapter.MyViewHolder> {
        private List<RemiderDetailsModel.ResponseData> model;

        public RemiderDetailsAdapter(List<RemiderDetailsModel.ResponseData> model) {
            this.model = model;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RemiderDetailsLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.remider_details_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.bind.tvName.setText(model.get(position).getPlaylistName());
            holder.bind.tvDate.setText(model.get(position).getReminderDay());
            holder.bind.tvTime.setText(model.get(position).getReminderTime());
            holder.bind.view.setClickable(false);
            holder.bind.view.setEnabled(false);

//            checkBox = holder.bind.cbChecked;
            /*holder.bind.llChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.bind.cbChecked.isChecked()) {
//                    notifyDataSetChanged();
                        if (!remiderIds.contains(model.get(position).getReminderId())) {
                            remiderIds.add(model.get(position).getReminderId());
                            binding.tvSelectAll.setText(remiderIds.size() + " selected");
                        } else {
                        }
                    } else {
                        remiderIds.remove(model.get(position).getReminderId());
                        binding.tvSelectAll.setText(remiderIds.size() + " selected");
                    }
                    if (remiderIds.size() == 0) {
                        binding.llSelectAll.setVisibility(View.GONE);
                        binding.btnAddReminder.setVisibility(View.VISIBLE);
                        binding.btnDeleteReminder.setVisibility(View.GONE);
                    } else {
                        binding.llSelectAll.setVisibility(View.VISIBLE);
                        binding.btnAddReminder.setVisibility(View.GONE);
                        binding.btnDeleteReminder.setVisibility(View.VISIBLE);
                    }
                    if (remiderIds.size() == model.size()) {
                        binding.cbChecked.setChecked(true);
                        binding.tvSelectAll.setText(remiderIds.size() + " selected");
                    }
                    Log.e("remiderIds", TextUtils.join(",", remiderIds));
                }
            });*/

            holder.bind.cbChecked.setOnCheckedChangeListener((compoundButton, b) -> {
                if (holder.bind.cbChecked.isChecked()) {
//                    notifyDataSetChanged();
                    if (!remiderIds.contains(model.get(position).getReminderId())) {
                        remiderIds.add(model.get(position).getReminderId());
                        binding.tvSelectAll.setText(remiderIds.size() + " selected");
                    } else {
                    }
                } else {
                    remiderIds.remove(model.get(position).getReminderId());
                    binding.tvSelectAll.setText(remiderIds.size() + " selected");
                }
                if (remiderIds.size() == 0) {
                    binding.llSelectAll.setVisibility(View.GONE);
                    binding.btnAddReminder.setVisibility(View.VISIBLE);
                    binding.btnDeleteReminder.setVisibility(View.GONE);
                } else {
                    binding.llSelectAll.setVisibility(View.VISIBLE);
                    binding.btnAddReminder.setVisibility(View.GONE);
                    binding.btnDeleteReminder.setVisibility(View.VISIBLE);
                }
                if (remiderIds.size() == model.size()) {
                    binding.cbChecked.setChecked(true);
                    binding.tvSelectAll.setText(remiderIds.size() + " selected");
                }
//                Log.e("remiderIds", TextUtils.join(",", remiderIds));
            });

            binding.llClose.setOnClickListener(view -> {
                remiderIds.clear();
                binding.llSelectAll.setVisibility(View.GONE);
                binding.cbChecked.setChecked(false);
                notifyDataSetChanged();
            });

            /*holder.bind.llChecked.setOnClickListener(view ->  {
                if (binding.cbChecked.isChecked()) {
                    remiderIds.clear();
                    for (int i = 0; i < model.size(); i++) {
                        remiderIds.add(model.get(i).getReminderId());
                    }
                } else {
                    binding.llSelectAll.setVisibility(View.GONE);
                    remiderIds.clear();
                }
                Log.e("remiderIds", TextUtils.join(",", remiderIds));
                notifyDataSetChanged();
            });*/

            binding.cbChecked.setOnClickListener(view -> {
                if (binding.cbChecked.isChecked()) {
                    remiderIds.clear();
                    for (int i = 0; i < model.size(); i++) {
                        remiderIds.add(model.get(i).getReminderId());
                    }
                } else {
                    binding.llSelectAll.setVisibility(View.GONE);
                    remiderIds.clear();
                }
//                Log.e("remiderIds", TextUtils.join(",", remiderIds));
                notifyDataSetChanged();
            });

            if (remiderIds.contains(model.get(position).getReminderId())) {
                holder.bind.cbChecked.setChecked(true);
                binding.tvSelectAll.setText(remiderIds.size() + " selected");
            } else {
                holder.bind.cbChecked.setChecked(false);
                binding.tvSelectAll.setText(remiderIds.size() + " selected");
            }
            if (remiderIds.size() == model.size()) {
                binding.cbChecked.setChecked(true);
                binding.tvSelectAll.setText(remiderIds.size() + " selected");
            }
            if (model.get(position).getIsCheck().equalsIgnoreCase("1")) {
                holder.bind.switchStatus.setChecked(true);
            } else {
                holder.bind.switchStatus.setChecked(false);
            }
            if (model.get(position).getIsLock().equalsIgnoreCase("1")) {
                holder.bind.switchStatus.setClickable(false);
                holder.bind.switchStatus.setEnabled(false);
                holder.bind.llSwitchStatus.setClickable(true);
                holder.bind.llSwitchStatus.setEnabled(true);
                holder.bind.llSwitchStatus.setOnClickListener(view -> {
                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    startActivity(i);
                });
            } else if (model.get(position).getIsLock().equalsIgnoreCase("2")) {
                holder.bind.switchStatus.setClickable(false);
                holder.bind.switchStatus.setEnabled(false);
                holder.bind.llSwitchStatus.setClickable(true);
                holder.bind.llSwitchStatus.setEnabled(true);
                holder.bind.llSwitchStatus.setOnClickListener(view -> {
                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
                });
            } else if (model.get(position).getIsLock().equalsIgnoreCase("0") || model.get(position).getIsLock().equalsIgnoreCase("")) {
                holder.bind.switchStatus.setClickable(true);
                holder.bind.switchStatus.setEnabled(true);
                holder.bind.llSwitchStatus.setClickable(false);
                holder.bind.llSwitchStatus.setEnabled(false);
                holder.bind.switchStatus.setOnCheckedChangeListener((compoundButton, checked) -> {
                    if (checked) {
                        prepareSwitchStatus("1", model.get(position).getPlaylistId());
                    } else {
                        prepareSwitchStatus("0", model.get(position).getPlaylistId());
                    }
                });
            }

            holder.bind.llMainLayout.setOnClickListener(view -> {
                notificationStatus = true;
                myBackPress = false;
                Intent i = new Intent(ctx, ReminderActivity.class);
                i.putExtra("ComeFrom", "1");
                i.putExtra("ReminderId", model.get(position).getReminderId());
                i.putExtra("PlaylistID", model.get(position).getPlaylistId());
                i.putExtra("PlaylistName", model.get(position).getPlaylistName());
                i.putExtra("Time", model.get(position).getReminderTime());
                i.putExtra("Day", model.get(position).getRDay());
                i.putExtra("ReminderDay", model.get(position).getReminderDay());
                i.putExtra("IsCheck", model.get(position).getIsCheck());
                startActivity(i);
                finish();
            });
        }

        @Override
        public int getItemCount() {
            return model.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            RemiderDetailsLayoutBinding bind;

            public MyViewHolder(RemiderDetailsLayoutBinding bind) {
                super(bind.getRoot());
                this.bind = bind;
            }
        }
    }

    private void prepareSwitchStatus(String reminderStatus, String PlaylistID) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<ReminderStatusModel> listCall = APIClient.getClient().getReminderStatus(UserID, PlaylistID, reminderStatus);/*set 1 or not 0 */
            listCall.enqueue(new Callback<ReminderStatusModel>() {
                @Override
                public void onResponse(Call<ReminderStatusModel> call, Response<ReminderStatusModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            ReminderStatusModel listModel = response.body();
                            BWSApplication.showToast(listModel.getResponseMessage(), activity);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ReminderStatusModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    class AppLifecycleCallback implements Application.ActivityLifecycleCallbacks {


        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                stackStatus = 1;
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
                    notificationStatus = false;
                    myBackPress = true;
                    stackStatus = 1;
                    Log.e("APPLICATION", "back press true ");
                }
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
                if (!notificationStatus) {
                    if (player != null) {
                        Log.e("Destroy", "Activity Destoryed");
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(notificationId);
                        relesePlayer(ctx);
                    }
                }
            } else {
                Log.e("Destroy", "Activity go in main activity");
            }
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT) {
                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.reminder_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
                final TextView tvSubTitle = dialog.findViewById(R.id.tvSubTitle);
                final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                final RelativeLayout tvconfirm = dialog.findViewById(R.id.tvconfirm);
                tvTitle.setText("Delete Reminder");
                tvSubTitle.setText("Are you sure you want to delete your reminder?");
                dialog.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                });

                tvconfirm.setOnClickListener(v -> {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        Call<DeleteRemiderModel> listCall = APIClient.getClient().getDeleteRemiderStatus(UserID,
                                listReminderModel.getResponseData().get(position).getReminderId());
                        listCall.enqueue(new Callback<DeleteRemiderModel>() {
                            @Override
                            public void onResponse(Call<DeleteRemiderModel> call, Response<DeleteRemiderModel> response) {
                                try {
                                    if (response.isSuccessful()) {
                                        DeleteRemiderModel model = response.body();
                                        BWSApplication.showToast(model.getResponseMessage(), ctx);
                                        prepareData();
                                        dialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<DeleteRemiderModel> call, Throwable t) {
                            }
                        });
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                    }
                });

                tvGoBack.setOnClickListener(v -> {
                    dialog.dismiss();
                    prepareData();
                });
                dialog.show();
                dialog.setCancelable(false);
            }
        }

        @Override
        public int getSwipeDirs(RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder) {
            final int position = viewHolder.getAdapterPosition();
            return super.getSwipeDirs(recyclerView, viewHolder);
        }
    };
}