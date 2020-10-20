package com.brainwellnessspa.LoginModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.LoginModule.Models.LoginModel;
import com.brainwellnessspa.MembershipModule.Activities.MembershipActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.SplashModule.SplashScreenActivity;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.WebView.TncActivity;
import com.brainwellnessspa.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.LoginModule.Activities.OtpActivity.comeLogin;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.releasePlayer;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    String Name = "", Code = "", MobileNo = "";
    Context ctx;
    Activity activity;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        ctx = LoginActivity.this;
        activity = LoginActivity.this;
        if (getIntent().getExtras() != null) {
            Name = getIntent().getStringExtra(CONSTANTS.Name);
            Code = getIntent().getStringExtra(CONSTANTS.Code);
            MobileNo = getIntent().getStringExtra(CONSTANTS.MobileNo);
        }

        if (isMediaStart) {
            stopMedia();
            releasePlayer();
        }
        binding.edtNumber.addTextChangedListener(loginTextWatcher);
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

        if (comeLogin == 1) {
            binding.edtNumber.requestFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        binding.tvtncs.setOnClickListener(view -> {
            Intent i = new Intent(ctx, TncActivity.class);
            i.putExtra(CONSTANTS.Web, "Tnc");
            startActivity(i);
        });

        binding.tvPrivacyPolicys.setOnClickListener(view -> {
            Intent i = new Intent(ctx, TncActivity.class);
            i.putExtra(CONSTANTS.Web, "PrivacyPolicy");
            startActivity(i);
        });

        binding.tvDisclaimers.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(ctx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.full_desc_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
            final TextView tvDesc = dialog.findViewById(R.id.tvDesc);
            final RelativeLayout tvClose = dialog.findViewById(R.id.tvClose);
            tvTitle.setText(R.string.Disclaimer);
            tvDesc.setText(R.string.Disclaimer_text);
            dialog.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            });

            tvClose.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
            dialog.setCancelable(false);
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
        if (binding.edtNumber.getText().toString().length() == 1 || binding.edtNumber.getText().toString().length() < 8 ||
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
                Call<LoginModel> listCall = APIClient.getClient().getLoginDatas(binding.edtNumber.getText().toString(), countryCode, CONSTANTS.FLAG_ONE, CONSTANTS.FLAG_ZERO, SplashScreenActivity.key);
                listCall.enqueue(new Callback<LoginModel>() {
                    @Override
                    public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        binding.txtError.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            LoginModel loginModel = response.body();
                            if (loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
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
                            } else if (loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))) {
                                binding.txtError.setVisibility(View.VISIBLE);
                                binding.txtError.setText(loginModel.getResponseMessage());
                            }
                        } else {
                            BWSApplication.showToast(response.message(), ctx);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    }
                });
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), ctx);
            }
        }
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
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