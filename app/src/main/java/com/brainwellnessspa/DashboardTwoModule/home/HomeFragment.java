package com.brainwellnessspa.DashboardTwoModule.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Adapters.DirectionAdapter;
import com.brainwellnessspa.DashboardTwoModule.AddAudioActivity;
import com.brainwellnessspa.DashboardTwoModule.AddPlaylistActivity;
import com.brainwellnessspa.DashboardTwoModule.Model.AudioDetailModel;
import com.brainwellnessspa.DashboardTwoModule.Model.HomeDataModel;
import com.brainwellnessspa.DashboardTwoModule.Model.SucessModel;
import com.brainwellnessspa.DashboardTwoModule.Model.PlaylistDetailsModel;
import com.brainwellnessspa.ManageModule.ManageAudioPlaylistActivity;
import com.brainwellnessspa.ManageModule.ManageAudioPlaylistActivity;
import com.brainwellnessspa.ManageModule.SleepTimeActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.UserModuleTwo.Activities.AddProfileActivity;
import com.brainwellnessspa.UserModuleTwo.Activities.CreateAccountActivity;
import com.brainwellnessspa.UserModuleTwo.Models.AddedUserListModel;
import com.brainwellnessspa.UserModuleTwo.Models.VerifyPinModel;
import com.brainwellnessspa.Utility.APINewClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.FragmentHomeBinding;
import com.brainwellnessspa.databinding.MultipleProfileChangeLayoutBinding;
import com.brainwellnessspa.databinding.UserListCustomLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    UserListAdapter adapter;
    String CoUSERID, USERID, UserName, PlaylistImage = "", PlaylistType = "", UserID, PlaylistID = "", Download = "", Liked = "", PlaylistDesc = "", PlaylistName = "", ScreenView = "", TotalAudio = "", Totalhour = "", Totalminute = "";
    private EditText[] editTexts;
    boolean tvSendOTPbool = true;
    boolean myBackPress = false;
    String AudioId;
    int SongListSize = 0;
    private long mLastClickTime = 0;
    BottomSheetDialog mBottomSheetDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        CoUSERID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        UserName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "");
        binding.tvName.setText(UserName);
        homeViewModel.getText().observe(getViewLifecycleOwner(), s -> {
//                textView.setText(s);
        });

        binding.llBottomView.setOnClickListener(v -> {
            UserListCustomLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity())
                    , R.layout.user_list_custom_layout, null, false);
            mBottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BaseBottomSheetDialog);
            mBottomSheetDialog.setContentView(layoutBinding.getRoot());
            BottomSheetBehavior mBottomSheetBehavior = new BottomSheetBehavior();
            mBottomSheetBehavior.setHideable(true);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            mBottomSheetDialog.show();
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            layoutBinding.rvUserList.setLayoutManager(mLayoutManager);
            layoutBinding.rvUserList.setItemAnimator(new DefaultItemAnimator());


            prepareUserData(layoutBinding.rvUserList, layoutBinding.progressBar);

            layoutBinding.llAddNewUser.setOnClickListener(v1 -> {
                Intent i = new Intent(getActivity(), AddProfileActivity.class);
                startActivity(i);
            });
        });

        binding.llPlayer.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), /*ManageAudioPlaylistActivity*/SleepTimeActivity.class);
            startActivity(i);
        });

        binding.llClick.setOnClickListener(v -> {
//            TODO Mansi Hint This code is Playlist Detail Dialog
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.open_playlist_detail_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final TextView tvDesc = dialog.findViewById(R.id.tvDesc);
            final TextView tvReadMore = dialog.findViewById(R.id.tvReadMore);
            final TextView tvSubDec = dialog.findViewById(R.id.tvSubDec);
            final TextView tvTitleDec = dialog.findViewById(R.id.tvTitleDec);
            final TextView tvTime = dialog.findViewById(R.id.tvTime);
            final TextView tvName = dialog.findViewById(R.id.tvName);
            final LinearLayout llDownload = dialog.findViewById(R.id.llDownload);
            final LinearLayout llOptions = dialog.findViewById(R.id.llOptions);
            final LinearLayout llRename = dialog.findViewById(R.id.llRename);
            final LinearLayout llDelete = dialog.findViewById(R.id.llDelete);
            final LinearLayout llFind = dialog.findViewById(R.id.llFind);
            final LinearLayout llLikes = dialog.findViewById(R.id.llLikes);
            final ImageView ivRestaurantImage = dialog.findViewById(R.id.ivRestaurantImage);
            final ImageView ivLike = dialog.findViewById(R.id.ivLike);
            final LinearLayout llAddPlaylist = dialog.findViewById(R.id.llAddPlaylist);
            final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
            final FrameLayout progressBarHolder = dialog.findViewById(R.id.progressBarHolder);
            final RecyclerView rvDirlist = dialog.findViewById(R.id.rvDirlist);

            dialog.setOnKeyListener((v1, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            });

            if (BWSApplication.isNetworkConnected(getActivity())) {
                BWSApplication.showProgressBar(progressBar, progressBarHolder, getActivity());
                Call<PlaylistDetailsModel> listCall = APINewClient.getClient().getPlaylistDetail(CoUSERID, "34");
                listCall.enqueue(new Callback<PlaylistDetailsModel>() {
                    @Override
                    public void onResponse(Call<PlaylistDetailsModel> call, Response<PlaylistDetailsModel> response) {
                        try {
                            if (response.isSuccessful()) {
                                BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                                PlaylistDetailsModel model = response.body();
                                llDownload.setVisibility(View.VISIBLE);
//                                playlistSongsList = model.getResponseData().getPlaylistSongs();
//                                downloadPlaylistDetails = new DownloadPlaylistDetails();
//                                downloadPlaylistDetails.setPlaylistID(model.getResponseData().getPlaylistID());
//                                downloadPlaylistDetails.setPlaylistName(model.getResponseData().getPlaylistName());
//                                downloadPlaylistDetails.setPlaylistDesc(model.getResponseData().getPlaylistDesc());
//                                downloadPlaylistDetails.setIsReminder(model.getResponseData().getIsReminder());
//                                downloadPlaylistDetails.setPlaylistMastercat(model.getResponseData().getPlaylistMastercat());
//                                downloadPlaylistDetails.setPlaylistSubcat(model.getResponseData().getPlaylistSubcat());
//                                downloadPlaylistDetails.setPlaylistImage(model.getResponseData().getPlaylistImage());
//                                downloadPlaylistDetails.setPlaylistImageDetails(model.getResponseData().getPlaylistImageDetail());
//                                downloadPlaylistDetails.setTotalAudio(model.getResponseData().getTotalAudio());
//                                downloadPlaylistDetails.setTotalDuration(model.getResponseData().getTotalDuration());
//                                downloadPlaylistDetails.setTotalhour(model.getResponseData().getTotalhour());
//                                downloadPlaylistDetails.setTotalminute(model.getResponseData().getTotalminute());
//                                downloadPlaylistDetails.setCreated(model.getResponseData().getCreated());
//                                downloadPlaylistDetails.setDownload(model.getResponseData().getDownload());
//                                downloadPlaylistDetails.setLike(model.getResponseData().getLike());
                                tvName.setText(model.getResponseData().getPlaylistName());

                                PlaylistDesc = model.getResponseData().getPlaylistDesc();
                                PlaylistName = model.getResponseData().getPlaylistName();
                                PlaylistID = model.getResponseData().getPlaylistID();
                                TotalAudio = model.getResponseData().getTotalAudio();
                                Totalhour = model.getResponseData().getTotalhour();
                                Totalminute = model.getResponseData().getTotalminute();
                                if (model.getResponseData().getPlaylistMastercat().equalsIgnoreCase("")) {
                                    tvDesc.setVisibility(View.GONE);
                                } else {
                                    tvDesc.setVisibility(View.VISIBLE);
                                    tvDesc.setText(model.getResponseData().getPlaylistMastercat());
                                }

//                                Properties p = new Properties();
//                                p.putValue("userId", UserID);
//                                p.putValue("playlistId", model.getResponseData().getPlaylistID());
//                                p.putValue("playlistName", model.getResponseData().getPlaylistName());
//                                p.putValue("playlistDescription", PlaylistDesc);
//                                if (PlaylistType.equalsIgnoreCase("1")) {
//                                    p.putValue("playlistType", "Created");
//                                } else if (PlaylistType.equalsIgnoreCase("0")) {
//                                    p.putValue("playlistType", "Default");
//                                }
//                                if (model.getResponseData().getTotalhour().equalsIgnoreCase("")) {
//                                    p.putValue("playlistDuration", "0h " + model.getResponseData().getTotalminute() + "m");
//                                } else if (model.getResponseData().getTotalminute().equalsIgnoreCase("")) {
//                                    p.putValue("playlistDuration", model.getResponseData().getTotalhour() + "h 0m");
//                                } else {
//                                    p.putValue("playlistDuration", model.getResponseData().getTotalhour() + "h " + model.getResponseData().getTotalminute() + "m");
//                                }
//
//                                p.putValue("audioCount", model.getResponseData().getTotalAudio());
//                                p.putValue("source", ScreenView);
//                                BWSApplication.addToSegment("Playlist Details Viewed", p, CONSTANTS.screen);

                                if (model.getResponseData().getTotalAudio().equalsIgnoreCase("") ||
                                        model.getResponseData().getTotalAudio().equalsIgnoreCase("0") &&
                                                model.getResponseData().getTotalhour().equalsIgnoreCase("")
                                                && model.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                    tvTime.setText("0 Audio | 0h 0m");
                                } else {
                                    if (model.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                        tvTime.setText(model.getResponseData().getTotalAudio() + " Audio | "
                                                + model.getResponseData().getTotalhour() + "h 0m");
                                    } else {
                                        tvTime.setText(model.getResponseData().getTotalAudio() + " Audio | "
                                                + model.getResponseData().getTotalhour() + "h " + model.getResponseData().getTotalminute() + "m");
                                    }
                                }

                                if (model.getResponseData().getCreated().equalsIgnoreCase("1")) {
                                    llOptions.setVisibility(View.GONE);
                                    llRename.setVisibility(View.VISIBLE);
                                    llDelete.setVisibility(View.VISIBLE);
                                    llFind.setVisibility(View.GONE);
                                    llLikes.setVisibility(View.VISIBLE);
                                } else if (model.getResponseData().getCreated().equalsIgnoreCase("0")) {
                                    llOptions.setVisibility(View.VISIBLE);
                                    llRename.setVisibility(View.GONE);
                                    llDelete.setVisibility(View.GONE);
                                    llLikes.setVisibility(View.VISIBLE);

                                    if (Liked.equalsIgnoreCase("1")) {
                                        llFind.setVisibility(View.GONE);
                                    } else if (Liked.equalsIgnoreCase("0") || Liked.equalsIgnoreCase("")) {
                                        llFind.setVisibility(View.VISIBLE);
                                    }
                                }

                                MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 20,
                                        1, 1, 0.54f, 20);
                                ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                                ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                                ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
                                if (!model.getResponseData().getPlaylistImage().equalsIgnoreCase("")) {
                                    Glide.with(getActivity()).load(model.getResponseData().getPlaylistImage()).thumbnail(0.05f)
                                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(10))).priority(Priority.HIGH)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(ivRestaurantImage);
                                } else {
                                    Glide.with(getActivity()).load(R.drawable.ic_playlist_bg).thumbnail(0.05f)
                                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(10))).priority(Priority.HIGH)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(ivRestaurantImage);
                                }

//                            getDownloadData();
                                SongListSize = model.getResponseData().getPlaylistSongs().size();
//                            getMediaByPer(PlaylistID,SongListSize);
//                            SongListSize = model.getResponseData().getPlaylistSongs().size();
                                Download = model.getResponseData().getDownload();
                                llAddPlaylist.setVisibility(View.VISIBLE);
//                            getDownloadData();
                                if (model.getResponseData().getLike().equalsIgnoreCase("1")) {
                                    ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                                } else if (model.getResponseData().getLike().equalsIgnoreCase("0") ||
                                        model.getResponseData().getLike().equalsIgnoreCase("")) {
                                    ivLike.setImageResource(R.drawable.ic_like_white_icon);
                                }

                                if (model.getResponseData().getPlaylistDesc().equalsIgnoreCase("")) {
                                    tvTitleDec.setVisibility(View.GONE);
                                    tvSubDec.setVisibility(View.GONE);
                                } else {
                                    tvTitleDec.setVisibility(View.VISIBLE);
                                    tvSubDec.setVisibility(View.VISIBLE);
                                }

                                tvSubDec.setText(model.getResponseData().getPlaylistDesc());
                                int linecount = tvSubDec.getLineCount();
                                if (linecount >= 4) {
                                    tvReadMore.setVisibility(View.VISIBLE);
                                } else {
                                    tvReadMore.setVisibility(View.GONE);
                                }

//                                if (model.getResponseData().getDownload().equalsIgnoreCase("1")) {
//                                    binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
//                                    binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
//                                    binding.tvDownload.setTextColor(getResources().getColor(R.color.light_gray));
//                                    binding.llDownload.setClickable(false);
//                                    binding.llDownload.setEnabled(false);
//                                } else if (!model.getResponseData().getDownload().equalsIgnoreCase("")) {
//                                    binding.llDownload.setClickable(true);
//                                    binding.llDownload.setEnabled(true);
//                                    binding.ivDownloads.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
//                                    binding.tvDownload.setTextColor(getResources().getColor(R.color.white));
//                                    binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
//                                }
//                               binding.llDownload.setOnClickListener(view -> {
//                                if (BWSApplication.isNetworkConnected(ctx)) {
//                                    BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
//                                    Call<DownloadPlaylistModel> listCall13 = null;
//                                    listCall13 = APIClient.getClient().getDownloadlistPlaylist(UserID, "", PlaylistID);
//                                    listCall13.enqueue(new Callback<DownloadPlaylistModel>() {
//                                        @Override
//                                        public void onResponse(Call<DownloadPlaylistModel> call13, Response<DownloadPlaylistModel> response13) {
//                                            if (response13.isSuccessful()) {
//                                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
//                                                DownloadPlaylistModel model1 = response13.body();
//                                                BWSApplication.showToast(model1.getResponseMessage(), ctx);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<DownloadPlaylistModel> call13, Throwable t) {
//                                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
//                                        }
//                                    });
//
//                                } else {
//                                    Toast.makeText(getApplicationContext(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
//                                }
//                            });

                                String[] elements = model.getResponseData().getPlaylistSubcat().split(",");
                                List<String> direction = Arrays.asList(elements);
                                DirectionAdapter directionAdapter = new DirectionAdapter(direction, getActivity());
                                RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                                rvDirlist.setLayoutManager(recentlyPlayed);
                                rvDirlist.setItemAnimator(new
                                        DefaultItemAnimator());
                                rvDirlist.setAdapter(directionAdapter);
                                String PlaylistID = model.getResponseData().getPlaylistID();


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<PlaylistDetailsModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                    }
                });
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }

            llDelete.setOnClickListener(view43 -> {
                myBackPress = true;
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                    BWSApplication.showToast("Currently this playlist is in player,so you can't delete this playlist as of now", getActivity());
                } else {
                    final Dialog dialoged = new Dialog(getActivity());
                    dialoged.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialoged.setContentView(R.layout.delete_playlist);
                    dialoged.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                    dialoged.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                    final TextView tvGoBack = dialoged.findViewById(R.id.tvGoBack);
                    final TextView tvHeader = dialoged.findViewById(R.id.tvHeader);
                    final RelativeLayout tvconfirm = dialog.findViewById(R.id.tvconfirm);
                    tvHeader.setText("Are you sure you want to delete " + PlaylistName + "  playlist?");
                    dialoged.setOnKeyListener((v44, keyCode, event) -> {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
//                            Fragment playlistFragment = new PlaylistFragment();
//                            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
//                            fragmentManager1.beginTransaction()
//                                    .add(R.id.flContainer, playlistFragment)
//                                    .commit();
//                            Bundle bundle = new Bundle();
//                            playlistFragment.setArguments(bundle);
                            return true;
                        }
                        return false;
                    });

                    tvconfirm.setOnClickListener(v33 -> {
                        myBackPress = true;
                        if (BWSApplication.isNetworkConnected(getActivity())) {
                            BWSApplication.showProgressBar(progressBar, progressBarHolder, getActivity());
                            Call<SucessModel> listCall12 = APINewClient.getClient().getDeletePlaylist(UserID, PlaylistID);
                            listCall12.enqueue(new Callback<SucessModel>() {
                                @Override
                                public void onResponse(Call<SucessModel> call12, Response<SucessModel> response12) {
                                    try {
                                        if (response12.isSuccessful()) {
//                                            MyPlaylistIds = "";
//                                            deleteFrg = 1;
                                            BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                                            SucessModel listModel = response12.body();
                                            dialoged.dismiss();
                                            BWSApplication.showToast(listModel.getResponseMessage(), getContext());
//                                            finish();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<SucessModel> call12, Throwable t) {
                                    BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                                }
                            });
                        } else {
                            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                        }
                    });

                    tvGoBack.setOnClickListener(v22 -> dialoged.dismiss());
                    dialoged.show();
                    dialoged.setCanceledOnTouchOutside(true);
                    dialoged.setCancelable(true);
                }
            });

            llRename.setOnClickListener(view22 -> {
                myBackPress = true;
                final Dialog dialogs = new Dialog(getActivity());
                dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogs.setContentView(R.layout.create_palylist);
                dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                dialogs.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                EditText edtCreate = dialogs.findViewById(R.id.edtCreate);
                TextView tvCancel = dialogs.findViewById(R.id.tvCancel);
                TextView tvHeading = dialogs.findViewById(R.id.tvHeading);
                Button btnSendCode = dialogs.findViewById(R.id.btnSendCode);
                tvHeading.setText(R.string.Rename_your_playlist);
                btnSendCode.setText(R.string.Save);
                edtCreate.requestFocus();
                edtCreate.setText(PlaylistName);
                int position1 = edtCreate.getText().length();
                Editable editObj = edtCreate.getText();
                Selection.setSelection(editObj, position1);
                dialog.setOnKeyListener((v23, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                });
                edtCreate.addTextChangedListener(new PopupTextWatcher(edtCreate, btnSendCode));

                btnSendCode.setOnClickListener(view1 -> {
                    myBackPress = true;
                    if (BWSApplication.isNetworkConnected(getActivity())) {
                        BWSApplication.showProgressBar(progressBar, progressBarHolder, getActivity());
                        Call<SucessModel> listCall1 = APINewClient.getClient().getRenameNewPlaylist(CoUSERID, /*PlaylistID*/"34", edtCreate.getText().toString());
                        listCall1.enqueue(new Callback<SucessModel>() {
                            @Override
                            public void onResponse(Call<SucessModel> call1, Response<SucessModel> response1) {
                                try {
                                    BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                                    SucessModel listModel = response1.body();
                                    if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                        BWSApplication.showToast(listModel.getResponseMessage(), getActivity());
//                                        Properties p = new Properties();
//                                        p.putValue("userId", UserID);
//                                        p.putValue("playlistId", PlaylistID);
//                                        p.putValue("playlistName", PlaylistName);
//                                        p.putValue("playlistDescription", PlaylistDesc);
//                                        if (PlaylistType.equalsIgnoreCase("1")) {
//                                            p.putValue("playlistType", "Created");
//                                        } else if (PlaylistType.equalsIgnoreCase("0")) {
//                                            p.putValue("playlistType", "Default");
//                                        }
//                                        if (Totalhour.equalsIgnoreCase("")) {
//                                            p.putValue("playlistDuration", "0h " + Totalminute + "m");
//                                        } else if (Totalminute.equalsIgnoreCase("")) {
//                                            p.putValue("playlistDuration", Totalhour + "h 0m");
//                                        } else {
//                                            p.putValue("playlistDuration", Totalhour + "h " + Totalminute + "m");
//                                        }
//                                        p.putValue("audioCount", TotalAudio);
//                                        p.putValue("source", ScreenView);
//                                        BWSApplication.addToSegment("Playlist Rename Clicked", p, CONSTANTS.track);
                                        dialogs.dismiss();
//                                        getActivity().finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<SucessModel> call1, Throwable t) {
                                BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                            }
                        });
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                    }

                });
                tvCancel.setOnClickListener(v34 -> dialogs.dismiss());
                dialogs.show();
                dialogs.setCanceledOnTouchOutside(true);
                dialogs.setCancelable(true);
            });
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);

