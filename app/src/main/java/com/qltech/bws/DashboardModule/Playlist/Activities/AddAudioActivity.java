package com.qltech.bws.DashboardModule.Playlist.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.qltech.bws.DashboardModule.Models.SerachListModel;
import com.qltech.bws.DashboardModule.Models.SuggestedModel;
import com.qltech.bws.DashboardModule.Playlist.Adapters.SerachListAdpater;
import com.qltech.bws.DashboardModule.Playlist.Adapters.SuggestedAdpater;
import com.qltech.bws.DownloadModule.Models.AudioListModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.ActivityAddAudioBinding;

import java.util.ArrayList;
import java.util.List;

public class AddAudioActivity extends AppCompatActivity {
    ActivityAddAudioBinding binding;
    List<SuggestedModel> listSuggestedList = new ArrayList<>();
    List<SerachListModel> listSerachList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_audio);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_audio);

        binding.searchView.onActionViewExpanded();
        EditText searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();
        closeButton.setOnClickListener(view -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);
        });

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SuggestedAdpater suggestedAdpater = new SuggestedAdpater(listSuggestedList, AddAudioActivity.this);
        RecyclerView.LayoutManager suggested = new LinearLayoutManager(AddAudioActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvSuggestedList.setLayoutManager(suggested);
        binding.rvSuggestedList.setItemAnimator(new DefaultItemAnimator());
        binding.rvSuggestedList.setAdapter(suggestedAdpater);

        SerachListAdpater serachListAdpater = new SerachListAdpater(listSerachList, AddAudioActivity.this);
        RecyclerView.LayoutManager serachList = new LinearLayoutManager(AddAudioActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvSerachList.setLayoutManager(serachList);
        binding.rvSerachList.setItemAnimator(new DefaultItemAnimator());
        binding.rvSerachList.setAdapter(serachListAdpater);

        prepareSuggestedData();
        prepareSerachListData();
    }
    private void prepareSuggestedData() {
        SuggestedModel list = new SuggestedModel("Focus Fixer Program", "12:37");
        listSuggestedList.add(list);
        list = new SuggestedModel("Motivation Program", "12:37");
        listSuggestedList.add(list);
        list = new SuggestedModel("Self-Discipline Program", "12:37");
        listSuggestedList.add(list);
        list = new SuggestedModel("Love Thy Self", "12:37");
        listSuggestedList.add(list);
        list = new SuggestedModel("I Can Attitude and Mind...", "12:37");
        listSuggestedList.add(list);
    }
    private void prepareSerachListData() {
        SerachListModel list = new SerachListModel("Home Maintenance", "12:37");
        listSerachList.add(list);
        list = new SerachListModel("Teenager Home Maintena...", "12:37");
        listSerachList.add(list);
    }
}