package com.qltech.bws.UserModule.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BuildConfig;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.UserModule.Models.AddProfileModel;
import com.qltech.bws.UserModule.Models.ProfileUpdateModel;
import com.qltech.bws.UserModule.Models.ProfileViewModel;
import com.qltech.bws.UserModule.Models.RemoveProfileModel;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.APIClientProfile;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityUserProfileBinding;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit.RetrofitError;
import retrofit.mime.TypedFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {
    ActivityUserProfileBinding binding;
    Context ctx;
    String UserID, profilePicPath = "", tryafter = "Try after 5 minutes";
    File image;
    Activity activity;
    CharSequence[] options;
    public int BirthYear;
    private static final int CONTENT_REQUEST = 100;
    RequestPermissionHandler mRequestPermissionHandler;
    private int mYear, mMonth, mDay;
    int ageYear, ageMonth, ageDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);
        ctx = UserProfileActivity.this;
        activity = UserProfileActivity.this;
        mRequestPermissionHandler = new RequestPermissionHandler();

        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        profileViewData(ctx);
        binding.llBack.setOnClickListener(view -> finish());

        binding.rlImageUpload.setOnClickListener(view -> selectImage());
        binding.btnSave.setOnClickListener(view -> profileUpdate());

        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 20,
                1, 1, 0.4f, 20);
        binding.civProfile.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.civProfile.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
    }


    void profileUpdate() {
        binding.flUser.setError("");
        binding.tlMobileNumber.setError("");
        binding.tlCalendar.setError("");
        binding.tlEmail.setError("");
        if (binding.etUser.getText().toString().equalsIgnoreCase("") &&
                binding.etCalendar.getText().toString().equalsIgnoreCase("") &&
                binding.etMobileNumber.getText().toString().equalsIgnoreCase("") &&
                binding.etEmail.getText().toString().equalsIgnoreCase("")) {
            binding.flUser.setError("Please enter your full name");
            binding.tlCalendar.setError("please enter date of birth");
            binding.tlMobileNumber.setError("please enter mobile number");
            binding.tlEmail.setError("Please provide a valid email address");
        } else if (binding.etUser.getText().toString().equalsIgnoreCase("") &&
                binding.etCalendar.getText().toString().equalsIgnoreCase("") &&
                binding.etEmail.getText().toString().equalsIgnoreCase("")) {
            binding.flUser.setError("Please enter your full name");
            binding.tlCalendar.setError("please enter date of birth");
            binding.tlEmail.setError("Please provide a valid email address");
        } else if (!binding.etUser.getText().toString().equalsIgnoreCase("") &&
                binding.etCalendar.getText().toString().equalsIgnoreCase("") &&
                binding.etEmail.getText().toString().equalsIgnoreCase("")) {
            binding.tlCalendar.setError("please enter date of birth");
            binding.tlEmail.setError("Please provide a valid email address");
        } else if (binding.etUser.getText().toString().equalsIgnoreCase("") &&
                binding.etCalendar.getText().toString().equalsIgnoreCase("") &&
                !binding.etEmail.getText().toString().equalsIgnoreCase("")) {
            binding.flUser.setError("Please enter your full name");
            binding.tlCalendar.setError("please enter date of birth");
        } else if (binding.etUser.getText().toString().equalsIgnoreCase("") &&
                !binding.etCalendar.getText().toString().equalsIgnoreCase("") &&
                binding.etEmail.getText().toString().equalsIgnoreCase("")) {
            binding.flUser.setError("Please enter your full name");
            binding.tlEmail.setError("Please provide a valid email address");
        } else if (binding.etUser.getText().toString().equalsIgnoreCase("")) {
            binding.flUser.setError("Please enter your full name");
        } else if (binding.etCalendar.getText().toString().equalsIgnoreCase("")) {
            binding.tlCalendar.setError("please enter date of birth");
        } else if (binding.etMobileNumber.getText().toString().equalsIgnoreCase("")) {
            binding.tlMobileNumber.setError("please enter mobile number");
        } else if (binding.etEmail.getText().toString().equalsIgnoreCase("")) {
            binding.tlEmail.setError("Please enter your email address");
        } else if (!binding.etEmail.getText().toString().equalsIgnoreCase("")
                && !BWSApplication.isEmailValid(binding.etEmail.getText().toString())) {
            binding.tlEmail.setError("Please provide a valid email address");
        } else {
            binding.flUser.setError("");
            binding.tlCalendar.setError("");
            binding.flUser.clearFocus();
            binding.tlEmail.clearFocus();
            if (BWSApplication.isNetworkConnected(ctx)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                String dob = "";
                if (!binding.etCalendar.getText().toString().isEmpty()) {
                    dob = binding.etCalendar.getText().toString();
                    SimpleDateFormat spf = new SimpleDateFormat(CONSTANTS.MONTH_DATE_YEAR_FORMAT);
                    Date newDate = null;
                    try {
                        newDate = spf.parse(dob);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    spf = new SimpleDateFormat(CONSTANTS.YEAR_TO_DATE_FORMAT);
                    dob = spf.format(newDate);
                }
                Call<ProfileUpdateModel> listCall = APIClient.getClient().getProfileUpdate(UserID, binding.etUser.getText().toString(), dob,
                        binding.etMobileNumber.getText().toString(), binding.etEmail.getText().toString(), "");
                listCall.enqueue(new Callback<ProfileUpdateModel>() {
                    @Override
                    public void onResponse(Call<ProfileUpdateModel> call, Response<ProfileUpdateModel> response) {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            ProfileUpdateModel viewModel = response.body();
                            finish();
                            BWSApplication.showToast(viewModel.getResponseMessage(), ctx);
                        } else {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileUpdateModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void setDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    view.setMinDate(System.currentTimeMillis() - 1000);
                    Calendar cal = Calendar.getInstance();
                    cal.getTimeInMillis();
                    cal.set(year, monthOfYear, dayOfMonth);
                    Date date = cal.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat(CONSTANTS.MONTH_DATE_YEAR_FORMAT);
                    String strDate = sdf.format(date);

                    ageYear = year;
                    ageMonth = monthOfYear;
                    ageDate = dayOfMonth;

                    BirthYear = getAge(ageYear, ageMonth, ageDate);
                    if (BirthYear < 18) {
                        binding.tlCalendar.setError("You must be 18 years of age to register");
                        binding.btnSave.setEnabled(false);
                        binding.btnSave.setClickable(false);
                    } else {
                        binding.tlCalendar.setError("");
                        binding.btnSave.setEnabled(true);
                        binding.btnSave.setClickable(true);
                    }
                    binding.etCalendar.setText(strDate);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public int getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        int ageS = ageInt;
        return ageS;
    }

    void profileViewData(Context ctx) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<ProfileViewModel> listCall = APIClient.getClient().getProfileView(UserID);
            listCall.enqueue(new Callback<ProfileViewModel>() {
                @Override
                public void onResponse(Call<ProfileViewModel> call, Response<ProfileViewModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        ProfileViewModel viewModel = response.body();

                        if (viewModel.getResponseData().getName().equalsIgnoreCase("") ||
                                viewModel.getResponseData().getName().equalsIgnoreCase(" ") ||
                                viewModel.getResponseData().getName() == null) {
                            binding.etUser.setText(R.string.Guest);
                        } else {
                            binding.etUser.setText(viewModel.getResponseData().getName());
                        }

                        profilePicPath = viewModel.getResponseData().getImage();
                        Glide.with(getApplicationContext()).load(profilePicPath)
                                .placeholder(R.drawable.default_profile)
                                .thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(false).into(binding.civProfile);

                        if (viewModel.getResponseData().getDOB().equalsIgnoreCase("0000-00-00")) {
                            binding.etCalendar.setText("");
                        } else {
                            String date = viewModel.getResponseData().getDOB();
                            SimpleDateFormat spf = new SimpleDateFormat(CONSTANTS.YEAR_TO_DATE_FORMAT);
                            if (!date.isEmpty()) {
                                Date newDate = null;
                                try {
                                    newDate = spf.parse(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                spf = new SimpleDateFormat(CONSTANTS.MONTH_DATE_YEAR_FORMAT);
                                date = spf.format(newDate);
                                binding.etCalendar.setText(date);
                            }
                        }

                        if (!viewModel.getResponseData().getEmail().equalsIgnoreCase("")
                                && BWSApplication.isEmailValid(viewModel.getResponseData().getEmail())) {
                            binding.ivCheckEmail.setColorFilter(ContextCompat.getColor(ctx, R.color.green_dark), android.graphics.PorterDuff.Mode.SRC_IN);
                        } else {
                            binding.ivCheckEmail.setColorFilter(ContextCompat.getColor(ctx, R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);

                        }
                        binding.etEmail.setText(viewModel.getResponseData().getEmail());
                        binding.etMobileNumber.setText(viewModel.getResponseData().getPhoneNumber());

                        if (!viewModel.getResponseData().getEmail().equalsIgnoreCase("")) {
                            binding.etEmail.setEnabled(true);
                            binding.etEmail.setClickable(true);
                        } else {
                            binding.etEmail.setEnabled(true);
                            binding.etEmail.setClickable(true);
                        }

                        if (!viewModel.getResponseData().getPhoneNumber().equalsIgnoreCase("")) {
                            binding.etMobileNumber.setEnabled(false);
                            binding.etMobileNumber.setClickable(false);
                        } else {
                            binding.etMobileNumber.setEnabled(true);
                            binding.etMobileNumber.setClickable(true);
                        }

//                        if (viewModel.getResponseData().getPatientid().equalsIgnoreCase("1")) { /*Enable */
                        binding.btnSave.setBackgroundResource(R.drawable.gray_extra_round_corners);
                        binding.btnSave.setClickable(true);
                        binding.btnSave.setEnabled(true);

                        binding.etCalendar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setDate();
                            }
                        });

                        /*  } else if (viewModel.getResponseData().getPatientid().equalsIgnoreCase("0")) { *//*Disable *//*
                            binding.btnSave.setBackgroundResource(R.drawable.gray_extra_light_round_corners);
                            binding.btnSave.setClickable(false);
                            binding.btnSave.setEnabled(false);
                        }*/

                        if ((viewModel.getResponseData().getIsVerify().equalsIgnoreCase("0"))) {
                            binding.ivCheckEmail.setVisibility(View.GONE);
                            binding.llEmailApply.setClickable(true);
                            binding.llEmailApply.setEnabled(true);
                            binding.tlEmail.setErrorEnabled(false);
                            binding.tlEmail.clearFocus();
//                            tvApply.setEnabled(true);
//                            tvApply.setClickable(true);
//                            tvApply.setText("Verify");
//                            tvApply.setTextColor(getResources().getColor(R.color.gray));
//                            tvApplytimer.setVisibility(View.GONE);
                        } else if (viewModel.getResponseData().getIsVerify().equalsIgnoreCase("1")) {
                            binding.ivCheckEmail.setVisibility(View.VISIBLE);
                            binding.llEmailApply.setClickable(false);
                            binding.llEmailApply.setEnabled(false);
                            binding.tlEmail.setErrorEnabled(false);
                            binding.tlEmail.clearFocus();
//                            tvApply.setText("Verified");
//                            tvApply.setTextColor(getResources().getColor(R.color.green));
//                            tvApply.setEnabled(false);
//                            tvApply.setClickable(false);
//                            tvApplytimer.setVisibility(View.GONE);
                        } else if (viewModel.getResponseData().getIsVerify().equalsIgnoreCase("2")) {
                            binding.llEmailApply.setEnabled(false);
                            binding.llEmailApply.setClickable(false);
                            binding.tlEmail.setError(tryafter);
                            binding.tlEmail.setErrorEnabled(true);
//                            tvApply.setText("Verify");
//                            tvApplytimer.setVisibility(View.GONE);
//                            tvApply.setClickable(false);
//                            tvApply.setEnabled(false);
//                            tvApply.setTextColor(getResources().getColor(R.color.gray));
                        }
                    } else {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    }
                }

                @Override
                public void onFailure(Call<ProfileViewModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        }
    }

    private void selectImage() {
        mRequestPermissionHandler.requestPermission(activity, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 123, new RequestPermissionHandler.RequestPermissionListener() {
            @Override
            public void onSuccess() {
                if (profilePicPath.equals("")) {
                    options = new String[]{getString(R.string.takePhoto), getString(R.string.chooseFromGallary), getString(R.string.cancel)};
                } else {
                    options = new String[]{getString(R.string.takePhoto), getString(R.string.chooseFromGallary), getString(R.string.removeProfilePicture), getString(R.string.cancel)};
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle(R.string.addPhoto);
                builder.setItems(options, (dialog, item) -> {
                    if (options[item].equals(getString(R.string.takePhoto))) {
                        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                            }

                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(ctx,
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
                        if (BWSApplication.isNetworkConnected(ctx)) {
                            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            Call<RemoveProfileModel> listCall = APIClient.getClient().getRemoveProfile(UserID);
                            listCall.enqueue(new Callback<RemoveProfileModel>() {
                                @Override
                                public void onResponse(Call<RemoveProfileModel> call, Response<RemoveProfileModel> response) {
                                    if (response.isSuccessful()) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                        RemoveProfileModel viewModel = response.body();
                                        BWSApplication.showToast(viewModel.getResponseMessage(), ctx);
                                        profileViewData(ctx);
                                    }
                                }

                                @Override
                                public void onFailure(Call<RemoveProfileModel> call, Throwable t) {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                }
                            });
                        }
                    } else if (options[item].equals(getString(R.string.cancel))) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert11 = builder.create();
                alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                alert11.show();
            }

            @Override
            public void onFailed() {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        profileViewData(ctx);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTENT_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                Glide.with(this).load(imageFilePath)
                        .placeholder(R.drawable.default_profile)
                        .thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false).into(binding.civProfile);
                if (BWSApplication.isNetworkConnected(ctx)) {
                    BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    HashMap<String, String> map = new HashMap<>();
                    map.put(CONSTANTS.PREF_KEY_UserID, UserID);
                    TypedFile typedFile = new TypedFile(CONSTANTS.MULTIPART_FORMAT, image);
                    APIClientProfile.getApiService().getAddProfiles(UserID, typedFile,
                            new retrofit.Callback<AddProfileModel>() {
                                @Override
                                public void success(AddProfileModel addProfileModel, retrofit.client.Response response) {
                                    if (addProfileModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                        profilePicPath = addProfileModel.getResponseData().getProfileImage();
                                        Glide.with(getApplicationContext()).load(profilePicPath)
                                                .placeholder(R.drawable.default_profile)
                                                .thumbnail(1f).diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .skipMemoryCache(false).into(binding.civProfile);
                                        BWSApplication.showToast(addProfileModel.getResponseMessage(), ctx);
                                    }
                                }

                                @Override
                                public void failure(RetrofitError e) {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                    BWSApplication.showToast(e.getMessage(), ctx);
                                }
                            });
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                Glide.with(this).load(selectedImageUri)
                        .placeholder(R.drawable.default_profile)
                        .thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false).into(binding.civProfile);
                if (BWSApplication.isNetworkConnected(ctx)) {
                    BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    HashMap<String, String> map = new HashMap<>();
                    map.put(CONSTANTS.PREF_KEY_UserID, UserID);
                    File file = new File(FileUtil.getPath(selectedImageUri, this));

                    TypedFile typedFile = new TypedFile(CONSTANTS.MULTIPART_FORMAT, file);
                    APIClientProfile.getApiService().getAddProfiles(UserID, typedFile,
                            new retrofit.Callback<AddProfileModel>() {
                                @Override
                                public void success(AddProfileModel addProfileModel, retrofit.client.Response response) {
                                    if (addProfileModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                        profilePicPath = addProfileModel.getResponseData().getProfileImage();
                                        Glide.with(getApplicationContext()).load(profilePicPath)
                                                .placeholder(R.drawable.default_profile)
                                                .thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .skipMemoryCache(false).into(binding.civProfile);
                                        BWSApplication.showToast(addProfileModel.getResponseMessage(), ctx);
                                    }
                                }

                                @Override
                                public void failure(RetrofitError e) {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                    BWSApplication.showToast(e.getMessage(), ctx);
                                }
                            });
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                }
            }
        } else if (requestCode == RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        }
    }

    String imageFilePath;

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }
}