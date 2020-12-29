package com.brainwellnessspa.ResourceModule.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ResourceModule.Fragments.AppsFragment;
import com.brainwellnessspa.ResourceModule.Fragments.AudioBooksFragment;
import com.brainwellnessspa.ResourceModule.Fragments.DocumentariesFragment;
import com.brainwellnessspa.ResourceModule.Fragments.PodcastsFragment;
import com.brainwellnessspa.ResourceModule.Fragments.WebsiteFragment;
import com.brainwellnessspa.ResourceModule.Models.ResourceFilterModel;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityResourceBinding;
import com.brainwellnessspa.databinding.FilterListLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;

public class ResourceActivity extends AppCompatActivity {
    ActivityResourceBinding binding;
    String UserID, Category = "";
    Activity activity;
    int CurruntTab = 0;
    Dialog dialogBox;
    private long mLastClickTime = 0;
    RecyclerView rvFilterList;
    ImageView ivFilter;
    TextView tvAll;
    LayoutInflater li;
    View promptsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_resource);
        activity = ResourceActivity.this;
        binding.llBack.setOnClickListener(view -> {
            ComeScreenAccount = 1;
            comefromDownload = "0";
            finish();
        });
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        binding.viewPager.setOffscreenPageLimit(5);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio Books"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Podcasts"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Apps"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Websites"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Documentaries"));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
                CurruntTab = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        setAdapter();
        li = LayoutInflater.from(activity);
        promptsView = li.inflate(R.layout.resource_filter_menu, null);
        dialogBox = new Dialog(activity, R.style.AppCompatAlertDialogStyle);
        Window window = dialogBox.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.requestFeature(Window.FEATURE_NO_TITLE);
        dialogBox.setContentView(promptsView);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        dialogBox.getWindow().getDecorView().setBottom(100);
        dialogBox.getWindow().getDecorView().setRight(100);
        dialogBox.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        wlp.y = 190;
        wlp.x = 33;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialogBox.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                finish();
            }
            return false;
        });

        rvFilterList = promptsView.findViewById(R.id.rvFilterList);
        ivFilter = promptsView.findViewById(R.id.ivFilter);
        tvAll = promptsView.findViewById(R.id.tvAll);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        rvFilterList.setLayoutManager(mLayoutManager);
        rvFilterList.setItemAnimator(new DefaultItemAnimator());
        binding.ivFilter.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            prepareData(activity, rvFilterList, dialogBox, tvAll, ivFilter);
            tvAll.setOnClickListener(view1 -> {
                Category = "";
                setAdapter();
                dialogBox.dismiss();
            });
            dialogBox.show();
        });
    }

    @Override
    protected void onResume() {
        prepareData(activity, rvFilterList, dialogBox, tvAll, ivFilter);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        ComeScreenAccount = 1;
        comefromDownload = "0";
        finish();
    }

    private void setAdapter() {
        if (BWSApplication.isNetworkConnected(activity)) {
            TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), this, binding.tabLayout.getTabCount());
            binding.viewPager.setAdapter(adapter);
            binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
            binding.viewPager.setCurrentItem(CurruntTab);
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity);
        }
    }

    void prepareData(Context ctx, RecyclerView rvFilterList, Dialog dialogBox, TextView tvAll, ImageView ivFilter) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            Call<ResourceFilterModel> listCall = APIClient.getClient().getResourcFilterLists(UserID);
            listCall.enqueue(new Callback<ResourceFilterModel>() {
                @Override
                public void onResponse(Call<ResourceFilterModel> call, Response<ResourceFilterModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            ResourceFilterModel listModel = response.body();
                            ResourceFilterAdapter adapter = new ResourceFilterAdapter(listModel.getResponseData(), ctx, dialogBox, tvAll, ivFilter);
                            rvFilterList.setAdapter(adapter);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResourceFilterModel> call, Throwable t) {
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getApplicationContext());
        }
    }

    public class ResourceFilterAdapter extends RecyclerView.Adapter<ResourceFilterAdapter.MyViewHolder> {
        Context ctx;
        Dialog dialogBox;
        private List<ResourceFilterModel.ResponseData> listModel;
        private TextView tvAll;
        ImageView ivFilter;

        public ResourceFilterAdapter(List<ResourceFilterModel.ResponseData> listModel, Context ctx, Dialog dialogBox, TextView tvAll,
                                     ImageView ivFilter) {
            this.listModel = listModel;
            this.ctx = ctx;
            this.dialogBox = dialogBox;
            this.tvAll = tvAll;
            this.ivFilter = ivFilter;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            FilterListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.filter_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            ivFilter.setVisibility(View.INVISIBLE);
            holder.binding.tvTitle.setText(listModel.get(position).getCategoryName());
            holder.binding.llMainLayout.setOnClickListener(view -> {
                holder.binding.tvTitle.setTextColor(getResources().getColor(R.color.blue));
                holder.binding.ivFiltered.setVisibility(View.VISIBLE);
                Category = listModel.get(position).getCategoryName();
                setAdapter();
                dialogBox.dismiss();
            });
            if (listModel.get(position).getCategoryName().equalsIgnoreCase(Category)) {
                ivFilter.setVisibility(View.INVISIBLE);
                tvAll.setTextColor(getResources().getColor(R.color.black));
                holder.binding.tvTitle.setTextColor(getResources().getColor(R.color.blue));
                holder.binding.ivFiltered.setVisibility(View.VISIBLE);
            } else if (Category.equalsIgnoreCase("")) {
                tvAll.setTextColor(getResources().getColor(R.color.blue));
                ivFilter.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return listModel.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            FilterListLayoutBinding binding;

            public MyViewHolder(FilterListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class TabAdapter extends FragmentStatePagerAdapter {
        int totalTabs;
        private Context myContext;

        public TabAdapter(FragmentManager fm, Context myContext, int totalTabs) {
            super(fm);
            this.myContext = myContext;
            this.totalTabs = totalTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    AudioBooksFragment audioBooksFragment = new AudioBooksFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("audio_books", "audio_books");
                    bundle.putString("UserID", UserID);
                    bundle.putString("Category", Category);
                    audioBooksFragment.setArguments(bundle);
                    return audioBooksFragment;
                case 1:
                    PodcastsFragment podcastsFragment = new PodcastsFragment();
                    bundle = new Bundle();
                    bundle.putString("podcasts", "podcasts");
                    bundle.putString("UserID", UserID);
                    bundle.putString("Category", Category);
                    podcastsFragment.setArguments(bundle);
                    return podcastsFragment;
                case 2:
                    AppsFragment appsFragment = new AppsFragment();
                    bundle = new Bundle();
                    bundle.putString("apps", "apps");
                    bundle.putString("UserID", UserID);
                    bundle.putString("Category", Category);
                    appsFragment.setArguments(bundle);
                    return appsFragment;
                case 3:
                    WebsiteFragment websiteFragment = new WebsiteFragment();
                    bundle = new Bundle();
                    bundle.putString("website", "website");
                    bundle.putString("UserID", UserID);
                    bundle.putString("Category", Category);
                    websiteFragment.setArguments(bundle);
                    return websiteFragment;
                case 4:
                    DocumentariesFragment documentariesFragment = new DocumentariesFragment();
                    bundle = new Bundle();
                    bundle.putString("documentaries", "documentaries");
                    bundle.putString("UserID", UserID);
                    bundle.putString("Category", Category);
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
}