package com.qltech.bws.DashboardModule.Search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.DashboardModule.Adapters.SuggestionAudiosAdpater;
import com.qltech.bws.DashboardModule.Models.SuggestionAudiosModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    List<SuggestionAudiosModel> listModelList = new ArrayList<>();
    FragmentSearchBinding binding;
    private SearchViewModel searchViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        View view = binding.getRoot();

        binding.searchView.onActionViewExpanded();
        EditText searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();
        closeButton.setOnClickListener(v -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);
        });

        binding.ivStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.ivStatus.setImageResource(R.drawable.ic_play_icon);
            }
        });

        SuggestionAudiosAdpater suggestionAudiosAdpater = new SuggestionAudiosAdpater(listModelList, getActivity());
        RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvDownloadsList.setLayoutManager(recentlyPlayed);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());
        binding.rvDownloadsList.setAdapter(suggestionAudiosAdpater);

        searchViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                binding.textSearch.setText(s);
            }
        });
//        prepareSuggestionAudioData();
        return view;
    }

   /* private void prepareSuggestionAudioData() {
        SuggestionAudiosModel list = new SuggestionAudiosModel("Motivation Program", R.drawable.add_icon);
        listModelList.add(list);
        list = new SuggestionAudiosModel("Self-Discipline Program", R.drawable.add_icon);
        listModelList.add(list);
        list = new SuggestionAudiosModel("Love Thy Self", R.drawable.add_icon);
        listModelList.add(list);
        list = new SuggestionAudiosModel("I Can Attitude and Mind...", R.drawable.add_icon);
        listModelList.add(list);
    }*/
}