package com.brainwellnessspa.billingOrderModule.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.R;
import com.brainwellnessspa.billingOrderModule.models.BillingAddressSaveModel;
import com.brainwellnessspa.billingOrderModule.models.BillingAddressViewModel;
import com.brainwellnessspa.databinding.FragmentBillingAddressBinding;
import com.brainwellnessspa.utility.APIClient;
import com.brainwellnessspa.utility.CONSTANTS;
import com.segment.analytics.Properties;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.billingOrderModule.activities.BillingOrderActivity.myBackPressbill;

public class BillingAddressFragment extends Fragment {
    FragmentBillingAddressBinding binding;
    String UserID, UserName, UserEmail, UserMobileNumber, UserCountry, UserAddressLine1, UserAddressLine2, UserCity, UserState, UserPostCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_billing_address, container, false);
        View view = binding.getRoot();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        binding.etName.addTextChangedListener(billingTextWatcher);
        binding.etEmail.addTextChangedListener(billingTextWatcher);
        binding.etMobileNumber.addTextChangedListener(billingTextWatcher);
        binding.etCountry.addTextChangedListener(billingTextWatcher);
        binding.etAddressLine1.addTextChangedListener(billingTextWatcher);
        binding.etAddressLine2.addTextChangedListener(billingTextWatcher);
        binding.etCity.addTextChangedListener(billingTextWatcher);
        binding.etState.addTextChangedListener(billingTextWatcher);
        binding.etPostCode.addTextChangedListener(billingTextWatcher);
        binding.btnSave.setEnabled(false);
        binding.btnSave.setTextColor(getResources().getColor(R.color.white));
        binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor);
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
                    binding.tlName.setError("Name is required");
                } else if (binding.etEmail.getText().toString().equalsIgnoreCase("")) {
                    binding.tlEmail.setError("Email address is required");
                } else if (!binding.etEmail.getText().toString().equalsIgnoreCase("") && !BWSApplication.isEmailValid(binding.etEmail.getText().toString())) {
                    binding.tlEmail.setError("Please enter a valid email address");
                } else if (binding.etMobileNumber.getText().toString().equalsIgnoreCase("")) {
                    binding.tlMobileNumber.setError("please enter mobile number");
                } else if (binding.etCountry.getText().toString().equalsIgnoreCase("")) {
                    binding.tlCountry.setError("Country is required");
                } else if (binding.etAddressLine1.getText().toString().equalsIgnoreCase("")) {
                    binding.tlAddressLine1.setError("Address Line is required");
                } else if (binding.etCity.getText().toString().equalsIgnoreCase("")) {
                    binding.tlCity.setError("Suburb / Town / City is required");
                } else if (binding.etState.getText().toString().equalsIgnoreCase("")) {
                    binding.tlState.setError("State is required");
                } else if (binding.etPostCode.getText().toString().equalsIgnoreCase("")) {
                    binding.tlPostCode.setError("Postcode is required");
                } else {
                    BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                    Call<BillingAddressSaveModel> listCall = APIClient.getClient().getBillingAddressSave(UserID, binding.etName.getText().toString(), binding.etEmail.getText().toString(), binding.etCountry.getText().toString(), binding.etAddressLine1.getText().toString(), binding.etAddressLine2.getText().toString(), binding.etCity.getText().toString(), binding.etState.getText().toString(), binding.etPostCode.getText().toString());
                    listCall.enqueue(new Callback<BillingAddressSaveModel>() {
                        @Override
                        public void onResponse(Call<BillingAddressSaveModel> call, Response<BillingAddressSaveModel> response) {
                            BillingAddressSaveModel listModel = response.body();
                            if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                try {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                                    BWSApplication.showToast(listModel.getResponseMessage(), getActivity());
                                    /*Properties p = new Properties();
                                    p.putValue("userId", UserID);
                                    BWSApplication.addToSegment("Billing Address Updated", p, CONSTANTS.track);*/
                                    myBackPressbill = true;
                                    getActivity().finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

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

    @Override
    public void onResume() {
        getPrepareData();
        super.onResume();
    }

    private void getPrepareData() {
        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
        Call<BillingAddressViewModel> listCall = APIClient.getClient().getBillingAddressView(UserID);
        listCall.enqueue(new Callback<BillingAddressViewModel>() {
            @Override
            public void onResponse(Call<BillingAddressViewModel> call, Response<BillingAddressViewModel> response) {
                try {
                    BillingAddressViewModel listModel = response.body();
                    if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        if (listModel.getResponseData().getName().equalsIgnoreCase("") || listModel.getResponseData().getName().equalsIgnoreCase(" ") || listModel.getResponseData().getName() == null) {
                            binding.etName.setText("");
                        } else {
                            binding.etName.setText(listModel.getResponseData().getName());
                        }
                        UserName = listModel.getResponseData().getName();
                        UserEmail = listModel.getResponseData().getEmail();
                        UserMobileNumber = listModel.getResponseData().getPhoneNumber();
                        UserCountry = listModel.getResponseData().getCountry();
                        UserAddressLine1 = listModel.getResponseData().getAddress1();
                        UserAddressLine2 = listModel.getResponseData().getAddress2();
                        UserCity = listModel.getResponseData().getSuburb();
                        UserState = listModel.getResponseData().getState();
                        UserPostCode = listModel.getResponseData().getPostcode();
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

                        Properties p = new Properties();
                        p.putValue("userId", UserID);
                        p.putValue("fullName", listModel.getResponseData().getName());
                        p.putValue("emailId", listModel.getResponseData().getEmail());
                        p.putValue("mobile", listModel.getResponseData().getPhoneNumber());
                        p.putValue("country", listModel.getResponseData().getCountry());
                        p.putValue("address1", listModel.getResponseData().getAddress1());
                        p.putValue("address2", listModel.getResponseData().getAddress2());
                        p.putValue("suburb", listModel.getResponseData().getSuburb());
                        p.putValue("state", listModel.getResponseData().getState());
                        p.putValue("postcode", listModel.getResponseData().getPostcode());
                        BWSApplication.addToSegment("Billing Address Screen Viewed", p, CONSTANTS.screen);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
            String Name = binding.etName.getText().toString();
            String Email = binding.etEmail.getText().toString();
            String MobileNumber = binding.etMobileNumber.getText().toString();
            String Country = binding.etCountry.getText().toString();
            String AddressLine1 = binding.etAddressLine1.getText().toString();
            String AddressLine2 = binding.etAddressLine2.getText().toString();
            String City = binding.etCity.getText().toString();
            String State = binding.etState.getText().toString();
            String PostCode = binding.etPostCode.getText().toString();

            if (Name.equalsIgnoreCase(UserName) && Email.equalsIgnoreCase(UserEmail) && MobileNumber.equalsIgnoreCase(UserMobileNumber) && Country.equalsIgnoreCase(UserCountry) && AddressLine1.equalsIgnoreCase(UserAddressLine1) && AddressLine2.equalsIgnoreCase(UserAddressLine2) && City.equalsIgnoreCase(UserCity) && State.equalsIgnoreCase(UserState) && PostCode.equalsIgnoreCase(UserPostCode)) {
                binding.btnSave.setEnabled(false);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor);
            } else if (!Name.equalsIgnoreCase(UserName)) {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled);
            } else if (!Email.equalsIgnoreCase(UserEmail)) {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled);
            } else if (!MobileNumber.equalsIgnoreCase(UserMobileNumber)) {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled);
            } else if (!Country.equalsIgnoreCase(UserCountry)) {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled);
            } else if (!AddressLine1.equalsIgnoreCase(UserAddressLine1)) {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled);
            } else if (!AddressLine2.equalsIgnoreCase(UserAddressLine2)) {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled);
            } else if (!City.equalsIgnoreCase(UserCity)) {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled);
            } else if (!State.equalsIgnoreCase(UserState)) {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled);
            } else if (!PostCode.equalsIgnoreCase(UserPostCode)) {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled);
            } else if (Name.equalsIgnoreCase(UserName) && Email.equalsIgnoreCase(UserEmail) && MobileNumber.equalsIgnoreCase(UserMobileNumber) && Country.equalsIgnoreCase(UserCountry) && AddressLine1.equalsIgnoreCase(UserAddressLine1) && AddressLine2.equalsIgnoreCase("") && UserAddressLine2.equalsIgnoreCase("") && City.equalsIgnoreCase(UserCity) && State.equalsIgnoreCase(UserState) && PostCode.equalsIgnoreCase(UserPostCode)) {
                binding.btnSave.setEnabled(false);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor);
            } else {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
}