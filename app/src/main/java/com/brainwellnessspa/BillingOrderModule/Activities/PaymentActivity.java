package com.brainwellnessspa.BillingOrderModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.brainwellnessspa.AddPayment.AddPaymentActivity;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Models.CardListModel;
import com.brainwellnessspa.BillingOrderModule.Models.CardModel;
import com.brainwellnessspa.BillingOrderModule.Models.PayNowDetailsModel;
import com.brainwellnessspa.BillingOrderModule.Models.PlanListBillingModel;
import com.brainwellnessspa.MembershipModule.Activities.OrderSummaryActivity;
import com.brainwellnessspa.MembershipModule.Models.MembershipPlanListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityPaymentBinding;
import com.brainwellnessspa.databinding.CardsListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity.renewPlanFlag;
import static com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity.renewPlanId;
import static com.brainwellnessspa.BillingOrderModule.Fragments.CurrentPlanFragment.PlanStatus;
import static com.brainwellnessspa.BillingOrderModule.Fragments.CurrentPlanFragment.invoicePayId;

public class PaymentActivity extends AppCompatActivity {
    ActivityPaymentBinding binding;
    AllCardsAdapter adapter;
    Context context;
    String card_id, userId, TrialPeriod, comeFrom = "", ComesTrue;
    Activity activity;
    int position;
    ArrayList<PlanListBillingModel.ResponseData.Plan> listModelList2;
    private ArrayList<MembershipPlanListModel.Plan> listModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment);
        context = PaymentActivity.this;
        activity = PaymentActivity.this;
        SharedPreferences shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        userId = (shared.getString(CONSTANTS.PREF_KEY_UserID, ""));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        binding.rvCardList.setLayoutManager(mLayoutManager);
        binding.rvCardList.setItemAnimator(new DefaultItemAnimator());

        if (getIntent() != null) {
            TrialPeriod = getIntent().getStringExtra("TrialPeriod");
            position = getIntent().getIntExtra("position", 0);
            if (getIntent().hasExtra("comeFrom")) {
                comeFrom = getIntent().getStringExtra("comeFrom");
                listModelList2 = getIntent().getParcelableArrayListExtra("PlanData");
            } else {
                listModelList = getIntent().getParcelableArrayListExtra("PlanData");
            }
        }

        if (getIntent() != null) {
            ComesTrue = getIntent().getStringExtra("ComesTrue");
        }

        binding.llBack.setOnClickListener(view -> {
            Intent i = new Intent(context, OrderSummaryActivity.class);
            i.putExtra("comeFrom", "membership");
            i.putExtra("ComesTrue", ComesTrue);
            i.putParcelableArrayListExtra("PlanData", listModelList2);
            i.putExtra("TrialPeriod", "");
            i.putExtra("position", position);
            startActivity(i);
            finish();
        });

        binding.llAddNewCard.setOnClickListener(view -> {
            if (BWSApplication.isNetworkConnected(context)) {
                Intent i = new Intent(context, AddPaymentActivity.class);
                i.putExtra("ComePayment", "2");
                startActivity(i);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), context);
            }
        });
        prepareCardList();
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareCardList();
    }

    private void prepareCardList() {
        try {
            if (BWSApplication.isNetworkConnected(context)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                Call<CardListModel> listCall = APIClient.getClient().getCardLists(userId);
                listCall.enqueue(new Callback<CardListModel>() {
                    @Override
                    public void onResponse(Call<CardListModel> call, Response<CardListModel> response) {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            try {
                                CardListModel cardListModel = response.body();
                                if (cardListModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                    if (cardListModel.getResponseData().size() == 0) {
                                        binding.rvCardList.setAdapter(null);
                                        binding.rvCardList.setVisibility(View.GONE);
                                    } else {
                                        binding.rvCardList.setVisibility(View.VISIBLE);
                                        adapter = new AllCardsAdapter(cardListModel.getResponseData(), binding.progressBar,
                                                binding.progressBarHolder, binding.rvCardList);
                                        binding.rvCardList.setAdapter(adapter);
                                    }

                                    binding.btnCheckout.setOnClickListener(view -> {
                                        if (cardListModel.getResponseData().size() == 0) {
                                            BWSApplication.showToast("Please enter card details", context);
                                        } else {

                                            if (BWSApplication.isNetworkConnected(context)) {
                                                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                                Call<PayNowDetailsModel> listCall = APIClient.getClient().getPayNowDetails(userId, card_id, renewPlanId, renewPlanFlag,
                                                        invoicePayId, PlanStatus);
                                                listCall.enqueue(new Callback<PayNowDetailsModel>() {
                                                    @Override
                                                    public void onResponse(Call<PayNowDetailsModel> call, Response<PayNowDetailsModel> response) {
                                                        try {
                                                            if (response.isSuccessful()) {
                                                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                                                PayNowDetailsModel listModel1 = response.body();
                                                                BWSApplication.showToast(listModel1.getResponseMessage(), context);
                                                                Intent i = new Intent(context, BillingOrderActivity.class);
                                                                startActivity(i);
                                                                finish();
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<PayNowDetailsModel> call, Throwable t) {
                                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                                    }
                                                });
                                            } else {
                                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                                BWSApplication.showToast(getString(R.string.no_server_found), context);
                                            }
                                        }
                                    });
                                } else {
                                    BWSApplication.showToast(cardListModel.getResponseMessage(), context);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CardListModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    }

                });
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class AllCardsAdapter extends RecyclerView.Adapter<AllCardsAdapter.MyViewHolder> {
        private List<CardListModel.ResponseData> listModelList;
        ProgressBar ImgV;
        FrameLayout progressBarHolder;
        RecyclerView rvCardList;
        AllCardsAdapter adapter;

        public AllCardsAdapter(List<CardListModel.ResponseData> listModelList, ProgressBar ImgV,
                               FrameLayout progressBarHolder, RecyclerView rvCardList) {
            this.listModelList = listModelList;
            this.ImgV = ImgV;
            this.progressBarHolder = progressBarHolder;
            this.rvCardList = rvCardList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CardsListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.cards_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            CardListModel.ResponseData listModel = listModelList.get(position);
            holder.binding.tvCardNo.setText(getString(R.string.first_card_chars) + " " + listModel.getLast4());
            holder.binding.tvExpiryTime.setText("Valid: " + listModel.getExpMonth() + "/" +
                    listModel.getExpYear());
            Glide.with(context).load(listModel.getImage()).thumbnail(0.05f).crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.ivCardimg);
            if (listModel.getIsDefault().equalsIgnoreCase(CONSTANTS.FLAG_ONE)) {
                holder.binding.ivCheck.setImageResource(R.drawable.ic_checked_icon);
                card_id = listModel.getCustomer();
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_CardID, MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString(CONSTANTS.PREF_KEY_CardID, card_id);
                editor.commit();
            } else {
                holder.binding.ivCheck.setImageResource(R.drawable.ic_unchecked_icon);
            }

            holder.binding.llAddNewCard.setOnClickListener(view -> {
                if (BWSApplication.isNetworkConnected(context)) {
                    BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    Call<CardListModel> listCall = APIClient.getClient().getChangeCard(userId, listModel.getCustomer());
                    listCall.enqueue(new Callback<CardListModel>() {
                        @Override
                        public void onResponse(Call<CardListModel> call, Response<CardListModel> response) {
                            try {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                CardListModel cardListModel = response.body();
                                if (cardListModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                    if (cardListModel.getResponseData().size() == 0) {
                                        rvCardList.setAdapter(null);
                                        rvCardList.setVisibility(View.GONE);
                                    } else {
                                        rvCardList.setVisibility(View.VISIBLE);
                                        adapter = new AllCardsAdapter(cardListModel.getResponseData(), ImgV, progressBarHolder, rvCardList);
                                        rvCardList.setAdapter(adapter);
                                    }
                                    BWSApplication.showToast(cardListModel.getResponseMessage(), activity);
                                } else {
                                    BWSApplication.showToast(cardListModel.getResponseMessage(), context);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<CardListModel> call, Throwable t) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        }

                    });
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), context);
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });

            holder.binding.rlRemoveCard.setOnClickListener(view -> {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.delete_payment_card);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                final Button Btn = dialog.findViewById(R.id.Btn);
                Btn.setText("DELETE");
                dialog.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                });

                Btn.setOnTouchListener((view1, event) -> {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            Button views = (Button) view1;
                            views.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                            view1.invalidate();
                            break;
                        }
                        case MotionEvent.ACTION_UP:
                            if (BWSApplication.isNetworkConnected(context)) {
                                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                Call<CardModel> listCall = APIClient.getClient().getRemoveCard(userId, listModel.getCustomer());
                                listCall.enqueue(new Callback<CardModel>() {
                                    @Override
                                    public void onResponse(Call<CardModel> call, Response<CardModel> response) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                        try {
                                            if (response.isSuccessful()) {
                                                CardModel cardModel = response.body();
                                                if (cardModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                                    prepareCardList();
                                                    dialog.dismiss();
                                                    BWSApplication.showToast(cardModel.getResponseMessage(), activity);
                                                } else {
                                                    BWSApplication.showToast(cardModel.getResponseMessage(), context);
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CardModel> call, Throwable t) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                    }
                                });
                            } else {
                                BWSApplication.showToast(getString(R.string.no_server_found), context);
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            }
                        case MotionEvent.ACTION_CANCEL: {
                            Button views = (Button) view1;
                            views.getBackground().clearColorFilter();
                            views.invalidate();
                            break;
                        }
                    }
                    return true;
                });

                tvGoBack.setOnClickListener(v -> {
                    dialog.dismiss();
                });
                dialog.show();
                dialog.setCancelable(false);
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            CardsListLayoutBinding binding;

            public MyViewHolder(CardsListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(PaymentActivity.this, OrderSummaryActivity.class);
        i.putExtra("comeFrom", "membership");
        i.putExtra("ComesTrue", ComesTrue);
        i.putParcelableArrayListExtra("PlanData", listModelList2);
        i.putExtra("TrialPeriod", "");
        i.putExtra("position", position);
        startActivity(i);
        finish();
    }
}