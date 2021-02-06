package com.brainwellnessspa.LoginModule.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;

import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.RoomDataBase.AudioDatabase;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.SmsReceiver;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity;
import com.brainwellnessspa.LoginModule.Models.LoginModel;
import com.brainwellnessspa.LoginModule.Models.OtpModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;

import static com.brainwellnessspa.BWSApplication.MIGRATION_1_2;
import static com.brainwellnessspa.BWSApplication.getKey;
import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.logout;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;

import static com.brainwellnessspa.SplashModule.SplashScreenActivity.analytics;

import com.brainwellnessspa.databinding.ActivityOtpBinding;
import com.google.gson.Gson;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity implements
        SmsReceiver.OTPReceiveListener {
    ActivityOtpBinding binding;
    private SmsReceiver smsReceiver;
    String Name, Code, MobileNo;
    private EditText[] editTexts;
    boolean tvSendOTPbool = true;
    Activity activity;
    CountDownTimer countDownTimer;
    private long mLastClickTime = 0;
    public static int comeLogin = 0;
    AudioDatabase DB;
    List<DownloadAudioDetails> downloadAudioDetails = new ArrayList<>();
    private BroadcastReceiver receiver;
//    AppEventsLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp);

        if (getIntent().getExtras() != null) {
            MobileNo = getIntent().getStringExtra(CONSTANTS.MobileNo);
            Name = getIntent().getStringExtra(CONSTANTS.Name);
            Code = getIntent().getStringExtra(CONSTANTS.Code);
        }
        activity = OtpActivity.this;
//        logger = AppEventsLogger.newLogger(this);
        binding.tvSendCodeText.setText("We sent an SMS with a 4-digit code to " + Code + MobileNo);

        DB = Room.databaseBuilder(this,
                AudioDatabase.class,
                "Audio_database")
                .addMigrations(MIGRATION_1_2)
                .build();
        editTexts = new EditText[]{binding.edtOTP1, binding.edtOTP2, binding.edtOTP3, binding.edtOTP4};
        binding.edtOTP1.addTextChangedListener(new PinTextWatcher(0));
        binding.edtOTP2.addTextChangedListener(new PinTextWatcher(1));
        binding.edtOTP3.addTextChangedListener(new PinTextWatcher(2));
        binding.edtOTP4.addTextChangedListener(new PinTextWatcher(3));
        binding.edtOTP1.setOnKeyListener(new PinOnKeyListener(0));
        binding.edtOTP2.setOnKeyListener(new PinOnKeyListener(1));
        binding.edtOTP3.setOnKeyListener(new PinOnKeyListener(2));
        binding.edtOTP4.setOnKeyListener(new PinOnKeyListener(3));
        startSMSListener();
        binding.txtError.setText("");
        binding.txtError.setVisibility(View.GONE);

        Properties p = new Properties();
        p.putValue("mobileNo", MobileNo);
        p.putValue("countryCode", Code);
        p.putValue("countryName", Name);
        BWSApplication.addToSegment("Otp Screen Viewed", p, CONSTANTS.screen);

        binding.llResendSms.setOnClickListener(view -> {
            if (BWSApplication.isNetworkConnected(OtpActivity.this)) {
                binding.txtError.setText("");
                binding.txtError.setVisibility(View.GONE);
                tvSendOTPbool = false;
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                String countryCode = Code.replace("+", "");
                SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_Splash, MODE_PRIVATE);
                String key = (shared1.getString(CONSTANTS.PREF_KEY_SplashKey, ""));
                if (key.equalsIgnoreCase("")) {
                    key = getKey(OtpActivity.this);
                }
                Call<LoginModel> listCall = APIClient.getClient().getLoginDatas(MobileNo, countryCode, CONSTANTS.FLAG_ONE, CONSTANTS.FLAG_ONE, key);
                listCall.enqueue(new Callback<LoginModel>() {
                    @Override
                    public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        LoginModel loginModel = response.body();
                        if (loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            logout = false;
                            countDownTimer = new CountDownTimer(30000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    binding.llResendSms.setEnabled(false);
                                    binding.tvResendOTP.setText(Html.fromHtml(millisUntilFinished / 1000 + "<font color=\"#999999\">" + " Resent SMS" + "</font>"));
                                }

                                public void onFinish() {
                                    binding.llResendSms.setEnabled(true);
                                    binding.tvResendOTP.setText(getString(R.string.resent_sms));
                                    binding.tvResendOTP.setTextColor(getResources().getColor(R.color.white));
                                    binding.tvResendOTP.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                                    binding.tvResendOTP.getPaint().setMaskFilter(null);
                                }
                            }.start();
                            Properties p = new Properties();
                            p.putValue("mobileNo", MobileNo);
                            p.putValue("countryCode", Code);
                            BWSApplication.addToSegment("SMS Resent", p, CONSTANTS.track);
                            binding.edtOTP1.requestFocus();
                            binding.edtOTP1.setText("");
                            binding.edtOTP2.setText("");
                            binding.edtOTP3.setText("");
                            binding.edtOTP4.setText("");
                            tvSendOTPbool = true;
                            BWSApplication.showToast(loginModel.getResponseMessage(), OtpActivity.this);
                            startSMSListener();
                        } else if (loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))) {
                            binding.txtError.setVisibility(View.VISIBLE);
                            binding.txtError.setText(loginModel.getResponseMessage());
                        } else {
                            binding.txtError.setVisibility(View.VISIBLE);
                            binding.txtError.setText(loginModel.getResponseMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        BWSApplication.showToast(t.getMessage(), OtpActivity.this);
                    }
                });
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), OtpActivity.this);
            }
        });

        binding.llEditNumber.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            comeLogin = 1;
            Intent i = new Intent(OtpActivity.this, LoginActivity.class);
            i.putExtra("Name", Name);
            i.putExtra("Code", Code);
            i.putExtra(CONSTANTS.MobileNo, MobileNo);
            startActivity(i);
            finish();
        });

        binding.btnSendCode.setOnClickListener(view -> {
            SharedPreferences sharedPreferences2 = getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE);
            String fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "");
            if (TextUtils.isEmpty(fcm_id)) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(activity, instanceIdResult -> {
                    String newToken = instanceIdResult.getToken();
                    Log.e("newToken", newToken);
                    SharedPreferences.Editor editor = getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE).edit();
                    editor.putString(CONSTANTS.Token, newToken); //Friend
                    editor.apply();
                    editor.commit();
                });
                SharedPreferences sharedPreferences3 = getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE);
                fcm_id = sharedPreferences3.getString(CONSTANTS.Token, "");
            }
            if (BWSApplication.isNetworkConnected(OtpActivity.this)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                Call<OtpModel> listCall = APIClient.getClient().getAuthOtps(
                        binding.edtOTP1.getText().toString() + "" +
                                binding.edtOTP2.getText().toString() + "" +
                                binding.edtOTP3.getText().toString() + "" +
                                binding.edtOTP4.getText().toString(), fcm_id, CONSTANTS.FLAG_ONE,
                        Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), MobileNo, CONSTANTS.FLAG_ZERO);
                listCall.enqueue(new Callback<OtpModel>() {
                    @Override
                    public void onResponse(Call<OtpModel> call, Response<OtpModel> response) {
                        try {
                            if (response.isSuccessful()) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                OtpModel otpModel = response.body();
                                if (otpModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                    binding.txtError.setVisibility(View.GONE);
                                    if (otpModel.getResponseData().getError().equalsIgnoreCase("0") ||
                                            otpModel.getResponseData().getError().equalsIgnoreCase("")) {
                                        String UserID = otpModel.getResponseData().getUserID();
                                        String MobileNO = otpModel.getResponseData().getPhoneNumber();
                                        analytics.identify(new Traits()
                                                .putValue("userId", UserID)
                                                .putValue("deviceId", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))
                                                .putValue("deviceType", CONSTANTS.FLAG_ONE)
                                                .putValue("countryCode", Code)
                                                .putValue("countryName", Name)
                                                .putValue("userName", otpModel.getResponseData().getName())
                                                .putValue("mobileNo", otpModel.getResponseData().getPhoneNumber())
                                                .putValue("plan", otpModel.getResponseData().getPlan())
                                                .putValue("planStatus", otpModel.getResponseData().getPlanStatus())
                                                .putValue("planStartDt", otpModel.getResponseData().getPlanStartDt())
                                                .putValue("planExpiryDt", otpModel.getResponseData().getPlanExpiryDate())
                                                .putValue("clinikoId", otpModel.getResponseData().getClinikoId()));
                                        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = shared.edit();
                                        editor.putString(CONSTANTS.PREF_KEY_UserID, UserID);
                                        editor.putString(CONSTANTS.PREF_KEY_IsDisclimer, otpModel.getResponseData().getShouldPlayDisclaimer());
                                        editor.putString(CONSTANTS.PREF_KEY_Name, otpModel.getResponseData().getName());
                                        editor.putString(CONSTANTS.PREF_KEY_PlayerFirstLogin, otpModel.getResponseData().getFirstLogin());
                                        editor.putString(CONSTANTS.PREF_KEY_AudioFirstLogin, otpModel.getResponseData().getFirstLogin());
                                        editor.putString(CONSTANTS.PREF_KEY_PlaylistFirstLogin, otpModel.getResponseData().getFirstLogin());
                                        editor.putString(CONSTANTS.PREF_KEY_AccountFirstLogin, otpModel.getResponseData().getFirstLogin());
                                        editor.putString(CONSTANTS.PREF_KEY_ReminderFirstLogin, otpModel.getResponseData().getFirstLogin());
                                        editor.putString(CONSTANTS.PREF_KEY_SearchFirstLogin, otpModel.getResponseData().getFirstLogin());
                                        editor.putString(CONSTANTS.PREF_KEY_MobileNo, MobileNO);
                                        editor.putString(CONSTANTS.PREF_KEY_Email, otpModel.getResponseData().getEmail());
                                        editor.putString(CONSTANTS.PREF_KEY_DeviceType, CONSTANTS.FLAG_ONE);
                                        editor.putBoolean(CONSTANTS.PREF_KEY_Identify, true);
                                        editor.putString(CONSTANTS.PREF_KEY_DeviceID, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                                        editor.commit();

                                        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGOUT, Context.MODE_PRIVATE);
                                        String Logout_UserID = (shared1.getString(CONSTANTS.PREF_KEY_LOGOUT_UserID, ""));
                                        String Logout_MobileNo = (shared1.getString(CONSTANTS.PREF_KEY_LOGOUT_MobileNO, ""));

                                        if (!UserID.equalsIgnoreCase(Logout_UserID)
                                                && !MobileNO.equalsIgnoreCase(Logout_MobileNo)) {
//                                                GetAllMedia();
                                            callObserve1();
                                        } else {
//                                                GetAllMedia2();
                                            callObserve2();
                                        }

                                        Log.e("New UserId MobileNo", UserID + "....." + MobileNO);
                                        Log.e("Old UserId MobileNo", Logout_UserID + "....." + Logout_MobileNo);
                                        logout = false;
                                        BWSApplication.showToast(otpModel.getResponseMessage(), OtpActivity.this);
                                    } else if (otpModel.getResponseData().getError().equalsIgnoreCase("1")) {
                                        binding.txtError.setText(otpModel.getResponseMessage());
                                        binding.txtError.setVisibility(View.VISIBLE);
                                    }
                                } else if (otpModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))) {
                                    binding.txtError.setText(otpModel.getResponseMessage());
                                    binding.txtError.setVisibility(View.VISIBLE);
                                } else {
                                    binding.txtError.setText(otpModel.getResponseMessage());
                                    binding.txtError.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<OtpModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    }
                });
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), OtpActivity.this);
            }
        });
    }

    private void callObserve2() {
        DatabaseClient
                .getInstance(this)
                .getaudioDatabase()
                .taskDao()
                .geAllData12().observe(this, audioList -> {
            List<String> fileNameList = new ArrayList<>();
            List<String> audioFile = new ArrayList<>();
            List<String> playlistDownloadId = new ArrayList<>();

            if (audioList.size() != 0) {
                for (int i = 0; i < audioList.size(); i++) {
                    if (audioList.get(i).getDownloadProgress() < 100) {
                        fileNameList.add(audioList.get(i).getName());
                        audioFile.add(audioList.get(i).getAudioFile());
                        playlistDownloadId.add(audioList.get(i).getPlaylistId());
                    }
                }
            }
            Gson gson = new Gson();
            SharedPreferences sharedxc = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorxc = sharedxc.edit();
            String nameJson = gson.toJson(fileNameList);
            String urlJson = gson.toJson(audioFile);
            String playlistIdJson = gson.toJson(playlistDownloadId);
            editorxc.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
            editorxc.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
            editorxc.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
            editorxc.commit();
            isDownloading = false;
            Intent i = new Intent(OtpActivity.this, DashboardActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void callObserve1() {
        DatabaseClient
                .getInstance(this)
                .getaudioDatabase()
                .taskDao()
                .geAllData12().observe(this, audioList -> {
            if (audioList.size() != 0) {
                for (int i = 0; i < audioList.size(); i++) {
                    FileUtils.deleteDownloadedFile(getApplicationContext(), audioList.get(i).getName());
                }
            }
            SharedPreferences preferences11 = getSharedPreferences(CONSTANTS.PREF_KEY_Logout_DownloadPlaylist, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit1 = preferences11.edit();
            edit1.remove(CONSTANTS.PREF_KEY_Logout_DownloadName);
            edit1.remove(CONSTANTS.PREF_KEY_Logout_DownloadUrl);
            edit1.remove(CONSTANTS.PREF_KEY_Logout_DownloadPlaylistId);
            edit1.clear();
            edit1.commit();
            DeletallLocalCart();

        });
    }

    private void startSMSListener() {
        try {
            smsReceiver = new SmsReceiver();
            smsReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            this.registerReceiver(smsReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(this);

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(aVoid -> {
                // API successfully started
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    /* Fail to start API */
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        comeLogin = 0;
        Intent i = new Intent(OtpActivity.this, LoginActivity.class);
        i.putExtra("Name", Name);
        i.putExtra("Code", Code);
        i.putExtra(CONSTANTS.MobileNo, MobileNo);
        startActivity(i);
        finish();
    }

    public void DeletallLocalCart() {
        AudioDatabase.databaseWriteExecutor.execute(() -> {
            DB.taskDao().deleteAll();
        });
        AudioDatabase.databaseWriteExecutor.execute(() -> {
            DB.taskDao().deleteAllPlalist();
            Intent i = new Intent(OtpActivity.this, DashboardActivity.class);
            startActivity(i);
            finish();
        });
    }

  /*      class DeletallCart extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient
                        .getInstance(OtpActivity.this)
                        .getaudioDatabase()
                        .taskDao()
                        .deleteAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                DeletallLocalCart1();
                super.onPostExecute(aVoid);
            }
        }
        DeletallCart st = new DeletallCart();
        st.execute();
    }

    public void DeletallLocalCart1() {
        class DeletallCart extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient
                        .getInstance(OtpActivity.this)
                        .getaudioDatabase()
                        .taskDao()
                        .deleteAllPlalist();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                Intent i = new Intent(OtpActivity.this, DashboardActivity.class);
                startActivity(i);
                finish();
                super.onPostExecute(aVoid);
            }
        }
        DeletallCart st = new DeletallCart();
        st.execute();
    }*/

    @Override
    public void onOTPReceived(String otp) {
        String[] splited = new String[0];
        if (otp.startsWith("[#] Your OTP is")) {
            splited = otp.split(" ");
        } else if (otp.startsWith("(#) Your OTP is")) {
            splited = otp.split(" ");
        } else if (otp.startsWith("<#> Your OTP is")) {
            splited = otp.split(" ");
        } else if (otp.startsWith("?<#?> Your OTP is")) {
            splited = otp.split(" ");
        } else {
            splited = otp.split(" ");
        }
        String message = splited[4];
        binding.edtOTP1.setText(String.valueOf(message.charAt(0)));
        binding.edtOTP2.setText(String.valueOf(message.charAt(1)));
        binding.edtOTP3.setText(String.valueOf(message.charAt(2)));
        binding.edtOTP4.setText(String.valueOf(message.charAt(3)));

        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }
    }

    @Override
    public void onOTPTimeOut() {
//        showToast("OTP Time out");

    }

    @Override
    public void onOTPReceivedError(String error) {
//        showToast(error);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
    }


    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    public class PinTextWatcher implements TextWatcher {
        private int currentIndex;
        private boolean isFirst = false, isLast = false;
        private String newTypedString = "";

        PinTextWatcher(int currentIndex) {
            this.currentIndex = currentIndex;

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
            String OTP1 = binding.edtOTP1.getText().toString().trim();
            String OTP2 = binding.edtOTP2.getText().toString().trim();
            String OTP3 = binding.edtOTP3.getText().toString().trim();
            String OTP4 = binding.edtOTP4.getText().toString().trim();
            if (!OTP1.isEmpty() && !OTP2.isEmpty() && !OTP3.isEmpty() && !OTP4.isEmpty()) {
                binding.btnSendCode.setEnabled(true);
                binding.btnSendCode.setTextColor(getResources().getColor(R.color.white));
                binding.btnSendCode.setBackgroundResource(R.drawable.extra_round_cornor);
            } else {
                binding.btnSendCode.setEnabled(false);
                binding.btnSendCode.setTextColor(getResources().getColor(R.color.white));
                binding.btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor);
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
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
}