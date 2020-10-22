package com.brainwellnessspa.LoginModule.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.SmsReceiver;
import com.facebook.appevents.AppEventsLogger;
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
import com.brainwellnessspa.SplashModule.SplashScreenActivity;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.logout;
import com.brainwellnessspa.databinding.ActivityOtpBinding;

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
    List<DownloadAudioDetails> downloadAudioDetails=new ArrayList<>();
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

        binding.btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "");
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
                            if (response.isSuccessful()) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                OtpModel otpModel = response.body();
                                if (otpModel.getResponseData().getError().equalsIgnoreCase("0") ||
                                        otpModel.getResponseData().getError().equalsIgnoreCase("")) {
                                    SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = shared.edit();
                                    String UserID =  otpModel.getResponseData().getUserID();
                                    String MobileNO=  otpModel.getResponseData().getPhoneNumber();
                                    editor.putString(CONSTANTS.PREF_KEY_UserID,UserID);
                                    editor.putString(CONSTANTS.PREF_KEY_MobileNo,MobileNO );
                                    editor.commit();
                                    SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGOUT, Context.MODE_PRIVATE);
                                    String Logout_UserID = (shared1.getString(CONSTANTS.PREF_KEY_LOGOUT_UserID, ""));
                                    String Logout_MobileNo = (shared1.getString(CONSTANTS.PREF_KEY_LOGOUT_MobileNO, ""));

                                    if(!UserID.equalsIgnoreCase(Logout_UserID)
                                    && !MobileNO.equalsIgnoreCase(Logout_MobileNo)){
                                        GetAllMedia();
                                    }

                                    Log.e("New UserId MobileNo",UserID+"....." +MobileNO);
                                    Log.e("Old UserId MobileNo",Logout_UserID+"....." + Logout_MobileNo);
                                    logout = false;
                                    BWSApplication.showToast(otpModel.getResponseMessage(), OtpActivity.this);
                                    Intent i = new Intent(OtpActivity.this, DashboardActivity.class);
                                    startActivity(i);
                                    finish();
                                } else if (otpModel.getResponseData().getError().equalsIgnoreCase("1")) {
                                    binding.txtError.setText(otpModel.getResponseMessage());
                                    binding.txtError.setVisibility(View.VISIBLE);
                                }
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

            }
        });

        binding.llResendSms.setOnClickListener(view -> prepareData());
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
    public void GetAllMedia() {

        class GetTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                downloadAudioDetails = DatabaseClient
                        .getInstance(OtpActivity.this)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllData1();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(downloadAudioDetails.size()!=0){
                    for(int i = 0;i<downloadAudioDetails.size();i++){
                        FileUtils.deleteDownloadedFile(getApplicationContext(), downloadAudioDetails.get(i).getName());
                    }
                }
                DeletallLocalCart();
                DeletallLocalCart1();
            }
        }

        GetTask st = new GetTask();
        st.execute();
    }
    public void DeletallLocalCart() {
        class DeletallCart extends AsyncTask<Void, Void, Void> {
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
                super.onPostExecute(aVoid);
            }
        }
        DeletallCart st = new DeletallCart();
        st.execute();
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

    void prepareData() {
        if (BWSApplication.isNetworkConnected(OtpActivity.this)) {
            tvSendOTPbool = false;
            binding.txtError.setText("");
            binding.txtError.setVisibility(View.GONE);
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            String countryCode = Code.replace("+","");
            Call<LoginModel> listCall = APIClient.getClient().getLoginDatas(MobileNo, countryCode, CONSTANTS.FLAG_ONE, CONSTANTS.FLAG_ONE, SplashScreenActivity.key);
            listCall.enqueue(new Callback<LoginModel>() {
                @Override
                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                    if (response.isSuccessful()) {
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
                            binding.edtOTP1.requestFocus();
                            binding.edtOTP1.setText("");
                            binding.edtOTP2.setText("");
                            binding.edtOTP3.setText("");
                            binding.edtOTP4.setText("");
                            tvSendOTPbool = true;
                            BWSApplication.showToast(loginModel.getResponseMessage(), OtpActivity.this);
                        } else {
                            binding.txtError.setVisibility(View.VISIBLE);
                            binding.txtError.setText(loginModel.getResponseMessage());
                        }
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