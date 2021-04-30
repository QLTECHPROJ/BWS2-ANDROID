package com.brainwellnessspa.DashboardTwoModule.profile;

import android.Manifest;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

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

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.BillingOrderActivity;
import com.brainwellnessspa.BuildConfig;
import com.brainwellnessspa.DashboardTwoModule.Model.SucessModel;
import com.brainwellnessspa.DashboardTwoModule.MyPlayerActivity;
import com.brainwellnessspa.ProfileTwoModule.AccountInfoActivity;
import com.brainwellnessspa.DownloadModule.Activities.DownloadsActivity;
import com.brainwellnessspa.FaqModule.Activities.FaqActivity;
import com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ReminderModule.Activities.ReminderDetailsActivity;
import com.brainwellnessspa.ResourceModule.Activities.ResourceActivity;
import com.brainwellnessspa.UserModule.Activities.FileUtil;
import com.brainwellnessspa.UserModule.Activities.RequestPermissionHandler;
import com.brainwellnessspa.UserModule.Models.AddProfileModel;
import com.brainwellnessspa.UserModule.Models.RemoveProfileModel;
import com.brainwellnessspa.UserModuleTwo.Activities.GetStartedActivity;
import com.brainwellnessspa.Utility.APIClientProfile;
import com.brainwellnessspa.Utility.APINewClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentProfileBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.installations.FirebaseInstallations;
import com.segment.analytics.Properties;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit.RetrofitError;
import retrofit.mime.TypedFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static com.brainwellnessspa.BWSApplication.deleteCache;
import static com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity.invoiceToRecepit;
import static com.brainwellnessspa.SplashModule.SplashScreenActivity.analytics;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    private long mLastClickTime = 0;
    Dialog logoutDialog;
    File image;
    RequestPermissionHandler mRequestPermissionHandler;
    private static final int CONTENT_REQUEST = 100;
    public static int ComeScreenReminder = 0;
    CharSequence[] options;
    String USERID, CoUserID, UserEmail, DeviceID, DeviceType, UserName, profilePicPath = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        USERID = (shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, ""));
        CoUserID = (shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, ""));
        UserName = (shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, ""));
        UserEmail = (shared1.getString(CONSTANTS.PREFE_ACCESS_USEREMAIL, ""));
        DeviceID = (shared1.getString(CONSTANTS.PREFE_ACCESS_DeviceID, ""));
        DeviceType = (shared1.getString(CONSTANTS.PREFE_ACCESS_DeviceType, ""));
        binding.tvName.setText(UserName);
        mRequestPermissionHandler = new RequestPermissionHandler();
        binding.tvVersion.setText("Version " + BuildConfig.VERSION_NAME);
        binding.llImageUpload.setOnClickListener(view15 -> {
            selectImage();
        });

        binding.tvName.setOnClickListener(view15 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), MyPlayerActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llAcInfo.setOnClickListener(view15 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), AccountInfoActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llDownloads.setOnClickListener(view12 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(getActivity(), DownloadsActivity.class);
            startActivity(i);
            getActivity().overridePendingTransition(0, 0);
        });

        binding.llInvoices.setOnClickListener(view14 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                invoiceToRecepit = 1;
                Intent i = new Intent(getActivity(), InvoiceActivity.class);
                i.putExtra("ComeFrom", "");
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llBillingOrder.setOnClickListener(view15 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), BillingOrderActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llReminder.setOnClickListener(view16 -> {
            ComeScreenReminder = 1;
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), ReminderDetailsActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llResources.setOnClickListener(view17 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), ResourceActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llFAQ.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), FaqActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        binding.llLogOut.setOnClickListener(view19 -> {
            if (BWSApplication.isNetworkConnected(getActivity())) {
                logoutDialog = new Dialog(getActivity());
                logoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                logoutDialog.setContentView(R.layout.logout_layout);
                logoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                logoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                final TextView tvGoBack = logoutDialog.findViewById(R.id.tvGoBack);
                final Button Btn = logoutDialog.findViewById(R.id.Btn);
                final ProgressBar progressBar = logoutDialog.findViewById(R.id.progressBar);
                final FrameLayout progressBarHolder = logoutDialog.findViewById(R.id.progressBarHolder);

                logoutDialog.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        logoutDialog.hide();
                        return true;
                    }
                    return false;
                });

                Btn.setOnClickListener(v -> {
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

    private void selectImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                callProfilePathSet();
            } else {
                mRequestPermissionHandler.requestPermission(getActivity(), new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }, 123, new RequestPermissionHandler.RequestPermissionListener() {
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
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                callProfilePathSet();
            } else {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    mRequestPermissionHandler.requestPermission(getActivity(), new String[]{
                            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 123, new RequestPermissionHandler.RequestPermissionListener() {
                        @Override
                        public void onSuccess() {
                            callProfilePathSet();
                        }

                        @Override
                        public void onFailed() {
                        }
                    });
                } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    callCamaraPermission();
                } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    callReadPermission();
                }
            }
        } else {
            callProfilePathSet();
        }
    }

    private void callCamaraPermission() {
        AlertDialog.Builder buildermain = new AlertDialog.Builder(getActivity());
        buildermain.setMessage("To camera allow " + getString(R.string.app_name) + " access to your camera. " +
                "\nTap Setting > permission, and turn Camera on.");
        buildermain.setCancelable(true);
        buildermain.setPositiveButton(
                getString(R.string.Settings),
                (dialogmain, id1) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialogmain.dismiss();
                });
        buildermain.setNegativeButton(
                getString(R.string.not_now),
                (dialogmain, id1) -> {
                    dialogmain.dismiss();
                });
        AlertDialog alert11 = buildermain.create();
        alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        alert11.show();
        alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
        alert11.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blue));
    }

    private void callReadPermission() {
        AlertDialog.Builder buildermain = new AlertDialog.Builder(getActivity());
        buildermain.setMessage("To upload image allow " + getString(R.string.app_name) + " access to your device's files. " +
                "\nTap Setting > permission, and turn \"Files and media\" on.");
        buildermain.setCancelable(true);
        buildermain.setPositiveButton(
                getString(R.string.Settings),
                (dialogmain, id1) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialogmain.dismiss();
                });
        buildermain.setNegativeButton(
                getString(R.string.not_now),
                (dialogmain, id1) -> {
                    dialogmain.dismiss();
                });
        AlertDialog alert11 = buildermain.create();
        alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        alert11.show();
        alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
        alert11.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blue));
    }

    private void callProfilePathSet() {
        if (profilePicPath.equals("")) {
            options = new String[]{getString(R.string.takePhoto), getString(R.string.chooseFromGallary), getString(R.string.cancel)};
        } else {
            options = new String[]{getString(R.string.takePhoto), getString(R.string.chooseFromGallary), getString(R.string.removeProfilePicture), getString(R.string.cancel)};
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.addPhoto);
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals(getString(R.string.takePhoto))) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                    }

                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                                BuildConfig.APPLICATION_ID + ".provider", photoFile);
                        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(pictureIntent,
                                CONTENT_REQUEST);
                    }
                }
            } else if (options[item].equals(getString(R.string.chooseFromGallary))) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            } else if (options[item].equals(getString(R.string.removeProfilePicture))) {
                if (BWSApplication.isNetworkConnected(getActivity())) {
                    BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                    Call<RemoveProfileModel> listCall = APINewClient.getClient().getRemoveProfile(CoUserID);
                    listCall.enqueue(new Callback<RemoveProfileModel>() {
                        @Override
                        public void onResponse(Call<RemoveProfileModel> call, Response<RemoveProfileModel> response) {
                            try {
                                RemoveProfileModel viewModel = response.body();
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                                if (viewModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                    BWSApplication.showToast(viewModel.getResponseMessage(), getActivity());
                                    profileViewData(getActivity());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<RemoveProfileModel> call, Throwable t) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
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
        File storageDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        profilePicPath = image.getAbsolutePath();
        return image;
    }

    void profileViewData(Context ctx) {
        String Name;
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
//        if (BWSApplication.isNetworkConnected(ctx)) {
//            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
//            Call<ProfileViewModel> listCall = APIClient.getClient().getProfileView(UserID);
//            listCall.enqueue(new Callback<ProfileViewModel>() {
//                @Override
//                public void onResponse(Call<ProfileViewModel> call, Response<ProfileViewModel> response) {
//                    try {
//                        ProfileViewModel viewModel = response.body();
//                        if (viewModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
//                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
//                            if (viewModel.getResponseData().getName().equalsIgnoreCase("") ||
//                                    viewModel.getResponseData().getName().equalsIgnoreCase(" ") ||
//                                    viewModel.getResponseData().getName() == null) {
//                                binding.etUser.setText(R.string.Guest);
//                            } else {
//                                binding.etUser.setText(viewModel.getResponseData().getName());
//                            }
//                            UserName = viewModel.getResponseData().getName();
//                            UserCalendar = viewModel.getResponseData().getDOB();
//                            UserMobileNumber = viewModel.getResponseData().getPhoneNumber();
//                            UserEmail = viewModel.getResponseData().getEmail();
//                            String Name;
//                            profilePicPath = viewModel.getResponseData().getImage();
//                            if (profilePicPath.equalsIgnoreCase("")) {
//                                binding.civProfile.setVisibility(View.GONE);
//                                if (viewModel.getResponseData().getName().equalsIgnoreCase("")) {
//                                    Name = "Guest";
//                                } else {
//                                    Name = viewModel.getResponseData().getName();
//                                }
//                                String Letter = Name.substring(0, 1);
//                                binding.rlLetter.setVisibility(View.VISIBLE);
//                                binding.tvLetter.setText(Letter);
//                            } else {
//                                binding.civProfile.setVisibility(View.VISIBLE);
//                                binding.rlLetter.setVisibility(View.GONE);
//                                setProfilePic(profilePicPath);
//                            }
//
//                            if (viewModel.getResponseData().getDOB().equalsIgnoreCase("0000-00-00")) {
//                                binding.etCalendar.setText("");
//                            } else {
//                            /*String date = viewModel.getResponseData().getDOB();
//                            SimpleDateFormat spf = new SimpleDateFormat(CONSTANTS.YEAR_TO_DATE_FORMAT);
//                            if (!date.isEmpty()) {
//                                Date newDate = null;
//                                try {
//                                    newDate = spf.parse(date);
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                                spf = new SimpleDateFormat(CONSTANTS.MONTH_DATE_YEAR_FORMAT);
//                                date = spf.format(newDate);*/
//                                binding.etCalendar.setText(viewModel.getResponseData().getDOB());
////                            }
//                            }
//
//                            if (!viewModel.getResponseData().getEmail().equalsIgnoreCase("")
//                                    && BWSApplication.isEmailValid(viewModel.getResponseData().getEmail())) {
//                                binding.ivCheckEmail.setColorFilter(ContextCompat.getColor(ctx, R.color.green_dark), android.graphics.PorterDuff.Mode.SRC_IN);
//                            } else {
//                                binding.ivCheckEmail.setColorFilter(ContextCompat.getColor(ctx, R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
//
//                            }
//                            binding.etEmail.setText(viewModel.getResponseData().getEmail());
//                            binding.etMobileNumber.setText(viewModel.getResponseData().getPhoneNumber());
//
//                            if (!viewModel.getResponseData().getEmail().equalsIgnoreCase("")) {
//                                binding.etEmail.setEnabled(true);
//                                binding.etEmail.setClickable(true);
//                            } else {
//                                binding.etEmail.setEnabled(true);
//                                binding.etEmail.setClickable(true);
//                            }
//
//                            if (!viewModel.getResponseData().getPhoneNumber().equalsIgnoreCase("")) {
//                                binding.etMobileNumber.setEnabled(false);
//                                binding.etMobileNumber.setClickable(false);
//                            } else {
//                                binding.etMobileNumber.setEnabled(true);
//                                binding.etMobileNumber.setClickable(true);
//                            }
//
//                            binding.etCalendar.setOnClickListener(view -> {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                    setDate();
//                                }
//                            });
//
//                            if ((viewModel.getResponseData().getIsVerify().equalsIgnoreCase("0"))) {
//                                binding.ivCheckEmail.setVisibility(View.GONE);
//                                binding.llEmailApply.setClickable(true);
//                                binding.llEmailApply.setEnabled(true);
//                                binding.tlEmail.setErrorEnabled(false);
//                                binding.tlEmail.clearFocus();
////                            tvApply.setEnabled(true);
////                            tvApply.setClickable(true);
////                            tvApply.setText("Verify");
////                            tvApply.setTextColor(getResources().getColor(R.color.gray));
////                            tvApplytimer.setVisibility(View.GONE);
//                            } else if (viewModel.getResponseData().getIsVerify().equalsIgnoreCase("1")) {
//                                binding.ivCheckEmail.setVisibility(View.VISIBLE);
//                                binding.llEmailApply.setClickable(false);
//                                binding.llEmailApply.setEnabled(false);
//                                binding.tlEmail.setErrorEnabled(false);
//                                binding.tlEmail.clearFocus();
////                            tvApply.setText("Verified");
////                            tvApply.setTextColor(getResources().getColor(R.color.green));
////                            tvApply.setEnabled(false);
////                            tvApply.setClickable(false);
////                            tvApplytimer.setVisibility(View.GONE);
//                            } else if (viewModel.getResponseData().getIsVerify().equalsIgnoreCase("2")) {
//                                binding.llEmailApply.setEnabled(false);
//                                binding.llEmailApply.setClickable(false);
//                                binding.tlEmail.setError(tryafter);
//                                binding.tlEmail.setErrorEnabled(true);
////                            tvApply.setText("Verify");
////                            tvApplytimer.setVisibility(View.GONE);
////                            tvApply.setClickable(false);
////                            tvApply.setEnabled(false);
////                            tvApply.setTextColor(getResources().getColor(R.color.gray));
//                            }
//                        } else {
//                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ProfileViewModel> call, Throwable t) {
//                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
//                }
//            });
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTENT_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                setProfilePic(profilePicPath);
                if (BWSApplication.isNetworkConnected(getActivity())) {
                    BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                    HashMap<String, String> map = new HashMap<>();
                    map.put(CONSTANTS.PREF_KEY_UserID, CoUserID);
                    TypedFile typedFile = new TypedFile(CONSTANTS.MULTIPART_FORMAT, image);
                    APIClientProfile.getApiService().getAddProfiles(CoUserID, typedFile,
                            new retrofit.Callback<AddProfileModel>() {
                                @Override
                                public void success(AddProfileModel addProfileModel, retrofit.client.Response response) {
                                    if (addProfileModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                                        Properties p = new Properties();
                                        p.putValue("userId", USERID);
                                        p.putValue("coUserId", CoUserID);
                                        BWSApplication.addToSegment("Camera Photo Added", p, CONSTANTS.track);
                                        profilePicPath = addProfileModel.getResponseData().getProfileImage();
                                        setProfilePic(profilePicPath);
                                        BWSApplication.showToast(addProfileModel.getResponseMessage(), getActivity());
                                        profileViewData(getActivity());
                                    }
                                }

                                @Override
                                public void failure(RetrofitError e) {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
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
                    BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                    HashMap<String, String> map = new HashMap<>();
                    map.put(CONSTANTS.PREF_KEY_UserID, CoUserID);
                    File file = new File(FileUtil.getPath(selectedImageUri, getActivity()));

                    TypedFile typedFile = new TypedFile(CONSTANTS.MULTIPART_FORMAT, file);
                    APIClientProfile.getApiService().getAddProfiles(CoUserID, typedFile,
                            new retrofit.Callback<AddProfileModel>() {
                                @Override
                                public void success(AddProfileModel addProfileModel, retrofit.client.Response response) {
                                    if (addProfileModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                                        Properties p = new Properties();
                                        p.putValue("userId", USERID);
                                        p.putValue("coUserId", CoUserID);
                                        BWSApplication.addToSegment("Gallery Photo Added", p, CONSTANTS.track);
                                        profilePicPath = addProfileModel.getResponseData().getProfileImage();
                                        setProfilePic(profilePicPath);
                                        BWSApplication.showToast(addProfileModel.getResponseMessage(), getActivity());
                                        profileViewData(getActivity());
                                    }
                                }

                                @Override
                                public void failure(RetrofitError e) {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
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
            getActivity().finish();
        }
    }

    private void setProfilePic(String profilePicPath) {
        Glide.with(getActivity()).load(profilePicPath)
                .thumbnail(0.10f).apply(RequestOptions.bitmapTransform(new RoundedCorners(126)))
                .into(binding.civProfile);
    }

    void DeleteCall(Dialog dialog, ProgressBar progressBar, FrameLayout progressBarHolder) {
        SharedPreferences preferences = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(CONSTANTS.PREF_KEY_UserID);
        edit.remove(CONSTANTS.PREF_KEY_MobileNo);
        edit.remove(CONSTANTS.PREF_KEY_Name);
        edit.clear();
        edit.commit();

        deleteCache(getActivity());
        callLogoutApi(dialog, progressBar, progressBarHolder);
    }

    private void callLogoutApi(Dialog dialog, ProgressBar progressBar, FrameLayout progressBarHolder) {
        SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE);
        String fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "");
        if (TextUtils.isEmpty(fcm_id)) {
            FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(getActivity(), task -> {
                String newToken = task.getResult().getToken();
                Log.e("newToken", newToken);
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE).edit();
                editor.putString(CONSTANTS.Token, newToken); //Friend
                editor.apply();
                editor.commit();
            });
            fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "");
        }

        Call<SucessModel> listCall = APINewClient.getClient().getLogout(USERID, fcm_id, CONSTANTS.FLAG_ONE);
        listCall.enqueue(new Callback<SucessModel>() {
            @Override
            public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                SucessModel loginModel = response.body();
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                        dialog.hide();
                        analytics.flush();
                        analytics.reset();
                        Properties p1 = new Properties();
                        p1.putValue("userId", USERID);
                        p1.putValue("coUserId", CoUserID);
                        p1.putValue("deviceId", DeviceID);
                        p1.putValue("deviceType", "Android");
                        p1.putValue("userName", UserName);
                        p1.putValue("userEmail", UserEmail);
                        BWSApplication.addToSegment("Signed Out", p1, CONSTANTS.track);
                        Intent i = new Intent(getActivity(), GetStartedActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SucessModel> call, Throwable t) {
                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            }
        });
    }
}