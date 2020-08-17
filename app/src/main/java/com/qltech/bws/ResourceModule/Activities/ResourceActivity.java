package com.qltech.bws.ResourceModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.tabs.TabLayout;
import com.qltech.bws.DownloadModule.Models.DownloadsHistoryModel;
import com.qltech.bws.R;
import com.qltech.bws.ResourceModule.Adapters.ResourceFilterAdapter;
import com.qltech.bws.ResourceModule.Fragments.AppsFragment;
import com.qltech.bws.ResourceModule.Fragments.AudioBooksFragment;
import com.qltech.bws.ResourceModule.Fragments.DocumentariesFragment;
import com.qltech.bws.ResourceModule.Fragments.PodcastsFragment;
import com.qltech.bws.ResourceModule.Fragments.WebsiteFragment;
import com.qltech.bws.ResourceModule.Models.ResourceFilterModel;
import com.qltech.bws.databinding.ActivityResourceBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;

public class ResourceActivity extends AppCompatActivity {
    ActivityResourceBinding binding;
    List<ResourceFilterModel> listModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_resource);

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.viewPager.setOffscreenPageLimit(5);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio Books"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Podcasts"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Apps"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Website"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Documentaries"));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), this, binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(ResourceActivity.this);
                View promptsView = li.inflate(R.layout.resource_filter_menu, null);
                Dialog dialogBox;
                dialogBox = new Dialog(ResourceActivity.this, R.style.AppCompatAlertDialogStyle);
                Window window = dialogBox.getWindow();
                window.setBackgroundDrawableResource(android.R.color.transparent);
                window.requestFeature(window.FEATURE_NO_TITLE);
                dialogBox.setContentView(promptsView);
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                dialogBox.getWindow().getDecorView().setBottom(100);
                dialogBox.getWindow().getDecorView().setRight(100);
                dialogBox.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                wlp.y = 170;
                wlp.x = 33;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
                dialogBox.show();

                RecyclerView rvFilterList = promptsView.findViewById(R.id.rvFilterList);
                dialogBox.setOnKeyListener((dialog, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        finish();
                    }
                    return false;
                });
                ResourceFilterAdapter adapter = new ResourceFilterAdapter(listModelList, ResourceActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ResourceActivity.this);
                rvFilterList.setLayoutManager(mLayoutManager);
                rvFilterList.setItemAnimator(new DefaultItemAnimator());
                rvFilterList.setAdapter(adapter);
                prepareResourceFilterData();
            }
        });
    }

    public class TabAdapter extends FragmentStatePagerAdapter {
        int totalTabs;
        private Context myContext;
        Callback<DownloadsHistoryModel> downloadsHistoryModelCallback;

        public TabAdapter(FragmentManager fm, Context myContext, int totalTabs) {
            super(fm);
            this.myContext = myContext;
            this.totalTabs = totalTabs;
        }

        public TabAdapter(FragmentManager fm, Callback<DownloadsHistoryModel> transactionHistoryModelCallback, int totalTabs) {
            super(fm);
            this.downloadsHistoryModelCallback = transactionHistoryModelCallback;
            this.totalTabs = totalTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    AudioBooksFragment audioBooksFragment = new AudioBooksFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("audio_books", "audio_books");
                    audioBooksFragment.setArguments(bundle);
                    return audioBooksFragment;
                case 1:
                    PodcastsFragment podcastsFragment = new PodcastsFragment();
                    bundle = new Bundle();
                    bundle.putString("podcasts", "podcasts");
                    podcastsFragment.setArguments(bundle);
                    return podcastsFragment;
                case 2:
                    AppsFragment appsFragment = new AppsFragment();
                    bundle = new Bundle();
                    bundle.putString("apps", "apps");
                    appsFragment.setArguments(bundle);
                    return appsFragment;
                case 3:
                    WebsiteFragment websiteFragment = new WebsiteFragment();
                    bundle = new Bundle();
                    bundle.putString("website", "website");
                    websiteFragment.setArguments(bundle);
                    return websiteFragment;
                case 4:
                    DocumentariesFragment documentariesFragment = new DocumentariesFragment();
                    bundle = new Bundle();
                    bundle.putString("documentaries", "documentaries");
                    documentariesFragment.setArguments(bundle);
                    return documentariesFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return totalTabs;
        }
    }

    private void prepareResourceFilterData() {
        ResourceFilterModel list = new ResourceFilterModel("All");
        listModelList.add(list);
        list = new ResourceFilterModel("Mental Health");
        listModelList.add(list);
        list = new ResourceFilterModel("Teenager");
        listModelList.add(list);
        list = new ResourceFilterModel("Biography");
        listModelList.add(list);
        list = new ResourceFilterModel("Self-Development");
        listModelList.add(list);
        list = new ResourceFilterModel("Novel");
        listModelList.add(list);
        list = new ResourceFilterModel("Spiritual");
        listModelList.add(list);
        list = new ResourceFilterModel("Relationshiops");
        listModelList.add(list);
        list = new ResourceFilterModel("Cocaching");
        listModelList.add(list);
        list = new ResourceFilterModel("Wellness");
        listModelList.add(list);
        list = new ResourceFilterModel("Parenting");
        listModelList.add(list);
        list = new ResourceFilterModel("Financess");
        listModelList.add(list);
        list = new ResourceFilterModel("Addiction");
        listModelList.add(list);
    }
}