package com.brainwellnessspa.dashboardModule.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BuildConfig;
import com.brainwellnessspa.DownloadModule.Activities.DownloadsActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.reminderModule.activities.ReminderDetailsActivity;
import com.brainwellnessspa.Utility.APIClientProfile;
import com.brainwellnessspa.Utility.APINewClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.FileUtil;
import com.brainwellnessspa.Utility.RequestPermissionHandler;
import com.brainwellnessspa.billingOrderModule.activities.BillingOrderActivity;
import com.brainwellnessspa.dashboardModule.models.AddProfileModel;
import com.brainwellnessspa.dashboardModule.models.SucessModel;
import com.brainwellnessspa.databinding.FragmentProfileBinding;
import com.brainwellnessspa.faqModule.activities.FaqActivity;
import com.brainwellnessspa.invoicemodule.activities.InvoiceActivity;
import com.brainwellnessspa.manageModule.ManageActivity;
import com.brainwellnessspa.resourceModule.activities.ResourceActivity;
import com.brainwellnessspa.userModuleTwo.activities.AccountInfoActivity;
import com.brainwellnessspa.userModuleTwo.activities.GetStartedActivity;
import com.brainwellnessspa.userModuleTwo.models.CoUserDetailsModel;
import com.brainwellnessspa.userModuleTwo.models.RemoveProfileModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.installations.FirebaseInstallations;
import com.segment.analytics.Properties;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import retrofit.RetrofitError;
import retrofit.mime.TypedFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static com.brainwellnessspa.BWSApplication.analytics;
import static com.brainwellnessspa.BWSApplication.deleteCache;
import static com.brainwellnessspa.BWSApplication.logout;
import static com.brainwellnessspa.invoicemodule.activities.InvoiceActivity.invoiceToRecepit;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    private long mLastClickTime = 0;
    Dialog logoutDialog;
    File image;
    RequestPermissionHandler mRequestPermissionHandler;
    private static final int CONTENT_REQUEST = 100;
    public static int ComeScreenReminder = 0;
    CharSequence[] options;
    String USERID,
            CoUserID,
            UserEmail,
            userImage,
            DeviceID,
            DeviceType,
            UserName,
            profilePicPath = "",
            userMobile,
            isProfileCompleted,
            isAssessmentCompleted,
            indexScore,
            scoreLevel,
            avgSleepTime;
    //    areaOfFocus

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(
            @NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 =
                requireActivity()
                        .getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        USERID = (shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, ""));
        CoUserID = (shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, ""));
        UserName = (shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, ""));
        UserEmail = (shared1.getString(CONSTANTS.PREFE_ACCESS_USEREMAIL, ""));
        userImage = (shared1.getString(CONSTANTS.PREFE_ACCESS_IMAGE, ""));
        userMobile = (shared1.getString(CONSTANTS.PREFE_ACCESS_MOBILE, ""));
        isProfileCompleted = (shared1.getString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, ""));
        isAssessmentCompleted = (shared1.getString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, ""));
        indexScore = (shared1.getString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, ""));
        scoreLevel = (shared1.getString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, ""));
        scoreLevel = (shared1.getString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, ""));
        DeviceID = (shared1.getString(CONSTANTS.PREFE_ACCESS_DeviceID, ""));
        DeviceType = (shared1.getString(CONSTANTS.PREFE_ACCESS_DeviceType, ""));

        SharedPreferences shared =
                requireActivity().getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE);
        avgSleepTime = shared.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "");
        binding.tvName.setText(UserName);
        mRequestPermissionHandler = new RequestPermissionHandler();
        binding.tvVersion.setText("Version " + BuildConfig.VERSION_NAME);

        if (UserName.equalsIgnoreCase("")
                || UserName.equalsIgnoreCase(" ")
                || UserName == null) {
            binding.tvName.setText(R.string.Guest);
        } else {
            binding.tvName.setText(UserName);
        }

        String Name;
        profilePicPath = userImage;
        if (BWSApplication.isNetworkConnected(getActivity())) {
            if (profilePicPath.equalsIgnoreCase("")) {
                binding.civProfile.setVisibility(View.GONE);
                if (UserName.equalsIgnoreCase("")) {
                    Name = "Guest";
                } else {
                    Name = UserName;
                }
                String Letter = Name.substring(0, 1);
                binding.rlLetter.setVisibility(View.VISIBLE);
                binding.tvLetter.setText(Letter);
            } else {
                binding.civProfile.setVisibility(View.VISIBLE);
                binding.rlLetter.setVisibility(View.GONE);
                setProfilePic(profilePicPath);
            }
        } else {
            binding.civProfile.setVisibility(View.GONE);
            if (UserName.equalsIgnoreCase("")) {
                Name = "Guest";
            } else {
                Name = UserName;
            }
            String Letter = Name.substring(0, 1);
            binding.rlLetter.setVisibility(View.VISIBLE);
            binding.tvLetter.setText(Letter);
        }


        profileViewData(getActivity());

        binding.llImageUpload.setOnClickListener(
                view15 -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (BWSApplication.isNetworkConnected(getActivity())) {
                        selectImage();
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                    }
                });

        Properties p = new Properties();
        p.putValue("coUserId", CoUserID);
        BWSApplication.addToSegment("Account Screen Viewed", p, CONSTANTS.screen);
        binding.llAcInfo.setOnClickListener(
                view15 -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (BWSApplication.isNetworkConnected(getActivity())) {
                        Intent i = new Intent(getActivity(), AccountInfoActivity.class);
                        startActivity(i);
                        requireActivity().overridePendingTransition(0, 0);
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                    }
                });

        binding.llDownloads.setOnClickListener(
                view12 -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent i = new Intent(getActivity(), DownloadsActivity.class);
                    startActivity(i);
                    requireActivity().overridePendingTransition(0, 0);
                });

        binding.llInvoices.setOnClickListener(
                view14 -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (BWSApplication.isNetworkConnected(getActivity())) {
                        invoiceToRecepit = 1;
                        Intent i = new Intent(getActivity(), InvoiceActivity.class);
                        i.putExtra("ComeFrom", "");
                        startActivity(i);
                        requireActivity().overridePendingTransition(0, 0);
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                    }
                });

        binding.llBillingOrder.setOnClickListener(
                view15 -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (BWSApplication.isNetworkConnected(getActivity())) {
                        Intent i = new Intent(getActivity(), BillingOrderActivity.class);
                        startActivity(i);
                        requireActivity().overridePendingTransition(0, 0);
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                    }
                });

        binding.llReminder.setOnClickListener(
                view16 -> {
                    ComeScreenReminder = 1;
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (BWSApplication.isNetworkConnected(getActivity())) {
                        Intent i = new Intent(getActivity(), ReminderDetailsActivity.class);
                        startActivity(i);
                        requireActivity().overridePendingTransition(0, 0);
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                    }
                });

        binding.llPlan.setOnClickListener(
                v -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (BWSApplication.isNetworkConnected(getActivity())) {
                        Intent i = new Intent(getActivity(), ManageActivity.class);
                        startActivity(i);
                        requireActivity().overridePendingTransition(0, 0);
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                    }
                });

        binding.llResources.setOnClickListener(
                view17 -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (BWSApplication.isNetworkConnected(getActivity())) {
                        Intent i = new Intent(getActivity(), ResourceActivity.class);
                        startActivity(i);
                        requireActivity().overridePendingTransition(0, 0);
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                    }
                });

        binding.llFAQ.setOnClickListener(
                v -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (BWSApplication.isNetworkConnected(getActivity())) {
                        Intent i = new Intent(getActivity(), FaqActivity.class);
                        startActivity(i);
                        requireActivity().overridePendingTransition(0, 0);
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                    }
                });

        binding.llLogOut.setOnClickListener(
                view19 -> {
                    if (BWSApplication.isNetworkConnected(getActivity())) {
                        logoutDialog = new Dialog(getActivity());
                        logoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        logoutDialog.setContentView(R.layout.logout_layout);
                        logoutDialog
                                .getWindow()
                                .setBackgroundDrawable(
                                        new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                        logoutDialog
                                .getWindow()
                                .setLayout(
                                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        final TextView tvGoBack = logoutDialog.findViewById(R.id.tvGoBack);
                        final Button Btn = logoutDialog.findViewById(R.id.Btn);
                        final ProgressBar progressBar = logoutDialog.findViewById(R.id.progressBar);
                        final FrameLayout progressBarHolder = logoutDialog.findViewById(R.id.progressBarHolder);

                        logoutDialog.setOnKeyListener(
                                (v, keyCode, event) -> {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        logoutDialog.hide();
                                        return true;
                                    }
                                    return false;
                                });

                        Btn.setOnClickListener(
                                v -> {
                                    logoutDialog.hide();
                                    BWSApplication.showProgressBar(progressBar, progressBarHolder, getActivity());
                                    DeleteCall(logoutDialog, progressBar, progressBarHolder);
                                });

                        tvGoBack.setOnClickListener(v -> logoutDialog.hide());
                        logoutDialog.show();
                        logoutDialog.setCancelable(false);
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                    }
                });
        return view;
    }

    @Override
    public void onResume() {
        profileViewData(getActivity());
        super.onResume();
    }

    private void selectImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                callProfilePathSet();
            } else {
                mRequestPermissionHandler.requestPermission(
                        requireActivity(),
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        },
                        123,
                        new RequestPermissionHandler.RequestPermissionListener() {
                            @Override
                            public void onSuccess() {
                                callProfilePathSet();
                            }

                            @Override
                            public void onFailed() {
                            }
                        });
            }
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                callProfilePathSet();
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    mRequestPermissionHandler.requestPermission(
                            requireActivity(),
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                            123,
                            new RequestPermissionHandler.RequestPermissionListener() {
                                @Override
                                public void onSuccess() {
                                    callProfilePathSet();
                                }

                                @Override
                                public void onFailed() {
                                }
                            });
                } else if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    callCamaraPermission();
                } else if (ContextCompat.checkSelfPermission(
                        requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    callReadPermission();
                }
            }
        } else {
            callProfilePathSet();
        }
    }

    private void callCamaraPermission() {
        AlertDialog.Builder building = new AlertDialog.Builder(requireActivity());
        building.setMessage(
                "To camera allow "
                        + getString(R.string.app_name)
                        + " access to your camera. "
                        + "\nTap Setting > permission, and turn Camera on.");
        building.setCancelable(true);
        building.setPositiveButton(
                getString(R.string.Settings),
                (dialogs, id1) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialogs.dismiss();
                });
        building.setNegativeButton(getString(R.string.not_now), (dialogs, id1) -> dialogs.dismiss());
        AlertDialog alert11 = building.create();
        alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        alert11.show();
        alert11
                .getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                .setTextColor(getResources().getColor(R.color.blue));
        alert11
                .getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(getResources().getColor(R.color.blue));
    }

    private void callReadPermission() {
        AlertDialog.Builder buildable = new AlertDialog.Builder(requireActivity());
        buildable.setMessage(
                "To upload image allow "
                        + getString(R.string.app_name)
                        + " access to your device's files. "
                        + "\nTap Setting > permission, and turn \"Files and media\" on.");
        buildable.setCancelable(true);
        buildable.setPositiveButton(
                getString(R.string.Settings),
                (dialogs, id1) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialogs.dismiss();
                });
        buildable.setNegativeButton(getString(R.string.not_now), (dialogue, id1) -> dialogue.dismiss());
        AlertDialog alert11 = buildable.create();
        alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        alert11.show();
        alert11
                .getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                .setTextColor(getResources().getColor(R.color.blue));
        alert11
                .getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(getResources().getColor(R.color.blue));
    }

    private void callProfilePathSet() {
        if (profilePicPath.equals("")) {
            options =
                    new String[]{
                            getString(R.string.takePhoto),
                            getString(R.string.chooseFromGallary),
                            getString(R.string.cancel)
                    };
        } else {
            options =
                    new String[]{
                            getString(R.string.takePhoto),
                            getString(R.string.chooseFromGallary),
                            getString(R.string.removeProfilePicture),
                            getString(R.string.cancel)
                    };
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.addPhoto);
        builder.setItems(
                options,
                (dialog, item) -> {
                    if (options[item].equals(getString(R.string.takePhoto))) {
                        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (pictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                            if (photoFile != null) {
                                Uri photoURI =
                                        FileProvider.getUriForFile(
                                                requireActivity(), BuildConfig.APPLICATION_ID + ".provider", photoFile);
                                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(pictureIntent, CONTENT_REQUEST);
                            }
                        }
                    } else if (options[item].equals(getString(R.string.chooseFromGallary))) {
                        Intent intent =
                                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                    } else if (options[item].equals(getString(R.string.removeProfilePicture))) {
                        if (BWSApplication.isNetworkConnected(getActivity())) {
                            BWSApplication.showProgressBar(
                                    binding.progressBar, binding.progressBarHolder, getActivity());
                            Call<RemoveProfileModel> listCall =
                                    APINewClient.getClient().getRemoveProfile(CoUserID);
                            listCall.enqueue(
                                    new Callback<RemoveProfileModel>() {
                                        @Override
                                        public void onResponse(
                                                @NotNull Call<RemoveProfileModel> call,
                                                @NotNull Response<RemoveProfileModel> response) {
                                            try {
                                                RemoveProfileModel viewModel = response.body();
                                                BWSApplication.hideProgressBar(
                                                        binding.progressBar, binding.progressBarHolder, requireActivity());
                                                if (viewModel != null) {
                                                    if (viewModel
                                                            .getResponseCode()
                                                            .equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                                        BWSApplication.showToast(
                                                                viewModel.getResponseMessage(), requireActivity());
                                                        SharedPreferences shared =
                                                                requireActivity()
                                                                        .getSharedPreferences(
                                                                                CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = shared.edit();
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, "");
                                                        editor.apply();
                                                        profileViewData(requireActivity());
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(
                                                @NotNull Call<RemoveProfileModel> call, @NotNull Throwable t) {
                                            BWSApplication.hideProgressBar(
                                                    binding.progressBar, binding.progressBarHolder, requireActivity());
                                        }
                                    });
                        }
                    } else if (options[item].equals(getString(R.string.cancel))) {
                        Properties p = new Properties();
                        p.putValue("userId", USERID);
                        p.putValue("coUserId", CoUserID);
                        BWSApplication.addToSegment("Profile Photo Cancelled", p, CONSTANTS.track);
                        dialog.dismiss();
                    }
                });
        AlertDialog alert11 = builder.create();
        alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        alert11.show();
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(imageFileName, ".jpg", storageDir);
        profilePicPath = image.getAbsolutePath();
        return image;
    }

    void profileViewData(Context ctx) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(
                    binding.progressBar, binding.progressBarHolder, requireActivity());
            Call<CoUserDetailsModel> listCall =
                    APINewClient.getClient().getCoUserDetails(USERID, CoUserID);
            listCall.enqueue(
                    new Callback<CoUserDetailsModel>() {
                        @Override
                        public void onResponse(
                                @NotNull Call<CoUserDetailsModel> call,
                                @NotNull Response<CoUserDetailsModel> response) {
                            try {
                                CoUserDetailsModel viewModel = response.body();
                                if (viewModel != null) {
                                    if (viewModel
                                            .getResponseCode()
                                            .equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                        BWSApplication.hideProgressBar(
                                                binding.progressBar, binding.progressBarHolder, requireActivity());

                   /* if (viewModel.getResponseData().getName().equalsIgnoreCase("")
                        || viewModel.getResponseData().getName().equalsIgnoreCase(" ")
                        || viewModel.getResponseData().getName() == null) {
                      binding.tvName.setText(R.string.Guest);
                    } else {
                      binding.tvName.setText(viewModel.getResponseData().getName());
                    }

                    String Name;
                    profilePicPath = viewModel.getResponseData().getImage();
                    if (profilePicPath.equalsIgnoreCase("")) {
                      binding.civProfile.setVisibility(View.GONE);
                      if (viewModel.getResponseData().getName().equalsIgnoreCase("")) {
                        Name = "Guest";
                      } else {
                        Name = viewModel.getResponseData().getName();
                      }
                      String Letter = Name.substring(0, 1);
                      binding.rlLetter.setVisibility(View.VISIBLE);
                      binding.tvLetter.setText(Letter);
                    } else {
                      binding.civProfile.setVisibility(View.VISIBLE);
                      binding.rlLetter.setVisibility(View.GONE);
                      setProfilePic(profilePicPath);
                    }*/
                                    } else {
                                        BWSApplication.hideProgressBar(
                                                binding.progressBar, binding.progressBarHolder, getActivity());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CoUserDetailsModel> call, @NotNull Throwable t) {
                            BWSApplication.hideProgressBar(
                                    binding.progressBar, binding.progressBarHolder, getActivity());
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTENT_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                setProfilePic(profilePicPath);
                if (BWSApplication.isNetworkConnected(requireActivity())) {
                    BWSApplication.showProgressBar(
                            binding.progressBar, binding.progressBarHolder, requireActivity());
                    HashMap<String, String> map = new HashMap<>();
                    map.put(CONSTANTS.PREF_KEY_UserID, CoUserID);
                    TypedFile typedFile = new TypedFile(CONSTANTS.MULTIPART_FORMAT, image);
                    APIClientProfile.getApiService()
                            .getAddProfiles(
                                    CoUserID,
                                    typedFile,
                                    new retrofit.Callback<AddProfileModel>() {
                                        @Override
                                        public void success(
                                                AddProfileModel addProfileModel, retrofit.client.Response response) {
                                            try {
                                                if (addProfileModel
                                                        .getResponseCode()
                                                        .equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                                    BWSApplication.hideProgressBar(
                                                            binding.progressBar, binding.progressBarHolder, requireActivity());
                                                    setProfilePic(profilePicPath);
                                                    BWSApplication.showToast(
                                                            addProfileModel.getResponseMessage(), requireActivity());
                                                    Properties p = new Properties();
                                                    p.putValue("userId", USERID);
                                                    p.putValue("coUserId", CoUserID);
                                                    BWSApplication.addToSegment("Camera Photo Added", p, CONSTANTS.track);
                                                    profilePicPath = addProfileModel.getResponseData().getProfileImage();
                                                    SharedPreferences shared =
                                                            requireActivity()
                                                                    .getSharedPreferences(
                                                                            CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = shared.edit();
                                                    editor.putString(
                                                            CONSTANTS.PREFE_ACCESS_IMAGE,
                                                            addProfileModel.getResponseData().getProfileImage());
                                                    editor.apply();
                                                    profileViewData(getActivity());
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void failure(RetrofitError e) {
                                            BWSApplication.hideProgressBar(
                                                    binding.progressBar, binding.progressBarHolder, getActivity());
                                            BWSApplication.showToast(e.getMessage(), getActivity());
                                        }
                                    });
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Permission", e.getMessage());
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                //                Glide.with(this).load(selectedImageUri).dontAnimate()
                //                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(126)))
                //                        .into(binding.civProfile);
                setProfilePic(selectedImageUri.toString());
                if (BWSApplication.isNetworkConnected(getActivity())) {
                    BWSApplication.showProgressBar(
                            binding.progressBar, binding.progressBarHolder, getActivity());
                    HashMap<String, String> map = new HashMap<>();
                    map.put(CONSTANTS.PREF_KEY_UserID, CoUserID);
                    File file =
                            new File(
                                    Objects.requireNonNull(FileUtil.getPath(selectedImageUri, requireActivity())));

                    TypedFile typedFile = new TypedFile(CONSTANTS.MULTIPART_FORMAT, file);
                    APIClientProfile.getApiService()
                            .getAddProfiles(
                                    CoUserID,
                                    typedFile,
                                    new retrofit.Callback<AddProfileModel>() {
                                        @Override
                                        public void success(
                                                AddProfileModel addProfileModel, retrofit.client.Response response) {
                                            if (addProfileModel
                                                    .getResponseCode()
                                                    .equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                                BWSApplication.hideProgressBar(
                                                        binding.progressBar, binding.progressBarHolder, getActivity());
                                                profilePicPath = addProfileModel.getResponseData().getProfileImage();
                                                setProfilePic(profilePicPath);
                                                Properties p = new Properties();
                                                p.putValue("userId", USERID);
                                                p.putValue("coUserId", CoUserID);
                                                BWSApplication.addToSegment("Gallery Photo Added", p, CONSTANTS.track);
                                                BWSApplication.showToast(
                                                        addProfileModel.getResponseMessage(), getActivity());
                                                SharedPreferences shared =
                                                        requireActivity()
                                                                .getSharedPreferences(
                                                                        CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = shared.edit();
                                                editor.putString(
                                                        CONSTANTS.PREFE_ACCESS_IMAGE,
                                                        addProfileModel.getResponseData().getProfileImage());
                                                editor.apply();
                                                profileViewData(getActivity());
                                            }
                                        }

                                        @Override
                                        public void failure(RetrofitError e) {
                                            BWSApplication.hideProgressBar(
                                                    binding.progressBar, binding.progressBarHolder, getActivity());
                                            BWSApplication.showToast(e.getMessage(), getActivity());
                                        }
                                    });
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                }
            }
        } else if (requestCode == RESULT_CANCELED) {
            Properties p = new Properties();
            p.putValue("userId", USERID);
            p.putValue("coUserId", CoUserID);
            BWSApplication.addToSegment("Profile Photo Cancelled", p, CONSTANTS.track);
            requireActivity().finish();
        }
    }

    private void setProfilePic(String profilePicPath) {
        Glide.with(requireActivity())
                .load(profilePicPath)
                .thumbnail(0.10f)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(126)))
                .into(binding.civProfile);
    }

    void DeleteCall(Dialog dialog, ProgressBar progressBar, FrameLayout progressBarHolder) {
        SharedPreferences preferences =
                requireActivity()
                        .getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(CONSTANTS.PREFE_ACCESS_UserID);
        edit.remove(CONSTANTS.PREFE_ACCESS_CoUserID);
        edit.remove(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER);
        edit.remove(CONSTANTS.PREFE_ACCESS_NAME);
        edit.remove(CONSTANTS.PREFE_ACCESS_USEREMAIL);
        edit.remove(CONSTANTS.PREFE_ACCESS_DeviceType);
        edit.remove(CONSTANTS.PREFE_ACCESS_DeviceID);
        edit.clear();
        edit.apply();

        SharedPreferences preferred =
                requireActivity().getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE);
        SharedPreferences.Editor edited = preferred.edit();
        edited.remove(CONSTANTS.selectedCategoriesTitle);
        edited.remove(CONSTANTS.selectedCategoriesName);
        edited.remove(CONSTANTS.PREFE_ACCESS_SLEEPTIME);
        edited.clear();
        edited.apply();

        SharedPreferences preferred1 =
                requireActivity().getSharedPreferences(CONSTANTS.AssMain, Context.MODE_PRIVATE);
        SharedPreferences.Editor edited1 = preferred1.edit();
        edited1.remove(CONSTANTS.AssQus);
        edited1.remove(CONSTANTS.AssAns);
        edited1.remove(CONSTANTS.AssSort);
        edited1.clear();
        edited1.apply();
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGOUT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorcv = shared.edit();
        editorcv.putString(CONSTANTS.PREF_KEY_LOGOUT_UserID, USERID);
        editorcv.putString(CONSTANTS.PREF_KEY_LOGOUT_CoUserID, CoUserID);
        editorcv.commit();
        SharedPreferences preferred2 =
                requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        SharedPreferences.Editor edited2 = preferred2.edit();
        edited2.remove(CONSTANTS.PREF_KEY_MainAudioList);
        edited2.remove(CONSTANTS.PREF_KEY_PlayerAudioList);
        edited2.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag);
        edited2.remove(CONSTANTS.PREF_KEY_PlayerPlaylistId);
        edited2.remove(CONSTANTS.PREF_KEY_PlayerPosition);
        edited2.remove(CONSTANTS.PREF_KEY_Cat_Name);
        edited2.remove(CONSTANTS.PREF_KEY_PlayFrom);
        edited2.clear();
        edited2.apply();

        logout = true;
        deleteCache(getActivity());
        callLogoutApi(dialog, progressBar, progressBarHolder);
    }

    private void callLogoutApi(
            Dialog dialog, ProgressBar progressBar, FrameLayout progressBarHolder) {
        SharedPreferences sharedPreferences2 =
                requireActivity().getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE);
        String fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "");
        if (TextUtils.isEmpty(fcm_id)) {
            FirebaseInstallations.getInstance()
                    .getToken(true)
                    .addOnCompleteListener(
                            requireActivity(),
                            task -> {
                                String newToken = task.getResult().getToken();
                                Log.e("newToken", newToken);
                                SharedPreferences.Editor editor =
                                        requireActivity()
                                                .getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE)
                                                .edit();
                                editor.putString(CONSTANTS.Token, newToken); // Friend
                                editor.apply();
                                editor.commit();
                            });
            fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "");
        }

        Call<SucessModel> listCall =
                APINewClient.getClient().getLogout(USERID, fcm_id, CONSTANTS.FLAG_ONE);
        listCall.enqueue(
                new Callback<SucessModel>() {
                    @Override
                    public void onResponse(
                            @NotNull Call<SucessModel> call, @NotNull Response<SucessModel> response) {
                        SucessModel sucessModel = response.body();
                        //                try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (sucessModel
                                .getResponseCode()
                                .equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            Properties p1 = new Properties();
                            p1.putValue("userId", USERID);
                            p1.putValue("coUserId", CoUserID);
                            p1.putValue("deviceId", DeviceID);
                            p1.putValue("deviceType", "Android");
                            p1.putValue("phone", userMobile);
                            p1.putValue("email", UserEmail);
                            p1.putValue("isProfileCompleted", isProfileCompleted);
                            p1.putValue("isAssessmentCompleted", isAssessmentCompleted);
                            p1.putValue("indexScore", indexScore);
                            p1.putValue("scoreLevel", scoreLevel);
                            p1.putValue("avgSleepTime", avgSleepTime);
                            p1.putValue("areaOfFocus", "");
                            BWSApplication.addToSegment("CoUser Logout", p1, CONSTANTS.track);
                            BWSApplication.hideProgressBar(
                                    binding.progressBar, binding.progressBarHolder, getActivity());
                            BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                            dialog.hide();
                            analytics.flush();
                            analytics.reset();
                            Intent i = new Intent(getActivity(), GetStartedActivity.class);
                            startActivity(i);
                            requireActivity().finish();
                        }
                        //                } catch (Exception e) {
                        //                    e.printStackTrace();
                        //                }
                    }

                    @Override
                    public void onFailure(@NotNull Call<SucessModel> call, @NotNull Throwable t) {
                        BWSApplication.hideProgressBar(
                                binding.progressBar, binding.progressBarHolder, getActivity());
                    }
                });
    }
}
