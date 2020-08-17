package com.qltech.bws.ResourceModule.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qltech.bws.R;
import com.qltech.bws.ResourceModule.Activities.ResourceDetailsActivity;
import com.qltech.bws.ResourceModule.Models.AppsModel;
import com.qltech.bws.ResourceModule.Models.AudioBooksModel;
import com.qltech.bws.databinding.AppsListLayoutBinding;
import com.qltech.bws.databinding.AudioBooksLayoutBinding;
import com.qltech.bws.databinding.FragmentAppsBinding;

import java.util.ArrayList;
import java.util.List;

public class AppsFragment extends Fragment {
    FragmentAppsBinding binding;
    String apps;
    List<AppsModel> listModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apps, container, false);
        View view = binding.getRoot();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            apps = bundle.getString("apps");
        }
        AppsAdapter adapter = new AppsAdapter(listModelList, getActivity(), apps);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        binding.rvAppsList.setLayoutManager(manager);
        binding.rvAppsList.setItemAnimator(new DefaultItemAnimator());
        binding.rvAppsList.setAdapter(adapter);
        prepareAppListsData();

        return view;
    }

    private void prepareAppListsData() {
        AppsModel list = new AppsModel("Headspace");
        listModelList.add(list);
        list = new AppsModel("Calm");
        listModelList.add(list);
        list = new AppsModel("BeyondNow / BeyondBlue");
        listModelList.add(list);
        list = new AppsModel("notOK");
        listModelList.add(list);
        list = new AppsModel("What's Up");
        listModelList.add(list);
        list = new AppsModel("MoodKit");
        listModelList.add(list);
        list = new AppsModel("Headspace");
        listModelList.add(list);
        list = new AppsModel("Calm");
        listModelList.add(list);
        list = new AppsModel("BeyondNow / BeyondBlue");
        listModelList.add(list);
        list = new AppsModel("notOK");
        listModelList.add(list);
        list = new AppsModel("What's Up");
        listModelList.add(list);
        list = new AppsModel("MoodKit");
        listModelList.add(list);
    }

    public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.MyViewHolder> {
        private List<AppsModel> listModelList;
        Context ctx;
        String apps;

        public AppsAdapter(List<AppsModel> listModelList, Context ctx, String apps) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.apps = apps;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AppsListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.apps_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            AppsModel listModel = listModelList.get(position);
            holder.binding.tvTitle.setText(listModel.getTitle());
            holder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), ResourceDetailsActivity.class);
                    i.putExtra("apps",apps);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            AppsListLayoutBinding binding;

            public MyViewHolder(AppsListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

    }
}