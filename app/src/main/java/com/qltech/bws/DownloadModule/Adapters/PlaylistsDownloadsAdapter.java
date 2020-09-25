package com.qltech.bws.DownloadModule.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.EncryptDecryptUtils.FileUtils;
import com.qltech.bws.R;
import com.qltech.bws.RoomDataBase.DatabaseClient;
import com.qltech.bws.RoomDataBase.DownloadAudioDetails;
import com.qltech.bws.RoomDataBase.DownloadPlaylistDetails;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.DownloadsLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import static com.qltech.bws.DashboardModule.Audio.AudioFragment.IsLock;

public class PlaylistsDownloadsAdapter extends RecyclerView.Adapter<PlaylistsDownloadsAdapter.MyViewHolder> {
    FragmentActivity ctx;
    String UserID;
    FrameLayout progressBarHolder;
    ImageView ImgV;
    List<DownloadAudioDetails> playlistWiseAudioDetails;
    List<DownloadAudioDetails> oneAudioDetailsList;
    private List<DownloadPlaylistDetails> listModelList;

    public PlaylistsDownloadsAdapter(List<DownloadPlaylistDetails> listModelList, FragmentActivity ctx, String UserID,
                                     FrameLayout progressBarHolder, ImageView ImgV) {
        this.listModelList = listModelList;
        this.ctx = ctx;
        this.UserID = UserID;
        this.progressBarHolder = progressBarHolder;
        this.ImgV = ImgV;
        playlistWiseAudioDetails = new ArrayList<>();
        oneAudioDetailsList = new ArrayList<>();
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
        holder.binding.tvTitle.setText(listModelList.get(position).getPlaylistName());
        if (listModelList.get(position).getTotalAudio().equalsIgnoreCase("") ||
                listModelList.get(position).getTotalAudio().equalsIgnoreCase("0") &&
                        listModelList.get(position).getTotalhour().equalsIgnoreCase("")
                        && listModelList.get(position).getTotalminute().equalsIgnoreCase("")) {
            holder.binding.tvTime.setText("0 Audio | 0h 0m");
        } else {
            if (listModelList.get(position).getTotalminute().equalsIgnoreCase("")) {
                holder.binding.tvTime.setText(listModelList.get(position).getTotalAudio() + " Audio | "
                        + listModelList.get(position).getTotalhour() + "h 0m");
            } else {
                holder.binding.tvTime.setText(listModelList.get(position).getTotalAudio() +
                        " Audios | " + listModelList.get(position).getTotalhour() + "h " + listModelList.get(position).getTotalminute() + "m");
            }

        }

        Glide.with(ctx).load(R.drawable.loading).asGif().into(ImgV);
        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                1, 1, 0.12f, 0);
        holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(ctx).load(listModelList.get(position).getPlaylistImage()).thumbnail(0.05f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

        holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsLock.equalsIgnoreCase("1")) {
                    BWSApplication.showToast("Please re-activate your membership plan", ctx);
                } else if (IsLock.equalsIgnoreCase("0")
                        || IsLock.equalsIgnoreCase("")) {
                    playlistWiseAudioDetails = GetMedia(listModelList.get(position).getPlaylistID());
                }
            }
        });
        holder.binding.llRemoveAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String PlaylistId = listModelList.get(position).getPlaylistID();
               /* if (BWSApplication.isNetworkConnected(ctx)) {
                    showProgressBar();
                    Call<SucessModel> listCall = APIClient.getClient().getRemoveAudioFromPlaylist(UserID, "", PlaylistId);
                    listCall.enqueue(new Callback<SucessModel>() {
                        @Override
                        public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                            if (response.isSuccessful()) {
                                hideProgressBar();
                                SucessModel listModel = response.body();
                                BWSApplication.showToast(listModel.getResponseMessage(), ctx);
                            }
                        }

                        @Override
                        public void onFailure(Call<SucessModel> call, Throwable t) {
                            hideProgressBar();
                        }
                    });
                } else {
                    BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                }*/
                playlistWiseAudioDetails = GetPlaylistMedia(listModelList.get(position).getPlaylistID());

                deleteDownloadFile(ctx.getApplicationContext(), PlaylistId, position);

            }
        });
    }

    public void GetSingleMedia(String AudioFile, Context ctx) {

        class GetMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                oneAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getLastIdByuId(AudioFile);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if (oneAudioDetailsList.size() != 0) {
                    if (oneAudioDetailsList.size() == 1) {
                        FileUtils.deleteDownloadedFile(ctx, oneAudioDetailsList.get(0).getName());
                    }
                }
                super.onPostExecute(aVoid);
            }
        }

        GetMedia sts = new GetMedia();
        sts.execute();
    }
    private void deleteDownloadFile(Context applicationContext, String PlaylistId, int position) {
        class DeleteMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(applicationContext)
                        .getaudioDatabase()
                        .taskDao()
                        .deleteByPlaylistId(PlaylistId);

                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                notifyItemRemoved(position);
                deletePlaylist(PlaylistId, position);
                super.onPostExecute(aVoid);
            }
        }

        DeleteMedia st = new DeleteMedia();
        st.execute();
    }

    private void deletePlaylist(String playlistId, int position) {
        class DeleteMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .deletePlaylist(playlistId);

                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {

                super.onPostExecute(aVoid);
            }
        }

        DeleteMedia st = new DeleteMedia();
        st.execute();
    }

    public List<DownloadAudioDetails> GetMedia(String playlistID) {

        playlistWiseAudioDetails = new ArrayList<>();
        class GetMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                playlistWiseAudioDetails = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getAllAudioByPlaylist(playlistID);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }

        GetMedia st = new GetMedia();
        st.execute();
        return playlistWiseAudioDetails;
    }public List<DownloadAudioDetails> GetPlaylistMedia(String playlistID) {

        playlistWiseAudioDetails = new ArrayList<>();
        class GetMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                playlistWiseAudioDetails = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getAllAudioByPlaylist(playlistID);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                for(int i = 0;i<playlistWiseAudioDetails.size();i++){
                    GetSingleMedia(playlistWiseAudioDetails.get(i).getAudioFile(),ctx.getApplicationContext());
                }
                super.onPostExecute(aVoid);
            }
        }

        GetMedia st = new GetMedia();
        st.execute();
        return playlistWiseAudioDetails;
    }

    private void hideProgressBar() {
        try {
            progressBarHolder.setVisibility(View.GONE);
            ImgV.setVisibility(View.GONE);
            ctx.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            progressBarHolder.setVisibility(View.VISIBLE);
            ctx.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            ImgV.setVisibility(View.VISIBLE);
            ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