//       TODO Mansi  Hint This code is Create playlist Dialog
         /* final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.create_palylist);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final EditText edtCreate = dialog.findViewById(R.id.edtCreate);
            final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
            final Button btnSendCode = dialog.findViewById(R.id.btnSendCode);
            final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
            final FrameLayout progressBarHolder = dialog.findViewById(R.id.progressBarHolder);
            edtCreate.requestFocus();
            TextWatcher popupTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String number = edtCreate.getText().toString().trim();
                    if (!number.isEmpty()) {
                        btnSendCode.setEnabled(true);
                        btnSendCode.setTextColor(getResources().getColor(R.color.light_black));
                        btnSendCode.setBackgroundResource(R.drawable.white_round_cornor);
                    } else {
                        btnSendCode.setEnabled(false);
                        btnSendCode.setTextColor(getResources().getColor(R.color.white));
                        btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };

            edtCreate.addTextChangedListener(popupTextWatcher);
            dialog.setOnKeyListener((v1, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            });

            btnSendCode.setOnClickListener(view1 -> {
                BWSApplication.showProgressBar(progressBar, progressBarHolder, getActivity());
                if (BWSApplication.isNetworkConnected(getActivity())) {
                    Call<CreateNewPlaylistModel> listCall = APINewClient.getClient().getCreatePlaylist(CoUSERID, edtCreate.getText().toString());
                    listCall.enqueue(new Callback<CreateNewPlaylistModel>() {
                        @Override
                        public void onResponse(Call<CreateNewPlaylistModel> call, Response<CreateNewPlaylistModel> response) {
                            try {
                                CreateNewPlaylistModel listModel = response.body();
                                if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                    BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                                    if (listModel.getResponseData().getIscreate().equalsIgnoreCase("0")) {
                                        BWSApplication.showToast(listModel.getResponseMessage(), getActivity());
                                    } else if (listModel.getResponseData().getIscreate().equalsIgnoreCase("1") ||
                                            listModel.getResponseData().getIscreate().equalsIgnoreCase("")) {
//                                        ComeScreenMyPlaylist = 1;
//                                        callMyPlaylistsFragment("1", listModel.getResponseData().getId(), listModel.getResponseData().getName(), "", "0", "Your Created");
                                        dialog.dismiss();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<CreateNewPlaylistModel> call, Throwable t) {
                            BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                        }
                    });
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                }
            });
            tvCancel.setOnClickListener(v13 -> dialog.dismiss());
            dialog.show();
            dialog.setCancelable(false);*/


        });

        binding.llProfile.setOnClickListener(v -> {
//            TODO Mansi  Hint This code is Audio Detail Dialog
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.open_detail_page_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            final TextView tvTitleDec = dialog.findViewById(R.id.tvTitleDec);
            final TextView tvSubDec = dialog.findViewById(R.id.tvSubDec);
            final TextView tvReadMore = dialog.findViewById(R.id.tvReadMore);
            final TextView tvSubDire = dialog.findViewById(R.id.tvSubDire);
            final TextView tvDire = dialog.findViewById(R.id.tvDire);
            final TextView tvDesc = dialog.findViewById(R.id.tvDesc);
            final TextView tvDuration = dialog.findViewById(R.id.tvDuration);
            final ImageView ivRestaurantImage = dialog.findViewById(R.id.ivRestaurantImage);
            final ImageView ivLike = dialog.findViewById(R.id.ivLike);
            final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
            final FrameLayout progressBarHolder = dialog.findViewById(R.id.progressBarHolder);
            final RelativeLayout cvImage = dialog.findViewById(R.id.cvImage);
            final LinearLayout llLike = dialog.findViewById(R.id.llLike);
            final LinearLayout llAddPlaylist = dialog.findViewById(R.id.llAddPlaylist);
            final LinearLayout llAddQueue = dialog.findViewById(R.id.llAddQueue);
            final LinearLayout llDownload = dialog.findViewById(R.id.llDownload);
            final LinearLayout llRemovePlaylist = dialog.findViewById(R.id.llRemovePlaylist);
            final LinearLayout llShuffle = dialog.findViewById(R.id.llShuffle);
            final LinearLayout llRepeat = dialog.findViewById(R.id.llRepeat);
            final LinearLayout llViewQueue = dialog.findViewById(R.id.llViewQueue);
            final RecyclerView rvDirlist = dialog.findViewById(R.id.rvDirlist);
            if (BWSApplication.isNetworkConnected(getActivity())) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.invalidate();
                Call<AudioDetailModel> listCall = APINewClient.getClient().getAudioDetail(CoUSERID, "10");
                listCall.enqueue(new Callback<AudioDetailModel>() {
                    @Override
                    public void onResponse(Call<AudioDetailModel> call, Response<AudioDetailModel> response) {
                        try {
                            progressBar.setVisibility(View.GONE);
                            AudioDetailModel listModel = response.body();
                            cvImage.setVisibility(View.VISIBLE);
                            llLike.setVisibility(View.GONE);
                            llAddPlaylist.setVisibility(View.VISIBLE);
                            llAddQueue.setVisibility(View.GONE);
                            llDownload.setVisibility(View.VISIBLE);
                            llShuffle.setVisibility(View.VISIBLE);
                            llRepeat.setVisibility(View.VISIBLE);
                            llViewQueue.setVisibility(View.VISIBLE);
                            AudioId = listModel.getResponseData().get(0).getId();
                            llRemovePlaylist.setVisibility(View.VISIBLE);

//                        if (comeFrom.equalsIgnoreCase("myPlayList") || comeFrom.equalsIgnoreCase("myLikeAudioList")) {
//                            binding.llRemovePlaylist.setVisibility(View.GONE);
//                        } else {
//                            if (MyPlaylist.equalsIgnoreCase("myPlaylist")) {
//                                binding.llRemovePlaylist.setVisibility(View.VISIBLE);
//                            } else {
//                                binding.llRemovePlaylist.setVisibility(View.GONE);
//                            }
//                        }

                            try {
                                Glide.with(getActivity()).load(listModel.getResponseData().get(0).getImageFile()).thumbnail(0.05f)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(12))).priority(Priority.HIGH)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(ivRestaurantImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            binding.tvName.setText(listModel.getResponseData().get(0).getName());
                            if (listModel.getResponseData().get(0).getAudioDescription().equalsIgnoreCase("")) {
                                tvTitleDec.setVisibility(View.GONE);
                                tvSubDec.setVisibility(View.GONE);
                            } else {
                                tvTitleDec.setVisibility(View.VISIBLE);
                                tvSubDec.setVisibility(View.VISIBLE);
                            }

                            tvSubDec.setText(listModel.getResponseData().get(0).getAudioDescription());
                            int linecount = tvSubDec.getLineCount();
                            if (linecount >= 4) {
                                tvReadMore.setVisibility(View.VISIBLE);
                            } else {
                                tvReadMore.setVisibility(View.GONE);
                            }
                            if (listModel.getResponseData().get(0).getAudiomastercat().equalsIgnoreCase("")) {
                                tvDesc.setVisibility(View.GONE);
                            } else {
                                tvDesc.setVisibility(View.VISIBLE);
                                tvDesc.setText(listModel.getResponseData().get(0).getAudiomastercat());
                            }
                            tvDuration.setText(listModel.getResponseData().get(0).getAudioDuration());

                            if (listModel.getResponseData().get(0).getAudioDirection().equalsIgnoreCase("")) {
                                tvSubDire.setText("");
                                tvSubDire.setVisibility(View.GONE);
                                tvDire.setVisibility(View.GONE);
                            } else {
                                tvSubDire.setText(listModel.getResponseData().get(0).getAudioDirection());
                                tvSubDire.setVisibility(View.VISIBLE);
                                tvDire.setVisibility(View.VISIBLE);
                            }

//                            if (listModel.getResponseData().get(0).getLike().equalsIgnoreCase("1")) {
//                                ivLike.setImageResource(R.drawable.ic_fill_like_icon);
//                            } else if (!listModel.getResponseData().get(0).getLike().equalsIgnoreCase("0")) {
//                                ivLike.setImageResource(R.drawable.ic_like_white_icon);
//                            }

                            tvReadMore.setOnClickListener(v12 -> {
                                final Dialog dialog1 = new Dialog(getActivity());
                                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog1.setContentView(R.layout.full_desc_layout);
                                dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                final TextView tvDesc = dialog1.findViewById(R.id.tvDesc);
                                final RelativeLayout tvClose = dialog1.findViewById(R.id.tvClose);
                                tvDesc.setText(listModel.getResponseData().get(0).getAudioDescription());

                                dialog1.setOnKeyListener((v3, keyCode, event) -> {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        dialog1.dismiss();
                                        return true;
                                    }
                                    return false;
                                });

                                tvClose.setOnClickListener(v14 -> dialog1.dismiss());
                                dialog1.show();
                                dialog1.setCancelable(false);
                            });

                            if (listModel.getResponseData().get(0).getAudioSubCategory().equalsIgnoreCase("")) {
                                rvDirlist.setVisibility(View.GONE);
                            } else {
                                rvDirlist.setVisibility(View.VISIBLE);
                                String[] elements = listModel.getResponseData().get(0).getAudioSubCategory().split(",");
                                List<String> direction = Arrays.asList(elements);

                                DirectionAdapter directionAdapter = new DirectionAdapter(direction, getActivity());
                                RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                                rvDirlist.setLayoutManager(recentlyPlayed);
                                rvDirlist.setItemAnimator(new DefaultItemAnimator());
                                rvDirlist.setAdapter(directionAdapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<AudioDetailModel> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            llAddPlaylist.setOnClickListener(v13 -> {
                if (BWSApplication.isNetworkConnected(getActivity())) {
                    BWSApplication.showProgressBar(progressBar, progressBarHolder, getActivity());
                    Call<SucessModel> listCall = APINewClient.getClient().RemoveAudio(CoUSERID, /*AudioId*/"10", /*PlaylistId*/"34");
                    listCall.enqueue(new Callback<SucessModel>() {
                        @Override
                        public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                            try {
                                myBackPress = true;
                                if (response.isSuccessful()) {
                                    BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                                    SucessModel listModel = response.body();
                                    SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
//                                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
//                                int pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
//
//                                if (audioPlay) {
//                                    if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
//                                        Gson gson12 = new Gson();
//                                        String json12 = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson12));
//                                        Type type1 = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
//                                        }.getType();
//                                        ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList1 = gson12.fromJson(json12, type1);
//
//                                        if (!comeFrom.equalsIgnoreCase("")) {
//                                            mData.remove(position);
//                                            String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
//                                            int oldpos = pos;
//                                            if (pID.equalsIgnoreCase(PlaylistId)) {
//                                                if (mData.size() != 0) {
//                                                    if (pos == position && position < mData.size() - 1) {
//                                                        pos = pos;
//                                                    } else if (pos == position && position == mData.size() - 1) {
//                                                        pos = 0;
//                                                    } else if (pos < position && pos < mData.size() - 1) {
//                                                        pos = pos;
//                                                    } else if (pos < position && pos == mData.size() - 1) {
//                                                        pos = pos;
//                                                    } else if (pos > position && pos == mData.size()) {
//                                                        pos = pos - 1;
//                                                    }
//                                                    SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
//                                                    SharedPreferences.Editor editor = sharedd.edit();
//                                                    Gson gson = new Gson();
//                                                    String json = gson.toJson(mData);
//                                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json);
//                                                    editor.putInt(CONSTANTS.PREF_KEY_position, pos);
//                                                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
//                                                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
//                                                    editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistId);
//                                                    editor.putString(CONSTANTS.PREF_KEY_myPlaylist, myPlaylist);
//                                                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
//                                                    editor.commit();
//                                                    Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
//                                                    }.getType();
//                                                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
//                                                    listSize = arrayList.size();
//                                                    for (int i = 0; i < listSize; i++) {
//                                                        MainPlayModel mainPlayModel = new MainPlayModel();
//                                                        mainPlayModel.setID(arrayList.get(i).getID());
//                                                        mainPlayModel.setName(arrayList.get(i).getName());
//                                                        mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
//                                                        mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistID());
//                                                        mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
//                                                        mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
//                                                        mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
//                                                        mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
//                                                        mainPlayModel.setLike(arrayList.get(i).getLike());
//                                                        mainPlayModel.setDownload(arrayList.get(i).getDownload());
//                                                        mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
//                                                        mainPlayModelList.add(mainPlayModel);
//                                                    }
//                                                    SharedPreferences sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
//                                                    SharedPreferences.Editor editor1 = sharedz.edit();
//                                                    Gson gsonz = new Gson();
//                                                    String jsonz = gsonz.toJson(mainPlayModelList);
//                                                    editor1.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
//                                                    editor1.commit();
//                                                    if (player != null) {
//                                                        player.removeMediaItem(oldpos);
//                                                        player.setPlayWhenReady(true);
//                                                    }
//                                                    finish();
//                                                }
//                                            }
//                                            finish();
//                                        } else {
//                                            mainPlayModelList.remove(pos);
//                                            arrayList1.remove(pos);
//                                            String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
//                                            if (pID.equalsIgnoreCase(PlaylistId)) {
//                                                int oldpos = pos;
//                                                if (mainPlayModelList.size() != 0) {
//                                                    if (pos < mainPlayModelList.size() - 1) {
//                                                        pos = pos;
//                                                    } else if (pos == mainPlayModelList.size() - 1) {
//                                                        pos = 0;
//                                                    } else if (pos == mainPlayModelList.size()) {
//                                                        pos = 0;
//                                                    } else if (pos > mainPlayModelList.size()) {
//                                                        pos = pos - 1;
//                                                    }
//                                                    SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
//                                                    SharedPreferences.Editor editor = sharedd.edit();
//                                                    Gson gson = new Gson();
//                                                    String json = gson.toJson(mainPlayModelList);
//                                                    String json1 = gson.toJson(arrayList1);
//                                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
//                                                    editor.putString(CONSTANTS.PREF_KEY_audioList, json);
//                                                    editor.putInt(CONSTANTS.PREF_KEY_position, pos);
//                                                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
//                                                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
//                                                    editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistId);
//                                                    editor.putString(CONSTANTS.PREF_KEY_myPlaylist, myPlaylist);
//                                                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
//                                                    editor.commit();
////                                                if(mainPlayModelList.size()==1){
////                                                    miniPlayer = 1;
////                                                    audioClick = true;
////                                                    callNewPlayerRelease();
////                                                }else {
//                                                    if (player != null) {
//                                                        player.removeMediaItem(oldpos);
//                                                    }
////                                                }
//                                                    Intent i = new Intent(ctx, AudioPlayerActivity.class);
//                                                    i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                                    ctx.startActivity(i);
//                                                    finish();
//                                                    overridePendingTransition(0, 0);
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<SucessModel> call, Throwable t) {
                            BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                        }
                    });
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                }
            });
            llAddPlaylist.setOnClickListener(view11 -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
//                playerpos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
                myBackPress = true;
//                comeAddPlaylist = 2;
                Intent i = new Intent(getActivity(), AddPlaylistActivity.class);
                i.putExtra("AudioId", AudioId);
                i.putExtra("ScreenView", "Audio Details Screen");
                i.putExtra("PlaylistID", "");
                i.putExtra("PlaylistName", "");
                i.putExtra("PlaylistImage", "");
                i.putExtra("PlaylistType", "");
                i.putExtra("Liked", "0");
                startActivity(i);
            });

            dialog.setOnKeyListener((v1, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            });
            dialog.show();
            dialog.setCancelable(false);
        });
        return view;
    }

    public void prepareUserData(RecyclerView rvUserList, ProgressBar progressBar) {
        if (BWSApplication.isNetworkConnected(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.invalidate();
            Call<AddedUserListModel> listCall = APINewClient.getClient().getUserList(USERID);
            listCall.enqueue(new Callback<AddedUserListModel>() {
                @Override
                public void onResponse(Call<AddedUserListModel> call, Response<AddedUserListModel> response) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        AddedUserListModel listModel = response.body();
                        if (listModel != null) {
                            adapter = new UserListAdapter(listModel.getResponseData());
                        }
                        rvUserList.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AddedUserListModel> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
        private AddedUserListModel.ResponseData model;
        int selectedItem = -1;

        public UserListAdapter(AddedUserListModel.ResponseData model) {
            this.model = model;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MultipleProfileChangeLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.multiple_profile_change_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            List<AddedUserListModel.ResponseData.CoUser> modelList = model.getCoUserList();
            holder.bind.tvName.setText(modelList.get(position).getName());
            holder.bind.ivCheck.setImageResource(R.drawable.ic_user_checked_icon);
            holder.bind.ivCheck.setVisibility(View.INVISIBLE);
            if (selectedItem == position) {
                holder.bind.ivCheck.setVisibility(View.VISIBLE);
            }
            holder.bind.llAddNewCard.setOnClickListener(v -> {
                int previousItem = selectedItem;
                selectedItem = position;
                notifyItemChanged(previousItem);
                notifyItemChanged(position);
                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.comfirm_pin_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(700, ViewGroup.LayoutParams.WRAP_CONTENT);
                Button btnDone = dialog.findViewById(R.id.btnDone);
                TextView tvTitle = dialog.findViewById(R.id.tvTitle);
                TextView txtError = dialog.findViewById(R.id.txtError);
                EditText edtOTP1 = dialog.findViewById(R.id.edtOTP1);
                EditText edtOTP2 = dialog.findViewById(R.id.edtOTP2);
                EditText edtOTP3 = dialog.findViewById(R.id.edtOTP3);
                EditText edtOTP4 = dialog.findViewById(R.id.edtOTP4);
                ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
                tvTitle.setText("Unlock");
                editTexts = new EditText[]{edtOTP1, edtOTP2, edtOTP3, edtOTP4};
                edtOTP1.addTextChangedListener(new PinTextWatcher(0, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone));
                edtOTP2.addTextChangedListener(new PinTextWatcher(1, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone));
                edtOTP3.addTextChangedListener(new PinTextWatcher(2, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone));
                edtOTP4.addTextChangedListener(new PinTextWatcher(3, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone));
                edtOTP1.setOnKeyListener(new PinOnKeyListener(0));
                edtOTP2.setOnKeyListener(new PinOnKeyListener(1));
                edtOTP3.setOnKeyListener(new PinOnKeyListener(2));
                edtOTP4.setOnKeyListener(new PinOnKeyListener(3));
                dialog.setOnKeyListener((v11, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                });
                btnDone.setOnClickListener(v1 -> {
                    if (edtOTP1.getText().toString().equalsIgnoreCase("")
                            && edtOTP2.getText().toString().equalsIgnoreCase("")
                            && edtOTP3.getText().toString().equalsIgnoreCase("")
                            && edtOTP4.getText().toString().equalsIgnoreCase("")) {
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText("Please enter OTP");
                    } else {
                        if (BWSApplication.isNetworkConnected(getActivity())) {
                            txtError.setVisibility(View.GONE);
                            txtError.setText("");
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.invalidate();
                            Call<VerifyPinModel> listCall = APINewClient.getClient().getVerifyPin(modelList.get(position).getCoUserId(),
                                    edtOTP1.getText().toString() + "" +
                                            edtOTP2.getText().toString() + "" +
                                            edtOTP3.getText().toString() + "" +
                                            edtOTP4.getText().toString());
                            listCall.enqueue(new Callback<VerifyPinModel>() {
                                @Override
                                public void onResponse(Call<VerifyPinModel> call, Response<VerifyPinModel> response) {
                                    try {
                                        progressBar.setVisibility(View.GONE);
                                        VerifyPinModel listModel = response.body();
                                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                            BWSApplication.showToast(listModel.getResponseMessage(), getActivity());
                                            dialog.dismiss();
                                            mBottomSheetDialog.hide();
                                        } else if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))) {
                                            txtError.setVisibility(View.VISIBLE);
                                            txtError.setText(listModel.getResponseMessage());
                                        } else {
                                            txtError.setVisibility(View.VISIBLE);
                                            txtError.setText(listModel.getResponseMessage());
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<VerifyPinModel> call, Throwable t) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }

                });
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
            });
        }

        @Override
        public int getItemCount() {
            return model.getCoUser().size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            MultipleProfileChangeLayoutBinding bind;

            public MyViewHolder(MultipleProfileChangeLayoutBinding bind) {
                super(bind.getRoot());
                this.bind = bind;
            }
        }
    }

    public class PinTextWatcher implements TextWatcher {
        private int currentIndex;
        EditText edtOTP1, edtOTP2, edtOTP3, edtOTP4;
        Button btnDone;
        private boolean isFirst = false, isLast = false;
        private String newTypedString = "";

        PinTextWatcher(int currentIndex, EditText edtOTP1, EditText edtOTP2, EditText edtOTP3, EditText edtOTP4, Button btnDone) {
            this.currentIndex = currentIndex;
            this.edtOTP1 = edtOTP1;
            this.edtOTP2 = edtOTP2;
            this.edtOTP3 = edtOTP3;
            this.edtOTP4 = edtOTP4;
            this.btnDone = btnDone;

            if (currentIndex == 0)
                this.isFirst = true;
            else if (currentIndex == editTexts.length - 1)
                this.isLast = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            newTypedString = s.subSequence(start, start + count).toString().trim();
            String OTP1 = edtOTP1.getText().toString().trim();
            String OTP2 = edtOTP2.getText().toString().trim();
            String OTP3 = edtOTP3.getText().toString().trim();
            String OTP4 = edtOTP4.getText().toString().trim();
            if (!OTP1.isEmpty() && !OTP2.isEmpty() && !OTP3.isEmpty() && !OTP4.isEmpty()) {
                btnDone.setEnabled(true);
                btnDone.setTextColor(getResources().getColor(R.color.white));
                btnDone.setBackgroundResource(R.drawable.light_green_rounded_filled);
            } else {
                btnDone.setEnabled(false);
                btnDone.setTextColor(getResources().getColor(R.color.white));
                btnDone.setBackgroundResource(R.drawable.gray_round_cornor);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = newTypedString;
            Log.e("OTP VERIFICATION", "" + text);

            /* Detect paste event and set first char */
            if (text.length() > 1)
                text = String.valueOf(text.charAt(0)); // TODO: We can fill out other EditTexts
            editTexts[currentIndex].removeTextChangedListener(this);
            editTexts[currentIndex].setText(text);
            editTexts[currentIndex].setSelection(text.length());
            editTexts[currentIndex].addTextChangedListener(this);

            if (text.length() == 1) {
                moveToNext();
            } else if (text.length() == 0) {
                if (!tvSendOTPbool) {
                    editTexts[0].requestFocus();
                } else {
                    moveToPrevious();
                }
            }
        }

        private void moveToNext() {
            if (!isLast)
                editTexts[currentIndex + 1].requestFocus();

            if (isAllEditTextsFilled() && isLast) { // isLast is optional
                editTexts[currentIndex].clearFocus();
                hideKeyboard();
            }
        }

        private void moveToPrevious() {
            if (!isFirst)
                editTexts[currentIndex - 1].requestFocus();
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : editTexts)
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            return true;
        }

        private void hideKeyboard() {
            if (getActivity().getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public class PinOnKeyListener implements View.OnKeyListener {
        private int currentIndex;

        PinOnKeyListener(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (editTexts[currentIndex].getText().toString().isEmpty() && currentIndex != 0)
                    editTexts[currentIndex - 1].requestFocus();
            }
            return false;
        }
    }

    public class PopupTextWatcher implements TextWatcher {
        Button btnSendCode;
        EditText edtCreate;

        public PopupTextWatcher(EditText edtCreate, Button btnSendCode) {
            this.edtCreate = edtCreate;
            this.btnSendCode = btnSendCode;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String number = edtCreate.getText().toString().trim();
            if (number.equalsIgnoreCase(PlaylistName)) {
                btnSendCode.setEnabled(false);
                btnSendCode.setTextColor(getResources().getColor(R.color.white));
                btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor);
            } else if (number.isEmpty()) {
                btnSendCode.setEnabled(false);
                btnSendCode.setTextColor(getResources().getColor(R.color.white));
                btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor);
            } else {
                btnSendCode.setEnabled(true);
                btnSendCode.setTextColor(getResources().getColor(R.color.light_black));
                btnSendCode.setBackgroundResource(R.drawable.white_round_cornor);
            }

            /* */
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}