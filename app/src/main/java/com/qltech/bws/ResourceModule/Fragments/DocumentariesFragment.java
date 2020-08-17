package com.qltech.bws.ResourceModule.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qltech.bws.R;
import com.qltech.bws.ResourceModule.Activities.ResourceDetailsActivity;
import com.qltech.bws.ResourceModule.Models.DocumentariesModel;
import com.qltech.bws.ResourceModule.Models.PodcastsModel;
import com.qltech.bws.databinding.DocumentariesListLayoutBinding;
import com.qltech.bws.databinding.FragmentDocumentariesBinding;
import com.qltech.bws.databinding.PodcastsListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class DocumentariesFragment extends Fragment {
    FragmentDocumentariesBinding binding;
    String documentaries;
    List<DocumentariesModel> listModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_documentaries, container, false);
        View view = binding.getRoot();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            documentaries = bundle.getString("documentaries");
        }

        DocumentariesAdapter adapter = new DocumentariesAdapter(listModelList, getActivity(), documentaries);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDocumentariesList.setLayoutManager(mLayoutManager);
        binding.rvDocumentariesList.setItemAnimator(new DefaultItemAnimator());
        binding.rvDocumentariesList.setAdapter(adapter);
        prepareDocumentariesData();

        return view;
    }

    private void prepareDocumentariesData() {
        DocumentariesModel list = new DocumentariesModel("I Am", "2010");
        listModelList.add(list);
        list = new DocumentariesModel("Kumare", "2011");
        listModelList.add(list);
        list = new DocumentariesModel("I Am", "2010");
        listModelList.add(list);
        list = new DocumentariesModel("Kumare", "2011");
        listModelList.add(list);
        list = new DocumentariesModel("I Am", "2010");
        listModelList.add(list);
        list = new DocumentariesModel("Kumare", "2011");
        listModelList.add(list);
        list = new DocumentariesModel("I Am", "2010");
        listModelList.add(list);
        list = new DocumentariesModel("Kumare", "2011");
        listModelList.add(list);
    }

    public class DocumentariesAdapter extends RecyclerView.Adapter<DocumentariesAdapter.MyViewHolder> {
        private List<DocumentariesModel> listModelList;
        Context ctx;
        String documentaries;

        public DocumentariesAdapter(List<DocumentariesModel> listModelList, Context ctx, String documentaries) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.documentaries = documentaries;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            DocumentariesListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.documentaries_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            DocumentariesModel listModel = listModelList.get(position);
            holder.binding.tvTitle.setText(listModel.getTitle());
            holder.binding.tvCreator.setText(listModel.getSubTitle());
            holder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), ResourceDetailsActivity.class);
                    i.putExtra("documentaries", documentaries);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            DocumentariesListLayoutBinding binding;

            public MyViewHolder(DocumentariesListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}