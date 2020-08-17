package com.qltech.bws.LoginModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.qltech.bws.LoginModule.Models.LoginModel;
import com.qltech.bws.MembershipModule.Activities.MembershipActivity;
import com.qltech.bws.OtpModule.Activities.OtpActivity;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.AppUtils;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        binding.rlCountrySelect.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, CountrySelectActivity.class);
            startActivity(i);
        });

        binding.btnSendCode.setOnClickListener(view -> {
            prepareData();
        });

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(binding.edtNumber.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        binding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, MembershipActivity.class);
                startActivity(i);
            }
        });

    }
    void prepareData(){
        if (binding.edtNumber.getText().toString().isEmpty()) {
            binding.edtNumber.setFocusable(true);
            binding.edtNumber.requestFocus();
            binding.txtError.setVisibility(View.VISIBLE);
            binding.txtError.setText(getString(R.string.no_add_digits));
        } else if (binding.edtNumber.getText().toString().length() == 1 || binding.edtNumber.getText().toString().length() != 8){
            binding.edtNumber.setFocusable(true);
            binding.edtNumber.requestFocus();
            binding.txtError.setVisibility(View.VISIBLE);
            binding.txtError.setText(getString(R.string.not_valid_number));
        } else {
            if (AppUtils.isNetworkConnected(LoginActivity.this)) {
                Call<LoginModel> listCall = APIClient.getClient().getLoginDatas(binding.edtNumber.toString(),61, CONSTANTS.FLAG_ONE,CONSTANTS.FLAG_ZERO,"sdsdsdsd");
                listCall.enqueue(new Callback<LoginModel>() {
                    @Override
                    public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                        if(response.isSuccessful()){
                            LoginModel loginModel = response.body();
                            if (loginModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))){
                                Toast.makeText(getApplicationContext(),"Sucess", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoginActivity.this, OtpActivity.class);
                                startActivity(i);
                            }else {
                                Toast.makeText(getApplicationContext(),"Fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginModel> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
            }
        }
    }
}