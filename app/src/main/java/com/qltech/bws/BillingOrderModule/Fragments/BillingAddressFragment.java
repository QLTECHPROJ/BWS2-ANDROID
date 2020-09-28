package com.qltech.bws.BillingOrderModule.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.BillingOrderModule.Models.BillingAddressSaveModel;
import com.qltech.bws.BillingOrderModule.Models.BillingAddressViewModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.FragmentBillingAddressBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillingAddressFragment extends Fragment {
    FragmentBillingAddressBinding binding;
    String UserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_billing_address, container, false);
        View view = binding.getRoot();
        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        getPrepareData();

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tlName.setError("");
                binding.tlEmail.setError("");
                binding.tlMobileNumber.setError("");
                binding.tlCountry.setError("");
                binding.tlAddressLine1.setError("");
                binding.tlCity.setError("");
                binding.tlState.setError("");
                binding.tlPostCode.setError("");
                if (binding.etName.getText().toString().equalsIgnoreCase("")) {
                    binding.tlName.setError("Please enter your full name");
                } else if (binding.etEmail.getText().toString().equalsIgnoreCase("")) {
                    binding.tlEmail.setError("Please enter your email address");
                } else if (!binding.etEmail.getText().toString().equalsIgnoreCase("")
                        && !BWSApplication.isEmailValid(binding.etEmail.getText().toString())) {
                    binding.tlEmail.setError("Please provide a valid email address");
                } else if (binding.etMobileNumber.getText().toString().equalsIgnoreCase("")) {
                    binding.tlMobileNumber.setError("please enter mobile number");
                } else if (binding.etCountry.getText().toString().equalsIgnoreCase("")) {
                    binding.tlCountry.setError("Please provide valid country details");
                } else if (binding.etAddressLine1.getText().toString().equalsIgnoreCase("")) {
                    binding.tlAddressLine1.setError("Please provide the address details");
                } else if (binding.etCity.getText().toString().equalsIgnoreCase("")) {
                    binding.tlCity.setError("Please provide suburb/town/city details");
                } else if (binding.etState.getText().toString().equalsIgnoreCase("")) {
                    binding.tlState.setError("Please provide the state you reside in");
                } else if (binding.etPostCode.getText().toString().equalsIgnoreCase("")) {
                    binding.tlPostCode.setError("Please provide a postal code");
                } else {
                    showProgressBar();
                    Call<BillingAddressSaveModel> listCall = APIClient.getClient().getBillingAddressSave(UserID,
                            binding.etName.getText().toString(), binding.etEmail.getText().toString(),
                            binding.etCountry.getText().toString(), binding.etAddressLine1.getText().toString(),
                            binding.etAddressLine2.getText().toString(), binding.etCity.getText().toString(),
                            binding.etState.getText().toString(),
                            binding.etPostCode.getText().toString());
                    listCall.enqueue(new Callback<BillingAddressSaveModel>() {
                        @Override
                        public void onResponse(Call<BillingAddressSaveModel> call, Response<BillingAddressSaveModel> response) {
                            if (response.isSuccessful()) {
                                hideProgressBar();
                                BillingAddressSaveModel listModel = response.body();
                                BWSApplication.showToast(listModel.getResponseMessage(), getActivity());
                                getActivity().finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<BillingAddressSaveModel> call, Throwable t) {
                            hideProgressBar();
                        }
                    });
                }
            }
        });

        return view;
    }

    private void getPrepareData() {
        showProgressBar();
        Call<BillingAddressViewModel> listCall = APIClient.getClient().getBillingAddressView(UserID);
        listCall.enqueue(new Callback<BillingAddressViewModel>() {
            @Override
            public void onResponse(Call<BillingAddressViewModel> call, Response<BillingAddressViewModel> response) {
                if (response.isSuccessful()) {
                    hideProgressBar();
                    BillingAddressViewModel listModel = response.body();
                    if (listModel.getResponseData().getName().equalsIgnoreCase("") ||
                            listModel.getResponseData().getName().equalsIgnoreCase(" ") ||
                            listModel.getResponseData().getName() == null) {
                        binding.etName.setText("");
                    } else {
                        binding.etName.setText(listModel.getResponseData().getName());
                    }
                    binding.etEmail.setText(listModel.getResponseData().getEmail());
                    binding.etMobileNumber.setText(listModel.getResponseData().getPhoneNumber());
                    binding.etMobileNumber.setEnabled(false);
                    binding.etMobileNumber.setClickable(false);
                    binding.etCountry.setText(listModel.getResponseData().getCountry());
                    binding.etAddressLine1.setText(listModel.getResponseData().getAddress1());
                    binding.etAddressLine2.setText(listModel.getResponseData().getAddress2());
                    binding.etCity.setText(listModel.getResponseData().getSuburb());
                    binding.etState.setText(listModel.getResponseData().getState());
                    binding.etPostCode.setText(listModel.getResponseData().getPostcode());
                }
            }

            @Override
            public void onFailure(Call<BillingAddressViewModel> call, Throwable t) {
                hideProgressBar();
            }
        });
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}