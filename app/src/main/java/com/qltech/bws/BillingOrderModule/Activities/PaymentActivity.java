package com.qltech.bws.BillingOrderModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.AddPayment.AddPaymentActivity;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.BillingOrderModule.Models.CardListModel;
import com.qltech.bws.BillingOrderModule.Models.CardModel;
import com.qltech.bws.BillingOrderModule.Models.PayNowDetailsModel;
import com.qltech.bws.BillingOrderModule.Models.PlanListBillingModel;
import com.qltech.bws.MembershipModule.Activities.OrderSummaryActivity;
import com.qltech.bws.MembershipModule.Models.MembershipPlanListModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityPaymentBinding;
import com.qltech.bws.databinding.CardsListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.qltech.bws.BillingOrderModule.Activities.MembershipChangeActivity.renewPlanFlag;
import static com.qltech.bws.BillingOrderModule.Activities.MembershipChangeActivity.renewPlanId;
import static com.qltech.bws.BillingOrderModule.Fragments.CurrentPlanFragment.PlanStatus;
import static com.qltech.bws.BillingOrderModule.Fragments.CurrentPlanFragment.invoicePayId;

public class PaymentActivity extends AppCompatActivity {
    ActivityPaymentBinding binding;
    AllCardsAdapter adapter;
    Context context;
    String card_id, userId, TrialPeriod, comeFrom = "";
    int position;
    ArrayList<PlanListBillingModel.ResponseData.Plan> listModelList2;
    private ArrayList<MembershipPlanListModel.Plan> listModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment);
        context = PaymentActivity.this;

        SharedPreferences shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        userId = (shared.getString(CONSTANTS.PREF_KEY_UserID, ""));
        Glide.with(context).load(R.drawable.loading).asGif().into(binding.ImgV);
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
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, OrderSummaryActivity.class);
                i.putExtra("comeFrom", "membership");
                i.putParcelableArrayListExtra("PlanData", listModelList2);
                i.putExtra("TrialPeriod", "");
                i.putExtra("position", position);
                startActivity(i);
                finish();
            }
        });


        binding.llAddNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AddPaymentActivity.class);
                i.putExtra("ComePayment", "2");
                startActivity(i);
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
                showProgressBar();
                Call<CardListModel> listCall = APIClient.getClient().getCardLists(userId);
                listCall.enqueue(new Callback<CardListModel>() {
                    @Override
                    public void onResponse(Call<CardListModel> call, Response<CardListModel> response) {
                        if (response.isSuccessful()) {
                            hideProgressBar();
                            try {
                                CardListModel cardListModel = response.body();
                                if (cardListModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                    if (cardListModel.getResponseData().size() == 0) {
                                        binding.rvCardList.setAdapter(null);
                                        binding.rvCardList.setVisibility(View.GONE);
                                    } else {
                                        binding.rvCardList.setVisibility(View.VISIBLE);
                                        adapter = new AllCardsAdapter(cardListModel.getResponseData(), binding.ImgV,
                                                binding.progressBarHolder, binding.rvCardList);
                                        binding.rvCardList.setAdapter(adapter);
                                    }

                                    binding.btnCheckout.setOnClickListener(view -> {
                                        if (cardListModel.getResponseData().size() == 0) {
                                            BWSApplication.showToast("Please enter card details", context);
                                        } else {

                                            if (BWSApplication.isNetworkConnected(context)) {
                                                showProgressBar();
                                                Call<PayNowDetailsModel> listCall = APIClient.getClient().getPayNowDetails(userId, card_id, renewPlanId, renewPlanFlag,
                                                        invoicePayId, PlanStatus);
                                                listCall.enqueue(new Callback<PayNowDetailsModel>() {
                                                    @Override
                                                    public void onResponse(Call<PayNowDetailsModel> call, Response<PayNowDetailsModel> response) {
                                                        if (response.isSuccessful()) {
                                                            hideProgressBar();
                                                            PayNowDetailsModel listModel1 = response.body();
                                                            BWSApplication.showToast(listModel1.getResponseMessage(), context);
                                                            Intent i = new Intent(context, BillingOrderActivity.class);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<PayNowDetailsModel> call, Throwable t) {
                                                        hideProgressBar();
                                                    }
                                                });
                                            } else {
                                                hideProgressBar();
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
                        hideProgressBar();
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
        ImageView ImgV;
        FrameLayout progressBarHolder;
        RecyclerView rvCardList;
        AllCardsAdapter adapter;

        public AllCardsAdapter(List<CardListModel.ResponseData> listModelList, ImageView ImgV,
                               FrameLayout progressBarHolder, RecyclerView rvCardList) {
            this.listModelList = listModelList;
            this.ImgV = ImgV;
            this.progressBarHolder = progressBarHolder;
            this.rvCardList = rvCardList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Glide.with(context).load(R.drawable.loading).asGif().into(ImgV);
            CardsListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.cards_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            CardListModel.ResponseData listModel = listModelList.get(position);
            holder.binding.tvCardNo.setText(getString(R.string.first_card_chars) + listModel.getLast4());
            holder.binding.tvExpiryTime.setText("Valid: " + listModel.getExpMonth() + "/" +
                    listModel.getExpYear());
            Glide.with(context).load(listModel.getImage())
                    .thumbnail(0.05f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.ivCardimg);
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
                    showProgressBar();
                    Call<CardListModel> listCall = APIClient.getClient().getChangeCard(userId, listModel.getCustomer());
                    listCall.enqueue(new Callback<CardListModel>() {
                        @Override
                        public void onResponse(Call<CardListModel> call, Response<CardListModel> response) {
                            hideProgressBar();
                            {
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
                                } else {
                                    BWSApplication.showToast(cardListModel.getResponseMessage(), context);
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<CardListModel> call, Throwable t) {
                            hideProgressBar();
                        }

                    });
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), context);
                    hideProgressBar();
                }

            });

            holder.binding.rlRemoveCard.setOnClickListener(view -> {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.cancel_membership);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
                final TextView tvSubTitle = dialog.findViewById(R.id.tvSubTitle);
                final RelativeLayout tvconfirm = dialog.findViewById(R.id.tvconfirm);
                tvTitle.setText("Delete payment card");
                tvSubTitle.setText("Are you sure you want to delete the payment card ?");
                dialog.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                });

                tvconfirm.setOnClickListener(v -> {
                    if (BWSApplication.isNetworkConnected(context)) {
                        showProgressBar();
                        Call<CardModel> listCall = APIClient.getClient().getRemoveCard(userId, listModel.getCustomer());
                        listCall.enqueue(new Callback<CardModel>() {
                            @Override
                            public void onResponse(Call<CardModel> call, Response<CardModel> response) {
                                hideProgressBar();
                                if (response.isSuccessful()) {
                                    CardModel cardModel = response.body();
                                    if (cardModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                        prepareCardList();
                                    } else {
                                        BWSApplication.showToast(cardModel.getResponseMessage(), context);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<CardModel> call, Throwable t) {
                                hideProgressBar();
                            }
                        });
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), context);
                        hideProgressBar();
                    }
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(PaymentActivity.this, OrderSummaryActivity.class);
        i.putExtra("comeFrom", "membership");
        i.putParcelableArrayListExtra("PlanData", listModelList2);
        i.putExtra("TrialPeriod", "");
        i.putExtra("position", position);
        startActivity(i);
        finish();
    }
}