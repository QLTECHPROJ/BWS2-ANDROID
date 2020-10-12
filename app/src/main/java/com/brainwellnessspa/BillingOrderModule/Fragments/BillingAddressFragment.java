package com.brainwellnessspa.BillingOrderModule.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Models.BillingAddressSaveModel;
import com.brainwellnessspa.BillingOrderModule.Models.BillingAddressViewModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentBillingAddressBinding;

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
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        getPrepareData();
        binding.etName.addTextChangedListener(billingTextWatcher);
        binding.etEmail.addTextChangedListener(billingTextWatcher);
        binding.etMobileNumber.addTextChangedListener(billingTextWatcher);
        binding.etCountry.addTextChangedListener(billingTextWatcher);
        binding.etAddressLine1.addTextChangedListener(billingTextWatcher);
        binding.etCity.addTextChangedListener(billingTextWatcher);
        binding.etState.addTextChangedListener(billingTextWatcher);
        binding.etPostCode.addTextChangedListener(billingTextWatcher);
        binding.btnSave.setOnClickListener(view1 -> {
            if (BWSApplication.isNetworkConnected(getActivity())) {
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
                    BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
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
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                                BillingAddressSaveModel listModel = response.body();
                                BWSApplication.showToast(listModel.getResponseMessage(), getActivity());
                                getActivity().finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<BillingAddressSaveModel> call, Throwable t) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        }
                    });
                }
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });
        return view;
    }

    private void getPrepareData() {
        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
        Call<BillingAddressViewModel> listCall = APIClient.getClient().getBillingAddressView(UserID);
        listCall.enqueue(new Callback<BillingAddressViewModel>() {
            @Override
            public void onResponse(Call<BillingAddressViewModel> call, Response<BillingAddressViewModel> response) {
                if (response.isSuccessful()) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
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
                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            }
        });
    }

    private TextWatcher billingTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String Name = binding.etName.getText().toString().trim();
            String Email = binding.etEmail.getText().toString().trim();
            String MobileNumber = binding.etMobileNumber.getText().toString().trim();
            String Country = binding.etCountry.getText().toString().trim();
            String AddressLine1 = binding.etAddressLine1.getText().toString().trim();
            String City = binding.etCity.getText().toString().trim();
            String State = binding.etState.getText().toString().trim();
            String PostCode = binding.etPostCode.getText().toString().trim();
            if (!Name.isEmpty() && !Email.isEmpty() && !MobileNumber.isEmpty()
                    && !Country.isEmpty() && !AddressLine1.isEmpty()
                    && !City.isEmpty() && !State.isEmpty() && !PostCode.isEmpty()) {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.extra_round_cornor);
            } else {
                binding.btnSave.setEnabled(false);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

}