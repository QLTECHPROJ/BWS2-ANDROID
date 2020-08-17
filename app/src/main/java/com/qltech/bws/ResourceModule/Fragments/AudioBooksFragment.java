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
import com.qltech.bws.ResourceModule.Models.AudioBooksModel;
import com.qltech.bws.ResourceModule.Models.ListItem;
import com.qltech.bws.databinding.AudioBooksLayoutBinding;
import com.qltech.bws.databinding.BannerImageBinding;
import com.qltech.bws.databinding.FragmentAudioBooksBinding;

import java.util.ArrayList;
import java.util.List;

public class AudioBooksFragment extends Fragment {
    FragmentAudioBooksBinding binding;
    String audio_books;
    List<AudioBooksModel> listModelList = new ArrayList<>();
    List<ListItem> consolidatedList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio_books, container, false);
        View view = binding.getRoot();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            audio_books = bundle.getString("audio_books");
        }

        AudioBooksAdapter adapter = new AudioBooksAdapter(listModelList, getActivity(), audio_books);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        binding.rvAudioBooksList.setLayoutManager(manager);
        binding.rvAudioBooksList.setItemAnimator(new DefaultItemAnimator());
        binding.rvAudioBooksList.setAdapter(adapter);
        prepareAudioBooksData();
        return view;
    }

    private void prepareAudioBooksData() {
        AudioBooksModel list = new AudioBooksModel("Happiness Is A Choice", "Barry Neil Kaufman");
        listModelList.add(list);
        list = new AudioBooksModel("Man's Search For Meaning", "Victor E Frankl");
        listModelList.add(list);
        list = new AudioBooksModel("Mindset", "Barry Neil Kaufman");
        listModelList.add(list);
        list = new AudioBooksModel("Awaken The Giant Within", "Barry Neil Kaufman");
        listModelList.add(list);
        list = new AudioBooksModel("Happiness Is A Choice", "Barry Neil Kaufman");
        listModelList.add(list);
        list = new AudioBooksModel("Man's Search For Meaning", "Victor E Frankl");
        listModelList.add(list);
        list = new AudioBooksModel("Mindset", "Barry Neil Kaufman");
        listModelList.add(list);
        list = new AudioBooksModel("Awaken The Giant Within", "Barry Neil Kaufman");
        listModelList.add(list);
        list = new AudioBooksModel("Happiness Is A Choice", "Barry Neil Kaufman");
        listModelList.add(list);
        list = new AudioBooksModel("Man's Search For Meaning", "Victor E Frankl");
        listModelList.add(list);
        list = new AudioBooksModel("Mindset", "Barry Neil Kaufman");
        listModelList.add(list);
        list = new AudioBooksModel("Awaken The Giant Within", "Barry Neil Kaufman");
        listModelList.add(list);
        list = new AudioBooksModel("Happiness Is A Choice", "Barry Neil Kaufman");
        listModelList.add(list);
        list = new AudioBooksModel("Man's Search For Meaning", "Victor E Frankl");
        listModelList.add(list);
        list = new AudioBooksModel("Mindset", "Barry Neil Kaufman");
        listModelList.add(list);
        list = new AudioBooksModel("Awaken The Giant Within", "Barry Neil Kaufman");
        listModelList.add(list);
    }

    public class AudioBooksAdapter extends RecyclerView.Adapter<AudioBooksAdapter.MyViewHolder> {
        List<AudioBooksModel> consolidatedList;
        Context ctx;
        String audio_books;

        public AudioBooksAdapter(List<AudioBooksModel> consolidatedList, Context ctx, String audio_books) {
            this.consolidatedList = consolidatedList;
            this.ctx = ctx;
            this.audio_books = audio_books;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            /*RecyclerView.ViewHolder viewHolder = null;
            switch (viewType) {
                case ListItem.TYPE_GENERAL:
                    AudioBooksLayoutBinding v1 = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                            , R.layout.audio_books_layout, parent, false);
                    viewHolder = new GeneralViewHolder(v1);
                    break;

                case ListItem.TYPE_BANNER:
                    BannerImageBinding v2 = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                            R.layout.banner_image, parent, false);
                    viewHolder = new BannerViewHolder(v2);
                    break;
            }
            return viewHolder;*/
            AudioBooksLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.audio_books_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            /*switch (holder.getItemViewType()) {
                case ListItem.TYPE_GENERAL:
                    GeneralItem generalItem = (GeneralItem) consolidatedList.get(position);
                    GeneralViewHolder generalViewHolder = (GeneralViewHolder) holder;
                    generalViewHolder.binding.tvTitle.setText(generalItem.getPojoOfJsonArray().getTitle());
                    generalViewHolder.binding.tvCreator.setText(generalItem.getPojoOfJsonArray().getSubTitle());
                    generalViewHolder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getActivity(), ResourceDetailsActivity.class);
                            i.putExtra("audio_books", audio_books);
                            startActivity(i);
                        }
                    });
                    break;
                case ListItem.TYPE_BANNER:
                    break;
            }*/
            AudioBooksModel listModel = listModelList.get(position);
            holder.binding.tvTitle.setText(listModel.getTitle());
            holder.binding.tvCreator.setText(listModel.getSubTitle());
            holder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), ResourceDetailsActivity.class);
                    i.putExtra("audio_books",audio_books);
                    startActivity(i);
                }
            });

        }

      /*  @Override
        public int getItemViewType(int position) {
            return consolidatedList.get(position).getType();
        }
*/
        @Override
        public int getItemCount() {
            return consolidatedList != null ? consolidatedList.size() : 0;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            AudioBooksLayoutBinding binding;

            public MyViewHolder(AudioBooksLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

        public class BannerViewHolder extends RecyclerView.ViewHolder {
            BannerImageBinding binding;

            public BannerViewHolder(BannerImageBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}