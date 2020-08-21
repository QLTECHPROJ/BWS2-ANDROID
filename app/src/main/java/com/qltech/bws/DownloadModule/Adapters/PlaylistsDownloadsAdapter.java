package com.qltech.bws.DownloadModule.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DownloadModule.Activities.DownloadsActivity;
import com.qltech.bws.DownloadModule.Models.DownloadlistModel;
import com.qltech.bws.DownloadModule.Models.PlaylistListModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.DownloadsLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistsDownloadsAdapter extends RecyclerView.Adapter<PlaylistsDownloadsAdapter.MyViewHolder> {
    private List<DownloadlistModel.Playlist> listModelList;
    FragmentActivity ctx;
    String UserID;

    public PlaylistsDownloadsAdapter(List<DownloadlistModel.Playlist> listModelList, FragmentActivity ctx, String UserID) {
        this.listModelList = listModelList;
        this.ctx = ctx;
        this.UserID = UserID;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DownloadsLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.downloads_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvTitle.setText(listModelList.get(position).getAudiolist().get(position).getAudioName());
        holder.binding.tvTime.setText(listModelList.get(position).getAudiolist().get(position).getAudioDuration());

        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                1, 1, 0.12f, 0);
        holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(ctx).load(listModelList.get(position).getAudiolist().get(position).getImageFile()).thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

        holder.binding.llRemoveAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String PlaylistId = listModelList.get(position).getPlaylistId();
//                showProgressBar();
                if (BWSApplication.isNetworkConnected(ctx)) {
                    Call<SucessModel> listCall = APIClient.getClient().getRemoveAudioFromPlaylist(UserID, "", PlaylistId);
                    listCall.enqueue(new Callback<SucessModel>() {
                        @Override
                        public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                            if (response.isSuccessful()) {
//                                hideProgressBar();
                                SucessModel listModel = response.body();
                                Toast.makeText(ctx, listModel.getResponseMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<SucessModel> call, Throwable t) {
                        }
                    });
                } else {
                    Toast.makeText(ctx, ctx.getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        DownloadsLayoutBinding binding;

        public MyViewHolder(DownloadsLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
