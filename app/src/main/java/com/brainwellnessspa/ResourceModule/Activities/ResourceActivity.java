package com.brainwellnessspa.ResourceModule.Activities;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
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

import com.brainwellnessspa.ResourceModule.Models.ResourceListModel;
import com.brainwellnessspa.ResourceModule.Models.SegmentResource;
import com.brainwellnessspa.Utility.APINewClient;
import com.google.android.material.tabs.TabLayout;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ResourceModule.Fragments.AppsFragment;
import com.brainwellnessspa.ResourceModule.Fragments.AudioBooksFragment;
import com.brainwellnessspa.ResourceModule.Fragments.DocumentariesFragment;
import com.brainwellnessspa.ResourceModule.Fragments.PodcastsFragment;
import com.brainwellnessspa.ResourceModule.Fragments.WebsiteFragment;
import com.brainwellnessspa.ResourceModule.Models.ResourceFilterModel;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityResourceBinding;
import com.brainwellnessspa.databinding.FilterListLayoutBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class ResourceActivity extends AppCompatActivity {
    ActivityResourceBinding binding;
    String USERID, Category = "", tabFlag = "1", CoUserID;
    Activity activity;
    int CurruntTab = 0;
    Dialog dialogBox;
    private long mLastClickTime = 0;
    RecyclerView rvFilterList;
    ImageView ivFilter;
    TextView tvAll;
    LayoutInflater li;
    View promptsView;
    Properties p, p4;
    ArrayList<String> section;
    GsonBuilder gsonBuilder;
    Gson gson;
    Context ctx;
    ResourceListModel resourceListModel;
    private int numStarted = 0;
    int stackStatus = 0;
    boolean myBackPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_resource);
        activity = ResourceActivity.this;
        ctx = ResourceActivity.this;
        binding.llBack.setOnClickListener(view -> {
            myBackPress = true;
            comefromDownload = "0";
            finish();
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        USERID = (shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, ""));
        CoUserID = (shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, ""));
        p4 = new Properties();
        p4.putValue("userId", USERID);
        section = new ArrayList<>();
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
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
                p = new Properties();
                p.putValue("userId", USERID);
                p.putValue("coUserId", CoUserID);
                if (tab.getPosition() == 0) {
                    tabFlag = CONSTANTS.FLAG_ONE;
                    p.putValue("resourceType", "Audio Books");
                    p4.putValue("resourceType", "Audio Books");
                } else if (tab.getPosition() == 1) {
                    tabFlag = CONSTANTS.FLAG_TWO;
                    p.putValue("resourceType", "Podcasts");
                    p4.putValue("resourceType", "Podcasts");
                } else if (tab.getPosition() == 2) {
                    tabFlag = CONSTANTS.FLAG_THREE;
                    p.putValue("resourceType", "Apps");
                    p4.putValue("resourceType", "Apps");
                } else if (tab.getPosition() == 3) {
                    tabFlag = CONSTANTS.FLAG_FOUR;
                    p.putValue("resourceType", "Websites");
                    p4.putValue("resourceType", "Websites");
                } else if (tab.getPosition() == 4) {
                    tabFlag = CONSTANTS.FLAG_FIVE;
                    p.putValue("resourceType", "Documentaries");
                    p4.putValue("resourceType", "Documentaries");
                }

                Call<ResourceListModel> listCalls = APINewClient.getClient().getResourceList(CoUserID, tabFlag, Category);
                listCalls.enqueue(new Callback<ResourceListModel>() {
                    @Override
                    public void onResponse(Call<ResourceListModel> call, Response<ResourceListModel> response) {
                        try {
                            ResourceListModel listModel = response.body();
                            if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                resourceListModel = listModel;
                                ArrayList<String> allResourceType = new ArrayList<>();
                                Gson gson = new Gson();
                                allResourceType.add("Audio Books");
                                allResourceType.add("Podcasts");
                                allResourceType.add("Apps");
                                allResourceType.add("Websites");
                                allResourceType.add("Documentaries");
                                p.putValue("allResourceType", gson.toJson(allResourceType));
                                ArrayList<SegmentResource> section1 = new ArrayList<>();
                                SegmentResource e = new SegmentResource();
                                Gson gsons = new Gson();
                                for (int i = 0; i < resourceListModel.getResponseData().size(); i++) {
                                    e.setResourceId(resourceListModel.getResponseData().get(i).getID());
                                    e.setResourceName(resourceListModel.getResponseData().get(i).getTitle());
                                    e.setAuthor(resourceListModel.getResponseData().get(i).getAuthor());
                                    e.setMasterCategory(resourceListModel.getResponseData().get(i).getMasterCategory());
                                    section1.add(e);
                                }
                                p.putValue("resources", gsons.toJson(section1));
                                BWSApplication.addToSegment("Resources Screen Viewed", p, CONSTANTS.screen);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResourceListModel> call, Throwable t) {
                    }
                });

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
            prepareData(rvFilterList, dialogBox, tvAll, ivFilter);
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
        prepareData(rvFilterList, dialogBox, tvAll, ivFilter);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        myBackPress = true;
        comefromDownload = "0";
        finish();
    }

    private void setAdapter() {
        if (BWSApplication.isNetworkConnected(activity)) {
            TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), binding.tabLayout.getTabCount());
            binding.viewPager.setAdapter(adapter);
            binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
            binding.viewPager.setCurrentItem(CurruntTab);
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity);
        }
    }

    void prepareData(RecyclerView rvFilterList, Dialog dialogBox, TextView tvAll, ImageView ivFilter) {
        try {
            if (BWSApplication.isNetworkConnected(ctx)) {
                Call<ResourceFilterModel> listCall = APINewClient.getClient().getResourceCatList(CoUserID);
                listCall.enqueue(new Callback<ResourceFilterModel>() {
                    @Override
                    public void onResponse(Call<ResourceFilterModel> call, Response<ResourceFilterModel> response) {
                        ResourceFilterModel listModel = response.body();
                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            ResourceFilterAdapter adapter = new ResourceFilterAdapter(listModel.getResponseData(), dialogBox, tvAll, ivFilter);
                            rvFilterList.setAdapter(adapter);
                            for (int i = 0; i < listModel.getResponseData().size(); i++) {
                                section.add(listModel.getResponseData().get(i).getCategoryName());
                            }
                            p4.putValue("allMasterCategory", gson.toJson(section));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResourceFilterModel> call, Throwable t) {
                    }
                });
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ResourceFilterAdapter extends RecyclerView.Adapter<ResourceFilterAdapter.MyViewHolder> {
        Dialog dialogBox;
        private List<ResourceFilterModel.ResponseData> listModel;
        private TextView tvAll;
        ImageView ivFilter;

        public ResourceFilterAdapter(List<ResourceFilterModel.ResponseData> listModel, Dialog dialogBox, TextView tvAll,
                                     ImageView ivFilter) {
            this.listModel = listModel;
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
                holder.binding.tvTitle.setTextColor(getResources().getColor(R.color.app_theme_color));
                holder.binding.ivFiltered.setVisibility(View.VISIBLE);
                Category = listModel.get(position).getCategoryName();
                setAdapter();
                dialogBox.dismiss();
                p4.putValue("masterCategory", listModel.get(position).getCategoryName());
                BWSApplication.addToSegment("Resources Filter Clicked", p4, CONSTANTS.screen);
            });
            if (listModel.get(position).getCategoryName().equalsIgnoreCase(Category)) {
                ivFilter.setVisibility(View.INVISIBLE);
                tvAll.setTextColor(getResources().getColor(R.color.black));
                holder.binding.tvTitle.setTextColor(getResources().getColor(R.color.app_theme_color));
                holder.binding.ivFiltered.setVisibility(View.VISIBLE);
            } else if (Category.equalsIgnoreCase("")) {
                tvAll.setTextColor(getResources().getColor(R.color.app_theme_color));
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

        public TabAdapter(FragmentManager fm, int totalTabs) {
            super(fm);
            this.totalTabs = totalTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    AudioBooksFragment audioBooksFragment = new AudioBooksFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("audio_books", "audio_books");
                    bundle.putString("CoUserID", CoUserID);
                    bundle.putString("Category", Category);
                    audioBooksFragment.setArguments(bundle);
                    return audioBooksFragment;
                case 1:
                    PodcastsFragment podcastsFragment = new PodcastsFragment();
                    bundle = new Bundle();
                    bundle.putString("podcasts", "podcasts");
                    bundle.putString("CoUserID", CoUserID);
                    bundle.putString("Category", Category);
                    podcastsFragment.setArguments(bundle);
                    return podcastsFragment;
                case 2:
                    AppsFragment appsFragment = new AppsFragment();
                    bundle = new Bundle();
                    bundle.putString("apps", "apps");
                    bundle.putString("CoUserID", CoUserID);
                    bundle.putString("Category", Category);
                    appsFragment.setArguments(bundle);
                    return appsFragment;
                case 3:
                    WebsiteFragment websiteFragment = new WebsiteFragment();
                    bundle = new Bundle();
                    bundle.putString("website", "website");
                    bundle.putString("CoUserID", CoUserID);
                    bundle.putString("Category", Category);
                    websiteFragment.setArguments(bundle);
                    return websiteFragment;
                case 4:
                    DocumentariesFragment documentariesFragment = new DocumentariesFragment();
                    bundle = new Bundle();
                    bundle.putString("documentaries", "documentaries");
                    bundle.putString("CoUserID", CoUserID);
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

    class AppLifecycleCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                stackStatus = 1;
                Log.e("APPLICATION", "APP IN FOREGROUND");
                //app went to foreground
            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            numStarted--;
            if (numStarted == 0) {
                if (!myBackPress) {
                    Log.e("APPLICATION", "Back press false");
                    stackStatus = 2;
                } else {
                    myBackPress = true;
                    stackStatus = 1;
                    Log.e("APPLICATION", "back press true ");
                }
                Log.e("APPLICATION", "App is in BACKGROUND");
                // app went to background
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationId);
                relesePlayer(getApplicationContext());
            } else {
                Log.e("Destroy", "Activity go in main activity");
            }
        }
    }

}