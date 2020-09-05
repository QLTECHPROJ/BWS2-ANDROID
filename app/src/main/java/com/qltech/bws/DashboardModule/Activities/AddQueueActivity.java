package com.qltech.bws.DashboardModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.qltech.bws.DashboardModule.Adapters.DirectionAdapter;
import com.qltech.bws.DashboardModule.Models.AudioLikeModel;
import com.qltech.bws.DashboardModule.Models.DirectionModel;
import com.qltech.bws.DashboardModule.Models.DownloadPlaylistModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.Utility.MySpannable;
import com.qltech.bws.databinding.ActivityQueueBinding;
import com.stripe.android.net.RequestOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddQueueActivity extends AppCompatActivity {
    ActivityQueueBinding binding;
    String play, UserID, PlaylistId, AudioId, Like, Download, IsRepeat, IsShuffle;
    Context ctx;
    ArrayList<String> queue;

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {
        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + "<font   color=\"#F89552\">" + expandText + "</font>";
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + "<font color=\"#F89552\">" + expandText + "</font>";
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + "<font color=\"#F89552\">" + expandText + "</font>";
                }
                tv.setText(Html.fromHtml(text));
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(
                        addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                viewMore), TextView.BufferType.SPANNABLE);
            }
        });
    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv, final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, 4, "Read More...", true);
                    } else {
                        makeTextViewResizable(tv, 4, "Read More...", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
        }
        return ssb;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_queue);
        ctx = AddQueueActivity.this;

        Glide.with(ctx).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        SharedPreferences Status = getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");

        if (getIntent().getExtras() != null) {
            AudioId = getIntent().getStringExtra(CONSTANTS.ID);
        }

        prepareData();

        if (getIntent().hasExtra("play")) {
            play = getIntent().getStringExtra("play");
        } else {
            play = "";
        }

        if (play.equalsIgnoreCase("play")) {
            binding.llOptions.setVisibility(View.VISIBLE);
            binding.llDownload.setVisibility(View.VISIBLE);
            binding.llAddPlaylist.setVisibility(View.VISIBLE);
            binding.llAddQueue.setVisibility(View.VISIBLE);
            binding.llRemovePlaylist.setVisibility(View.GONE);
        } else {
            binding.llOptions.setVisibility(View.VISIBLE); /*GONE*/
            binding.llAddPlaylist.setVisibility(View.GONE);
            binding.llDownload.setVisibility(View.VISIBLE);
            binding.llAddQueue.setVisibility(View.VISIBLE);/*GONE*/
            binding.llRemovePlaylist.setVisibility(View.VISIBLE);
        }

        binding.llLike.setOnClickListener(view -> {
            if (BWSApplication.isNetworkConnected(ctx)) {
                showProgressBar();
                Call<AudioLikeModel> listCall = APIClient.getClient().getAudioLike(AudioId, UserID);
                listCall.enqueue(new Callback<AudioLikeModel>() {
                    @Override
                    public void onResponse(Call<AudioLikeModel> call, Response<AudioLikeModel> response) {
                        if (response.isSuccessful()) {
                            binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                            hideProgressBar();
                            AudioLikeModel model = response.body();
                            if (model.getResponseData().getFlag().equalsIgnoreCase("0")) {
                                binding.ivLike.setImageResource(R.drawable.ic_like_white_icon);
                                Like = "0";
                            } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                                binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                                Like = "1";
                            }
                            Toast.makeText(ctx, model.getResponseMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AudioLikeModel> call, Throwable t) {
                        hideProgressBar();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
            }
        });

        binding.llDownload.setOnClickListener(view -> {
            if (BWSApplication.isNetworkConnected(ctx)) {
                showProgressBar();
                Call<DownloadPlaylistModel> listCall = APIClient.getClient().getDownloadlistPlaylist(UserID, AudioId, PlaylistId);
                listCall.enqueue(new Callback<DownloadPlaylistModel>() {
                    @Override
                    public void onResponse(Call<DownloadPlaylistModel> call, Response<DownloadPlaylistModel> response) {
                        if (response.isSuccessful()) {
                            hideProgressBar();
                            DownloadPlaylistModel model = response.body();
                            if (model.getResponseData().getFlag().equalsIgnoreCase("0")
                                    || model.getResponseData().getFlag().equalsIgnoreCase("")) {
                                binding.llDownload.setClickable(true);
                                binding.llDownload.setEnabled(true);
                                binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                                Download = "0";
                            } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                                binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                                binding.ivDownloads.setColorFilter(Color.argb(99, 99, 99, 99));
                                binding.ivDownloads.setAlpha(255);
                                binding.llDownload.setClickable(false);
                                binding.llDownload.setEnabled(false);
                                Download = "1";
                            }
                            Toast.makeText(ctx, model.getResponseMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DownloadPlaylistModel> call, Throwable t) {
                        hideProgressBar();
                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
            }
        });

        binding.llRemovePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBar();
                if (BWSApplication.isNetworkConnected(ctx)) {
                    Call<SucessModel> listCall = APIClient.getClient().getRemoveAudioFromPlaylist(UserID, AudioId, PlaylistId);
                    listCall.enqueue(new Callback<SucessModel>() {
                        @Override
                        public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                            if (response.isSuccessful()) {
                                hideProgressBar();
                                SucessModel listModel = response.body();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<SucessModel> call, Throwable t) {
                            hideProgressBar();
                        }
                    });
                } else {
                    Toast.makeText(ctx, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent i = new Intent(ctx, PlayWellnessActivity.class);
                i.putExtra("Like", Like);
                i.putExtra("Download", Download);
                startActivity(i);*/
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
       /* Intent i = new Intent(ctx, PlayWellnessActivity.class);
        i.putExtra("Like", Like);
        i.putExtra("Download", Download);
        startActivity(i);*/
        finish();
    }

    private void prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            showProgressBar();
            Call<List<DirectionModel.ResponseData>> listCall = APIClient.getClient().getAudioDetailLists(AudioId);
            listCall.enqueue(new Callback<List<DirectionModel.ResponseData>>() {
                @Override
                public void onResponse(Call<List<DirectionModel.ResponseData>> call, Response<List<DirectionModel.ResponseData>> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        List<DirectionModel.ResponseData> directionModel = response.body();

                        binding.tvSubDec.setText(directionModel.get(0).getAudioDescription());
                        binding.tvSubDec.post(() -> {
                            int lineCount = binding.tvSubDec.getLineCount();
                            if (lineCount >= 4) {
                                makeTextViewResizable(binding.tvSubDec, 4, "Read More...", true);
                                binding.tvSubDec.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        final Dialog dialog = new Dialog(ctx);
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.full_desc_layout);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                        final TextView tvDesc = dialog.findViewById(R.id.tvDesc);
                                        final RelativeLayout tvClose = dialog.findViewById(R.id.tvClose);
                                        tvDesc.setText(directionModel.get(0).getAudioDescription());

                                        dialog.setOnKeyListener((v, keyCode, event) -> {
                                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                                dialog.dismiss();
                                                return true;
                                            }
                                            return false;
                                        });

                                        tvClose.setOnClickListener(v -> dialog.dismiss());

                                        dialog.show();
                                        dialog.setCancelable(false);
                                    }
                                });
                            } else {

                            }
                        });

                        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 40,
                                1, 1, 0.6f, 40);
                        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
                        Glide.with(ctx).load(directionModel.get(0).getImageFile())
                                .thumbnail(0.05f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);

                        Like = directionModel.get(0).getLike();
                        Download = directionModel.get(0).getDownload();

                        binding.tvName.setText(directionModel.get(0).getName());
                        binding.tvDesc.setText(directionModel.get(0).getAudioSubCategory());
                        binding.tvDuration.setText(directionModel.get(0).getAudioDuration());

                        if (directionModel.get(0).getAudioDirection().equalsIgnoreCase("")) {
                            binding.tvSubDire.setText("");
                            binding.tvSubDire.setVisibility(View.GONE);
                            binding.tvDire.setVisibility(View.GONE);
                            binding.rvDirlist.setVisibility(View.GONE);
                        } else {
                            binding.tvSubDire.setText(directionModel.get(0).getAudioDirection());
                            binding.tvSubDire.setVisibility(View.VISIBLE);
                            binding.tvDire.setVisibility(View.VISIBLE);
                            binding.rvDirlist.setVisibility(View.VISIBLE);
                        }


                        if (directionModel.get(0).getLike().equalsIgnoreCase("1")) {
                            binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                        } else if (!directionModel.get(0).getLike().equalsIgnoreCase("0")) {
                            binding.ivLike.setImageResource(R.drawable.ic_like_white_icon);
                        }

                        if (directionModel.get(0).getDownload().equalsIgnoreCase("1")) {
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                            binding.ivDownloads.setColorFilter(Color.argb(99, 99, 99, 99));
                            binding.ivDownloads.setAlpha(255);
                            binding.llDownload.setClickable(false);
                            binding.llDownload.setEnabled(false);
                        } else if (!directionModel.get(0).getDownload().equalsIgnoreCase("")) {
                            binding.llDownload.setClickable(true);
                            binding.llDownload.setEnabled(true);
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                        }

                        binding.llAddPlaylist.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(ctx, AddPlaylistActivity.class);
                                i.putExtra("AudioId", AudioId);
                                startActivity(i);
                            }
                        });

                        binding.llViewQueue.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(ctx, ViewQueueActivity.class);
                                startActivity(i);
                            }
                        });

                        String[] elements = directionModel.get(0).getAudiomastercat().split(",");
                        List<String> direction = Arrays.asList(elements);

                        DirectionAdapter directionAdapter = new DirectionAdapter(direction, ctx);
                        RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                        binding.rvDirlist.setLayoutManager(recentlyPlayed);
                        binding.rvDirlist.setItemAnimator(new DefaultItemAnimator());
                        binding.rvDirlist.setAdapter(directionAdapter);

                        binding.llAddQueue.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                    } else {
                    }
                }

                @Override
                public void onFailure(Call<List<DirectionModel.ResponseData>> call, Throwable t) {
                    hideProgressBar();
                    Toast.makeText(ctx, t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("getMessagegetMessage", "" + t.getMessage());
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}