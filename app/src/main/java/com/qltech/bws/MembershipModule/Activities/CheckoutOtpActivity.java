package com.qltech.bws.MembershipModule.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.BillingOrderModule.Models.CardModel;
import com.qltech.bws.LoginModule.Models.LoginModel;
import com.qltech.bws.LoginModule.Models.OtpModel;
import com.qltech.bws.MembershipModule.Models.MembershipPlanListModel;
import com.qltech.bws.R;
import com.qltech.bws.SplashModule.SplashScreenActivity;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityCheckoutOtpBinding;

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
        binding.tvSendCodeText.setText("We sent an SMS with a 4-digit code to +" + Code + MobileNo);
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

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, CheckoutGetCodeActivity.class);
                i.putExtra("MobileNo",MobileNo);
                i.putExtra("Name", Name);
                i.putExtra("Code", Code);
                startActivity(i);
                finish();
            }
        });
        binding.btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                if (binding.edtOTP1.getText().toString().equalsIgnoreCase("") ||
                        binding.edtOTP2.getText().toString().equalsIgnoreCase("") ||
                        binding.edtOTP3.getText().toString().equalsIgnoreCase("") ||
                        binding.edtOTP4.getText().toString().equalsIgnoreCase("")) {
                    binding.txtError.setText("Wait a sec! We need to exchange digits to get started");
                    binding.txtError.setVisibility(View.VISIBLE);
                } else {
                    if (BWSApplication.isNetworkConnected(CheckoutOtpActivity.this)) {
                        BWSApplication.showProgressBar(binding.ImgV,binding.progressBarHolder,activity);

                        Call<CardModel> listCall = APIClient.getClient().getAuthOtps1(
                                binding.edtOTP1.getText().toString() + "" +
                                        binding.edtOTP2.getText().toString() + "" +
                                        binding.edtOTP3.getText().toString() + "" +
                                        binding.edtOTP4.getText().toString(), fcm_id, CONSTANTS.FLAG_ONE,
                                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), MobileNo, CONSTANTS.FLAG_ONE);
                        listCall.enqueue(new Callback<CardModel>() {
                            @Override
                            public void onResponse(Call<CardModel> call, Response<CardModel> response) {
                                if (response.isSuccessful()) {
                                    BWSApplication.hideProgressBar(binding.ImgV,binding.progressBarHolder,activity);

                                    CardModel otpModel = response.body();
                                    Intent i = new Intent(CheckoutOtpActivity.this, CheckoutPaymentActivity.class);
                                    i.putExtra("MobileNo",MobileNo);
                                    startActivity(i);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<CardModel> call, Throwable t) {
                                BWSApplication.hideProgressBar(binding.ImgV,binding.progressBarHolder,activity);

                            }
                        });
                    } else {
                        Toast.makeText(CheckoutOtpActivity.this, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        binding.llResendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareData();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ctx, CheckoutGetCodeActivity.class);
        i.putExtra("MobileNo",MobileNo);
        i.putExtra("Name", Name);
        i.putExtra("Code", Code);
        startActivity(i);
        finish();
    }

    void prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            tvSendOTPbool = false;
            BWSApplication.showProgressBar(binding.ImgV,binding.progressBarHolder,activity);

            Call<LoginModel> listCall = APIClient.getClient().getSignUpDatas(MobileNo, Code, CONSTANTS.FLAG_ONE, CONSTANTS.FLAG_ONE, SplashScreenActivity.key);
            listCall.enqueue(new Callback<LoginModel>() {
                @Override
                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV,binding.progressBarHolder,activity);

                        LoginModel loginModel = response.body();
                        if (loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            tvSendOTPbool = true;
                            binding.edtOTP1.requestFocus();
                            binding.edtOTP1.setText("");
                            binding.edtOTP2.setText("");
                            binding.edtOTP3.setText("");
                            binding.edtOTP4.setText("");
                        } else {
                            binding.txtError.setVisibility(View.VISIBLE);
                            binding.txtError.setText(loginModel.getResponseMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV,binding.progressBarHolder,activity);

                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
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