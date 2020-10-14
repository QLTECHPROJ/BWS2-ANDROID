package com.brainwellnessspa.MembershipModule.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.LoginModule.Models.OtpModel;
import com.brainwellnessspa.MembershipModule.Models.MembershipPlanListModel;
import com.brainwellnessspa.MembershipModule.Models.SignUpModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.SplashModule.SplashScreenActivity;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityCheckoutOtpBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutOtpActivity extends AppCompatActivity {
    String Name, Code, MobileNo;
    private EditText[] editTexts;
    boolean tvSendOTPbool = true;
    Activity activity;
    Context ctx;
    String TrialPeriod;
    private ArrayList<MembershipPlanListModel.Plan> listModelList;
    int position;
    ActivityCheckoutOtpBinding binding;
    CountDownTimer countDownTimer;
    private long mLastClickTime = 0;
    public static int comeRegister = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_checkout_otp);

        if (getIntent().getExtras() != null) {
            MobileNo = getIntent().getStringExtra(CONSTANTS.MobileNo);
            Name = getIntent().getStringExtra(CONSTANTS.Name);
            Code = getIntent().getStringExtra(CONSTANTS.Code);
//            TrialPeriod = getIntent().getStringExtra("TrialPeriod");
//            listModelList = getIntent().getParcelableArrayListExtra("PlanData");
//            position = getIntent().getIntExtra("position", 0);
        }
        activity = CheckoutOtpActivity.this;
        ctx = CheckoutOtpActivity.this;
        binding.tvSendCodeText.setText("We sent an SMS with a 4-digit code to " + Code + MobileNo);
        editTexts = new EditText[]{binding.edtOTP1, binding.edtOTP2, binding.edtOTP3, binding.edtOTP4};
        binding.edtOTP1.addTextChangedListener(new PinTextWatcher(0));
        binding.edtOTP2.addTextChangedListener(new PinTextWatcher(1));
        binding.edtOTP3.addTextChangedListener(new PinTextWatcher(2));
        binding.edtOTP4.addTextChangedListener(new PinTextWatcher(3));
        binding.edtOTP1.setOnKeyListener(new PinOnKeyListener(0));
        binding.edtOTP2.setOnKeyListener(new PinOnKeyListener(1));
        binding.edtOTP3.setOnKeyListener(new PinOnKeyListener(2));
        binding.edtOTP4.setOnKeyListener(new PinOnKeyListener(3));

        binding.txtError.setText("");
        binding.txtError.setVisibility(View.GONE);

        binding.llBack.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            comeRegister = 0;
            Intent i = new Intent(ctx, CheckoutGetCodeActivity.class);
            i.putExtra("MobileNo", MobileNo);
            i.putExtra("Name", Name);
            i.putExtra("Code", Code);
            startActivity(i);
            finish();
        });

        binding.llEditNumber.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            comeRegister = 1;
            Intent i = new Intent(ctx, CheckoutGetCodeActivity.class);
            i.putExtra("MobileNo", MobileNo);
            i.putExtra("Name", Name);
            i.putExtra("Code", Code);
            startActivity(i);
            finish();
        });

        binding.btnSendCode.setOnClickListener(view -> {
            SharedPreferences sharedPreferences2 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE);
            String fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "");
            if (TextUtils.isEmpty(fcm_id)) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(activity, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newToken = instanceIdResult.getToken();
                        Log.e("newToken", newToken);
                        SharedPreferences.Editor editor = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE).edit();
                        editor.putString(CONSTANTS.Token, newToken); //Friend
                        editor.apply();
                        editor.commit();
                    }
                });
                fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "");
            }
            if (BWSApplication.isNetworkConnected(CheckoutOtpActivity.this)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                String deviceid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                Call<OtpModel> listCall = APIClient.getClient().getAuthOtps1(
                        binding.edtOTP1.getText().toString() + "" +
                                binding.edtOTP2.getText().toString() + "" +
                                binding.edtOTP3.getText().toString() + "" +
                                binding.edtOTP4.getText().toString(), fcm_id, CONSTANTS.FLAG_ONE, deviceid
                        , MobileNo, CONSTANTS.FLAG_ONE);
                listCall.enqueue(new Callback<OtpModel>() {
                    @Override
                    public void onResponse(Call<OtpModel> call, Response<OtpModel> response) {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            OtpModel otpModel = response.body();
                            if (otpModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                Intent i = new Intent(CheckoutOtpActivity.this, CheckoutPaymentActivity.class);
                                i.putExtra("MobileNo", MobileNo);
                                startActivity(i);
                                finish();
                            } else if (otpModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))) {
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
                BWSApplication.showToast(getString(R.string.no_server_found), getApplicationContext());
            }
        });

        binding.llResendSms.setOnClickListener(view -> prepareData());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        comeRegister = 0;
        Intent i = new Intent(ctx, CheckoutGetCodeActivity.class);
        i.putExtra("MobileNo", MobileNo);
        i.putExtra("Name", Name);
        i.putExtra("Code", Code);
        startActivity(i);
        finish();
    }

    void prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            tvSendOTPbool = false;
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SignUpModel> listCall = APIClient.getClient().getSignUpDatas(MobileNo, Code, CONSTANTS.FLAG_ONE, CONSTANTS.FLAG_ONE, SplashScreenActivity.key);
            listCall.enqueue(new Callback<SignUpModel>() {
                @Override
                public void onResponse(Call<SignUpModel> call, Response<SignUpModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        SignUpModel loginModel = response.body();
                        if (loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            countDownTimer = new CountDownTimer(30000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    binding.llResendSms.setEnabled(false);
                                    binding.tvResendOTP.setText(Html.fromHtml(millisUntilFinished / 1000 + "<font color=\"#999999\">" + " Resent SMS" + "</font>"));
                                }

                                public void onFinish() {
                                    binding.llResendSms.setEnabled(true);
                                    binding.tvResendOTP.setText(getString(R.string.resent_sms));
                                    binding.tvResendOTP.setTextColor(getResources().getColor(R.color.dark_blue_gray));
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
                            BWSApplication.showToast(loginModel.getResponseMessage(), getApplicationContext());
                        } else if (loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))) {
                            binding.txtError.setVisibility(View.VISIBLE);
                            binding.txtError.setText(loginModel.getResponseMessage());
                        } else {
                            binding.txtError.setVisibility(View.VISIBLE);
                            binding.txtError.setText(loginModel.getResponseMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<SignUpModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    BWSApplication.showToast(t.getMessage(), getApplicationContext());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getApplicationContext());
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