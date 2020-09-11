package com.qltech.bws.MembershipModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.Glide;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.LoginModule.Activities.CountrySelectActivity;
import com.qltech.bws.LoginModule.Models.LoginModel;
import com.qltech.bws.MembershipModule.Models.MembershipPlanListModel;
import com.qltech.bws.MembershipModule.Models.SignUpModel;
import com.qltech.bws.R;
import com.qltech.bws.SplashModule.SplashScreenActivity;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityCheckoutGetCodeBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        Glide.with(ctx).load(R.drawable.loading).asGif().into(binding.ImgV);

        if (getIntent().hasExtra("Name")) {
            Name = getIntent().getStringExtra(CONSTANTS.Name);
            Code = getIntent().getStringExtra(CONSTANTS.Code);
            MobileNo = getIntent().getStringExtra(CONSTANTS.MobileNo);
//            TrialPeriod = getIntent().getStringExtra("TrialPeriod");
//            listModelList = getIntent().getParcelableArrayListExtra("PlanData");
//            position = getIntent().getIntExtra("position", 0);
        }

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

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(ctx, OrderSummaryActivity.class);
//                i.putParcelableArrayListExtra("PlanData",listModelList);
//                i.putExtra("TrialPeriod",TrialPeriod);
//                i.putExtra("position",position);
//                startActivity(i);
                finish();
            }
        });

        binding.rlCountrySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(binding.edtNumber.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        binding.btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareData();
            }
        });
    }

    void prepareData() {
        if (binding.edtNumber.getText().toString().isEmpty()) {
            binding.edtNumber.setFocusable(true);
            binding.edtNumber.requestFocus();
            binding.txtError.setVisibility(View.VISIBLE);
            binding.txtError.setText(getString(R.string.Please_enter_your_mobile_number));
        } else if (binding.edtNumber.getText().toString().length() == 0 || binding.edtNumber.getText().toString().length() < 8 ||
                binding.edtNumber.getText().toString().length() > 10) {
            binding.edtNumber.setFocusable(true);
            binding.edtNumber.requestFocus();
            binding.txtError.setVisibility(View.VISIBLE);
            binding.txtError.setText(getString(R.string.not_valid_mobile_number));
        } else {
            binding.txtError.setVisibility(View.GONE);
            if (BWSApplication.isNetworkConnected(ctx)) {
                BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                Call<SignUpModel> listCall = APIClient.getClient().getSignUpDatas(binding.edtNumber.getText().toString(), binding.tvCountryCode.getText().toString(), CONSTANTS.FLAG_ONE, CONSTANTS.FLAG_ZERO, SplashScreenActivity.key);
                listCall.enqueue(new Callback<SignUpModel>() {
                    @Override
                    public void onResponse(Call<SignUpModel> call, Response<SignUpModel> response) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
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
                            }else if(loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))){
                                binding.txtError.setVisibility(View.VISIBLE);
                                binding.txtError.setText(loginModel.getResponseMessage());
                            }
                        } else {
                            BWSApplication.showToast(response.message(), ctx);
                        }
                    }

                    @Override
                    public void onFailure(Call<SignUpModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                    }
                });
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), ctx);
            }
        }
    }
}