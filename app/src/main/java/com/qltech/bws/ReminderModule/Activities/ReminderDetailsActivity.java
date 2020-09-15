package com.qltech.bws.ReminderModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.qltech.bws.R;
import com.qltech.bws.ReminderModule.Models.RemiderDetailsModel;
import com.qltech.bws.ReminderModule.Models.SelectPlaylistModel;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityReminderDetailsBinding;
import com.qltech.bws.databinding.ActivityResourceDetailsBinding;
import com.qltech.bws.databinding.RemiderDetailsLayoutBinding;
import com.qltech.bws.databinding.SelectPlaylistLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class ReminderDetailsActivity extends AppCompatActivity {
    private List<RemiderDetailsModel> model = new ArrayList<>();
    ActivityReminderDetailsBinding binding;
    String UserId;
    Context ctx;
    RemiderDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder_details);
        ctx = ReminderDetailsActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        Glide.with(ctx).load(R.drawable.loading).asGif().into(binding.ImgV);
        adapter = new RemiderDetailsAdapter(model);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvReminderDetails.setLayoutManager(mLayoutManager);
        binding.rvReminderDetails.setItemAnimator(new DefaultItemAnimator());
        binding.rvReminderDetails.setAdapter(adapter);
        prepareData();
        binding.btnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, ReminderActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void prepareData() {
        RemiderDetailsModel list = new RemiderDetailsModel("Ultimate Anger Relief Bundle");
        model.add(list);
        list = new RemiderDetailsModel("Ultimate Children’s Bundle");
        model.add(list);
        list = new RemiderDetailsModel("Ultimate Communication Bundle");
        model.add(list);
        list = new RemiderDetailsModel("Ultimate Executive Performance Bundle");
        model.add(list);
        list = new RemiderDetailsModel("Ultimate Fatherhood Stress Bundle -in");
        model.add(list);
        list = new RemiderDetailsModel("Ultimate FIFO Workers Survival Bundle");
        model.add(list);
        list = new RemiderDetailsModel("Ultimate Insomnia Bundle");
        model.add(list);
        list = new RemiderDetailsModel("Ultimate Mental Health Bundle");
        model.add(list);
        list = new RemiderDetailsModel("Ultimate Motherhood Stress Bundle");
        model.add(list);
        list = new RemiderDetailsModel("Ultimate Peaceful Sleeping Bundle");
        model.add(list);
        list = new RemiderDetailsModel("Ultimate Powerful Public Speaking Bundle");
        model.add(list);
        list = new RemiderDetailsModel("Ultimate Relationship Breakdown Bundle");
        model.add(list);
    }

    public class RemiderDetailsAdapter extends RecyclerView.Adapter<RemiderDetailsAdapter.MyViewHolder>{
        private List<RemiderDetailsModel> model;

        public RemiderDetailsAdapter(List<RemiderDetailsModel> model) {
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
            RemiderDetailsModel listModel = model.get(position);
            holder.binding.tvName.setText(listModel.getTitle());
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
}