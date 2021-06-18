package com.brainwellnessspa.membershipModule.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.billingOrderModule.activities.MembershipChangeActivity;
import com.brainwellnessspa.billingOrderModule.models.PlanListBillingModel;
import com.brainwellnessspa.dashboardModule.models.PlanlistInappModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.referralModule.models.CheckReferCodeModel;
import com.brainwellnessspa.utility.APIClient;
import com.brainwellnessspa.utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityOrderSummaryBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderSummaryActivity extends AppCompatActivity {
    ActivityOrderSummaryBinding binding;
    String TrialPeriod, comeFrom = "", UserId,/* renewPlanFlag, renewPlanId, */
            ComesTrue, Promocode = "", OldPromocode = "";
    private ArrayList<PlanlistInappModel.ResponseData.Plan> listModelList;
    ArrayList<PlanListBillingModel.ResponseData.Plan> listModelList2;
    int position;
    private long mLastClickTime = 0;
    Context ctx;
    String json = "";
    Gson gson = new Gson();
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_summary);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        ctx = OrderSummaryActivity.this;
        activity = OrderSummaryActivity.this;
        if (getIntent() != null) {
            TrialPeriod = getIntent().getStringExtra("TrialPeriod");
//            renewPlanFlag = getIntent().getStringExtra("renewPlanFlag");
//            renewPlanId = getIntent().getStringExtra("renewPlanId");
            position = getIntent().getIntExtra("position", 0);
            if (getIntent().hasExtra("comeFrom")) {
                comeFrom = getIntent().getStringExtra("comeFrom");
                listModelList2 = getIntent().getParcelableArrayListExtra("PlanData");
            } else {
                json = getIntent().getStringExtra("PlanData");
                Type type = new TypeToken<ArrayList<PlanlistInappModel.ResponseData.Plan>>() {
                }.getType();
                listModelList = gson.fromJson(json, type);
            }
        }

        if (getIntent() != null) {
            ComesTrue = getIntent().getStringExtra("ComesTrue");
        }

        if (getIntent().getExtras() != null) {
            OldPromocode = getIntent().getStringExtra(CONSTANTS.Promocode);
        }
        binding.edtCode.addTextChangedListener(promoCodeTextWatcher);
        Properties p = new Properties();

        if (!comeFrom.equalsIgnoreCase("")) {
            Gson gson;
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
            p.putValue("plan", gson.toJson(listModelList2));
        } else {
            Gson gson;
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
            p.putValue("plan", gson.toJson(listModelList));
        }
        BWSApplication.addToSegment("Order Summary Viewed", p, CONSTANTS.screen);

        if (!OldPromocode.equalsIgnoreCase("")) {
            binding.edtCode.setText(OldPromocode);
        }
        if (!comeFrom.equalsIgnoreCase("")) {
            binding.tvPromoCode.setVisibility(View.GONE);
            binding.llPromoCode.setVisibility(View.GONE);
        } else {
            binding.tvPromoCode.setVisibility(View.GONE);
            binding.llPromoCode.setVisibility(View.GONE);
        }

        try {
            if (!comeFrom.equalsIgnoreCase("")) {
                binding.tvTrialPeriod.setVisibility(View.GONE);
                binding.tvPlanInterval.setText(listModelList2.get(position).getPlanInterval() + " Membership");
                binding.tvPlanTenure.setText(listModelList2.get(position).getPlanTenure());
                binding.tvPlanNextRenewal.setText(listModelList2.get(position).getPlanNextRenewal());
                binding.tvSubName.setText(listModelList2.get(position).getSubName());
                binding.tvSubName1.setText(listModelList2.get(position).getSubName());
                binding.tvPlanAmount.setText("$" + listModelList2.get(position).getPlanAmount());
                binding.tvPlanAmount1.setText("$" + listModelList2.get(position).getPlanAmount());
                binding.tvTotalAmount.setText("$" + listModelList2.get(position).getPlanAmount());
            } else {
                binding.tvTrialPeriod.setVisibility(View.VISIBLE);
                binding.tvPlanInterval.setText(listModelList.get(position).getPlanInterval() + " Membership");
                binding.tvPlanTenure.setText(listModelList.get(position).getPlanTenure());
                binding.tvPlanNextRenewal.setText(listModelList.get(position).getPlanNextRenewal());
                binding.tvSubName.setText(listModelList.get(position).getSubName());
                binding.tvSubName1.setText(listModelList.get(position).getSubName());
                binding.tvTrialPeriod.setText(TrialPeriod);
                binding.tvPlanAmount.setText("$" + listModelList.get(position).getPlanAmount());
                binding.tvPlanAmount1.setText("$" + listModelList.get(position).getPlanAmount());
                binding.tvTotalAmount.setText("$" + listModelList.get(position).getPlanAmount());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.llBack.setOnClickListener(view -> {
            if (!comeFrom.equalsIgnoreCase("")) {
                Intent i = new Intent(ctx, MembershipChangeActivity.class);
                i.putExtra("ComeFrom", ComesTrue);
                startActivity(i);
                finish();
            } else {
                finish();
            }
        });

        binding.btnApply.setOnClickListener(v -> {
            prepareCheckReferCode(binding.edtCode.getText().toString());
        });

        binding.btnCheckout.setOnClickListener(view -> {
          /*  try {
                if (binding.edtCode.getText().toString().equalsIgnoreCase("")) {
                    Promocode = "";
                    Properties p1 = new Properties();
                    if (!comeFrom.equalsIgnoreCase("")) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Gson gson;
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        gson = gsonBuilder.create();
                        p1.putValue("plan", gson.toJson(listModelList2));
                        p1.putValue("planStartDt ", "");
                        p1.putValue("planExpiryDt", listModelList2.get(position).getPlanNextRenewal());
                        p1.putValue("planRenewalDt", listModelList2.get(position).getPlanNextRenewal());
                        p1.putValue("planAmount", listModelList2.get(position).getPlanAmount());
                      *//*  Intent i = new Intent(ctx, PaymentActivity.class);
                        i.putExtra("ComesTrue", ComesTrue);
                        i.putExtra("comeFrom", "membership");
                        i.putParcelableArrayListExtra("PlanData", listModelList2);
                        i.putExtra("TrialPeriod", "");
                        i.putExtra("position", position);
                        startActivity(i);
                        finish();*//*
                    } else {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson;
                        gson = gsonBuilder.create();
                        p1.putValue("plan", gson.toJson(listModelList));
                        p1.putValue("planStartDt ", "");
                        p1.putValue("planExpiryDt", listModelList.get(position).getPlanNextRenewal());
                        p1.putValue("planRenewalDt", listModelList.get(position).getPlanNextRenewal());
                        p1.putValue("planAmount", listModelList.get(position).getPlanAmount());
                        Intent i = new Intent(ctx, ThankYouMpActivity.class);
                        i.putExtra("Name", "");
                        i.putExtra("Code", "");
                        i.putExtra("MobileNo", "");
                        i.putExtra("PlanData", gson.toJson(listModelList));
                        i.putExtra("TrialPeriod", TrialPeriod);
                        i.putExtra("position", position);
                        i.putExtra("Promocode", Promocode);
                        startActivity(i);
                        finish();
                    }
                    BWSApplication.addToSegment("Checkout Proceeded", p1, CONSTANTS.track);
                } else {
                    Promocode = binding.edtCode.getText().toString();
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        Call<CheckReferCodeModel> listCall = APIClient.getClient().CheckReferCode(Promocode);
                        listCall.enqueue(new Callback<CheckReferCodeModel>() {
                            @Override
                            public void onResponse(Call<CheckReferCodeModel> call, Response<CheckReferCodeModel> response) {
                                    CheckReferCodeModel listModel = response.body();
                                    if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                        if (!listModel.getResponseData().getCodeExist().equalsIgnoreCase("0")){
                                            BWSApplication.showToast(listModel.getResponseMessage(), activity);
                                            Properties p1 = new Properties();
                                            if (!comeFrom.equalsIgnoreCase("")) {
                                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                                    return;
                                                }
                                                mLastClickTime = SystemClock.elapsedRealtime();
                                                Gson gson;
                                                GsonBuilder gsonBuilder = new GsonBuilder();
                                                gson = gsonBuilder.create();
                                                p1.putValue("plan", gson.toJson(listModelList2));
                                                p1.putValue("planStartDt ", "");
                                                p1.putValue("planExpiryDt", listModelList2.get(position).getPlanNextRenewal());
                                                p1.putValue("planRenewalDt", listModelList2.get(position).getPlanNextRenewal());
                                                p1.putValue("planAmount", listModelList2.get(position).getPlanAmount());
                                             *//*   Intent i = new Intent(ctx, PaymentActivity.class);
                                                i.putExtra("ComesTrue", ComesTrue);
                                                i.putExtra("comeFrom", "membership");
                                                i.putParcelableArrayListExtra("PlanData", listModelList2);
                                                i.putExtra("TrialPeriod", "");
                                                i.putExtra("position", position);
                                                startActivity(i);
                                                finish();*//*
                                            } else {
                                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                                    return;
                                                }
                                                mLastClickTime = SystemClock.elapsedRealtime();
                                                Gson gson;
                                                GsonBuilder gsonBuilder = new GsonBuilder();
                                                gson = gsonBuilder.create();
                                                p1.putValue("plan", gson.toJson(listModelList));
                                                p1.putValue("planStartDt ", "");
                                                p1.putValue("planExpiryDt", listModelList.get(position).getPlanNextRenewal());
                                                p1.putValue("planRenewalDt", listModelList.get(position).getPlanNextRenewal());
                                                p1.putValue("planAmount", listModelList.get(position).getPlanAmount());
                                                Intent i = new Intent(ctx, ThankYouMpActivity.class);
                                                i.putExtra("Name", "");
                                                i.putExtra("Code", "");
                                                i.putExtra("MobileNo", "");
                                                i.putExtra("PlanData", gson.toJson(listModelList));
                                                i.putExtra("TrialPeriod", TrialPeriod);
                                                i.putExtra("position", position);
                                                i.putExtra("Promocode", Promocode);
                                                startActivity(i);
                                                finish();
                                            }
                                            BWSApplication.addToSegment("Checkout Proceeded", p1, CONSTANTS.track);
                                        }else {
                                            BWSApplication.showToast(listModel.getResponseMessage(), activity);
                                        }
                                    }
                            }

                            @Override
                            public void onFailure(Call<CheckReferCodeModel> call, Throwable t) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            }
                        });
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), activity);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }*/
            PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
                @Override
                public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                    // To be implemented in a later section.
                }
            };

            BillingClient billingClient = BillingClient.newBuilder(activity)
                    .setListener(purchasesUpdatedListener)
                    .enablePendingPurchases()
                    .build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                        // The BillingClient is ready. You can query purchases here.
                    }
                }
                @Override
                public void onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }
            });
        });
    }

    @Override
    public void onBackPressed() {
        if (!comeFrom.equalsIgnoreCase("")) {
            Intent i = new Intent(ctx, MembershipChangeActivity.class);
            i.putExtra("ComeFrom", ComesTrue);
            startActivity(i);
            finish();
        } else {
            finish();
        }
    }

    private TextWatcher promoCodeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String number = binding.edtCode.getText().toString().trim();
            if (number.isEmpty()) {
                binding.btnApply.setEnabled(false);
                binding.btnApply.setTextColor(getResources().getColor(R.color.gray));
            } else {
                binding.btnApply.setEnabled(true);
                binding.btnApply.setTextColor(getResources().getColor(R.color.dark_yellow));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public void prepareCheckReferCode(String promoCode) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<CheckReferCodeModel> listCall = APIClient.getClient().CheckReferCode(promoCode);
            listCall.enqueue(new Callback<CheckReferCodeModel>() {
                @Override
                public void onResponse(Call<CheckReferCodeModel> call, Response<CheckReferCodeModel> response) {
                    try {
                        CheckReferCodeModel listModel = response.body();
                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            if (listModel.getResponseData().getCodeExist().equalsIgnoreCase("0")){
                                BWSApplication.showToast(listModel.getResponseMessage(), activity);
                            }else {
                                BWSApplication.showToast(listModel.getResponseMessage(), activity);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<CheckReferCodeModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity);
        }
    }
}