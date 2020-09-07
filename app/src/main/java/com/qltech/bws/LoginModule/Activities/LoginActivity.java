package com.qltech.bws.LoginModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.Glide;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.LoginModule.Models.LoginModel;
import com.qltech.bws.MembershipModule.Activities.MembershipActivity;
import com.qltech.bws.R;
import com.qltech.bws.SplashModule.SplashScreenActivity;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    String Name = "", Code = "", MobileNo = "";
    Context ctx;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        ctx = LoginActivity.this;
        if (getIntent().getExtras() != null) {
            Name = getIntent().getStringExtra(CONSTANTS.Name);
            Code = getIntent().getStringExtra(CONSTANTS.Code);
            MobileNo = getIntent().getStringExtra(CONSTANTS.MobileNo);
        }

        if (Code.equalsIgnoreCase("") || Name.equalsIgnoreCase("")) {
            binding.tvCountryCode.setText(R.string.code);
            binding.tvCountry.setText(R.string.Australia);
            binding.edtNumber.setText("");
        } else {
            binding.tvCountryCode.setText(Code);
            binding.tvCountry.setText(Name);
        }

        if (MobileNo.equalsIgnoreCase("")) {
            binding.edtNumber.setText("");
        } else {
            binding.edtNumber.setText(MobileNo);
        }

        Glide.with(getApplicationContext()).load(R.drawable.loading).asGif().into(binding.ImgV);

        binding.rlCountrySelect.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(ctx, CountrySelectActivity.class);
            i.putExtra(CONSTANTS.Name, binding.tvCountry.getText().toString());
            i.putExtra(CONSTANTS.Code, binding.tvCountryCode.getText().toString());
            i.putExtra(CONSTANTS.MobileNo, binding.edtNumber.getText().toString());
            i.putExtra(CONSTANTS.Check, "1");
            startActivity(i);
            finish();
        });

        binding.btnSendCode.setOnClickListener(view -> {
            prepareData();
        });

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(binding.edtNumber.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        binding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(ctx, MembershipActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    void prepareData() {
        if (binding.edtNumber.getText().toString().isEmpty()) {
            binding.edtNumber.setFocusable(true);
            binding.edtNumber.requestFocus();
            binding.txtError.setVisibility(View.VISIBLE);
            binding.txtError.setText(getString(R.string.no_add_digits));
        } else if (binding.edtNumber.getText().toString().length() == 1 || binding.edtNumber.getText().toString().length() < 8 ||
                binding.edtNumber.getText().toString().length() > 10) {
            binding.edtNumber.setFocusable(true);
            binding.edtNumber.requestFocus();
            binding.txtError.setVisibility(View.VISIBLE);
            binding.txtError.setText(getString(R.string.not_valid_number));
        } else {
            binding.txtError.setVisibility(View.GONE);
            if (BWSApplication.isNetworkConnected(ctx)) {
                showProgressBar();
                Call<LoginModel> listCall = APIClient.getClient().getLoginDatas(binding.edtNumber.getText().toString(), binding.tvCountryCode.getText().toString(), CONSTANTS.FLAG_ONE, CONSTANTS.FLAG_ZERO, SplashScreenActivity.key);
                listCall.enqueue(new Callback<LoginModel>() {
                    @Override
                    public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                        hideProgressBar();
                        binding.txtError.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            LoginModel loginModel = response.body();
                            if(loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))){
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                Intent i = new Intent(ctx, OtpActivity.class);
                                i.putExtra("MobileNo", binding.edtNumber.getText().toString());
                                i.putExtra("Name", binding.tvCountry.getText().toString());
                                i.putExtra("Code", binding.tvCountryCode.getText().toString());
                                startActivity(i);
                                finish();
                                BWSApplication.showToast(loginModel.getResponseMessage(), ctx);
                            }else if(loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))){
                                binding.txtError.setVisibility(View.VISIBLE);
                                binding.txtError.setText(loginModel.getResponseMessage());
                            }
                        } else {
                            BWSApplication.showToast(response.message(), ctx);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginModel> call, Throwable t) {
                        hideProgressBar();
                    }
                });
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), ctx);
            }
        }
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}