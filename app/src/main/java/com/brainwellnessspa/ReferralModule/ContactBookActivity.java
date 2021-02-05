package com.brainwellnessspa.ReferralModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.ActivityContactBookBinding;

public class ContactBookActivity extends AppCompatActivity {
    ActivityContactBookBinding binding;
    Context ctx;
    Activity activity;
    EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_book);
        ctx = ContactBookActivity.this;
        activity = ContactBookActivity.this;

        binding.searchView.onActionViewExpanded();
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.dark_blue_gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();
        closeButton.setOnClickListener(v -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);

        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                binding.searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                /*try {
                    if (adpater2 != null) {
                        adpater2.getFilter().filter(search);
                        SearchFlag = search;
                        Log.e("searchsearch", "" + search);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                return false;
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
        binding.rvFavContactList.setLayoutManager(mLayoutManager);
        binding.rvFavContactList.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvContactList.setLayoutManager(mLayoutManager1);
        binding.rvContactList.setItemAnimator(new DefaultItemAnimator());
        prepareData();
    }

    private void prepareData() {

    }
}