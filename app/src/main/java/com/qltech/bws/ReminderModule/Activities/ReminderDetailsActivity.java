package com.qltech.bws.ReminderModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.R;
import com.qltech.bws.ReminderModule.Models.RemiderDetailsModel;
import com.qltech.bws.ReminderModule.Models.ReminderStatusModel;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityReminderDetailsBinding;
import com.qltech.bws.databinding.RemiderDetailsLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.qltech.bws.DashboardModule.Account.AccountFragment.IsLock;

public class ReminderDetailsActivity extends AppCompatActivity {
    ActivityReminderDetailsBinding binding;
    String UserId;
    Context ctx;
    Activity activity;
    RemiderDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder_details);
        ctx = ReminderDetailsActivity.this;
        activity = ReminderDetailsActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        Glide.with(ctx).load(R.drawable.loading).asGif().into(binding.ImgV);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvReminderDetails.setLayoutManager(mLayoutManager);
        binding.rvReminderDetails.setItemAnimator(new DefaultItemAnimator());

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        prepareData();

        binding.btnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsLock.equalsIgnoreCase("1")){
                    BWSApplication.showToast("Please re-activate your membership plan", ctx);
                }else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")){
                    Intent i = new Intent(ctx, ReminderActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
            Call<RemiderDetailsModel> listCall = APIClient.getClient().getReminderStatus(UserId);
            listCall.enqueue(new Callback<RemiderDetailsModel>() {
                @Override
                public void onResponse(Call<RemiderDetailsModel> call, Response<RemiderDetailsModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        RemiderDetailsModel listModel = response.body();
                        adapter = new RemiderDetailsAdapter(listModel.getResponseData());
                        binding.rvReminderDetails.setAdapter(adapter);

                        if (listModel.getResponseData().size() == 0){
                            binding.llError.setVisibility(View.VISIBLE);
                            binding.rvReminderDetails.setVisibility(View.GONE);
                        }else {
                            binding.llError.setVisibility(View.GONE);
                            binding.rvReminderDetails.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<RemiderDetailsModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
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
            holder.binding.tvName.setText(model.get(position).getPlaylistName());
            holder.binding.tvDate.setText(model.get(position).getReminderDay());
            holder.binding.tvTime.setText(model.get(position).getReminderTime());

            if (model.get(position).getIsCheck().equalsIgnoreCase("1")) {
                holder.binding.switchStatus.setChecked(true);
            } else {
                holder.binding.switchStatus.setChecked(false);
            }

            holder.binding.switchStatus.setOnCheckedChangeListener((compoundButton, checked) -> {
                if (checked) {
                    prepareSwitchStatus("1");
                } else {
                    prepareSwitchStatus("0");
                }
            });

        }

        @Override
        public int getItemCount() {
            return model.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            RemiderDetailsLayoutBinding binding;

            public MyViewHolder(RemiderDetailsLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
    private void prepareSwitchStatus(String reminderStatus) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
            Call<ReminderStatusModel> listCall = APIClient.getClient().getReminderStatus(UserId, reminderStatus);/*set 1 or not 0 */
            listCall.enqueue(new Callback<ReminderStatusModel>() {
                @Override
                public void onResponse(Call<ReminderStatusModel> call, Response<ReminderStatusModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        ReminderStatusModel listModel = response.body();
                        BWSApplication.showToast(listModel.getResponseMessage(),activity);
                        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_ReminderStatus, MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString(CONSTANTS.PREF_KEY_ReminderStatus, reminderStatus);
                        editor.commit();
                    }
                }

                @Override
                public void onFailure(Call<ReminderStatusModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }
}