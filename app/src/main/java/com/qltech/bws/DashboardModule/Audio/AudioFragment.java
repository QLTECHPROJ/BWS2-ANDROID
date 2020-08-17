package com.qltech.bws.DashboardModule.Audio;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.DashboardModule.Audio.Adapters.InspiredAdapter;
import com.qltech.bws.DashboardModule.Audio.Adapters.MyDownloadsAdapter;
import com.qltech.bws.DashboardModule.Audio.Adapters.RecentlyPlayedAdapter;
import com.qltech.bws.DashboardModule.Audio.Adapters.RecommendedAdapter;
import com.qltech.bws.DashboardModule.Audio.Adapters.TopCategoriesAdapter;
import com.qltech.bws.DashboardModule.Models.InspiredModel;
import com.qltech.bws.DashboardModule.Models.MyDownloadsModel;
import com.qltech.bws.DashboardModule.Models.PopularModel;
import com.qltech.bws.DashboardModule.Models.RecentlyPlayedModel;
import com.qltech.bws.DashboardModule.Models.RecommendedModel;
import com.qltech.bws.DashboardModule.Models.TopCategoriesModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentAudioBinding;

import java.util.ArrayList;
import java.util.List;

public class AudioFragment extends Fragment {
    List<RecentlyPlayedModel> recentlyPlayedList = new ArrayList<>();
    List<MyDownloadsModel> myDownloadsList = new ArrayList<>();
    List<RecommendedModel> recommendedList = new ArrayList<>();
    List<InspiredModel> inspiredList = new ArrayList<>();
    List<PopularModel> popularList = new ArrayList<>();
    List<TopCategoriesModel> topCategoriesList = new ArrayList<>();
    FragmentAudioBinding binding;
    private AudioViewModel audioViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        audioViewModel =
                ViewModelProviders.of(this).get(AudioViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio, container, false);
        View view = binding.getRoot();
        audioViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                binding.tvExplore.setText(s);
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        float width = (displayMetrics.widthPixels / displayMetrics.density);
        float prop = ((width - 12 - 4) / 2) / (width - 12);


        MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                1, 1, 0.11f, 0);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.ivRestaurantImage.setImageResource(R.drawable.square_logo);

        binding.ivStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.ivStatus.setImageResource(R.drawable.ic_play_icon);
            }
        });

        RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(recentlyPlayedList, getActivity());
        RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvRecentlyPlayed.setLayoutManager(recentlyPlayed);
        binding.rvRecentlyPlayed.setItemAnimator(new DefaultItemAnimator());
        binding.rvRecentlyPlayed.setAdapter(recentlyPlayedAdapter);

        MyDownloadsAdapter myDownloadsAdapter = new MyDownloadsAdapter(myDownloadsList, getActivity());
        RecyclerView.LayoutManager myDownloads = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvMyDownloads.setLayoutManager(myDownloads);
        binding.rvMyDownloads.setItemAnimator(new DefaultItemAnimator());
        binding.rvMyDownloads.setAdapter(myDownloadsAdapter);


        RecommendedAdapter recommendedAdapter = new RecommendedAdapter(recommendedList, getActivity());
        RecyclerView.LayoutManager recommended = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvRecommended.setLayoutManager(recommended);
        binding.rvRecommended.setItemAnimator(new DefaultItemAnimator());
        binding.rvRecommended.setAdapter(recommendedAdapter);

        InspiredAdapter inspiredAdapter = new InspiredAdapter(inspiredList, getActivity());
        RecyclerView.LayoutManager inspired = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvInspired.setLayoutManager(inspired);
        binding.rvInspired.setItemAnimator(new DefaultItemAnimator());
        binding.rvInspired.setAdapter(inspiredAdapter);

        RecentlyPlayedAdapter recentlyPlayedAdapter1 = new RecentlyPlayedAdapter(recentlyPlayedList, getActivity());
        RecyclerView.LayoutManager recentlyPlayed1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvPopular.setLayoutManager(recentlyPlayed1);
        binding.rvPopular.setItemAnimator(new DefaultItemAnimator());
        binding.rvPopular.setAdapter(recentlyPlayedAdapter1);

        /*PopularAdapter popularAdapter = new PopularAdapter(popularList, getActivity());
        RecyclerView.LayoutManager popular = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvPopular.setLayoutManager(popular);
        binding.rvPopular.setItemAnimator(new DefaultItemAnimator());
        binding.rvPopular.setAdapter(popularAdapter);
*/
        TopCategoriesAdapter topCategoriesAdapter = new TopCategoriesAdapter(topCategoriesList, getActivity());
        RecyclerView.LayoutManager topCategories = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvTopCategories.setLayoutManager(topCategories);
        binding.rvTopCategories.setItemAnimator(new DefaultItemAnimator());
        binding.rvTopCategories.setAdapter(topCategoriesAdapter);
        prepareRecentlyPlayedData();
        prepareMyDownloadsData();
        prepareRecommendedData();
        prepareInspiredsData();
        preparePopularsData();
        prepareTopCategoriesData();
        return view;
    }
    private void prepareRecentlyPlayedData() {
        RecentlyPlayedModel list = new RecentlyPlayedModel("Love Thy Self");
        recentlyPlayedList.add(list);
        list = new RecentlyPlayedModel("Discipline");
        recentlyPlayedList.add(list);
        list = new RecentlyPlayedModel("Loving and...");
        recentlyPlayedList.add(list);
        list = new RecentlyPlayedModel("Love Thy Self");
        recentlyPlayedList.add(list);
        list = new RecentlyPlayedModel("Discipline");
        recentlyPlayedList.add(list);
        list = new RecentlyPlayedModel("Loving and...");
        recentlyPlayedList.add(list);
        list = new RecentlyPlayedModel("Love Thy Self");
        recentlyPlayedList.add(list);
        list = new RecentlyPlayedModel("Discipline");
        recentlyPlayedList.add(list);
        list = new RecentlyPlayedModel("Loving and...");
        recentlyPlayedList.add(list);
    }

    private void prepareMyDownloadsData() {
        MyDownloadsModel list = new MyDownloadsModel("Focus Fixer Progr...");
        myDownloadsList.add(list);
        list = new MyDownloadsModel("Motivation Program");
        myDownloadsList.add(list);
        list = new MyDownloadsModel("Focus Fixer Progr...");
        myDownloadsList.add(list);
        list = new MyDownloadsModel("Motivation Program");
        myDownloadsList.add(list);
        list = new MyDownloadsModel("Focus Fixer Progr...");
        myDownloadsList.add(list);
        list = new MyDownloadsModel("Motivation Program");
        myDownloadsList.add(list);
        list = new MyDownloadsModel("Focus Fixer Progr...");
        myDownloadsList.add(list);
        list = new MyDownloadsModel("Motivation Program");
        myDownloadsList.add(list);
        list = new MyDownloadsModel("Focus Fixer Progr...");
        myDownloadsList.add(list);
        list = new MyDownloadsModel("Motivation Program");
        myDownloadsList.add(list);
    }

    private void prepareRecommendedData() {
        RecommendedModel list = new RecommendedModel("Ultimate Spiritual...");
        recommendedList.add(list);
        list = new RecommendedModel("Body Dysmorphia");
        recommendedList.add(list);
        list = new RecommendedModel("Ultimate Spiritual...");
        recommendedList.add(list);
        list = new RecommendedModel("Body Dysmorphia");
        recommendedList.add(list);
        list = new RecommendedModel("Ultimate Spiritual...");
        recommendedList.add(list);
        list = new RecommendedModel("Body Dysmorphia");
        recommendedList.add(list);
        list = new RecommendedModel("Ultimate Spiritual...");
        recommendedList.add(list);
        list = new RecommendedModel("Body Dysmorphia");
        recommendedList.add(list);
        list = new RecommendedModel("Ultimate Spiritual...");
        recommendedList.add(list);
        list = new RecommendedModel("Body Dysmorphia");
        recommendedList.add(list);
    }

    private void prepareInspiredsData() {
        InspiredModel list = new InspiredModel("OCD");
        inspiredList.add(list);
        list = new InspiredModel("Passion");
        inspiredList.add(list);
        list = new InspiredModel("OCD");
        inspiredList.add(list);
        list = new InspiredModel("Passion");
        inspiredList.add(list);
        list = new InspiredModel("OCD");
        inspiredList.add(list);
        list = new InspiredModel("Passion");
        inspiredList.add(list);
        list = new InspiredModel("OCD");
        inspiredList.add(list);
        list = new InspiredModel("Passion");
        inspiredList.add(list);
        list = new InspiredModel("OCD");
        inspiredList.add(list);
        list = new InspiredModel("Passion");
        inspiredList.add(list);
    }

    private void preparePopularsData() {
        PopularModel list = new PopularModel("Combat...");
        popularList.add(list);
        list = new PopularModel("Powerful Pub...");
        popularList.add(list);
        list = new PopularModel("Say Goodby...");
        popularList.add(list);
        list = new PopularModel("Combat...");
        popularList.add(list);
        list = new PopularModel("Powerful Pub...");
        popularList.add(list);
        list = new PopularModel("Say Goodby...");
        popularList.add(list);
        list = new PopularModel("Combat...");
        popularList.add(list);
        list = new PopularModel("Powerful Pub...");
        popularList.add(list);
        list = new PopularModel("Say Goodby...");
        popularList.add(list);
        list = new PopularModel("Powerful Pub...");
        popularList.add(list);
    }

    private void prepareTopCategoriesData() {
        TopCategoriesModel list = new TopCategoriesModel("Mental Health");
        topCategoriesList.add(list);
        list = new TopCategoriesModel("Self Development");
        topCategoriesList.add(list);
        list = new TopCategoriesModel("Mental Health");
        topCategoriesList.add(list);
        list = new TopCategoriesModel("Self Development");
        topCategoriesList.add(list);
        list = new TopCategoriesModel("Mental Health");
        topCategoriesList.add(list);
        list = new TopCategoriesModel("Self Development");
        topCategoriesList.add(list);
        list = new TopCategoriesModel("Mental Health");
        topCategoriesList.add(list);
        list = new TopCategoriesModel("Self Development");
        topCategoriesList.add(list);
        list = new TopCategoriesModel("Mental Health");
        topCategoriesList.add(list);
        list = new TopCategoriesModel("Self Development");
        topCategoriesList.add(list);
    }
}