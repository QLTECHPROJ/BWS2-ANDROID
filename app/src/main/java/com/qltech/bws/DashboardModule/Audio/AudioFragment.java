package com.qltech.bws.DashboardModule.Audio;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qltech.bws.DashboardModule.Audio.Adapters.InspiredAdapter;
import com.qltech.bws.DashboardModule.Audio.Adapters.MyDownloadsAdapter;
import com.qltech.bws.DashboardModule.Audio.Adapters.RecentlyPlayedAdapter;
import com.qltech.bws.DashboardModule.Audio.Adapters.RecommendedAdapter;
import com.qltech.bws.DashboardModule.Audio.Adapters.TopCategoriesAdapter;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentAudioBinding;
import com.qltech.bws.databinding.MainAudioLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioFragment extends Fragment {
    FragmentAudioBinding binding;
    private AudioViewModel audioViewModel;
    String UserID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        audioViewModel =
                ViewModelProviders.of(this).get(AudioViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio, container, false);
        View view = binding.getRoot();
        audioViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

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

        prepareData();
        return view;
    }

    private void prepareData() {
        showProgressBar();
        if (BWSApplication.isNetworkConnected(getActivity())) {
            Call<MainAudioModel> listCall = APIClient.getClient().getMainAudioLists(UserID);
            listCall.enqueue(new Callback<MainAudioModel>() {
                @Override
                public void onResponse(Call<MainAudioModel> call, Response<MainAudioModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        MainAudioModel listModel = response.body();
                        MainAudioListAdapter adapter = new MainAudioListAdapter(listModel.getResponseData(), getActivity(),getActivity());
                        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                        binding.rvMainAudioList.setLayoutManager(manager);
                        binding.rvMainAudioList.setItemAnimator(new DefaultItemAnimator());
                        binding.rvMainAudioList.setAdapter(adapter);
                    } else {
                        hideProgressBar();
                    }
                }

                @Override
                public void onFailure(Call<MainAudioModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }
    }

    public class MainAudioListAdapter extends RecyclerView.Adapter<MainAudioListAdapter.MyViewHolder> {
        private List<MainAudioModel.ResponseData> listModelList;
        Context ctx;
        FragmentActivity activity;

        public MainAudioListAdapter(List<MainAudioModel.ResponseData> listModelList, Context ctx, FragmentActivity activity) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.activity = activity;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MainAudioLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.main_audio_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if (listModelList.get(position).getDetails() != null &&
                    listModelList.get(position).getDetails().size() > 6) {
                holder.binding.tvViewAll.setVisibility(View.VISIBLE);
            } else {
                holder.binding.tvViewAll.setVisibility(View.GONE);
            }

            if (listModelList.get(position).getDetails().size() == 0) {
                holder.binding.llMainLayout.setVisibility(View.GONE);
            } else {
                holder.binding.llMainLayout.setVisibility(View.VISIBLE);
                holder.binding.tvTitle.setText(listModelList.get(position).getView());
                if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.my_download))) {
                    MyDownloadsAdapter myDownloadsAdapter = new MyDownloadsAdapter(listModelList.get(position).getDetails(), getActivity(),activity);
                    RecyclerView.LayoutManager myDownloads = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(myDownloads);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(myDownloadsAdapter);
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.my_like))) {
                    holder.binding.llMainLayout.setVisibility(View.GONE);
                    /*RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(listModelList.get(position).getDetails(), getActivity());
                    RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recentlyPlayed);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recentlyPlayedAdapter);*/
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.recently_played))) {
                    RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(listModelList.get(position).getDetails(), getActivity(),activity);
                    RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recentlyPlayed);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recentlyPlayedAdapter);
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.recommended))) {
                    RecommendedAdapter recommendedAdapter = new RecommendedAdapter(listModelList.get(position).getDetails(), getActivity(),activity);
                    RecyclerView.LayoutManager recommended = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recommended);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recommendedAdapter);
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.get_inspired))) {
                    InspiredAdapter inspiredAdapter = new InspiredAdapter(listModelList.get(position).getDetails(), getActivity(),activity);
                    RecyclerView.LayoutManager inspired = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(inspired);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(inspiredAdapter);
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.popular))) {
                    RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(listModelList.get(position).getDetails(), getActivity(),activity);
                    RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recentlyPlayed);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recentlyPlayedAdapter);

                           /*PopularAdapter popularAdapter = new PopularAdapter(popularList, getActivity());
                                RecyclerView.LayoutManager popular = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                                binding.rvMainAudioList.setLayoutManager(popular);
                                binding.rvMainAudioList.setItemAnimator(new DefaultItemAnimator());
                                binding.rvMainAudioList.setAdapter(popularAdapter); */
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.top_categories))) {
                    TopCategoriesAdapter topCategoriesAdapter = new TopCategoriesAdapter(listModelList.get(position).getDetails(), getActivity(),activity);
                    RecyclerView.LayoutManager topCategories = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(topCategories);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(topCategoriesAdapter);
                }
            }

            if (!listModelList.get(position).getDetails().equals("")) {

            }
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            MainAudioLayoutBinding binding;

            public MyViewHolder(MainAudioLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareData();
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}