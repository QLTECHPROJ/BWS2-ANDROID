package com.brainwellnessspa.MembershipModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.LoginModule.Activities.CountrySelectActivity;
import com.brainwellnessspa.MembershipModule.Models.MembershipPlanListModel;
import com.brainwellnessspa.MembershipModule.Models.SignUpModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.SplashModule.SplashScreenActivity;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityCheckoutGetCodeBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.BWSApplication.getKey;
import static com.brainwellnessspa.MembershipModule.Activities.CheckoutOtpActivity.comeRegister;

public class CheckoutGetCodeActivity extends AppCompatActivity {
    ActivityCheckoutGetCodeBinding binding;
    String Name = "", Code = "", MobileNo = "";
    Context ctx;
    Activity activity;
    String TrialPeriod;
    private ArrayList<MembershipPlanListModel.Plan> listModelList;
    int position;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_checkout_get_code);
        ctx = CheckoutGetCodeActivity.this;
        activity = CheckoutGetCodeActivity.this;

        if (getIntent().hasExtra("Name")) {
            Name = getIntent().getStringExtra(CONSTANTS.Name);
            Code = getIntent().getStringExtra(CONSTANTS.Code);
            MobileNo = getIntent().getStringExtra(CONSTANTS.MobileNo);
//            TrialPeriod = getIntent().getStringExtra("TrialPeriod");
//            listModelList = getIntent().getParcelableArrayListExtra("PlanData");
//            position = getIntent().getIntExtra("position", 0);
        }
        binding.edtNumber.addTextChangedListener(signupTextWatcher);

        if (Code.equalsIgnoreCase("") || Name.equalsIgnoreCase("")) {
            binding.tvCountryCode.setText(R.string.code);
            binding.tvCountry.setText(R.string.Australia);
        } else {
            binding.tvCountryCode.setText(Code);
            binding.tvCountry.setText(Name);
        }

        if (MobileNo.equalsIgnoreCase("")) {
            binding.edtNumber.setText("");
        } else {
            binding.edtNumber.setText(MobileNo);
        }

        binding.llBack.setOnClickListener(view -> {
//                Intent i = new Intent(ctx, OrderSummaryActivity.class);
//                i.putParcelableArrayListExtra("PlanData",listModelList);
//                i.putExtra("TrialPeriod",TrialPeriod);
//                i.putExtra("position",position);
//                startActivity(i);
            finish();
        });

        binding.rlCountrySelect.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(ctx, CountrySelectActivity.class);
//                i.putParcelableArrayListExtra("PlanData",listModelList);
//                i.putExtra("TrialPeriod",TrialPeriod);
//                i.putExtra("position",position);
            i.putExtra("Name", binding.tvCountry.getText().toString());
            i.putExtra("Code", binding.tvCountryCode.getText().toString());
            i.putExtra("MobileNo", binding.edtNumber.getText().toString());
            i.putExtra("Check", "0");
            startActivity(i);
            finish();
        });

        if (comeRegister == 1) {
            binding.edtNumber.requestFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(binding.edtNumber.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        binding.btnSendCode.setOnClickListener(view -> prepareData());
    }

    void prepareData() {
        if (binding.edtNumber.getText().toString().length() == 0 || binding.edtNumber.getText().toString().length() < 8 ||
                binding.edtNumber.getText().toString().length() > 10) {
            binding.edtNumber.setFocusable(true);
            binding.edtNumber.requestFocus();
            binding.txtError.setVisibility(View.VISIBLE);
            binding.txtError.setText(getString(R.string.not_valids_mobile_number));
        } else {
            binding.txtError.setVisibility(View.GONE);
            if (BWSApplication.isNetworkConnected(ctx)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                String countryCode = binding.tvCountryCode.getText().toString().replace("+", "");
                SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_Splash, MODE_PRIVATE);
                String key = (shared1.getString(CONSTANTS.PREF_KEY_SplashKey, ""));
                if (key.equalsIgnoreCase("")) {
                    key = getKey(ctx);
                }
                Call<SignUpModel> listCall = APIClient.getClient().getSignUpDatas(binding.edtNumber.getText().toString(), countryCode, CONSTANTS.FLAG_ONE, CONSTANTS.FLAG_ZERO, key);
                listCall.enqueue(new Callback<SignUpModel>() {
                    @Override
                    public void onResponse(Call<SignUpModel> call, Response<SignUpModel> response) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        try {
                            if (response.isSuccessful()) {
                                SignUpModel loginModel = response.body();
                                if (loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                    Intent i = new Intent(ctx, CheckoutOtpActivity.class);
//                            i.putParcelableArrayListExtra("PlanData",listModelList);
//                            i.putExtra("TrialPeriod",TrialPeriod);
//                            i.putExtra("position",position);
                                    i.putExtra("MobileNo", binding.edtNumber.getText().toString());
                                    i.putExtra("Name", binding.tvCountry.getText().toString());
                                    i.putExtra("Code", binding.tvCountryCode.getText().toString());
                                    BWSApplication.showToast(loginModel.getResponseMessage(), ctx);
                                    startActivity(i);
                                    finish();
                                } else if (loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))) {
                                    binding.txtError.setVisibility(View.VISIBLE);
                                    binding.txtError.setText(loginModel.getResponseMessage());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<SignUpModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    }
                });
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), ctx);
            }
        }
    }

    private TextWatcher signupTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String number = binding.edtNumber.getText().toString().trim();
            if (!number.isEmpty()) {
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
        }
    };
}