package com.qltech.bws.ReminderModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.BillingOrderModule.Activities.MembershipChangeActivity;
import com.qltech.bws.BillingOrderModule.Models.CancelPlanModel;
import com.qltech.bws.R;
import com.qltech.bws.ReminderModule.Models.DeleteRemiderModel;
import com.qltech.bws.ReminderModule.Models.RemiderDetailsModel;
import com.qltech.bws.ReminderModule.Models.ReminderStatusModel;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityReminderDetailsBinding;
import com.qltech.bws.databinding.RemiderDetailsLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.qltech.bws.DashboardModule.Account.AccountFragment.IsLock;
import static com.qltech.bws.Utility.MusicService.isMediaStart;
import static com.qltech.bws.Utility.MusicService.isPause;
import static com.qltech.bws.Utility.MusicService.pauseMedia;
import static com.qltech.bws.Utility.MusicService.resumeMedia;

public class ReminderDetailsActivity extends AppCompatActivity {
    ActivityReminderDetailsBinding binding;
    String UserId;
    Context ctx;
    Activity activity;
    ArrayList<String> remiderIds = new ArrayList<>();
    RemiderDetailsAdapter adapter;
    public static String comeBack = "";
    RemiderDetailsModel listReminderModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder_details);
        ctx = ReminderDetailsActivity.this;
        activity = ReminderDetailsActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvReminderDetails.setLayoutManager(mLayoutManager);
        binding.rvReminderDetails.setItemAnimator(new DefaultItemAnimator());

        /*ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.rvReminderDetails);*/

        binding.llBack.setOnClickListener(view -> {
            finish();
        });

        prepareData();

        binding.btnAddReminder.setOnClickListener(view -> {
            if (BWSApplication.isNetworkConnected(ctx)) {
                Intent i = new Intent(ctx, ReminderActivity.class);
                i.putExtra("ComeFrom", "");
                i.putExtra("PlaylistID", "");
                i.putExtra("PlaylistName", "");
                i.putExtra("Time", "");
                i.putExtra("Day", "");
                startActivity(i);
                finish();
            } else {
                BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareData();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<RemiderDetailsModel> listCall = APIClient.getClient().getGetReminderStatus(UserId);
            listCall.enqueue(new Callback<RemiderDetailsModel>() {
                @Override
                public void onResponse(Call<RemiderDetailsModel> call, Response<RemiderDetailsModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        RemiderDetailsModel listModel = response.body();
                        listReminderModel = listModel;
                        adapter = new RemiderDetailsAdapter(listModel.getResponseData());
                        binding.rvReminderDetails.setAdapter(adapter);
                        binding.btnAddReminder.setVisibility(View.VISIBLE);
                        if (listModel.getResponseData().size() == 0) {
                            binding.llError.setVisibility(View.VISIBLE);
                            binding.rvReminderDetails.setVisibility(View.GONE);
                        } else {
                            binding.llError.setVisibility(View.GONE);
                            binding.rvReminderDetails.setVisibility(View.VISIBLE);
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
                    Call<DeleteRemiderModel> listCall = APIClient.getClient().getDeleteRemiderStatus(UserId,
                            TextUtils.join(",", remiderIds));
                    listCall.enqueue(new Callback<DeleteRemiderModel>() {
                        @Override
                        public void onResponse(Call<DeleteRemiderModel> call, Response<DeleteRemiderModel> response) {
                            if (response.isSuccessful()) {
                                DeleteRemiderModel model = response.body();
                                remiderIds.clear();
                                BWSApplication.showToast(model.getResponseMessage(), ctx);
                                dialog.dismiss();
                                prepareData();
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
                Log.e("remiderIds", TextUtils.join(",", remiderIds));
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
                Log.e("remiderIds", TextUtils.join(",", remiderIds));
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
                    BWSApplication.showToast("Please re-activate your membership plan", ctx);
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
                Intent i = new Intent(ctx, ReminderActivity.class);
                i.putExtra("ComeFrom", "1");
                i.putExtra("PlaylistID", model.get(position).getPlaylistId());
                i.putExtra("PlaylistName", model.get(position).getPlaylistName());
                i.putExtra("Time", model.get(position).getReminderTime());
                i.putExtra("Day", model.get(position).getRDay());
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
            Call<ReminderStatusModel> listCall = APIClient.getClient().getReminderStatus(UserId, PlaylistID, reminderStatus);/*set 1 or not 0 */
            listCall.enqueue(new Callback<ReminderStatusModel>() {
                @Override
                public void onResponse(Call<ReminderStatusModel> call, Response<ReminderStatusModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        ReminderStatusModel listModel = response.body();
                        BWSApplication.showToast(listModel.getResponseMessage(), activity);
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
                dialog.setContentView(R.layout.cancel_membership);
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
                        Call<DeleteRemiderModel> listCall = APIClient.getClient().getDeleteRemiderStatus(UserId,
                                listReminderModel.getResponseData().get(position).getReminderId());
                        listCall.enqueue(new Callback<DeleteRemiderModel>() {
                            @Override
                            public void onResponse(Call<DeleteRemiderModel> call, Response<DeleteRemiderModel> response) {
                                if (response.isSuccessful()) {
                                    DeleteRemiderModel model = response.body();
                                    BWSApplication.showToast(model.getResponseMessage(), ctx);
                                    prepareData();
                                    dialog.dismiss();
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